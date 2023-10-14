package org.relaymodding.witcheroo.recipe.rituals.alchemy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.relaymodding.witcheroo.recipe.rituals.Ritual;

public interface AlchemyRitual extends Ritual {

    boolean canAffect(Player player, Level level, BlockPos pos, BlockState state);

    void apply(Player player, Level level, BlockPos pos, BlockState state);
}
