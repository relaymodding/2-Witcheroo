package org.relaymodding.witcheroo;

import java.util.Optional;

import org.relaymodding.witcheroo.capabilities.PhysicalFamiliar;
import org.relaymodding.witcheroo.capabilities.PhysicalFamiliarImpl;
import org.relaymodding.witcheroo.capabilities.PhysicalFamiliarProvider;
import org.relaymodding.witcheroo.capabilities.Witch;
import org.relaymodding.witcheroo.capabilities.WitchImpl;
import org.relaymodding.witcheroo.capabilities.WitchProvider;
import org.relaymodding.witcheroo.commands.WitcherooCommands;
import org.relaymodding.witcheroo.datagen.WitcherooDatagen;
import org.relaymodding.witcheroo.familiar.FamiliarBounding;
import org.relaymodding.witcheroo.network.SyncFamiliarPacket;
import org.relaymodding.witcheroo.network.WitcherooPacketHandler;
import org.relaymodding.witcheroo.recipe.rituals.alchemy.SwitchBlockRitual;
import org.relaymodding.witcheroo.registries.WitcherooRegistries;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.PacketDistributor;

@Mod(Witcheroo.MOD_ID)
public class Witcheroo {
    public static final String MOD_ID = "witcheroo";
    public static final Capability<PhysicalFamiliar> FAMILIAR_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final Capability<Witch> WITCH_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public Witcheroo() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(WitcherooRegistries::registerDatapackRegistry);
        modEventBus.addListener(WitcherooDatagen::datagen);
        modEventBus.addListener(Witcheroo::registerCapabilitiesEvent);

        MinecraftForge.EVENT_BUS.addListener(Witcheroo::ritualTest);
        MinecraftForge.EVENT_BUS.addListener(Witcheroo::familiarBodyLoadEvent);
        MinecraftForge.EVENT_BUS.addListener(Witcheroo::familiarBodyDeathEvent);
        MinecraftForge.EVENT_BUS.addListener(Witcheroo::familiarSummonTest);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, Witcheroo::attachCapabilitiesEvent);
        MinecraftForge.EVENT_BUS.addListener(Witcheroo::registerCommands);

        WitcherooRegistries.FAMILIAR_TYPE_SERIALIZERS.register(modEventBus);
        WitcherooRegistries.FAMILIAR_BEHAVIOURS.register(modEventBus);

        WitcherooPacketHandler.registerPackets();
    }

    public static void registerCapabilitiesEvent(final RegisterCapabilitiesEvent registerCapabilitiesEvent) {
        registerCapabilitiesEvent.register(PhysicalFamiliar.class);
        registerCapabilitiesEvent.register(Witch.class);
    }

    public static void attachCapabilitiesEvent(final AttachCapabilitiesEvent<Entity> attachCapabilitiesEvent) {
        if (attachCapabilitiesEvent.getObject() instanceof Player) {
            Witch capability = new WitchImpl();
            LazyOptional<Witch> lazyOptional = LazyOptional.of(() -> capability);
            attachCapabilitiesEvent.addCapability(Witch.ID, new WitchProvider(lazyOptional));
        }
        if (attachCapabilitiesEvent.getObject() instanceof PathfinderMob mob) {
            PhysicalFamiliar capability = new PhysicalFamiliarImpl(mob);
            LazyOptional<PhysicalFamiliar> lazyOptional = LazyOptional.of(() -> capability);
            attachCapabilitiesEvent.addCapability(PhysicalFamiliar.ID, new PhysicalFamiliarProvider(lazyOptional));
        }
    }

    public static void ritualTest(PlayerInteractEvent.RightClickBlock event) {

        if (SwitchBlockRitual.DIRT_TO_DIAMOND.canAffect(event.getEntity(), event.getLevel(), event.getPos(), event.getLevel().getBlockState(event.getPos()))) {

            SwitchBlockRitual.DIRT_TO_DIAMOND.apply(event.getEntity(), event.getLevel(), event.getPos(), event.getLevel().getBlockState(event.getPos()));
        }
    }
    
    // TODO FIXME
    public static void familiarBodyLoadEvent(final EntityJoinLevelEvent event) {
    	final Entity entity = event.getEntity();
    	entity.getCapability(Witcheroo.FAMILIAR_CAPABILITY).ifPresent(cap -> {
    		if (cap.getOwner() != null) {
                WitcherooPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncFamiliarPacket(entity.getId(), SyncFamiliarPacket.ADD_ENTITY));
    		}
    	});
    }
    
    public static void familiarBodyDeathEvent(final LivingDeathEvent event) {
    	final LivingEntity entity = event.getEntity();
    	final Level level = entity.level();
    	entity.getCapability(Witcheroo.FAMILIAR_CAPABILITY).ifPresent(cap -> {
    		final Player player = Optional.ofNullable(cap.getOwner()).map(level::getPlayerByUUID).orElse(null);
    		
    		if (player != null) {
    			player.getCapability(WITCH_CAPABILITY).ifPresent(capability -> {
    				capability.getOwnedFamiliars().stream()
    					.filter(f -> f.hasPhysicalBody() && f.getEntityId().equals(entity.getUUID()))
    					.forEach(f -> f.setPhysicalBody(false));
    			});
    		}
    		
            WitcherooPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncFamiliarPacket(entity.getId(), SyncFamiliarPacket.REMOVE_ENTITY));
            cap.setBound(false);
    	});
    }
    
    public static void familiarSummonTest(final PlayerInteractEvent.EntityInteractSpecific event) {
        if (!(event.getLevel() instanceof ServerLevel) || event.getHand() != InteractionHand.MAIN_HAND) return;
        
        if (event.getTarget() instanceof PathfinderMob mob && !mob.getCapability(Witcheroo.FAMILIAR_CAPABILITY).map(PhysicalFamiliar::isBound).orElse(true)) {
            event.getEntity().getCapability(WITCH_CAPABILITY).ifPresent(capability -> {
                if (!capability.getOwnedFamiliars().isEmpty()) {
                	capability.getOwnedFamiliars().stream()
                		.filter(f -> !f.hasPhysicalBody() && f.getType().isSuitableVessel(mob)).findAny()
                		.ifPresent(familiar -> {
                			FamiliarBounding.grantPhysicalBody(event.getEntity(), familiar, mob);
                		});
                }
            });
        }
        
        if (event.getTarget() instanceof Pig) {

            event.getEntity().getCapability(Witcheroo.WITCH_CAPABILITY).ifPresent(witch -> {

                WitcherooRegistries.getFamiliarTypeRegistry().stream().filter(type -> !witch.hasFamiliar(type)).findAny().ifPresent(newType -> {
                    FamiliarBounding.unlockFamiliarType(event.getEntity(), newType, WitcherooRegistries.getFamiliarDefinitionRegistry().stream().findAny().get());
                    event.getEntity().sendSystemMessage(Component.translatable("witcheroo.notices.obtained_familiair", newType.getDisplayName()));
                });
            });
        }
    }

    public static void registerCommands(RegisterCommandsEvent event) {

        WitcherooCommands.buildCommands(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }


    public static ResourceLocation resourceLocation(final String value) {
        return new ResourceLocation(MOD_ID, value);
    }
}
