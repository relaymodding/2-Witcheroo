package org.relaymodding.witcheroo.events;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.tuple.Triple;
import org.relaymodding.witcheroo.capabilities.Capabilities;
import org.relaymodding.witcheroo.capabilities.PhysicalFamiliar;
import org.relaymodding.witcheroo.encounters.InitialWitchEncounter;
import org.relaymodding.witcheroo.familiar.Familiar;
import org.relaymodding.witcheroo.familiar.FamiliarBounding;
import org.relaymodding.witcheroo.items.WitcherooItems;
import org.relaymodding.witcheroo.network.SyncFamiliarPacket;
import org.relaymodding.witcheroo.network.WitcherooPacketHandler;
import org.relaymodding.witcheroo.registries.WitcherooRegistries;
import org.relaymodding.witcheroo.util.Reference;
import org.relaymodding.witcheroo.witch.ManaCosts;

import java.util.Optional;

public class WitcherooForgeEvents {

	 // TODO FIXME
    public static void familiarBodyLoadEvent(final EntityJoinLevelEvent e) {
    	final Entity entity = e.getEntity();
    	entity.getCapability(Capabilities.FAMILIAR_CAPABILITY).ifPresent(cap -> {
    		if (cap.getOwner() != null) {
                WitcherooPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncFamiliarPacket(entity.getId(), SyncFamiliarPacket.ADD_ENTITY));
    		}
    	});
    }

    public static void familiarBodyDeathEvent(final LivingDeathEvent e) {
    	final LivingEntity entity = e.getEntity();
    	final Level level = entity.level();
    	entity.getCapability(Capabilities.FAMILIAR_CAPABILITY).ifPresent(cap -> {
			Optional.ofNullable(cap.getOwner()).map(level::getPlayerByUUID).ifPresent(player -> player.getCapability(Capabilities.WITCH_CAPABILITY).ifPresent(capability -> {
				capability.getOwnedFamiliars().stream()
						.filter(f -> f.hasPhysicalBody() && f.getEntityId().equals(entity.getUUID()))
						.forEach(f -> f.setPhysicalBody(false));
			}));

			WitcherooPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncFamiliarPacket(entity.getId(), SyncFamiliarPacket.REMOVE_ENTITY));
            cap.setBound(false);
    	});
    }

	public static void onServerTick(final TickEvent.ServerTickEvent e) {
		if (!e.phase.equals(TickEvent.Phase.END)) return;

		if (FamiliarBounding.familiarBindingData.size() > 0) {
			for (Familiar familiar : FamiliarBounding.familiarBindingData.keySet()) {
				Triple<Integer, Player, PathfinderMob> triple = FamiliarBounding.familiarBindingData.get(familiar);

				PathfinderMob mob = triple.getRight();

				int timeLeft = triple.getLeft();
				if (timeLeft == FamiliarBounding.boundingTimeTicks) {
					mob.setPos(mob.position().relative(Direction.UP, 2.5));
				}
				else if (timeLeft <= 0) {
					mob.setSecondsOnFire(0);
					mob.setHealth(mob.getMaxHealth());
					FamiliarBounding.finishBoundingProcess(familiar);
					continue;
				}

				mob.setSpeed(0F);
				mob.setDeltaMovement(0D, 0D, 0D);
				mob.setPos(mob.position().relative(Direction.UP, 0.03));
				mob.setHealth(mob.getMaxHealth());
				mob.setSecondsOnFire(1);

				FamiliarBounding.familiarBindingData.put(familiar, Triple.of(timeLeft-1, triple.getMiddle(), triple.getRight()));
			}
		}
	}

	// Spawn a Witch encounter on first night of player to give knowledge (and staff?)
	public static void onPlayerTick(final TickEvent.PlayerTickEvent e) {
		Player player = e.player;
		Level level = player.level();
		if (level.isClientSide || !(e.phase.equals(TickEvent.Phase.END))) {
			return;
		}

		if (player.tickCount % 20 != 0) {
			return;
		}

		if (player.getTags().contains(Reference.MOD_ID + ".encounteredWitch")) {
			return;
		}

		if (!level.isNight()) {
			return;
		}

		InitialWitchEncounter.generateWitchForPlayer(level, player);
	}

	// Absorbing a spirit via targetting a mob with the Witch Staff.
	public static void onEntityInteract(final PlayerInteractEvent.EntityInteract e) {
		Level level = e.getLevel();
		if (level.isClientSide || e.getHand() != InteractionHand.MAIN_HAND) return;

		Player player = e.getEntity();
		Entity target = e.getTarget();
		ItemStack itemStack = e.getItemStack();

		if (itemStack.getItem().equals(WitcherooItems.WITCH_STAFF)) {
			e.setCanceled(true);

			if (target instanceof PathfinderMob mob && !mob.getCapability(Capabilities.FAMILIAR_CAPABILITY).map(PhysicalFamiliar::isBound).orElse(true)) {
				player.getCapability(Capabilities.WITCH_CAPABILITY).ifPresent(witch -> {

					WitcherooRegistries.getFamiliarTypeRegistry().stream().filter(type -> !witch.hasFamiliar(type) && type.isSuitableVessel(mob)).findAny().ifPresent(newType -> {
						if (FamiliarBounding.unlockFamiliarType(player, newType, WitcherooRegistries.getFamiliarDefinitionRegistry().stream().findAny().get())) {
							if (!witch.consumeMana(ManaCosts.absorpSpiritCost)) {
								player.sendSystemMessage(Component.translatable("witcheroo.notices.not_enough_mana", witch.getMana(), ManaCosts.absorpSpiritCost).withStyle(ChatFormatting.RED));
								return;
							}

							if (!player.isCreative()) {
								itemStack.shrink(1);
							}

							((PathfinderMob) target).setHealth(2F);
							target.setSecondsOnFire(3);

							player.sendSystemMessage(Component.translatable("witcheroo.notices.mana_used", ManaCosts.absorpSpiritCost, witch.getMana()).withStyle(ChatFormatting.GOLD));
							player.sendSystemMessage(Component.translatable("witcheroo.notices.absorbed_familiar", newType.getDisplayName()).withStyle(ChatFormatting.DARK_GRAY));
						}
					});
				});
			}
		}
	}

	// Creating a familiar by targetting a cauldron with the Witch Staff
	public static void onCauldronRightClick(final PlayerInteractEvent.RightClickBlock e) {
		Level level = e.getLevel();
		if (level.isClientSide || e.getHand() != InteractionHand.MAIN_HAND) return;

		Player player = e.getEntity();
		BlockPos pos = e.getPos();
		BlockState blockState = level.getBlockState(pos);
		if (blockState.getBlock() instanceof CauldronBlock) {
			ItemStack itemStack = e.getItemStack();
			if (itemStack.getItem().equals(WitcherooItems.WITCH_STAFF)) {
				e.setCanceled(true);
				for (Entity entityAround : level.getEntities(null, new AABB(pos.getX()+1, pos.getY(), pos.getZ()+1, pos.getX()-1, pos.getY()+2, pos.getZ()-1))) {
					if (entityAround instanceof PathfinderMob mob && !mob.getCapability(Capabilities.FAMILIAR_CAPABILITY).map(PhysicalFamiliar::isBound).orElse(true)) {
						player.getCapability(Capabilities.WITCH_CAPABILITY).ifPresent(witch -> {
							if (!witch.getOwnedFamiliars().isEmpty()) {
								witch.getOwnedFamiliars().stream()
									.filter(f -> !f.hasPhysicalBody() && f.getType().isSuitableVessel(mob)).findAny()
									.ifPresent(familiar -> {
										if (!witch.consumeMana(ManaCosts.grantPhysicalBodyCost)) {
											player.sendSystemMessage(Component.translatable("witcheroo.notices.not_enough_mana", witch.getMana(), ManaCosts.grantPhysicalBodyCost).withStyle(ChatFormatting.RED));
											return;
										}

										FamiliarBounding.startBoundingProcess(player, familiar, mob);
										player.sendSystemMessage(Component.translatable("witcheroo.notices.mana_used", ManaCosts.grantPhysicalBodyCost, witch.getMana()).withStyle(ChatFormatting.GOLD));
									});
							}
						});
					}
				}
			}
		}
	}

	// Shoot firecharge with Witch Staff, with a mana cost. Not entirely sure about this one
	public static void onWitchStaffRightClick(final PlayerInteractEvent.RightClickItem e) {
		/*Level level = e.getLevel();
		if (level.isClientSide || e.getHand() != InteractionHand.MAIN_HAND) return;

		ItemStack itemStack = e.getItemStack();
		if (!itemStack.getItem().equals(WitcherooItems.WITCH_STAFF)) {
			return;
		}

		System.out.println(level.getBlockState(e.getPos()).getBlock());

		Player player = e.getEntity();
		player.getCapability(Capabilities.WITCH_CAPABILITY).ifPresent(witch -> {
			if (!witch.consumeMana(ManaCosts.shootFireChargeCost)) {
				player.sendSystemMessage(Component.translatable("witcheroo.notices.not_enough_mana", witch.getMana(), ManaCosts.shootFireChargeCost).withStyle(ChatFormatting.RED));
				return;
			}

			ItemStack fireCharge = new ItemStack(Items.FIRE_CHARGE, 1);

			Direction direction = player.getDirection();
			Vec3 vec = player.position().relative(Direction.UP, 1.5D);
			double d0 = vec.x() + (double) ((float) direction.getStepX() * 0.3F);
			double d1 = vec.y() + (double) ((float) direction.getStepY() * 0.3F);
			double d2 = vec.z() + (double) ((float) direction.getStepZ() * 0.3F);

			RandomSource randomsource = level.random;
			double d3 = randomsource.triangle((double) direction.getStepX(), 0.11485000000000001D);
			double d4 = randomsource.triangle((double) direction.getStepY(), 0.11485000000000001D);
			double d5 = randomsource.triangle((double) direction.getStepZ(), 0.11485000000000001D);
			SmallFireball smallfireball = new SmallFireball(level, d0, d1, d2, d3, d4, d5);

			level.addFreshEntity(Util.make(smallfireball, (p_123552_) -> {
				p_123552_.setItem(fireCharge);
			}));

			player.sendSystemMessage(Component.translatable("witcheroo.notices.mana_used", ManaCosts.shootFireChargeCost, witch.getMana()).withStyle(ChatFormatting.GOLD));
		});*/
	}
}
