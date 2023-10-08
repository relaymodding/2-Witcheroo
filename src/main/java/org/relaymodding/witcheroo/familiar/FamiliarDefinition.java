package org.relaymodding.witcheroo.familiar;

import net.minecraft.world.entity.EntityType;
import org.relaymodding.witcheroo.familiar.behaviour.FamiliarBehaviour;

import java.util.List;

public record FamiliarDefinition(List<EntityType<?>> validTypes, FamiliarBehaviour behaviour) {


}
