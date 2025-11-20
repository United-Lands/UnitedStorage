package org.unitedlands.commands.handlers.storage;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedStorage;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.objects.StorageContainerType;
import org.unitedlands.util.Messenger;
import org.unitedlands.util.Utilities;

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
            Messenger.sendMessageListTemplate(sender, "usage-cmd-item-clear", null, true);
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
        var container = plugin.getDataManager().getStorageContainerAtLocation(location);
        if (container == null) {
            Messenger.sendMessageTemplate(player, "error-no-container-in-location", null, true);
            return;
        }

        if (container.getType() != StorageContainerType.TARGET) {
            Messenger.sendMessageTemplate(player, "error-not-target-chest", null, true);
            return;
        }

        if (!container.getOwner().equals(player.getUniqueId()) && !player.hasPermission("united.storage.admin")) {
            Messenger.sendMessageTemplate(player, "error-not-owner", null, true);
            return;
        }

        container.clearFilter();
        plugin.getDataManager().saveStorageContainerFile(container);

        Messenger.sendMessageTemplate(player, "success-item-clear", null, true);

    }

}
