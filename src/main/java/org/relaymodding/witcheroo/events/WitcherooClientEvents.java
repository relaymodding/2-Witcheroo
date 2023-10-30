package org.relaymodding.witcheroo.events;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.relaymodding.witcheroo.Witcheroo;
import org.relaymodding.witcheroo.client.renderer.RitualVisualBlockEntityRenderer;
import org.relaymodding.witcheroo.client.renderer.WitcherooRenderTypes;
import org.relaymodding.witcheroo.registries.WitcherooRegistries;
import org.relaymodding.witcheroo.util.Reference;

import java.io.IOException;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WitcherooClientEvents {

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), Witcheroo.resourceLocation("burn"), DefaultVertexFormat.BLOCK), shaderInstance -> {
            WitcherooRenderTypes.BURN_SHADER_INSTANCE = shaderInstance;
        });
    }

    @SubscribeEvent
    public static void registerBERs(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(WitcherooRegistries.RITUAL_VISUAL_BLOCK_ENTITY.get(), RitualVisualBlockEntityRenderer::new);
    }
}
