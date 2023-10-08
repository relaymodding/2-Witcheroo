package org.relaymodding.witcheroo.familiar;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import org.relaymodding.witcheroo.Witcheroo;
import org.relaymodding.witcheroo.network.SyncFamiliarPacket;
import org.relaymodding.witcheroo.network.WitcherooPacketHandler;

public class FamiliarBounding {

    public static void createFamiliar(Player player, FamiliarDefinition definition) {
        player.getCapability(Witcheroo.WITCH_CAPABILITY).ifPresent(capability -> {
            Familiar familiar = new Familiar();
            familiar.selectRandomName();
            familiar.setFamiliarDefinition(definition);
            capability.addFamiliar(familiar);
        });
    }

    @SuppressWarnings("unchecked")
    public static void grantPhysicalBody(Player owner, Familiar familiar, PathfinderMob oldBody) {
        PathfinderMob body = oldBody.convertTo((EntityType<? extends PathfinderMob>) oldBody.getType(), true);
        body.getCapability(Witcheroo.FAMILIAR_CAPABILITY).ifPresent(capability -> {
            capability.setBound();
            capability.setBehaviour(familiar.getFamiliarDefinition().behaviour());
            familiar.attachTo(body, owner);
            capability.attachTo(oldBody);
            capability.setOwner(owner.getUUID());
            familiar.setPhysicalBody(true);
            body.setCustomName(Component.literal(familiar.getName()));
            WitcherooPacketHandler.INSTANCE.send(
                    PacketDistributor.ALL.noArg(), new SyncFamiliarPacket(body.getId(), SyncFamiliarPacket.ADD_ENTITY));
        });
    }
}
