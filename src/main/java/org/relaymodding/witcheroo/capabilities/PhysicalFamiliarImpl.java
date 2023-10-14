package org.relaymodding.witcheroo.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.relaymodding.witcheroo.familiar.behaviour.FamiliarBehaviour;

import java.util.UUID;

public class PhysicalFamiliarImpl implements PhysicalFamiliar {

    private static final String ENTITY_ID = "witcheroo_entityid";
    private int entityId;
    private FamiliarBehaviour behaviour;
    private UUID ownerId;

    private static final String OWNER = "witcheroo_owner";

    private boolean isBound;

    public PhysicalFamiliarImpl() {
    }

    public PhysicalFamiliarImpl(PathfinderMob entity) {
        this.entityId = entity.getId();
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    @Override
    public PathfinderMob getEntity(Level level) {
        return (PathfinderMob) level.getEntity(this.entityId);
    }

    @Override
    public void attachTo(PathfinderMob mob) {
        this.entityId = mob.getId();
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
            tag.putInt(ENTITY_ID, entityId);
            tag.putUUID(OWNER, ownerId);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains(ENTITY_ID)) {
            entityId = tag.getInt(ENTITY_ID);
            ownerId = tag.getUUID(OWNER);
        }
    }
}
