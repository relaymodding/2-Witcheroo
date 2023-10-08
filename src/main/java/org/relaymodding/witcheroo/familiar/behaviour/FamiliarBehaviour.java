package org.relaymodding.witcheroo.familiar.behaviour;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;

public interface FamiliarBehaviour {
    void registerGoals(PathfinderMob target, Player owner);

}
