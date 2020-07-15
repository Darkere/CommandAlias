package com.darkere.commandalias;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class AliasCommand {
    public static LiteralArgumentBuilder<CommandSource> register() {
        return LiteralArgumentBuilder.<CommandSource>literal("addAlias")
            .then(Commands.argument("alias", StringArgumentType.string())
                .then(Commands.literal("->")
                    .then(Commands.argument("cmd", StringArgumentType.string())
                        .executes(ctx -> newAlias(ctx, StringArgumentType.getString(ctx, "alias"), StringArgumentType.getString(ctx, "cmd"))))));
    }

    private static int newAlias(CommandContext<CommandSource> ctx, String alias, String cmd) {
        AliasRegistry.addAliases(alias, cmd);
        ctx.getSource().sendFeedback(new StringTextComponent("New command alias"), false);
        ctx.getSource().sendFeedback(new StringTextComponent("\"/" + alias + "\"" + " now runs " + "\"/" + cmd + "\""), false);
        ctx.getSource().sendFeedback(new StringTextComponent("Use /reload to make the command work"), false);
        return 1;
    }
}
