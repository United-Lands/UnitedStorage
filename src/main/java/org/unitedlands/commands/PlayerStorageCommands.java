package org.unitedlands.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.unitedlands.UnitedStorage;
import org.unitedlands.commands.handlers.base.ICommandHandler;
import org.unitedlands.commands.handlers.storage.StorageCmdCreate;
import org.unitedlands.commands.handlers.storage.StorageCmdInfo;
import org.unitedlands.commands.handlers.storage.StorageCmdItemSubhandler;
import org.unitedlands.commands.handlers.storage.StorageCmdRemove;
import org.unitedlands.commands.handlers.storage.StorageCmdStart;
import org.unitedlands.commands.handlers.storage.StorageCmdStop;
import org.unitedlands.commands.handlers.storage.StorageCmdToggleDisplay;
import org.unitedlands.util.Formatter;

public class PlayerStorageCommands implements CommandExecutor, TabCompleter {

    @SuppressWarnings("unused")
    private final UnitedStorage plugin;
    private final Map<String, ICommandHandler> handlers = new HashMap<>();

    public PlayerStorageCommands(UnitedStorage plugin) {
        this.plugin = plugin;
        registerHandlers();
    }

    private void registerHandlers() {
        handlers.put("create", new StorageCmdCreate(plugin));
        handlers.put("remove", new StorageCmdRemove(plugin));
        handlers.put("info", new StorageCmdInfo(plugin));
        handlers.put("start", new StorageCmdStart(plugin));
        handlers.put("stop", new StorageCmdStop(plugin));
        handlers.put("toggledisplay", new StorageCmdToggleDisplay(plugin));
        handlers.put("targetitem", new StorageCmdItemSubhandler(plugin));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, String alias,
            String[] args) {

        if (args.length == 0)
            return null;

        List<String> options = null;
        String input = args[args.length - 1];

        if (args.length == 1) {
            options = new ArrayList<>(handlers.keySet());
        } else {
            String subcommand = args[0].toLowerCase();
            ICommandHandler handler = handlers.get(subcommand);

            if (handler != null) {
                options = handler.handleTab(sender, Arrays.copyOfRange(args, 1, args.length));
            }
        }

        return Formatter.getSortedCompletions(input, options);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            String[] args) {

        if (args.length == 0)
            return false;

        String subcommand = args[0].toLowerCase();
        ICommandHandler handler = handlers.get(subcommand);

        if (handler == null) {
            return false;
        }

        handler.handleCommand(sender, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

}
