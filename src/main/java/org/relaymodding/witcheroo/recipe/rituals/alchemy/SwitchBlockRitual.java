package org.relaymodding.witcheroo.recipe.rituals.alchemy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.relaymodding.witcheroo.Witcheroo;
import org.relaymodding.witcheroo.capabilities.Witch;

import java.util.Map;

public class SwitchBlockRitual implements AlchemyRitual {

    public static final SwitchBlockRitual DIRT_TO_DIAMOND = new SwitchBlockRitual(Map.of(TagKey.create(Registries.BLOCK, new ResourceLocation("minecraft", "dirt")), Blocks.DIAMOND_ORE.defaultBlockState()), 25);

    private final Map<TagKey<Block>, BlockState> exchanges;
    private final int manaCost;

    public SwitchBlockRitual(Map<TagKey<Block>, BlockState> exchanges, int manaCost) {

        this.exchanges = exchanges;
        this.manaCost = manaCost;
    }

    @Override
    public boolean canPlayerPerformRitual(ServerPlayer player) {

        final Witch witch = player.getCapability(Witcheroo.WITCH_CAPABILITY).resolve().orElse(null);
        return witch != null && witch.getMana() > this.getManaCost();
    }

    @Override
    public boolean canAffect(Player player, Level level, BlockPos pos, BlockState state) {

        for (TagKey<Block> tag : this.exchanges.keySet()) {

            if (state.is(tag)) {

                return true;
            }
        }

        return false;
    }

    @Override
    public void apply(Player player, Level level, BlockPos pos, BlockState state) {

        for (Map.Entry<TagKey<Block>, BlockState> entry : this.exchanges.entrySet()) {

            if (state.is(entry.getKey())) {

                level.setBlock(pos, entry.getValue(), 3);
            }
        }
    }

    @Override
    public int getManaCost() {

        return this.manaCost;
    }

    @Override
    public void onPlayerPerformedRitual(ServerPlayer player) {

        final Witch witch = player.getCapability(Witcheroo.WITCH_CAPABILITY).resolve().orElse(null);

        if (witch != null) {

        }
    }
}
