package org.relaymodding.witcheroo.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.relaymodding.witcheroo.Witcheroo;
import org.relaymodding.witcheroo.familiar.Familiar;

import java.util.Collection;
import java.util.Set;

public interface Witch {

    ResourceLocation ID = Witcheroo.resourceLocation("witch");

    Collection<Familiar> getOwnedFamiliars();

    void addFamiliar(Familiar Familiar);

    void removeFamiliarByName(final String name);

    Familiar getFamiliarByName(final String name);

    Set<ResourceLocation> getCompletedRituals();

    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag tag);

    int getMana();
}
