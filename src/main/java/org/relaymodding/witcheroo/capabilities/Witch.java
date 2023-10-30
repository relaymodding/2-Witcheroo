package org.relaymodding.witcheroo.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import org.relaymodding.witcheroo.Witcheroo;
import org.relaymodding.witcheroo.familiar.Familiar;
import org.relaymodding.witcheroo.familiar.type.FamiliarType;
import org.relaymodding.witcheroo.network.SyncWitchPacket;
import org.relaymodding.witcheroo.network.WitcherooPacketHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public interface Witch {

    ResourceLocation ID = Witcheroo.resourceLocation("witch");

    Collection<Familiar> getOwnedFamiliars();

    void addFamiliar(Familiar Familiar);

    void removeFamiliar(FamiliarType type);

    void setFamiliars(Collection<Familiar> familiars);

    Familiar getFamiliar(FamiliarType type);

    boolean hasFamiliar(FamiliarType type);

    Set<ResourceLocation> getCompletedRituals();

    void setCompletedRituals(Set<ResourceLocation> rituals);


    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag tag);


    int getMana();

    int getMaxMana();


    void setMana(int mana);

    boolean consumeMana(int mana);

    default void toNetwork(FriendlyByteBuf buf) {
        buf.writeCollection(getOwnedFamiliars(), (buf1, familiar) -> familiar.toNetwork(buf1));
        buf.writeCollection(getCompletedRituals(), FriendlyByteBuf::writeResourceLocation);
        buf.writeInt(getMana());
    }

    default void fromNetwork(FriendlyByteBuf buf) {
        setFamiliars(buf.readCollection(ArrayList::new, Familiar::createFromNetwork));
        setCompletedRituals(buf.readCollection(HashSet::new, FriendlyByteBuf::readResourceLocation));
        setMana(buf.readInt());
    }

    default void sync(ServerPlayer player) {
        WitcherooPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SyncWitchPacket(this.getOwnedFamiliars(), this.getCompletedRituals(), this.getMana()));
    }
}
