package org.relaymodding.witcheroo;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.relaymodding.witcheroo.capabilities.PhysicalFamiliar;
import org.relaymodding.witcheroo.capabilities.PhysicalFamiliarImpl;
import org.relaymodding.witcheroo.capabilities.PhysicalFamiliarProvider;
import org.relaymodding.witcheroo.capabilities.Witch;
import org.relaymodding.witcheroo.capabilities.WitchImpl;
import org.relaymodding.witcheroo.capabilities.WitchProvider;
import org.relaymodding.witcheroo.commands.WitcherooCommands;
import org.relaymodding.witcheroo.datagen.WitcherooDatagen;
import org.relaymodding.witcheroo.familiar.Familiar;
import org.relaymodding.witcheroo.familiar.FamiliarBounding;
import org.relaymodding.witcheroo.network.WitcherooPacketHandler;
import org.relaymodding.witcheroo.recipe.rituals.alchemy.AlchemyRitual;
import org.relaymodding.witcheroo.recipe.rituals.alchemy.SwitchBlockRitual;
import org.relaymodding.witcheroo.registries.WitcherooRegistries;

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

        MinecraftForge.EVENT_BUS.addListener(Witcheroo::summonTest);
        MinecraftForge.EVENT_BUS.addListener(Witcheroo::ritualTest);
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

    public static void summonTest(final PlayerInteractEvent.EntityInteractSpecific event) {
        if (!(event.getLevel() instanceof ServerLevel) || event.getHand() != InteractionHand.MAIN_HAND) return;
        if (event.getTarget() instanceof Cat cat) {
            event.getEntity().getCapability(WITCH_CAPABILITY).ifPresent(capability -> {
                if (!capability.getOwnedFamiliars().isEmpty()) {
                    FamiliarBounding.grantPhysicalBody(event.getEntity(), capability.getOwnedFamiliars().toArray(Familiar[]::new)[0], cat);
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
