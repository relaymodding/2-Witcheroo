package org.relaymodding.witcheroo.recipe.rituals.familiar;

import org.relaymodding.witcheroo.familiar.type.FamiliarType;
import org.relaymodding.witcheroo.recipe.rituals.Ritual;

public interface FamiliarSummoningRitual extends Ritual {

    FamiliarType getTypeToSummon();
}
