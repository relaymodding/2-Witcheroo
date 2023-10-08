package org.relaymodding.witcheroo.client;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class WitcherooClientRenderer {

    static final Set<Integer> trackedFamiliars = new HashSet<>();
    public static int entityId;
    public static MultiBufferSource buffer;

    public static ResourceLocation texture;


    public static float modifyAlpha(float originalValue) {
        if (trackedFamiliars.contains(entityId)) return 0.5f;
        return originalValue;
    }

    public static VertexConsumer modifyRenderType(VertexConsumer original) {
        if (trackedFamiliars.contains(entityId)) {
            return VertexMultiConsumer.create(buffer.getBuffer(RenderType.entityGlint()), buffer.getBuffer(RenderType.entityTranslucent(texture)));
        }
        return original;
    }
}
