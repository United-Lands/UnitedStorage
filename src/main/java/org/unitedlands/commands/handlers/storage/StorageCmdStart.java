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
import org.unitedlands.utils.Messenger;
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
            Messenger.sendMessage(sender, messageProvider.getList("messages.usage-cmd-start"), null, messageProvider.get("messages.prefix"));
            return;
        }

        var player = (Player) sender;
        var block = Utilities.getTargetBlock(player, 6);

        if (block == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-no-chest-in-los"), null, messageProvider.get("messages.prefix"));
            return;
        }

        if (!(block.getType() == Material.CHEST)) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-no-chest-in-los"), null, messageProvider.get("messages.prefix"));
            return;
        }

        var location = block.getLocation();
        var container = plugin.getDataManager().getSorterContainerAtLocation(location);
        if (container == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-no-sorter-in-location"), null, messageProvider.get("messages.prefix"));
            return;
        }

        if (!container.getOwner().equals(player.getUniqueId()) && !player.hasPermission("united.storage.admin")) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-not-owner"), null, messageProvider.get("messages.prefix"));
            return;
        }

        var linkedTargets = plugin.getDataManager().getTargetContainersForSorter(container.getUuid());
        if (linkedTargets == null || linkedTargets.size() == 0)
            Messenger.sendMessage(sender, messageProvider.get("messages.warning-no-target"), null, messageProvider.get("messages.prefix"));

        var linkedOverflows = plugin.getDataManager().getOverflowContainersForSorter(container.getUuid());
        if (linkedOverflows == null || linkedOverflows.size() == 0)
            Messenger.sendMessage(sender, messageProvider.get("messages.warning-no-overflow"), null, messageProvider.get("messages.prefix"));

        container.setState(StorageContainerState.ENABLED);
        plugin.getDataManager().saveStorageContainerFile(container);

        Messenger.sendMessage(sender, messageProvider.get("messages.success-sorter-toggle"), Map.of("sorter-state", "<green>enabled</green>"), messageProvider.get("messages.prefix"));
    }

}
