package org.relaymodding.witcheroo.familiar.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.relaymodding.witcheroo.Witcheroo;
import org.relaymodding.witcheroo.familiar.Familiar;

public class BasicType implements FamiliarType {

    public static FamiliarTypeSerializer<BasicType> SERIALIZER = new Serializer();

    private final int maxMana;
    private final int maxLevel;
    private final Component displayName;
    private final Component description;
    private final TagKey<EntityType<?>> vesselTag;

    public BasicType(int maxMana, int maxLevel, Component displayName, Component description, ResourceLocation vesselTagId) {

        this.maxMana = maxMana;
        this.maxLevel = maxLevel;
        this.displayName = displayName;
        this.description = description;
        this.vesselTag = TagKey.create(Registries.ENTITY_TYPE, vesselTagId);
    }

    @Override
    public int getMaxMana() {
        return this.maxMana;
    }

    @Override
    public int getMaxLevel() {
        return this.maxLevel;
    }

    @Override
    public Component getDisplayName() {
        return this.displayName;
    }

    @Override
    public Component getDescription() {

        return this.description;
    }

    public ResourceLocation getVesselTagId() {

        return this.vesselTag.location();
    }

    @Override
    public boolean isSuitableVessel(LivingEntity entity) {
        return entity.getType().is(this.vesselTag);
    }

    @Override
    public boolean is(TagKey<FamiliarType> tag) {

        final Holder<FamiliarType> holder = this.getHolder();
        return holder != null && holder.is(tag);
    }

    @Override
    public FamiliarTypeSerializer<? extends FamiliarType> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public Familiar createInstance() {

        final Familiar familiar = new Familiar();
        familiar.setType(this);
        familiar.setLevel(1);
        return familiar;
    }

    public static class Serializer implements FamiliarTypeSerializer<BasicType> {

        private static final ResourceLocation TYPE_ID = Witcheroo.resourceLocation("basic_familiar");

        private static final Codec<BasicType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("max_mana").forGetter(BasicType::getMaxMana),
                Codec.INT.fieldOf("max_level").forGetter(BasicType::getMaxLevel),
                ExtraCodecs.COMPONENT.fieldOf("name").forGetter(BasicType::getDisplayName),
                ExtraCodecs.COMPONENT.fieldOf("description").forGetter(BasicType::getDescription),
                ResourceLocation.CODEC.fieldOf("vessel_tag").forGetter(BasicType::getVesselTagId)
        ).apply(instance, BasicType::new));


        @Override
        public Codec<BasicType> getCodec() {
            return CODEC;
        }

        @Override
        public BasicType fromByteBuf(FriendlyByteBuf buffer) {

            final int maxMana = buffer.readInt();
            final int maxLevel = buffer.readInt();
            final Component name = buffer.readComponent();
            final Component desc = buffer.readComponent();
            final ResourceLocation vesselTag = buffer.readResourceLocation();

            return new BasicType(maxMana, maxLevel, name, desc, vesselTag);
        }

        @Override
        public void toByteBuf(FriendlyByteBuf buffer, BasicType toWrite) {

            buffer.writeInt(toWrite.getMaxMana());
            buffer.writeInt(toWrite.getMaxLevel());
            buffer.writeComponent(toWrite.getDisplayName());
            buffer.writeComponent(toWrite.getDescription());
            buffer.writeResourceLocation(toWrite.getVesselTagId());
        }

        @Override
        public ResourceLocation getId() {

            return TYPE_ID;
        }
    }
}