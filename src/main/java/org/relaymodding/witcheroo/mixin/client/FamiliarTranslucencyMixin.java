package org.relaymodding.witcheroo.mixin.client;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.relaymodding.witcheroo.client.WitcherooClientRenderer;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true, print = true)
@Mixin(LivingEntityRenderer.class)
public abstract class FamiliarTranslucencyMixin extends EntityRenderer {

    private FamiliarTranslucencyMixin(EntityRendererProvider.Context p_174008_){
        super(p_174008_);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void witcheroo_renderinject(LivingEntity entity, float yaw,
                                        float partialTicks, PoseStack stack,
                                        MultiBufferSource source, int packedLight, CallbackInfo cinfo) {

        WitcherooClientRenderer.entityId = entity.getId();
        WitcherooClientRenderer.buffer = source;
        WitcherooClientRenderer.texture = getTextureLocation(entity);
    }

    @ModifyArg(method = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/Model;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"), index = 7)
    private static float witcheroo_modifyAlpha(float alpha) {
        return WitcherooClientRenderer.modifyAlpha(alpha);
    }

    @ModifyArg(method = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/Model;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"), index = 1)
    private static VertexConsumer witcheroo_modifyRenderType(VertexConsumer originalVertexConsumer) {
        return WitcherooClientRenderer.modifyRenderType(originalVertexConsumer);
    }
}

