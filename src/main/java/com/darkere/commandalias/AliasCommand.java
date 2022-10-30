package com.darkere.commandalias;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class AliasCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("addAlias")
            .requires(x -> x.hasPermission(4))
            .then(Commands.argument("alias", StringArgumentType.string())
                .then(Commands.literal("->")
                    .then(Commands.argument("cmd", StringArgumentType.string())
                        .executes(ctx -> newAlias(ctx, StringArgumentType.getString(ctx, "alias"), StringArgumentType.getString(ctx, "cmd"))))));
    }

    private static int newAlias(CommandContext<CommandSourceStack> ctx, String alias, String cmd) {
        AliasRegistry.addAliases(alias, cmd);
        ctx.getSource().sendSuccess(Component.literal("New command alias"), false);
        ctx.getSource().sendSuccess(Component.literal("\"/" + alias + "\"" + " now runs " + "\"/" + cmd + "\""), false);
        ctx.getSource().sendSuccess(Component.literal("Use /reload to make the command work"), false);
        return 1;
    }
}
