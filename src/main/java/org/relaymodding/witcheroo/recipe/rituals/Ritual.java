package org.relaymodding.witcheroo.recipe.rituals;

import net.minecraft.server.level.ServerPlayer;

public interface Ritual {

    boolean canPlayerPerformRitual(ServerPlayer player);

    int getManaCost();

    void onPlayerPerformedRitual(ServerPlayer player);
}