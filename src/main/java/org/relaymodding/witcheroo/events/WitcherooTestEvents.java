package org.relaymodding.witcheroo.events;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class WitcherooTestEvents {
    public static void ritualTest(PlayerInteractEvent.RightClickBlock event) {
        //if (SwitchBlockRitual.DIRT_TO_DIAMOND.canAffect(event.getEntity(), event.getLevel(), event.getPos(), event.getLevel().getBlockState(event.getPos()))) {
        //    SwitchBlockRitual.DIRT_TO_DIAMOND.apply(event.getEntity(), event.getLevel(), event.getPos(), event.getLevel().getBlockState(event.getPos()));
        //}
    }

    public static void familiarSummonTest(final PlayerInteractEvent.EntityInteractSpecific event) {
        if (!(event.getLevel() instanceof ServerLevel) || event.getHand() != InteractionHand.MAIN_HAND) return;

        /*if (event.getTarget() instanceof PathfinderMob mob && !mob.getCapability(Capabilities.FAMILIAR_CAPABILITY).map(PhysicalFamiliar::isBound).orElse(true)) {
            event.getEntity().getCapability(Capabilities.WITCH_CAPABILITY).ifPresent(capability -> {
                if (!capability.getOwnedFamiliars().isEmpty()) {
                	capability.getOwnedFamiliars().stream()
                		.filter(f -> !f.hasPhysicalBody() && f.getType().isSuitableVessel(mob)).findAny()
                		.ifPresent(familiar -> {
                			FamiliarBounding.grantPhysicalBody(event.getEntity(), familiar, mob);
                		});
                }
            });
        }*/

        /*if (event.getTarget() instanceof Pig) {

            event.getEntity().getCapability(Capabilities.WITCH_CAPABILITY).ifPresent(witch -> {

                WitcherooRegistries.getFamiliarTypeRegistry().stream().filter(type -> !witch.hasFamiliar(type)).findAny().ifPresent(newType -> {
                    if (FamiliarBounding.unlockFamiliarType(event.getEntity(), newType, WitcherooRegistries.getFamiliarDefinitionRegistry().stream().findAny().get())) {
                        event.getEntity().sendSystemMessage(Component.translatable("witcheroo.notices.obtained_familiar", newType.getDisplayName()));
                    }
                });
            });
        }*/
    }
}
