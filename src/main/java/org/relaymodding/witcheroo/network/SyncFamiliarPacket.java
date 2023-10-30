package org.relaymodding.witcheroo.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.relaymodding.witcheroo.client.WitcherooClientHandler;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncFamiliarPacket {

    public static final boolean ADD_ENTITY = true;
    public static final boolean REMOVE_ENTITY = false;

    private final UUID entityId;
    private final boolean operation;

    public SyncFamiliarPacket(UUID entityId, boolean operation) {
        this.entityId = entityId;
        this.operation = operation;
    }

    public static SyncFamiliarPacket decode(FriendlyByteBuf byteBuf) {
        return new SyncFamiliarPacket(byteBuf.readUUID(), byteBuf.readBoolean());
    }

    public void encode(FriendlyByteBuf byteBuf) {
        byteBuf.writeUUID(entityId);
        byteBuf.writeBoolean(operation);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            WitcherooClientHandler.handleFamiliarSync(entityId, operation);
        });
        context.get().setPacketHandled(true);
    }


}
