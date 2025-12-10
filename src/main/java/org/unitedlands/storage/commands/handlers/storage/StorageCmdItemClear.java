package org.unitedlands.storage.commands.handlers.storage;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.storage.UnitedStorage;
import org.unitedlands.storage.objects.StorageContainerType;
import org.unitedlands.storage.util.Utilities;
import org.unitedlands.utils.Messenger;

public class StorageCmdItemClear extends BaseCommandHandler<UnitedStorage> {

    public StorageCmdItemClear(UnitedStorage plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            Messenger.sendMessage(sender, messageProvider.getList("messages.usage-cmd-item-clear"), null, messageProvider.get("messages.prefix"));
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

        container.clearFilter();
        plugin.getDataManager().saveStorageContainerFile(container);

        Messenger.sendMessage(sender, messageProvider.get("messages.success-item-clear"), null, messageProvider.get("messages.prefix"));

    }

}
