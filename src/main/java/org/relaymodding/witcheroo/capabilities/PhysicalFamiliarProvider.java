package org.relaymodding.witcheroo.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.relaymodding.witcheroo.Witcheroo;

public class PhysicalFamiliarProvider implements ICapabilitySerializable<CompoundTag> {

    private final LazyOptional<PhysicalFamiliar> familiarLazyOptional;

    public PhysicalFamiliarProvider(LazyOptional<PhysicalFamiliar> familiarLazyOptional) {
        this.familiarLazyOptional = familiarLazyOptional;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == Witcheroo.FAMILIAR_CAPABILITY) {
            return familiarLazyOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return familiarLazyOptional.orElseThrow(RuntimeException::new).serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        familiarLazyOptional.orElseThrow(RuntimeException::new).deserializeNBT(tag);

    }
}
