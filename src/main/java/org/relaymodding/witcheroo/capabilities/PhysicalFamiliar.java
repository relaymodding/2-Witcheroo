package org.relaymodding.witcheroo.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.relaymodding.witcheroo.Witcheroo;
import org.relaymodding.witcheroo.familiar.behaviour.FamiliarBehaviour;

import java.util.UUID;

public interface PhysicalFamiliar {

    ResourceLocation ID = Witcheroo.resourceLocation("physical_familiar");

    int getEntityId();

    @Nullable PathfinderMob getEntity(Level level);

    void attachTo(PathfinderMob entity);

    boolean isBound();

    void setBound();

    void setOwner(UUID uuid);

    UUID getOwner();

    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag tag);

    FamiliarBehaviour getBehaviour();

    void setBehaviour(FamiliarBehaviour behaviour);
}
