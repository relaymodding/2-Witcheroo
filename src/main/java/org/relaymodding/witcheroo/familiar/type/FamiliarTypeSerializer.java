package org.relaymodding.witcheroo.familiar.type;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.relaymodding.witcheroo.registries.WitcherooRegistries;

public interface FamiliarTypeSerializer<T extends FamiliarType> {

    Codec<FamiliarTypeSerializer<?>> REGISTRY_CODEC = ResourceLocation.CODEC.xmap(id -> WitcherooRegistries.SUPPLIER_FAMILIAR_TYPE_SERIALIZERS.get().getValue(id), value -> WitcherooRegistries.SUPPLIER_FAMILIAR_TYPE_SERIALIZERS.get().getKey(value));

    Codec<T> getCodec();

    T fromByteBuf(FriendlyByteBuf buffer);

    void toByteBuf(FriendlyByteBuf buffer, T toWrite);

    ResourceLocation getId();
}