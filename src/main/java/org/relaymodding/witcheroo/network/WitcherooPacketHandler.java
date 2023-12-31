package org.relaymodding.witcheroo.network;

import org.relaymodding.witcheroo.Witcheroo;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.relaymodding.witcheroo.util.Reference;

public class WitcherooPacketHandler {

    private static int id = 0;

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Reference.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerPackets() {
        INSTANCE.registerMessage(id++, SyncFamiliarPacket.class, SyncFamiliarPacket::encode, SyncFamiliarPacket::decode, SyncFamiliarPacket::handle);
        INSTANCE.registerMessage(id++, SyncWitchPacket.class, SyncWitchPacket::encode, SyncWitchPacket::decode, SyncWitchPacket::handle);
        INSTANCE.registerMessage(id++, SyncAllFamiliarsPacket.class, SyncAllFamiliarsPacket::encode, SyncAllFamiliarsPacket::decode, SyncAllFamiliarsPacket::handle);
    }

}
