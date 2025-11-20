package org.unitedlands.commands.handlers.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedStorage;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.objects.StorageContainerState;
import org.unitedlands.util.Messenger;
import org.unitedlands.util.Utilities;

public class StorageCmdStart extends BaseCommandHandler<UnitedStorage> {

    public StorageCmdStart(UnitedStorage plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            Messenger.sendMessageListTemplate(sender, "usage-cmd-start", null, true);
            return;
        }

        var player = (Player) sender;
        var block = Utilities.getTargetBlock(player, 6);

        if (block == null) {
            Messenger.sendMessageTemplate(player, "error-no-chest-in-los", null, true);
            return;
        }

        if (!(block.getType() == Material.CHEST)) {
            Messenger.sendMessageTemplate(player, "error-no-chest-in-los", null, true);
            return;
        }

        var location = block.getLocation();
        var container = plugin.getDataManager().getSorterContainerAtLocation(location);
        if (container == null) {
            Messenger.sendMessageTemplate(player, "error-no-sorter-in-location", null, true);
            return;
        }

        if (!container.getOwner().equals(player.getUniqueId()) && !player.hasPermission("united.storage.admin")) {
            Messenger.sendMessageTemplate(player, "error-not-owner", null, true);
            return;
        }

        var linkedTargets = plugin.getDataManager().getTargetContainersForSorter(container.getUuid());
        if (linkedTargets == null || linkedTargets.size() == 0)
            Messenger.sendMessageTemplate(player, "warning-no-target", null, true);

        var linkedOverflows = plugin.getDataManager().getOverflowContainersForSorter(container.getUuid());
        if (linkedOverflows == null || linkedOverflows.size() == 0)
            Messenger.sendMessageTemplate(player, "warning-no-overflow", null, true);

        container.setState(StorageContainerState.ENABLED);
        plugin.getDataManager().saveStorageContainerFile(container);

        Messenger.sendMessageTemplate(player, "success-sorter-toggle", Map.of("sorter-state", "§aenabled"), true);
    }

}
