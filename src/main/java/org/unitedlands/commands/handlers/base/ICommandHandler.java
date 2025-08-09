package org.unitedlands.commands.handlers.base;

import java.util.List;

import org.bukkit.command.CommandSender;

public interface ICommandHandler {
    List<String> handleTab(CommandSender sender, String[] args);
    void handleCommand(CommandSender sender, String[] args);
}