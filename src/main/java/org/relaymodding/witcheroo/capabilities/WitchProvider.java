package org.relaymodding.witcheroo.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.relaymodding.witcheroo.Witcheroo;

public class WitchProvider implements ICapabilitySerializable<CompoundTag> {

    private final LazyOptional<Witch> witchLazyOptional;

    public WitchProvider(LazyOptional<Witch> witchLazyOptional) {
        this.witchLazyOptional = witchLazyOptional;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == Witcheroo.WITCH_CAPABILITY) {
            return witchLazyOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return witchLazyOptional.orElseThrow(RuntimeException::new).serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        witchLazyOptional.orElseThrow(RuntimeException::new).deserializeNBT(tag);
    }
}
