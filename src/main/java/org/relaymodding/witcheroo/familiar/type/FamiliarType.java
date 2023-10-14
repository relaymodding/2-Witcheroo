package org.relaymodding.witcheroo.familiar.type;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import org.relaymodding.witcheroo.familiar.Familiar;
import org.relaymodding.witcheroo.registries.WitcherooRegistries;

import javax.annotation.Nullable;
import java.util.Optional;

public interface FamiliarType {

    int getMaxMana();

    int getMaxLevel();

    Component getDisplayName();

    Component getDescription();

    boolean isSuitableVessel(LivingEntity entity);

    boolean is(TagKey<FamiliarType> tag);

    @Nullable
    default Holder<FamiliarType> getHolder() {

        final Optional<ResourceKey<FamiliarType>> typeKey = WitcherooRegistries.getFamiliarTypeRegistry().getResourceKey(this);

        if (typeKey.isPresent()) {

            return WitcherooRegistries.getFamiliarTypeRegistry().getHolder(typeKey.get()).get();
        }

        return null;
    }

    FamiliarTypeSerializer<? extends FamiliarType> getSerializer();

    Familiar createInstance();
}