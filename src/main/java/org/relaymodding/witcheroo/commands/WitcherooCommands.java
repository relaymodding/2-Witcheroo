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
import org.relaymodding.witcheroo.Witcheroo;
import org.relaymodding.witcheroo.capabilities.Witch;
import org.relaymodding.witcheroo.familiar.Familiar;

public class WitcherooCommands {

    public static void buildCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection environment) {

        final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(Witcheroo.MOD_ID);
        playerCommand("familiars", root, WitcherooCommands::printFamiliars);
        playerCommand("mana", root, WitcherooCommands::getCurrentMana);
        dispatcher.register(root);
    }

    private static void playerCommand(String name, LiteralArgumentBuilder<CommandSourceStack> parent, PlayerCommandFunction function) {

        final LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal(name);

        command.executes(ctx -> function.apply(ctx.getSource().getPlayer(), ctx));
        command.requires(user -> user.hasPermission(2)).then(Commands.argument("player", EntityArgument.player()).executes(ctx -> function.apply(EntityArgument.getPlayer(ctx, "player"), ctx)));

        parent.then(command);
    }

    private static int getCurrentMana(Player player, CommandContext<CommandSourceStack> ctx) {

        if (player instanceof ServerPlayer serverPlayer) {

            final LazyOptional<Witch> cap = serverPlayer.getCapability(Witcheroo.WITCH_CAPABILITY);

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

    private static int printFamiliars(Player player, CommandContext<CommandSourceStack> ctx) {

        if (player instanceof ServerPlayer serverPlayer) {

            final LazyOptional<Witch> cap = serverPlayer.getCapability(Witcheroo.WITCH_CAPABILITY);

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

                        ctx.getSource().sendFailure(Component.translatable("witcheroo.notices.no_familiairs", player.getDisplayName()));
                    }
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
