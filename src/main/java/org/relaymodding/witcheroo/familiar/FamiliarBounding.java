package org.relaymodding.witcheroo.familiar;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.tuple.Triple;
import org.relaymodding.witcheroo.capabilities.Capabilities;
import org.relaymodding.witcheroo.capabilities.Witch;
import org.relaymodding.witcheroo.familiar.type.FamiliarType;
import org.relaymodding.witcheroo.network.SyncFamiliarPacket;
import org.relaymodding.witcheroo.network.WitcherooPacketHandler;
import org.relaymodding.witcheroo.witch.ManaCosts;

import java.util.WeakHashMap;

public class FamiliarBounding {
    public static final int boundingTimeTicks = 60;
    public static WeakHashMap<Familiar, Triple<Integer, Player, PathfinderMob>> familiarBindingData = new WeakHashMap<Familiar, Triple<Integer, Player, PathfinderMob>>();

    public static boolean unlockFamiliarType(Player player, FamiliarType type, FamiliarDefinition definition) {

        final Witch witch = player.getCapability(Capabilities.WITCH_CAPABILITY).resolve().orElse(null);

        if (witch != null && !witch.hasFamiliar(type)) {

            final Familiar familiar = type.createInstance();
            familiar.setFamiliarDefinition(definition);
            witch.addFamiliar(familiar);
            return true;
        }

        return false;
    }

    public static void startBoundingProcess(Player player, Familiar familiar, PathfinderMob mob) {
        familiarBindingData.put(familiar, Triple.of(boundingTimeTicks, player, mob));
    }

    public static void finishBoundingProcess(Familiar familiar) {
        Triple<Integer, Player, PathfinderMob> triple = familiarBindingData.get(familiar);
        familiarBindingData.remove(familiar);
        triple.getRight().setPos(triple.getRight().position().relative(Direction.DOWN, 1.5D));
        grantPhysicalBody(triple.getMiddle(), familiar, triple.getRight());
    }

    @SuppressWarnings("unchecked")
    public static void grantPhysicalBody(Player owner, Familiar familiar, PathfinderMob oldBody) {
        PathfinderMob body = oldBody.convertTo((EntityType<? extends PathfinderMob>) oldBody.getType(), true);
        body.getCapability(Capabilities.FAMILIAR_CAPABILITY).ifPresent(capability -> {
            capability.setBound(true);
            capability.setBehaviour(familiar.getFamiliarDefinition().behaviour());
            familiar.attachTo(body, owner);
            capability.attachTo(oldBody);
            capability.setOwner(owner.getUUID());
            body.setCustomName(familiar.getType().getDisplayName());

            keepSameType(oldBody, body);

            WitcherooPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncFamiliarPacket(body.getUUID(), SyncFamiliarPacket.ADD_ENTITY));
        });

        owner.sendSystemMessage(Component.translatable("witcheroo.notices.bound_familiar", familiar.getType().getDisplayName()).withStyle(ChatFormatting.DARK_GRAY));
    }

    private static void keepSameType(PathfinderMob oldBody, PathfinderMob newBody) {
        if (oldBody instanceof Cat) {
            ((Cat)newBody).setVariant(((Cat)oldBody).getVariant());
        }
        else if (oldBody instanceof Panda) {
            ((Panda)newBody).setMainGene(((Panda)oldBody).getMainGene());
            ((Panda)newBody).setHiddenGene(((Panda)oldBody).getHiddenGene());
        }
        else if (oldBody instanceof Parrot) {
            ((Parrot)newBody).setVariant(((Parrot)oldBody).getVariant());
        }
    }
}
