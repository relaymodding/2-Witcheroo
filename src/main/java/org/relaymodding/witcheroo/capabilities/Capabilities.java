package org.relaymodding.witcheroo.capabilities;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class Capabilities {
    public static final Capability<PhysicalFamiliar> FAMILIAR_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final Capability<Witch> WITCH_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
}
