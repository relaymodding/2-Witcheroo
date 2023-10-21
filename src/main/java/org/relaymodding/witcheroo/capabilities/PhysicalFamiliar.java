package org.relaymodding.witcheroo.capabilities;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.relaymodding.witcheroo.Witcheroo;
import org.relaymodding.witcheroo.familiar.behaviour.FamiliarBehaviour;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;

public interface PhysicalFamiliar {

    ResourceLocation ID = Witcheroo.resourceLocation("physical_familiar");

    UUID getEntityId();

    @Nullable PathfinderMob getEntity(ServerLevel level);

    void attachTo(PathfinderMob entity);

    boolean isBound();

    void setBound(boolean bound);

    void setOwner(UUID uuid);

    UUID getOwner();

    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag tag);

    FamiliarBehaviour getBehaviour();

    void setBehaviour(FamiliarBehaviour behaviour);
}
