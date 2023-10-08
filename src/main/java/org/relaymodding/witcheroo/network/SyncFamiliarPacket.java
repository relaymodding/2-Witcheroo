package org.relaymodding.witcheroo.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.relaymodding.witcheroo.client.WitcherooClientHandler;

import java.util.function.Supplier;

public class SyncFamiliarPacket {

    public static final boolean ADD_ENTITY = true;
    public static final boolean REMOVE_ENTITY = false;

    private final int entityId;
    private final boolean operation;

    public SyncFamiliarPacket(int entityId, boolean operation) {
        this.entityId = entityId;
        this.operation = operation;
    }

    public static SyncFamiliarPacket decode(FriendlyByteBuf byteBuf) {
        return new SyncFamiliarPacket(byteBuf.readInt(), byteBuf.readBoolean());
    }

    public void encode(FriendlyByteBuf byteBuf) {
        byteBuf.writeInt(entityId);
        byteBuf.writeBoolean(operation);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            WitcherooClientHandler.handlePacket(entityId, operation);
        });
        context.get().setPacketHandled(true);
    }


}
