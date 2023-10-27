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
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.relaymodding.witcheroo.commands.WitcherooCommands;
import org.relaymodding.witcheroo.datagen.WitcherooDatagen;
import org.relaymodding.witcheroo.events.WitcherooCustomEvents;
import org.relaymodding.witcheroo.events.WitcherooForgeEvents;
import org.relaymodding.witcheroo.events.WitcherooTestEvents;
import org.relaymodding.witcheroo.events.WitchStaffGUIEvents;
import org.relaymodding.witcheroo.items.WitcherooItems;
import org.relaymodding.witcheroo.network.WitcherooPacketHandler;
import org.relaymodding.witcheroo.registries.WitcherooRegistries;
import org.relaymodding.witcheroo.util.Reference;

@Mod(Reference.MOD_ID)
public class Witcheroo {

    public Witcheroo() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(WitcherooRegistries::registerDatapackRegistry);
        modEventBus.addListener(WitcherooDatagen::datagen);
        modEventBus.addListener(Witcheroo::loadComplete);
        modEventBus.addListener(Witcheroo::addToCreativeTab);

        modEventBus.addListener(WitcherooCustomEvents::registerCapabilitiesEvent);
		MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, WitcherooCustomEvents::attachCapabilitiesEvent);

        MinecraftForge.EVENT_BUS.addListener(WitcherooForgeEvents::familiarBodyLoadEvent);
        MinecraftForge.EVENT_BUS.addListener(WitcherooForgeEvents::familiarBodyDeathEvent);

        MinecraftForge.EVENT_BUS.addListener(WitcherooForgeEvents::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(WitcherooForgeEvents::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(WitcherooForgeEvents::onEntityInteract);
        MinecraftForge.EVENT_BUS.addListener(WitcherooForgeEvents::onCauldronRightClick);
        MinecraftForge.EVENT_BUS.addListener(WitcherooForgeEvents::onWitchStaffRightClick);

		MinecraftForge.EVENT_BUS.addListener(WitcherooTestEvents::ritualTest);
        MinecraftForge.EVENT_BUS.addListener(WitcherooTestEvents::familiarSummonTest);

        MinecraftForge.EVENT_BUS.addListener(Witcheroo::registerCommands);

        WitcherooRegistries.FAMILIAR_TYPE_SERIALIZERS.register(modEventBus);
        WitcherooRegistries.FAMILIAR_BEHAVIOURS.register(modEventBus);
        WitcherooRegistries.ITEMS.register(modEventBus);

        WitcherooPacketHandler.registerPackets();
    }

    private static void loadComplete(final FMLLoadCompleteEvent event) {
        WitcherooItems.loadItems();

        if (FMLEnvironment.dist.equals(Dist.CLIENT)) {
            MinecraftForge.EVENT_BUS.register(new WitchStaffGUIEvents(Minecraft.getInstance(), Minecraft.getInstance().getItemRenderer()));
        }
    }

    public static void addToCreativeTab(BuildCreativeModeTabContentsEvent e) {
        if (e.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            e.accept(WitcherooItems.WITCH_STAFF);
        }
    }

    public static void registerCommands(RegisterCommandsEvent event) {
        WitcherooCommands.buildCommands(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }

    public static ResourceLocation resourceLocation(final String value) {
        return new ResourceLocation(Reference.MOD_ID, value);
    }
}
