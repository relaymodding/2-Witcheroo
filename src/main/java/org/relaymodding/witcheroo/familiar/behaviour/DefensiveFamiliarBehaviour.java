package org.relaymodding.witcheroo.familiar.behaviour;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.CatLieOnBedGoal;
import net.minecraft.world.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;

public class DefensiveFamiliarBehaviour implements FamiliarBehaviour {

    @Override
    public void registerGoals(PathfinderMob target, Player owner) {
        target.goalSelector.addGoal(1, new FloatGoal(target));
        target.goalSelector.addGoal(5, new MeleeAttackGoal(target, 1.0D, true));
        
        if (target instanceof TamableAnimal tamableAnimal) {
            tamableAnimal.tame(owner);
            target.goalSelector.addGoal(2, new SitWhenOrderedToGoal(tamableAnimal));
            if (tamableAnimal instanceof Cat cat) {
                target.goalSelector.addGoal(5, new CatLieOnBedGoal(cat, 1.1D, 8));
                target.goalSelector.addGoal(7, new CatSitOnBlockGoal(cat, 0.8D));
            }
            target.goalSelector.addGoal(6, new FollowOwnerGoal(tamableAnimal, 1.0D, 10.0F, 5.0F, false));
            
            target.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(tamableAnimal));
            target.targetSelector.addGoal(2, new OwnerHurtTargetGoal(tamableAnimal));
        }
        
        target.goalSelector.addGoal(8, new LeapAtTargetGoal(target, 0.3F));
        target.goalSelector.addGoal(11, new WaterAvoidingRandomStrollGoal(target, 0.8D, 1.0000001E-5F));
        target.goalSelector.addGoal(12, new LookAtPlayerGoal(target, Player.class, 10.0F));
    }

    @Override
    public String toString() {
        return "DefensiveFamiliarBehaviour{}";
    }
}
