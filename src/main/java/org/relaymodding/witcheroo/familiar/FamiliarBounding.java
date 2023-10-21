package org.relaymodding.witcheroo.familiar;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import org.relaymodding.witcheroo.Witcheroo;
import org.relaymodding.witcheroo.capabilities.Witch;
import org.relaymodding.witcheroo.familiar.type.FamiliarType;
import org.relaymodding.witcheroo.network.SyncFamiliarPacket;
import org.relaymodding.witcheroo.network.WitcherooPacketHandler;

public class FamiliarBounding {

    public static boolean unlockFamiliarType(Player player, FamiliarType type, FamiliarDefinition definition) {

        final Witch witch = player.getCapability(Witcheroo.WITCH_CAPABILITY).resolve().orElse(null);

        if (witch != null && !witch.hasFamiliar(type)) {

            final Familiar familiar = type.createInstance();
            familiar.setFamiliarDefinition(definition);
            witch.addFamiliar(familiar);
            return true;
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public static void grantPhysicalBody(Player owner, Familiar familiar, PathfinderMob oldBody) {
        PathfinderMob body = oldBody.convertTo((EntityType<? extends PathfinderMob>) oldBody.getType(), true);
        body.getCapability(Witcheroo.FAMILIAR_CAPABILITY).ifPresent(capability -> {
            capability.setBound(true);
            capability.setBehaviour(familiar.getFamiliarDefinition().behaviour());
            familiar.attachTo(body, owner);
            capability.attachTo(oldBody);
            capability.setOwner(owner.getUUID());
            body.setCustomName(familiar.getType().getDisplayName());
            WitcherooPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncFamiliarPacket(body.getId(), SyncFamiliarPacket.ADD_ENTITY));
        });
    }
}
