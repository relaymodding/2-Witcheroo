package org.relaymodding.witcheroo.registries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.*;
import org.relaymodding.witcheroo.Witcheroo;
import org.relaymodding.witcheroo.familiar.*;
import org.relaymodding.witcheroo.familiar.behaviour.DefensiveFamiliarBehaviour;
import org.relaymodding.witcheroo.familiar.behaviour.FamiliarBehaviour;
import org.relaymodding.witcheroo.familiar.behaviour.HostileFamiliarBehaviour;
import org.relaymodding.witcheroo.familiar.behaviour.PassiveFamiliarBehaviour;

import java.util.function.Supplier;

public class WitcherooRegistries {

    public static final DeferredRegister<FamiliarBehaviour> FAMILIAR_BEHAVIOURS = DeferredRegister.create(
            Witcheroo.resourceLocation("familiar_behaviours"), Witcheroo.MOD_ID);

    public static final Supplier<IForgeRegistry<FamiliarBehaviour>> SUPPLIER_FAMILIAR_BEHAVIOURS = FAMILIAR_BEHAVIOURS.makeRegistry(
            RegistryBuilder::new);

    public static final RegistryObject<FamiliarBehaviour> DEFENSIVE_FAMILIAR_BEHAVIOUR = WitcherooRegistries.FAMILIAR_BEHAVIOURS.register(
            "defensive_behaviour", DefensiveFamiliarBehaviour::new);
    public static final RegistryObject<FamiliarBehaviour> HOSTILE_FAMILIAR_BEHAVIOUR = WitcherooRegistries.FAMILIAR_BEHAVIOURS.register(
            "hostile_behaviour", HostileFamiliarBehaviour::new);
    public static final RegistryObject<FamiliarBehaviour> PASSIVE_FAMILIAR_BEHAVIOUR = WitcherooRegistries.FAMILIAR_BEHAVIOURS.register(
            "passive_behaviour", PassiveFamiliarBehaviour::new);


    public static final ResourceKey<Registry<FamiliarDefinition>> FAMILIAR_DEFINITION_RESOURCE_KEY = ResourceKey.createRegistryKey(
            Witcheroo.resourceLocation("familiar_definitions"));

    public static final ResourceKey<FamiliarDefinition> CAT_PASSIVE_DEFINITION = ResourceKey.create(FAMILIAR_DEFINITION_RESOURCE_KEY,
            Witcheroo.resourceLocation("passive_cat"));

    public static final Codec<FamiliarBehaviour> FAMILIAR_BEHAVIOUR_CODEC = ResourceLocation.CODEC.xmap(
            resourceLocation -> SUPPLIER_FAMILIAR_BEHAVIOURS.get().getValue(resourceLocation),
            behaviour -> SUPPLIER_FAMILIAR_BEHAVIOURS.get().getKey(behaviour));
    public static final Codec<FamiliarDefinition> FAMILIAR_DEFINITION_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(ForgeRegistries.ENTITY_TYPES.getCodec().listOf().fieldOf("validTypes").forGetter(
                            FamiliarDefinition::validTypes),
                    FAMILIAR_BEHAVIOUR_CODEC.fieldOf("behaviour").forGetter(FamiliarDefinition::behaviour)).apply(
                    instance, FamiliarDefinition::new));

    public static void registerDatapackRegistry(final DataPackRegistryEvent.NewRegistry newRegistryEvent){
        newRegistryEvent.dataPackRegistry(FAMILIAR_DEFINITION_RESOURCE_KEY, FAMILIAR_DEFINITION_CODEC);
    }

}
