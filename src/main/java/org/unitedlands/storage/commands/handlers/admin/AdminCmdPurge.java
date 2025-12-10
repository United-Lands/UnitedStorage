package org.unitedlands.storage.commands.handlers.admin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.storage.objects.StorageContainer;
import org.unitedlands.storage.UnitedStorage;
import org.unitedlands.utils.Logger;
import org.unitedlands.utils.Messenger;


public class AdminCmdPurge extends BaseCommandHandler<UnitedStorage> {

    public AdminCmdPurge(UnitedStorage plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            Messenger.sendMessage(sender, messageProvider.getList("messages.usage-cmd-admin-validate"), null, messageProvider.get("messages.prefix"));
            return;
        }

        plugin.getVisualisationManager().stopVisualisation();
        plugin.getScheduler().stopChecks();

        var threshold = plugin.getConfig().getLong("settings.purge-time-threshold", 0L);
        if (threshold <= 0L) {
            Logger.logWarning("Ivalid threshold value, cancelling.", "UnitedStorage");
            return;
        }

        // Convert to milliseconds
        threshold = threshold * 1000L;

        var sorters = plugin.getDataManager().getSorters().values();
        Logger.log("Inspecting " + sorters.size() + " storage containers...", "UnitedStorage");

        Set<StorageContainer> containersToRemove = new HashSet<>();
        for (var sorter : sorters) {
            var lastInteraction = sorter.getLastInteractionTime();
            var difference = System.currentTimeMillis() - lastInteraction;

            if (difference > threshold) {
                containersToRemove.add(sorter);
            }

        }

        if (containersToRemove.size() > 0) {
            plugin.getLogger().info("Found " + containersToRemove.size() + " sorters to purge.");
            for (var c : containersToRemove) {
                if (plugin.getDataManager().removeStorageContainer(c))
                    Logger.log("Container " + c.getUuid() + " removed.", "UnitedStorage");
            }
        }

        Logger.log("Purge complete.");

        plugin.getScheduler().startChecks();
        plugin.getVisualisationManager().startVisualisation();

        Messenger.sendMessage(sender, messageProvider.get("messages.purge-complete-info"), null, messageProvider.get("messages.prefix"));
    }
}
