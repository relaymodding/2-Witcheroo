package org.relaymodding.witcheroo.familiar;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.relaymodding.witcheroo.familiar.type.FamiliarType;
import org.relaymodding.witcheroo.registries.WitcherooRegistries;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;

public class Familiar {

    private static final String TYPE = "type";
    private static final String ENTITY_ID = "entity_id";
    private static final String DEFINITION = "definition";
    private static final String LEVEL = "level";

    private FamiliarType type;
    private UUID entityId;
    private FamiliarDefinition definition;
    private int level;

    private boolean physicalBody = false;

    public FamiliarDefinition getFamiliarDefinition() {
        return definition;
    }

    public void setFamiliarDefinition(final FamiliarDefinition definition) {
        this.definition = definition;
    }

    public void setType(FamiliarType type) {

        this.type = type;
    }

    public FamiliarType getType() {

        return this.type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setPhysicalBody(boolean value) {
        physicalBody = value;
    }

    @Nullable
    public PathfinderMob getEntity(ServerLevel level) {
    	if (hasPhysicalBody() && level.getEntity(this.entityId) instanceof PathfinderMob mob) {
    		return mob;
    	}
    	
        return null;
    }

    public void attachTo(PathfinderMob mob, Player owner) {
        this.entityId = mob.getUUID();
        mob.removeFreeWill();
        mob.targetSelector.removeAllGoals(goal -> true);
        definition.behaviour().registerGoals(mob, owner);
        
        this.setPhysicalBody(true);
    }

    public boolean hasPhysicalBody() {
        return physicalBody;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString(TYPE, WitcherooRegistries.getFamiliarTypeRegistry().getKey(this.type).toString());
        if (hasPhysicalBody()) {
            tag.putUUID(ENTITY_ID, entityId);
        }
        tag.putString(DEFINITION, WitcherooRegistries.getFamiliarDefinitionRegistry().getKey(definition).toString());
        tag.putInt(LEVEL, level);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        this.type = WitcherooRegistries.getFamiliarTypeRegistry().get(new ResourceLocation(tag.getString(TYPE)));
        if (tag.contains(ENTITY_ID, Tag.TAG_INT_ARRAY)) {
            this.physicalBody = true;
            entityId = tag.getUUID(ENTITY_ID);
        }
        definition = WitcherooRegistries.getFamiliarDefinitionRegistry().get(new ResourceLocation(tag.getString(DEFINITION)));
        level = tag.getInt(LEVEL);
    }
}
