package org.unitedlands.commands.handlers.admin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.unitedlands.UnitedStorage;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.objects.StorageContainer;
import org.unitedlands.util.Messenger;

public class AdminCmdValidate extends BaseCommandHandler<UnitedStorage> {

    public AdminCmdValidate(UnitedStorage plugin, IMessageProvider messageProvider) {
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
        
        var containers = plugin.getDataManager().getAllStorageContainers();
        plugin.getLogger().info("Validating " + containers.size() + " storage containers...");
        plugin.getLogger().info("Validating blocks...");

        Set<StorageContainer> containersToRemove = new HashSet<>();
        for (var container : containers) {
            var loc = container.getLocation();
            var block = loc.getBlock();

            var loc2 = container.getLocation2();
            var block2 = loc2.getBlock();

            if (block.getType() != Material.CHEST || block2.getType() != Material.CHEST) {
                containersToRemove.add(container);
            }
        }

        if (containersToRemove.size() > 0) {
            for (var c : containersToRemove) {
                if (plugin.getDataManager().removeStorageContainer(c))
                    plugin.getLogger().info("Container " + c.getUuid() + " removed.");
            }
        }

        plugin.getLogger().info("Validation complete.");

        plugin.getScheduler().startChecks();
        plugin.getVisualisationManager().startVisualisation();

        Messenger.sendMessageTemplate(sender, "validation-complete-info", null, true);
    }
}
