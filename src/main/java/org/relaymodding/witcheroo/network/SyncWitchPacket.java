package org.relaymodding.witcheroo.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import org.relaymodding.witcheroo.client.WitcherooClientHandler;
import org.relaymodding.witcheroo.familiar.Familiar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class SyncWitchPacket {

    private final Collection<Familiar> ownedFamiliars;
    private final Set<ResourceLocation> completedRituals;
    private final int mana;

    public SyncWitchPacket(Collection<Familiar> ownedFamiliars, Set<ResourceLocation> completedRituals, int mana) {
        this.ownedFamiliars = ownedFamiliars;
        this.completedRituals = completedRituals;
        this.mana = mana;
    }

    public static SyncWitchPacket decode(FriendlyByteBuf byteBuf) {
        Collection<Familiar> ownedFamiliars = byteBuf.readCollection(ArrayList::new, Familiar::createFromNetwork);
        Set<ResourceLocation> completeRituals = byteBuf.readCollection(HashSet::new, FriendlyByteBuf::readResourceLocation);
        int mana = byteBuf.readInt();
        return new SyncWitchPacket(ownedFamiliars, completeRituals, mana);
    }

    public void encode(FriendlyByteBuf byteBuf) {
        byteBuf.writeCollection(ownedFamiliars, (buf1, familiar) -> familiar.toNetwork(buf1));
        byteBuf.writeCollection(completedRituals, FriendlyByteBuf::writeResourceLocation);
        byteBuf.writeInt(mana);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            WitcherooClientHandler.handleWitchSync(ownedFamiliars, completedRituals, mana);
        });
        context.get().setPacketHandled(true);
    }


}
