package org.unitedlands.commands.handlers.admin;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.unitedlands.UnitedStorage;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.util.Messenger;

public class AdminCmdReload extends BaseCommandHandler<UnitedStorage> {

    public AdminCmdReload(UnitedStorage plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length > 1) {
            Messenger.sendMessageListTemplate(sender, "usage-cmd-admin-reload", null, true);
        }

        plugin.getVisualisationManager().stopVisualisation();
        plugin.getScheduler().stopChecks();
        plugin.reloadConfig();

        if (args.length == 0) {
            plugin.getScheduler().startChecks();
        }
        else if (args[0].equalsIgnoreCase("-all"))
        {
            plugin.getLogger().info("Force-reloading data files...");
            plugin.getDataManager().loadData();
        }

        plugin.getVisualisationManager().startVisualisation();

        Messenger.sendMessageTemplate(sender, "reload-info", null, true);
    }

}
