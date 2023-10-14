package org.relaymodding.witcheroo.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.relaymodding.witcheroo.Witcheroo;
import org.relaymodding.witcheroo.familiar.Familiar;
import org.relaymodding.witcheroo.familiar.type.FamiliarType;

import java.util.Collection;
import java.util.Set;

public interface Witch {

    ResourceLocation ID = Witcheroo.resourceLocation("witch");

    Collection<Familiar> getOwnedFamiliars();

    void addFamiliar(Familiar Familiar);

    void removeFamiliar(FamiliarType type);

    Familiar getFamiliar(FamiliarType type);

    boolean hasFamiliar(FamiliarType type);

    Set<ResourceLocation> getCompletedRituals();

    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag tag);

    int getMana();

    int getMaxMana();
}
