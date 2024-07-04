package com.darkere.commandalias;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CommandAlias.MODID)
public class CommandAlias
{
    public static final String MODID = "commandalias";

    public CommandAlias(IEventBus modEventbus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
    }


    @SubscribeEvent
    public void onCommandRegistered(RegisterCommandsEvent event) {
        event.getDispatcher().register(AliasCommand.register());
        AliasRegistry.registerAliases(event.getDispatcher());
    }

}
