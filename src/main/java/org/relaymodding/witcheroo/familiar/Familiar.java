package org.relaymodding.witcheroo.familiar;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.relaymodding.witcheroo.familiar.type.FamiliarType;
import org.relaymodding.witcheroo.registries.WitcherooRegistries;

public class Familiar {

    private static final String TYPE = "type";
    private static final String ENTITY_ID = "entity_id";
    private static final String DEFINITION = "definition";
    private static final String LEVEL = "level";

    private FamiliarType type;
    private int entityId;
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

    public int getEntityId() {
        return entityId;
    }

    public void setPhysicalBody(boolean value) {
        physicalBody = value;
    }

    @Nullable
    public PathfinderMob getEntity(Level level) {
        return (PathfinderMob) level.getEntity(this.entityId);
    }

    public void attachTo(PathfinderMob mob, Player owner) {
        this.entityId = mob.getId();
        mob.removeFreeWill();
        mob.targetSelector.removeAllGoals(goal -> true);
        definition.behaviour().registerGoals(mob, owner);
    }

    public boolean hasPhysicalBody() {
        return physicalBody;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString(TYPE, WitcherooRegistries.getFamiliarTypeRegistry().getKey(this.type).toString());
        if (hasPhysicalBody()) {
            tag.putInt(ENTITY_ID, entityId);
        }
        tag.putString(DEFINITION, WitcherooRegistries.getFamiliarDefinitionRegistry().getKey(definition).toString());
        tag.putInt(LEVEL, level);
        return tag;
    }


    public void deserializeNBT(CompoundTag tag) {
        this.type = WitcherooRegistries.getFamiliarTypeRegistry().get(new ResourceLocation(tag.getString(TYPE)));
        if (tag.contains(ENTITY_ID)) {
            this.physicalBody = true;
            entityId = tag.getInt(ENTITY_ID);
        }
        definition = WitcherooRegistries.getFamiliarDefinitionRegistry().get(new ResourceLocation(tag.getString(DEFINITION)));
        level = tag.getInt(LEVEL);
    }
}
