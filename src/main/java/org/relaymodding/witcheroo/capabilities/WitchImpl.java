package org.relaymodding.witcheroo.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.relaymodding.witcheroo.familiar.Familiar;

import java.util.*;
import java.util.function.Function;

public class WitchImpl implements Witch {

    private static final String OWNED_FAMILIARS = "witcheroo_owned_familiars";
    private static final String COMPLETED_RITUALS = "witcheroo_completed_rituals";
    private static final String INDIVIDUAL_RITUAL = "witcheroo_ritual";

    private final Map<String, Familiar> familiars = new HashMap<>();
    private final Set<ResourceLocation> rituals = new HashSet<>();

    @Override
    public Collection<Familiar> getOwnedFamiliars(){
        return Collections.unmodifiableCollection(familiars.values());
    }

    @Override
    public void addFamiliar(Familiar Familiar){
        familiars.put(Familiar.getName(), Familiar);
    }

    @Override
    public void removeFamiliarByName(final String name){
        familiars.remove(name);
    }

    @Override
    public Familiar getFamiliarByName(String name){
        return familiars.get(name);
    }

    @Override
    public Set<ResourceLocation> getCompletedRituals(){
        return rituals;
    }

    @Override
    public CompoundTag serializeNBT(){
        CompoundTag tag = new CompoundTag();
        ListTag rituals = new ListTag();
        for(ResourceLocation location : getCompletedRituals()) {
            CompoundTag rl = new CompoundTag();
            rl.putString(INDIVIDUAL_RITUAL, location.toString());
            rituals.add(rl);
        }
        ListTag familiars = new ListTag();
        for(Familiar familiar : this.familiars.values()) {
            familiars.add(familiar.serializeNBT());
        }
        tag.put(COMPLETED_RITUALS, rituals);
        tag.put(OWNED_FAMILIARS, familiars);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag){
        ListTag rituals = tag.getList(COMPLETED_RITUALS, Tag.TAG_COMPOUND);
        for(int i = 0; i < rituals.size(); ++i) {
            CompoundTag resourceLocation = rituals.getCompound(i);
            this.rituals.add(new ResourceLocation(resourceLocation.getString(INDIVIDUAL_RITUAL)));
        }
        ListTag familiars = tag.getList(OWNED_FAMILIARS, Tag.TAG_COMPOUND);
        for(int i = 0; i < familiars.size(); ++i) {
            CompoundTag serializedFamiliar = familiars.getCompound(i);
            Familiar familiar = new Familiar();
            familiar.deserializeNBT(serializedFamiliar);
        }

    }

    @Override
    public int getMana() {
        return familiars.values().stream().map(Familiar::getMana).reduce(0, Integer::sum);
    }
}
