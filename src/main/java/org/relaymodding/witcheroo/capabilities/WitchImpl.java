package org.relaymodding.witcheroo.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.relaymodding.witcheroo.familiar.Familiar;
import org.relaymodding.witcheroo.familiar.type.FamiliarType;

import java.util.*;

public class WitchImpl implements Witch {

    private static final String OWNED_FAMILIARS = "owned_familiars";
    private static final String COMPLETED_RITUALS = "completed_rituals";
    private static final String INDIVIDUAL_RITUAL = "ritual";
    private static final String MANA = "mana";

    private final Map<FamiliarType, Familiar> familiars = new HashMap<>();
    private final Set<ResourceLocation> rituals = new HashSet<>();

    private int mana = 0;

    @Override
    public Collection<Familiar> getOwnedFamiliars() {
        return Collections.unmodifiableCollection(familiars.values());
    }

    @Override
    public void addFamiliar(Familiar familiar) {
        familiars.put(familiar.getType(), familiar);
    }

    @Override
    public void removeFamiliar(FamiliarType type) {

        this.familiars.remove(type);
    }

    @Override
    public Familiar getFamiliar(FamiliarType type) {
        return this.familiars.get(type);
    }

    @Override
    public boolean hasFamiliar(FamiliarType type) {
        return this.familiars.containsKey(type);
    }

    @Override
    public Set<ResourceLocation> getCompletedRituals() {
        return rituals;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag rituals = new ListTag();
        for (ResourceLocation location : getCompletedRituals()) {
            CompoundTag rl = new CompoundTag();
            rl.putString(INDIVIDUAL_RITUAL, location.toString());
            rituals.add(rl);
        }
        ListTag familiars = new ListTag();
        for (Familiar familiar : this.familiars.values()) {
            familiars.add(familiar.serializeNBT());
        }
        tag.put(COMPLETED_RITUALS, rituals);
        tag.put(OWNED_FAMILIARS, familiars);
        tag.putInt(MANA, this.mana);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        ListTag rituals = tag.getList(COMPLETED_RITUALS, Tag.TAG_COMPOUND);
        for (int i = 0; i < rituals.size(); ++i) {
            CompoundTag resourceLocation = rituals.getCompound(i);
            this.rituals.add(new ResourceLocation(resourceLocation.getString(INDIVIDUAL_RITUAL)));
        }
        ListTag familiars = tag.getList(OWNED_FAMILIARS, Tag.TAG_COMPOUND);
        for (int i = 0; i < familiars.size(); ++i) {
            CompoundTag serializedFamiliar = familiars.getCompound(i);
            Familiar familiar = new Familiar();
            familiar.deserializeNBT(serializedFamiliar);
            this.familiars.put(familiar.getType(), familiar);
        }
        this.mana = tag.getInt(MANA);
    }

    @Override
    public int getMana() {
        return this.mana;
    }

    @Override
    public int getMaxMana() {
        return familiars.values().stream().map(familiar -> familiar.getType().getMaxMana()).reduce(0, Integer::sum);
    }

    @Override
    public void setMana(int mana) {
        if (mana > getMaxMana()) {
            mana = getMaxMana();
        }
        else if (mana < 0) {
            mana = 0;
        }

        this.mana = mana;
    }

    @Override
    public boolean consumeMana(int mana) {
        if (mana < this.mana) {
            setMana(this.mana - mana);
            return true;
        }
        return false;
    }
}
