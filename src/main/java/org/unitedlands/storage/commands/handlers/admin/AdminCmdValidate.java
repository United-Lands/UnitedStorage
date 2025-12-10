package org.unitedlands.storage.commands.handlers.admin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.storage.objects.StorageContainer;
import org.unitedlands.storage.UnitedStorage;
import org.unitedlands.utils.Logger;
import org.unitedlands.utils.Messenger;

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
            Messenger.sendMessage(sender, messageProvider.getList("messages.usage-cmd-admin-validate"), null, messageProvider.get("messages.prefix"));
            return;
        }

        plugin.getVisualisationManager().stopVisualisation();
        plugin.getScheduler().stopChecks();
        
        var containers = plugin.getDataManager().getAllStorageContainers();
        Logger.log("Validating " + containers.size() + " storage containers...", "UnitedStorage");
        Logger.log("Validating blocks...", "UnitedStorage");

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
                    Logger.log("Container " + c.getUuid() + " removed.", "UnitedStorage");
            }
        }

        Logger.log("Validation complete.", "UnitedStorage");

        plugin.getScheduler().startChecks();
        plugin.getVisualisationManager().startVisualisation();

        Messenger.sendMessage(sender, messageProvider.get("messages.validation-complete-info"), null, messageProvider.get("messages.prefix"));
    }
}
