package org.relaymodding.witcheroo.datagen;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import org.relaymodding.witcheroo.Witcheroo;
import org.relaymodding.witcheroo.familiar.FamiliarDefinition;
import org.relaymodding.witcheroo.registries.WitcherooRegistries;

import java.util.List;
import java.util.Set;

public class WitcherooDatagen {

    public static void datagen(final GatherDataEvent event){
        event.getGenerator().addProvider(event.includeServer(),
                (DataProvider.Factory<DatapackBuiltinEntriesProvider>) output ->
                        new DatapackBuiltinEntriesProvider(output, event.getLookupProvider(),
                        new RegistrySetBuilder().add(WitcherooRegistries.FAMILIAR_DEFINITION_RESOURCE_KEY, bootstrap -> {
                            bootstrap.register(WitcherooRegistries.CAT_PASSIVE_DEFINITION, new FamiliarDefinition(
                                    List.of(EntityType.CAT),
                                    WitcherooRegistries.PASSIVE_FAMILIAR_BEHAVIOUR.get()
                            ));
                        }),
                        Set.of(Witcheroo.MOD_ID)));
    }
}
