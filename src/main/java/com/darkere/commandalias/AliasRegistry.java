package com.darkere.commandalias;

import com.electronwill.nightconfig.core.utils.StringUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class AliasRegistry {
    private static final Map<String, String> aliases = new HashMap<>();
    private static final File file = new File("config/commandaliases.txt");

    public static int runAlias(CommandContext<CommandSource> context) {
        String input = context.getInput();
        String command = input.substring(input.indexOf(context.getNodes().get(0).getNode().getName()));
        String commandToRun = getCommandWithArguments(command);
        context.getSource().getServer().getCommandManager().handleCommand(context.getSource(), commandToRun);
        return 1;
    }

    private static String getCommandWithArguments(String command) {
        List<String> nodes = StringUtils.split(command, ' ');
        String test = "";
        for (Iterator<String> iterator = nodes.iterator(); iterator.hasNext(); ) {
            test = test + " " + iterator.next();
            test = test.trim();
            iterator.remove();
            if (aliases.containsKey(test)) {
                break;
            }
        }

        String com = aliases.get(test);
        StringBuilder args = new StringBuilder();
        for (String node : nodes) {
            args.append(" ").append(node);
        }
        return com == null ? "" : com + args;
    }

    public static void registerAliases(CommandDispatcher<CommandSource> dispatcher) {
        readAliases();
        for (String x : aliases.keySet()) {
            List<String> nodes = StringUtils.split(x, ' ');
            List<LiteralArgumentBuilder<CommandSource>> literals = new ArrayList<>();
            for (int i = 0; i < nodes.size(); i++) {
                if (i == 0) {
                    literals.add(LiteralArgumentBuilder.literal(nodes.get(i)));
                } else {
                    literals.add(Commands.literal(nodes.get(i)));
                }
            }

            LiteralArgumentBuilder<CommandSource> literal = null;
            if (literals.size() == 1) {
                literal = literals.get(0).executes(AliasRegistry::runAlias).then(Commands.argument("args", StringArgumentType.greedyString()).executes(AliasRegistry::runAlias));
            } else {
                for (int i = literals.size() - 1; i > 0; i--) {
                    if (i == literals.size() - 1) {
                        literals.get(i).executes(AliasRegistry::runAlias).then(Commands.argument("args", StringArgumentType.greedyString()).executes(AliasRegistry::runAlias));
                    }
                    literal = literals.get(i - 1).then(literals.get(i));
                }
            }
            dispatcher.register(literal);
        }
    }


    public static void addAliases(String name, String cmd) {
        aliases.put(name, cmd);
        writeAliases();
    }

    private static void writeAliases() {
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        aliases.forEach((k, v) -> {
            String toWrite = k + " | " + v + System.lineSeparator();
            try {
                Files.write(file.toPath(), toWrite.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void readAliases() {
        List<String> lines = new ArrayList<>();
        if (file.exists()) {
            try {
                lines = Files.readAllLines(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        aliases.clear();
        for (String x : lines) {
            List<String> strings = StringUtils.split(x, '|');
            for (int i = 0; i < strings.size(); i++) {
                strings.set(i, strings.get(i).trim());
            }
            aliases.put(strings.get(0), strings.get(1));
        }
    }


}
