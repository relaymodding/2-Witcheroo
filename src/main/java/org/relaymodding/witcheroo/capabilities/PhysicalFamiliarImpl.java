package org.relaymodding.witcheroo.capabilities;

import java.util.UUID;

import org.relaymodding.witcheroo.familiar.behaviour.FamiliarBehaviour;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;

public class PhysicalFamiliarImpl implements PhysicalFamiliar {

    private static final String ENTITY_ID = "entity_id";
    private UUID entityId;
    private FamiliarBehaviour behaviour;
    private UUID ownerId;

    private static final String OWNER = "owner";

    private boolean isBound;

    public PhysicalFamiliarImpl() {
    }

    public PhysicalFamiliarImpl(PathfinderMob entity) {
        this.entityId = entity.getUUID();
    }

    @Override
    public UUID getEntityId() {
        return entityId;
    }

    @Override
    public PathfinderMob getEntity(ServerLevel level) {
        return (PathfinderMob) level.getEntity(this.entityId);
    }

    @Override
    public void attachTo(PathfinderMob mob) {
        this.entityId = mob.getUUID();
    }

    @Override
    public boolean isBound() {
        return isBound;
    }

    @Override
    public void setBound(boolean toBind) {
        isBound = toBind;
    }

    @Override
    public UUID getOwner() {
        return ownerId;
    }

    @Override
    public void setOwner(UUID uuid) {
        ownerId = uuid;
    }

    @Override
    public FamiliarBehaviour getBehaviour() {
        return behaviour;
    }

    @Override
    public void setBehaviour(FamiliarBehaviour behaviour) {
        this.behaviour = behaviour;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (isBound) {
            tag.putUUID(ENTITY_ID, entityId);
            tag.putUUID(OWNER, ownerId);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains(ENTITY_ID, Tag.TAG_INT_ARRAY)) {
            entityId = tag.getUUID(ENTITY_ID);
            ownerId = tag.getUUID(OWNER);
            isBound = true;
        }
    }
}
