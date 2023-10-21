package org.relaymodding.witcheroo.registries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.relaymodding.witcheroo.Witcheroo;
import org.relaymodding.witcheroo.familiar.FamiliarDefinition;
import org.relaymodding.witcheroo.familiar.behaviour.DefensiveFamiliarBehaviour;
import org.relaymodding.witcheroo.familiar.behaviour.FamiliarBehaviour;
import org.relaymodding.witcheroo.familiar.behaviour.HostileFamiliarBehaviour;
import org.relaymodding.witcheroo.familiar.behaviour.PassiveFamiliarBehaviour;
import org.relaymodding.witcheroo.familiar.type.BasicType;
import org.relaymodding.witcheroo.familiar.type.FamiliarType;
import org.relaymodding.witcheroo.familiar.type.FamiliarTypeSerializer;

import java.util.function.Supplier;

public class WitcherooRegistries {

    public static final DeferredRegister<FamiliarBehaviour> FAMILIAR_BEHAVIOURS = DeferredRegister.create(Witcheroo.resourceLocation("familiar_behaviours"), Witcheroo.MOD_ID);
    public static final Supplier<IForgeRegistry<FamiliarBehaviour>> SUPPLIER_FAMILIAR_BEHAVIOURS = FAMILIAR_BEHAVIOURS.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<FamiliarBehaviour> DEFENSIVE_FAMILIAR_BEHAVIOUR = WitcherooRegistries.FAMILIAR_BEHAVIOURS.register("defensive_behaviour", DefensiveFamiliarBehaviour::new);
    public static final RegistryObject<FamiliarBehaviour> HOSTILE_FAMILIAR_BEHAVIOUR = WitcherooRegistries.FAMILIAR_BEHAVIOURS.register("hostile_behaviour", HostileFamiliarBehaviour::new);
    public static final RegistryObject<FamiliarBehaviour> PASSIVE_FAMILIAR_BEHAVIOUR = WitcherooRegistries.FAMILIAR_BEHAVIOURS.register("passive_behaviour", PassiveFamiliarBehaviour::new);


    public static final ResourceKey<Registry<FamiliarDefinition>> FAMILIAR_DEFINITION_RESOURCE_KEY = ResourceKey.createRegistryKey(Witcheroo.resourceLocation("familiar_definitions"));
    public static final ResourceKey<FamiliarDefinition> PASSIVE_CAT_DEFINITION = ResourceKey.create(FAMILIAR_DEFINITION_RESOURCE_KEY, Witcheroo.resourceLocation("passive_cat"));
    public static final ResourceKey<FamiliarDefinition> DEFENSIVE_BEAR_DEFINITION = ResourceKey.create(FAMILIAR_DEFINITION_RESOURCE_KEY, Witcheroo.resourceLocation("defensive_bird"));
    public static final ResourceKey<FamiliarDefinition> HOSTILE_BIRD_DEFINITION = ResourceKey.create(FAMILIAR_DEFINITION_RESOURCE_KEY, Witcheroo.resourceLocation("hostile_bear"));

    public static final Codec<FamiliarBehaviour> FAMILIAR_BEHAVIOUR_CODEC = ResourceLocation.CODEC.xmap(
            resourceLocation -> SUPPLIER_FAMILIAR_BEHAVIOURS.get().getValue(resourceLocation),
            behaviour -> SUPPLIER_FAMILIAR_BEHAVIOURS.get().getKey(behaviour));

    // TODO
    public static final Codec<FamiliarDefinition> FAMILIAR_DEFINITION_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(ForgeRegistries.ENTITY_TYPES.getCodec().listOf().fieldOf("validTypes").forGetter(
                            FamiliarDefinition::validTypes),
                    FAMILIAR_BEHAVIOUR_CODEC.fieldOf("behaviour").forGetter(FamiliarDefinition::behaviour)).apply(
                    instance, FamiliarDefinition::new));

    public static Registry<FamiliarDefinition> getFamiliarDefinitionRegistry() {

        return ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(WitcherooRegistries.FAMILIAR_DEFINITION_RESOURCE_KEY);
    }

    // Familiar Type Serializers
    public static final DeferredRegister<FamiliarTypeSerializer<?>> FAMILIAR_TYPE_SERIALIZERS = DeferredRegister.create(Witcheroo.resourceLocation("familiar_type_serializer"), Witcheroo.MOD_ID);
    public static final RegistryObject<FamiliarTypeSerializer<?>> BASIC = WitcherooRegistries.FAMILIAR_TYPE_SERIALIZERS.register("basic", () -> BasicType.SERIALIZER);
    public static final Supplier<IForgeRegistry<FamiliarTypeSerializer<?>>> SUPPLIER_FAMILIAR_TYPE_SERIALIZERS = FAMILIAR_TYPE_SERIALIZERS.makeRegistry(RegistryBuilder::new);

    // Familiar Types
    public static final ResourceKey<Registry<FamiliarType>> FAMILIAR_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(Witcheroo.resourceLocation("familiar_types"));
    public static final Codec<FamiliarType> FAMILIAR_TYPE_CODEC = FamiliarTypeSerializer.REGISTRY_CODEC.dispatch(FamiliarType::getSerializer, FamiliarTypeSerializer::getCodec);

    public static Registry<FamiliarType> getFamiliarTypeRegistry() {

        return ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(WitcherooRegistries.FAMILIAR_TYPE_REGISTRY_KEY);
    }

    public static void registerDatapackRegistry(final DataPackRegistryEvent.NewRegistry newRegistryEvent) {
        newRegistryEvent.dataPackRegistry(FAMILIAR_DEFINITION_RESOURCE_KEY, FAMILIAR_DEFINITION_CODEC);
        newRegistryEvent.dataPackRegistry(FAMILIAR_TYPE_REGISTRY_KEY, FAMILIAR_TYPE_CODEC);
    }
}
