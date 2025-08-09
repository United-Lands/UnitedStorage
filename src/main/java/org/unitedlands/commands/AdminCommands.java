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
import org.unitedlands.commands.handlers.admin.AdminCmdPurge;
import org.unitedlands.commands.handlers.admin.AdminCmdReload;
import org.unitedlands.commands.handlers.admin.AdminCmdValidate;
import org.unitedlands.commands.handlers.base.ICommandHandler;
import org.unitedlands.util.Formatter;
import org.unitedlands.util.Messenger;

public class AdminCommands implements CommandExecutor, TabCompleter {

    @SuppressWarnings("unused")
    private final UnitedStorage plugin;
    private final Map<String, ICommandHandler> handlers = new HashMap<>();

    public AdminCommands(UnitedStorage plugin) {
        this.plugin = plugin;
        registerHandlers();
    }

    private void registerHandlers() {
        handlers.put("reload", new AdminCmdReload(plugin));
        handlers.put("validatedata", new AdminCmdValidate(plugin));
        handlers.put("purge", new AdminCmdPurge(plugin));
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

        if (!sender.hasPermission("united.storage.admin")) {
            Messenger.sendMessageTemplate(sender, "error-no-permission", null, true);
            return false;
        }

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
