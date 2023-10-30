package org.relaymodding.witcheroo.client;


import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.EntityRenderersEvent;
import org.relaymodding.witcheroo.capabilities.Capabilities;
import org.relaymodding.witcheroo.client.renderer.RitualVisualBlockEntityRenderer;
import org.relaymodding.witcheroo.familiar.Familiar;
import org.relaymodding.witcheroo.registries.WitcherooRegistries;
import org.relaymodding.witcheroo.util.Reference;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class WitcherooClientHandler {

    public static void handleAllFamiliarsSync(Collection<UUID> ids) {
        WitcherooClientRenderer.trackedFamiliars.clear();
        WitcherooClientRenderer.trackedFamiliars.addAll(ids);
    }

    public static void handleRitualRenderedBlockSync(BlockPos pos, BlockState state) {
        Objects.requireNonNull(Minecraft.getInstance().level).getBlockEntity(pos, WitcherooRegistries.RITUAL_VISUAL_BLOCK_ENTITY.get()).ifPresent(entity -> {
            entity.renderedState(state);
        });
    }

    public static void handleFamiliarSync(UUID entityId, boolean addition) {
        if (addition) {
            WitcherooClientRenderer.trackedFamiliars.add(entityId);
        } else {
            WitcherooClientRenderer.trackedFamiliars.remove(entityId);
        }
    }

    public static void handleWitchSync(Collection<Familiar> ownedFamiliars, Set<ResourceLocation> completedRituals, int mana) {
        Objects.requireNonNull(Minecraft.getInstance().player).getCapability(Capabilities.WITCH_CAPABILITY).ifPresent(witch -> {
            witch.setFamiliars(ownedFamiliars);
            witch.setCompletedRituals(completedRituals);
            witch.setMana(mana);
        });
    }

    public static void handleFamiliarAbsorbingSync(int entityId) {
        Minecraft.getInstance().level.getEntity(entityId).getPersistentData().putBoolean(Reference.ABSORBING_TAG, true);
    }
}
