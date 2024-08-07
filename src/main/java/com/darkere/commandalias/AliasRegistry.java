package com.darkere.commandalias;

import com.electronwill.nightconfig.core.utils.StringUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class AliasRegistry {
    private static final Map<String, String> aliases = new HashMap<>();
    private static final File file = new File("config/commandaliases.txt");

    public static int runAlias(CommandContext<CommandSourceStack> context) {
        String input = context.getInput();
        String command = input.substring(input.indexOf(context.getNodes().get(0).getNode().getName()));
        String commandToRun = getCommandWithArguments(command);
        context.getSource().getServer().getCommands().performPrefixedCommand(context.getSource(), commandToRun);
        return 1;
    }

    private static String getCommandWithArguments(String command) {
        List<String> nodes = StringUtils.split(command, ' ');
        String test = "";

        //Iterate until the command matches alias, this allows for postfixing
        for (Iterator<String> iterator = nodes.iterator(); iterator.hasNext(); ) {
            test = test + " " + iterator.next();
            test = test.trim();
            iterator.remove();
            if (aliases.containsKey(test)) {
                break;
            }
        }

        // replace %note% with next node
        String com = aliases.get(test);
        while (com.contains("%")) {
            int start = com.indexOf("%");
            int end = com.indexOf("%", start + 1);
            String begin = com.substring(0, start);
            String ending = com.substring(end + 1);
            if (!nodes.isEmpty()) {
                com = begin + nodes.get(0) + ending;
                nodes.remove(0);
            } else {
                break;
            }
        }

        //append remaining nodes
        StringBuilder args = new StringBuilder();
        for (String node : nodes) {
            args.append(" ").append(node);
        }
        return com + args;
    }

    public static void registerAliases(CommandDispatcher<CommandSourceStack> dispatcher) {
        readAliases();
        for (String x : aliases.keySet()) {
            List<String> nodes = StringUtils.split(x, ' ');
            List<LiteralArgumentBuilder<CommandSourceStack>> literals = new ArrayList<>();
            for (int i = 0; i < nodes.size(); i++) {
                if (i == 0) {
                    literals.add(LiteralArgumentBuilder.literal(nodes.get(i)));
                } else {
                    literals.add(Commands.literal(nodes.get(i)));
                }
            }

            LiteralArgumentBuilder<CommandSourceStack> literal = null;
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
