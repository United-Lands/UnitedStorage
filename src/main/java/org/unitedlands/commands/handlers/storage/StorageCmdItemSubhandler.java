package org.unitedlands.commands.handlers.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.unitedlands.UnitedStorage;
import org.unitedlands.commands.handlers.base.BaseCommandHandler;
import org.unitedlands.commands.handlers.base.ICommandHandler;
import org.unitedlands.util.Messenger;

public class StorageCmdItemSubhandler extends BaseCommandHandler {

    private final Map<String, ICommandHandler> subhandlers = new HashMap<>();

    public StorageCmdItemSubhandler(UnitedStorage plugin) {
        super(plugin);
        registerSubHandler();
    }

    private void registerSubHandler() {
        subhandlers.put("add", new StorageCmdItemAdd(plugin));
        subhandlers.put("remove", new StorageCmdItemRemove(plugin));
        subhandlers.put("clear", new StorageCmdItemClear(plugin));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {

        if (args.length == 0)
            return null;

        List<String> options = null;
        if (args.length == 1) {
            options = new ArrayList<>(subhandlers.keySet());
        } else {
            String subcommand = args[0].toLowerCase();
            ICommandHandler subhandler = subhandlers.get(subcommand);

            if (subhandler != null) {
                options = subhandler.handleTab(sender, Arrays.copyOfRange(args, 1, args.length));
            }
        }

        return options;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length == 0)
        {
            Messenger.sendMessageListTemplate(sender, "usage-cmd-item", null, true);
            return;
        }

        String subcommand = args[0].toLowerCase();
        ICommandHandler subhandler = subhandlers.get(subcommand);

        if (subhandler == null) {
            Messenger.sendMessageListTemplate(sender, "usage-cmd-item", null, true);
            return;
        }

        subhandler.handleCommand(sender, Arrays.copyOfRange(args, 1, args.length));
        return;

    }

}
