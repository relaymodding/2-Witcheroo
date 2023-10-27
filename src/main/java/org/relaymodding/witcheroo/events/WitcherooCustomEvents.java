package org.relaymodding.witcheroo.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.relaymodding.witcheroo.capabilities.*;

public class WitcherooCustomEvents {
	public static void registerCapabilitiesEvent(final RegisterCapabilitiesEvent registerCapabilitiesEvent) {
        registerCapabilitiesEvent.register(PhysicalFamiliar.class);
        registerCapabilitiesEvent.register(Witch.class);
    }

    public static void attachCapabilitiesEvent(final AttachCapabilitiesEvent<Entity> attachCapabilitiesEvent) {
        if (attachCapabilitiesEvent.getObject() instanceof Player) {
            Witch capability = new WitchImpl();
            LazyOptional<Witch> lazyOptional = LazyOptional.of(() -> capability);
            attachCapabilitiesEvent.addCapability(Witch.ID, new WitchProvider(lazyOptional));
        }
        if (attachCapabilitiesEvent.getObject() instanceof PathfinderMob mob) {
            PhysicalFamiliar capability = new PhysicalFamiliarImpl(mob);
            LazyOptional<PhysicalFamiliar> lazyOptional = LazyOptional.of(() -> capability);
            attachCapabilitiesEvent.addCapability(PhysicalFamiliar.ID, new PhysicalFamiliarProvider(lazyOptional));
        }
    }
}
