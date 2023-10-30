package org.relaymodding.witcheroo.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.relaymodding.witcheroo.blocks.entity.RitualVisualBlockEntity;

public class RitualVisualBlockEntityRenderer implements BlockEntityRenderer<RitualVisualBlockEntity> {

    private final BlockRenderDispatcher blockRenderer;

    public RitualVisualBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(RitualVisualBlockEntity entity, float partialTicks, PoseStack pose, MultiBufferSource buffers, int p_112311_, int p_112312_) {
        Level level = entity.getLevel();
        entity.renderedState().ifPresent(state -> {
            VertexConsumer vertex = buffers.getBuffer(WitcherooRenderTypes.BURN);
            blockRenderer.renderBatched(state, entity.getBlockPos(), level, pose, vertex, false, level.random);
        });
    }

    public boolean shouldRenderOffScreen(BeaconBlockEntity p_112138_) {
        return true;
    }

    public int getViewDistance() {
        return 256;
    }

    public boolean shouldRender(BeaconBlockEntity p_173531_, Vec3 p_173532_) {
        return Vec3.atCenterOf(p_173531_.getBlockPos()).multiply(1.0D, 0.0D, 1.0D).closerThan(p_173532_.multiply(1.0D, 0.0D, 1.0D), (double) this.getViewDistance());
    }
}
