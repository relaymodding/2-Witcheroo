package org.relaymodding.witcheroo.encounters;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.relaymodding.witcheroo.Witcheroo;
import org.relaymodding.witcheroo.registries.WitcherooRegistries;
import org.relaymodding.witcheroo.util.Reference;

public class InitialWitchEncounter {
	public static void generateWitchForPlayer(Level level, Player player) {
		Witch witch = EntityType.WITCH.create(level);
		if (witch != null) {
			witch.setPos(player.position().relative(Direction.NORTH, 3));
			witch.removeAllGoals((Goal goal) -> true);
			witch.removeFreeWill();
			level.addFreshEntity(witch);

			sendMessageToPlayer(player);

			player.getTags().add(Reference.MOD_ID + ".encounteredWitch");
		}
	}

	private static void sendMessageToPlayer(Player player) {
		player.sendSystemMessage(Component.empty());
		player.sendSystemMessage(Component.translatable("witcheroo.encounters.first_witch", player.getName(), Items.OAK_LOG.getDefaultInstance().getHoverName(), Items.CRYING_OBSIDIAN.getDefaultInstance().getHoverName(), WitcherooRegistries.WITCH_STAFF_OBJECT.get().getDefaultInstance().getHoverName(),  WitcherooRegistries.WITCH_STAFF_OBJECT.get().getDefaultInstance().getHoverName(), Blocks.GLOWSTONE.getName()).withStyle(ChatFormatting.DARK_RED));
	}
}
