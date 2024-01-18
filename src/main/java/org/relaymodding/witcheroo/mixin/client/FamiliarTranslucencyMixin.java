package org.relaymodding.witcheroo.mixin.client;

import org.relaymodding.witcheroo.client.WitcherooClientRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntityRenderer.class)
public abstract class FamiliarTranslucencyMixin extends EntityRenderer<LivingEntity> {

    private FamiliarTranslucencyMixin() {
        super(null);
    }

    @Inject(method = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"))
    private void witcheroo_renderinject(LivingEntity entity, float yaw,
                                        float partialTicks, PoseStack stack,
                                        MultiBufferSource source, int packedLight, CallbackInfo cinfo) {
        WitcherooClientRenderer.entity = entity;
        WitcherooClientRenderer.buffer = source;
        WitcherooClientRenderer.texture = getTextureLocation(entity);
    }

    @ModifyArg(method = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"), index = 7)
    private float witcheroo_modifyAlpha(float alpha) {
        return WitcherooClientRenderer.modifyAlpha(alpha);
    }

    @ModifyArg(method = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"), index = 4)
    private float witcheroo_modifyRed(float red) {
        return WitcherooClientRenderer.modifyRed(red);
    }

    @ModifyArg(method = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"), index = 1)
    private VertexConsumer witcheroo_modifyRenderType(VertexConsumer originalVertexConsumer) {
        return WitcherooClientRenderer.modifyRenderType(originalVertexConsumer);
    }
}
