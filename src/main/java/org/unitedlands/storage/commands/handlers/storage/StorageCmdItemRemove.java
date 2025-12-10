package org.unitedlands.storage.commands.handlers.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.storage.UnitedStorage;
import org.unitedlands.storage.objects.StorageContainerType;
import org.unitedlands.storage.util.Utilities;
import org.unitedlands.utils.Messenger;

public class StorageCmdItemRemove extends BaseCommandHandler<UnitedStorage> {

    public StorageCmdItemRemove(UnitedStorage plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        var player = (Player) sender;
        if (args.length == 1) {
            var block = Utilities.getTargetBlock(player, 6);
            if (block != null && block.getType() == Material.CHEST) {
                var location = block.getLocation();
                var container = plugin.getDataManager().getStorageContainerAtLocation(location);
                if (container != null) {
                    return new ArrayList<>(container.getFilter());
                }
            }
        }

        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            Messenger.sendMessage(sender, messageProvider.getList("messages.usage-cmd-item-remove"), null, messageProvider.get("messages.prefix"));
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
        var container = plugin.getDataManager().getStorageContainerAtLocation(location);
        if (container == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-no-container-in-location"), null, messageProvider.get("messages.prefix"));
            return;
        }

        if (container.getType() != StorageContainerType.TARGET) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-not-target-chest"), null, messageProvider.get("messages.prefix"));
            return;
        }

        if (!container.getOwner().equals(player.getUniqueId()) && !player.hasPermission("united.storage.admin")) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-not-owner"), null, messageProvider.get("messages.prefix"));
            return;
        }

        if (!container.getFilter().contains(args[0])) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-not-filter-item"), null, messageProvider.get("messages.prefix"));
            return;
        }

        container.removeFilterItem(args[0]);
        plugin.getDataManager().saveStorageContainerFile(container);

         Messenger.sendMessage(sender, messageProvider.get("messages.success-item-remove"), Map.of("material", args[0]), messageProvider.get("messages.prefix"));
    }

}
