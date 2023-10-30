package org.relaymodding.witcheroo.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.relaymodding.witcheroo.registries.WitcherooRegistries;
import org.relaymodding.witcheroo.ritual.Ritual;

import java.util.Currency;
import java.util.Optional;

public class RitualVisualBlockEntity extends BlockEntity {

    @Nullable
    private BlockState renderedState;

    @Nullable
    private BlockPos controller;

    @Nullable
    private Ritual ritual;

    private int currentTime;

    public RitualVisualBlockEntity(BlockPos pos, BlockState state) {
        super(WitcherooRegistries.RITUAL_VISUAL_BLOCK_ENTITY.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, RitualVisualBlockEntity _this) {
        if (!_this.isController() && level.getGameTime() % 5 == 0) {
            _this.controller().ifPresentOrElse(controllerPos -> {
                if (!level.getBlockState(controllerPos).is(state.getBlock())) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                }
            }, () -> {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            });

            return;
        }
        _this.ritual().ifPresent(ritual -> {
            int currentTime = _this.currentTime;
            if (currentTime < ritual.maxTime()) {
                _this.currentTime++;
                return;
            }

            //noinspection SwitchStatementWithTooFewBranches
            switch (ritual) {
                case STAFF_FROM_TREE ->
                        level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, WitcherooRegistries.WITCH_STAFF_OBJECT.get().getDefaultInstance()));
            }
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        });

    }


    public Optional<BlockState> renderedState() {
        return Optional.ofNullable(renderedState);
    }

    public void renderedState(BlockState renderedState) {
        this.renderedState = renderedState;
    }

    public Optional<BlockPos> controller() {
        return Optional.ofNullable(controller);
    }

    public boolean isController() {
        return controller().isEmpty();
    }

    public void setController(@Nullable BlockPos controller) {
        this.controller = controller;
    }

    public Optional<Ritual> ritual() {
        return Optional.ofNullable(ritual);
    }

    public void setRitual(Ritual ritual) {
        this.ritual = ritual;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.renderedState = Block.stateById(nbt.getInt("renderedState"));
        this.currentTime = nbt.getInt("currentTime");
        if (nbt.contains("ritual")) {
            this.ritual = Ritual.values()[nbt.getInt("ritual")];
        }
        if (nbt.contains("controller")) {
            this.controller = BlockPos.of(nbt.getLong("controller"));
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("renderedState", Block.getId(this.renderedState));
        nbt.putInt("currentTime", currentTime);
        ritual().ifPresent(ritual1 -> nbt.putInt("ritual", ritual1.ordinal()));
        controller().ifPresent(pos -> nbt.putLong("controller", pos.asLong()));
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        //TODO how do I do this again
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        nbt.putInt("renderedState", Block.getId(this.renderedState));
        nbt.putInt("currentTime", currentTime);
        ritual().ifPresent(ritual1 -> nbt.putInt("ritual", ritual1.ordinal()));
        controller().ifPresent(pos -> nbt.putLong("controller", pos.asLong()));
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt) {
        super.handleUpdateTag(nbt);
        this.renderedState = Block.stateById(nbt.getInt("renderedState"));
        this.currentTime = nbt.getInt("currentTime");
        if (nbt.contains("ritual")) {
            this.ritual = Ritual.values()[nbt.getInt("ritual")];
        }
        if (nbt.contains("controller")) {
            this.controller = BlockPos.of(nbt.getLong("controller"));
        }
    }
}
