package org.relaymodding.witcheroo.familiar;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import org.relaymodding.witcheroo.registries.WitcherooRegistries;

import java.util.concurrent.ThreadLocalRandom;

public class Familiar {

    private static final String ENTITY_ID = "witcheroo_entityid";
    private static final String DEFINITION = "witcheroo_definition";
    private static final String LEVEL = "witcheroo_level";
    private static final String MANA = "witcheroo_mana";
    private static final String NAME = "witcheroo_name";
    private int entityId;
    private FamiliarDefinition definition;
    private int level;
    private int mana;
    private String name;

    private static final String[] NAMES = {
         "Agatha",
         "Ambrose",
         "Ash",
         "Clover",
         "Grimm",
         "Luna",
         "Nero"
    };

    private boolean physicalBody = false;
    
    public FamiliarDefinition getFamiliarDefinition(){
        return definition;
    }
    
    public void setFamiliarDefinition(final FamiliarDefinition definition){
        this.definition = definition;
    }
    
    public int getLevel(){
        return level;
    }
    
    public void setLevel(int level){
        this.level = level;
    }
    
    public int getMana(){
        return mana;
    }

    
    public void setMana(int mana){
        this.mana = mana;
    }

    
    public String getName(){
        return name;
    }

    
    public void setName(String name){
        this.name = name;
    }

    
    public int getEntityId(){
        return entityId;
    }

    public void setPhysicalBody(boolean value) {
        physicalBody = value;
    }

    @Nullable
    public PathfinderMob getEntity(Level level){
        return (PathfinderMob) level.getEntity(this.entityId);
    }

    public void selectRandomName() {
        name = NAMES[ThreadLocalRandom.current().nextInt(NAMES.length)];
    }
    
    public void attachTo(PathfinderMob mob, Player owner){
        this.entityId = mob.getId();
        mob.removeFreeWill();
        mob.targetSelector.removeAllGoals(goal -> true);
        definition.behaviour().registerGoals(mob, owner);
    }
    
    public boolean hasPhysicalBody(){
        return physicalBody;
    }

    public CompoundTag serializeNBT(){
        CompoundTag tag = new CompoundTag();
        if(hasPhysicalBody()) {
            tag.putInt(ENTITY_ID, entityId);
        }
        tag.putString(DEFINITION, ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(
                WitcherooRegistries.FAMILIAR_DEFINITION_RESOURCE_KEY).getKey(definition).toString());
        tag.putInt(LEVEL, level);
        tag.putInt(MANA, mana);
        tag.putString(NAME, name);
        return tag;
    }

    
    public void deserializeNBT(CompoundTag tag){
        if(tag.contains(ENTITY_ID)) {
            this.physicalBody = true;
            entityId = tag.getInt(ENTITY_ID);
        }
        definition = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(
                WitcherooRegistries.FAMILIAR_DEFINITION_RESOURCE_KEY).get(
                new ResourceLocation(tag.getString(DEFINITION)));
        level = tag.getInt(LEVEL);
        mana = tag.getInt(MANA);
        name = tag.getString(NAME);
    }
}
