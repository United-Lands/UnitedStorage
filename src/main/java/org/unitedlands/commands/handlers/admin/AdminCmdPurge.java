package org.unitedlands.commands.handlers.admin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.unitedlands.UnitedStorage;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.objects.StorageContainer;
import org.unitedlands.util.Messenger;

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
            Messenger.sendMessageListTemplate(sender, "usage-cmd-admin-validate", null, true);
            return;
        }

        plugin.getVisualisationManager().stopVisualisation();
        plugin.getScheduler().stopChecks();

        var threshold = plugin.getConfig().getLong("settings.purge-time-threshold", 0L);
        if (threshold <= 0L) {
            plugin.getLogger().warning("Ivalid threshold value, cancelling.");
            return;
        }

        // Convert to milliseconds
        threshold = threshold * 1000L;

        var sorters = plugin.getDataManager().getSorters().values();
        plugin.getLogger().info("Inspecting " + sorters.size() + " storage containers...");

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
                    plugin.getLogger().info("Container " + c.getUuid() + " removed.");
            }
        }

        plugin.getLogger().info("Purge complete.");

        plugin.getScheduler().startChecks();
        plugin.getVisualisationManager().startVisualisation();

        Messenger.sendMessageTemplate(sender, "purge-complete-info", null, true);
    }
}
