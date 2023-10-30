package org.relaymodding.witcheroo.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.relaymodding.witcheroo.client.WitcherooClientHandler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncAllFamiliarsPacket {

    private final Collection<UUID> entityIds;

    public SyncAllFamiliarsPacket(Collection<UUID> entityIds) {
        this.entityIds = entityIds;
    }

    public static SyncAllFamiliarsPacket decode(FriendlyByteBuf byteBuf) {
        return new SyncAllFamiliarsPacket(byteBuf.readCollection(HashSet::new, FriendlyByteBuf::readUUID));
    }

    public void encode(FriendlyByteBuf byteBuf) {
        byteBuf.writeCollection(this.entityIds, FriendlyByteBuf::writeUUID);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            WitcherooClientHandler.handleAllFamiliarsSync(entityIds);
        });
        context.get().setPacketHandled(true);
    }


}
