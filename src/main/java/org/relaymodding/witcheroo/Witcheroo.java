package org.relaymodding.witcheroo;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.relaymodding.witcheroo.commands.WitcherooCommands;
import org.relaymodding.witcheroo.datagen.WitcherooDatagen;
import org.relaymodding.witcheroo.events.*;
import org.relaymodding.witcheroo.network.WitcherooPacketHandler;
import org.relaymodding.witcheroo.registries.WitcherooRegistries;
import org.relaymodding.witcheroo.util.Reference;

@Mod(Reference.MOD_ID)
public class Witcheroo {

    public Witcheroo() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(WitcherooRegistries::registerDatapackRegistry);
        modEventBus.addListener(WitcherooDatagen::datagen);
        modEventBus.addListener(Witcheroo::clientSetup);
        modEventBus.addListener(Witcheroo::addToCreativeTab);

        modEventBus.addListener(WitcherooCustomEvents::registerCapabilitiesEvent);
        modEventBus.addListener(WitcherooClientEvents::registerBERs);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, WitcherooCustomEvents::attachCapabilitiesEvent);

        MinecraftForge.EVENT_BUS.addListener(WitcherooForgeEvents::familiarBodyLoadEvent);
        MinecraftForge.EVENT_BUS.addListener(WitcherooForgeEvents::playerLogin);
        MinecraftForge.EVENT_BUS.addListener(WitcherooForgeEvents::familiarBodyDeathEvent);

        MinecraftForge.EVENT_BUS.addListener(WitcherooForgeEvents::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(WitcherooForgeEvents::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(WitcherooForgeEvents::onEntityInteract);
        MinecraftForge.EVENT_BUS.addListener(WitcherooForgeEvents::onCauldronRightClick);
        MinecraftForge.EVENT_BUS.addListener(WitcherooForgeEvents::triggerConsumeNature);
        MinecraftForge.EVENT_BUS.addListener(WitcherooForgeEvents::triggerMana);
        MinecraftForge.EVENT_BUS.addListener(WitcherooForgeEvents::onWitchStaffRightClick);

        MinecraftForge.EVENT_BUS.addListener(WitcherooTestEvents::ritualTest);
        MinecraftForge.EVENT_BUS.addListener(WitcherooTestEvents::familiarSummonTest);

        MinecraftForge.EVENT_BUS.addListener(Witcheroo::registerCommands);

        WitcherooRegistries.FAMILIAR_TYPE_SERIALIZERS.register(modEventBus);
        WitcherooRegistries.FAMILIAR_BEHAVIOURS.register(modEventBus);
        WitcherooRegistries.ITEMS.register(modEventBus);
        WitcherooRegistries.BLOCKS.register(modEventBus);
        WitcherooRegistries.BLOCK_ENTITY_TYPE.register(modEventBus);



        WitcherooPacketHandler.registerPackets();
    }

    private static void clientSetup(final FMLClientSetupEvent event){
        MinecraftForge.EVENT_BUS.register(new WitchStaffGUIEvents(Minecraft.getInstance(), Minecraft.getInstance().getItemRenderer()));
    }

    public static void addToCreativeTab(BuildCreativeModeTabContentsEvent e) {
        if (e.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            e.accept(WitcherooRegistries.WITCH_STAFF_OBJECT.get());
        }
    }

    public static void registerCommands(RegisterCommandsEvent event) {
        WitcherooCommands.buildCommands(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }

    public static ResourceLocation resourceLocation(final String value) {
        return new ResourceLocation(resourceString(value));
    }

    public static String resourceString(final String value) {
        return Reference.MOD_ID + ":" + value;
    }
}
