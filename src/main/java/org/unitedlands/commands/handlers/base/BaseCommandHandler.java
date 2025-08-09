package org.unitedlands.commands.handlers.base;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.unitedlands.UnitedStorage;

public abstract class BaseCommandHandler implements ICommandHandler, Listener {

    protected final UnitedStorage plugin;

    public BaseCommandHandler(UnitedStorage plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public abstract List<String> handleTab(CommandSender sender, String[] args);
    @Override
    public abstract void handleCommand(CommandSender sender, String[] args);

}
