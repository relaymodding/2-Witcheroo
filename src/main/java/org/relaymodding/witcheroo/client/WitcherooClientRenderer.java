package org.relaymodding.witcheroo.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.relaymodding.witcheroo.client.renderer.WitcherooRenderTypes;
import org.relaymodding.witcheroo.util.Reference;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WitcherooClientRenderer {

    static final Set<UUID> trackedFamiliars = new HashSet<>();
    public static Entity entity;
    public static MultiBufferSource buffer;

    public static ResourceLocation texture;


    public static float modifyAlpha(float originalValue) {
        if (trackedFamiliars.contains(entity.getUUID())) return 0.5f;
        return originalValue;
    }

    public static float modifyRed(float red) {
        if (trackedFamiliars.contains(entity.getUUID())) return 0.5f;
        return red;
    }

    public static VertexConsumer modifyRenderType(VertexConsumer original) {
        if (trackedFamiliars.contains(entity.getUUID())) {
            return VertexMultiConsumer.create(buffer.getBuffer(RenderType.entityGlint()), buffer.getBuffer(RenderType.entityTranslucent(texture)));
        }
        return original;
    }
}
