package com.darkere.commandalias;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CommandAlias.MODID)
public class CommandAlias
{
    public static final String MODID = "commandalias";

    public CommandAlias() {
        MinecraftForge.EVENT_BUS.register(this);
    }


    @SubscribeEvent
    public void onCommandRegistered(RegisterCommandsEvent event) {
        event.getDispatcher().register(AliasCommand.register());
        AliasRegistry.registerAliases(event.getDispatcher());
    }

}
