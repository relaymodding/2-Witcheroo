package org.relaymodding.witcheroo.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import org.relaymodding.witcheroo.capabilities.Capabilities;
import org.relaymodding.witcheroo.capabilities.Witch;
import org.relaymodding.witcheroo.familiar.Familiar;
import org.relaymodding.witcheroo.util.Reference;

import java.util.ArrayList;
import java.util.List;

public class WitcherooCommands {

    public static void buildCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection environment) {

        final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(Reference.MOD_ID);
        playerCommand(List.of("familiars", "list"), root, WitcherooCommands::printFamiliars);
        playerCommand(List.of("familiars", "clear"), root, WitcherooCommands::clearFamiliars);
        playerCommand(List.of("mana", "show"), root, WitcherooCommands::getCurrentMana);
		playerCommand(List.of("mana", "fill"), root, WitcherooCommands::fillMana);
        dispatcher.register(root);
    }

    private static void playerCommand(List<String> names, LiteralArgumentBuilder<CommandSourceStack> parent, PlayerCommandFunction function) {
        LiteralArgumentBuilder<CommandSourceStack> command = null, next = null;
        
        for (int i = names.size() - 1; i >= 0; i--) {
        	next = Commands.literal(names.get(i));
        	if (command == null) {
        		command = next.executes(ctx ->
            			function.apply(ctx.getSource().getPlayer(), ctx)
            		)
                	.then(Commands.argument("player", EntityArgument.player())
                		.executes(ctx -> 
                			function.apply(EntityArgument.getPlayer(ctx, "player"), ctx)
                		)
                	);
        	} else if (i == 0) {
        		command = next.requires(user -> user.hasPermission(2))
        			.then(command);
			} else {
            	command = next.then(command);
        	}
		}
        
		parent.then(command);
    }

    private static int printFamiliars(Player player, CommandContext<CommandSourceStack> ctx) {

        if (player instanceof ServerPlayer serverPlayer) {

            final LazyOptional<Witch> cap = serverPlayer.getCapability(Capabilities.WITCH_CAPABILITY);

            if (cap.isPresent()) {

                cap.ifPresent(witch -> {

                    if (!witch.getOwnedFamiliars().isEmpty()) {

                        for (Familiar familiar : witch.getOwnedFamiliars()) {

                            final Component name = familiar.getType().getDisplayName();
                            final Component mana = Component.translatable("witcheroo.notices.mana", familiar.getType().getMaxMana());
                            final Component level = Component.translatable("witcheroo.notices.level", familiar.getLevel(), familiar.getType().getMaxLevel()).withStyle(ChatFormatting.AQUA);
                            final Component status = familiar.hasPhysicalBody() ? Component.translatable("witcheroo.notices.bound").withStyle(ChatFormatting.RED) : Component.translatable("witcheroo.notices.incorporeal").withStyle(ChatFormatting.GREEN);
                            ctx.getSource().sendSuccess(() -> Component.translatable("witcheroo.notices.familiar_info", name, mana, level, status), false);
                        }
                    }

                    else {

                        ctx.getSource().sendFailure(Component.translatable("witcheroo.notices.no_familiars", player.getDisplayName()));
                    }
                });
            }

            else {
                ctx.getSource().sendFailure(Component.translatable("witcheroo.notices.no_witch_cap", player.getDisplayName()));
            }
        }

        return 0;
    }

    private static int clearFamiliars(Player player, CommandContext<CommandSourceStack> ctx) {

        if (player instanceof ServerPlayer serverPlayer) {

            final LazyOptional<Witch> cap = serverPlayer.getCapability(Capabilities.WITCH_CAPABILITY);

            if (cap.isPresent()) {

                cap.ifPresent(witch -> {

                    if (!witch.getOwnedFamiliars().isEmpty()) {
                    	final List<Familiar> toRemove = new ArrayList<>(witch.getOwnedFamiliars());
                    	toRemove.stream().map(Familiar::getType).forEach(witch::removeFamiliar);

                        ctx.getSource().sendSuccess(() -> Component.translatable("witcheroo.notices.cleared_familiars", toRemove.size(), player.getDisplayName()), false);
                    }

                    else {

                        ctx.getSource().sendFailure(Component.translatable("witcheroo.notices.no_familiars", player.getDisplayName()));
                    }
                });
            }

            else {
                ctx.getSource().sendFailure(Component.translatable("witcheroo.notices.no_witch_cap", player.getDisplayName()));
            }
        }

        return 0;
    }

    private static int getCurrentMana(Player player, CommandContext<CommandSourceStack> ctx) {

        if (player instanceof ServerPlayer serverPlayer) {

            final LazyOptional<Witch> cap = serverPlayer.getCapability(Capabilities.WITCH_CAPABILITY);

            if (cap.isPresent()) {

                cap.ifPresent(witch -> {

                    ctx.getSource().sendSuccess(() -> Component.translatable("witcheroo.notices.player_mana", serverPlayer.getDisplayName(), Component.literal(Integer.toString(witch.getMana())), Component.literal(Integer.toString(witch.getMaxMana()))), false);
                });
            }

            else {

                ctx.getSource().sendFailure(Component.translatable("witcheroo.notices.no_witch_cap", player.getDisplayName()));
            }
        }

        return 0;
    }

    private static int fillMana(Player player, CommandContext<CommandSourceStack> ctx) {

        if (player instanceof ServerPlayer serverPlayer) {

            final LazyOptional<Witch> cap = serverPlayer.getCapability(Capabilities.WITCH_CAPABILITY);

            if (cap.isPresent()) {

                cap.ifPresent(witch -> {
					witch.setMana(witch.getMaxMana());
                    witch.sync(serverPlayer);

                    ctx.getSource().sendSuccess(() -> Component.translatable("witcheroo.notices.mana_filled", Component.literal(Integer.toString(witch.getMana())), Component.literal(Integer.toString(witch.getMaxMana()))), false);
                });
            }

            else {

                ctx.getSource().sendFailure(Component.translatable("witcheroo.notices.no_witch_cap", player.getDisplayName()));
            }
        }

        return 0;
    }

    public interface PlayerCommandFunction {

        int apply(Player player, CommandContext<CommandSourceStack> ctx);
    }
}
