package org.unitedlands.commands.handlers.storage;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedStorage;
import org.unitedlands.commands.handlers.base.BaseCommandHandler;
import org.unitedlands.util.Messenger;
import org.unitedlands.util.Utilities;

public class StorageCmdRemove extends BaseCommandHandler {

    public StorageCmdRemove(UnitedStorage plugin) {
        super(plugin);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            Messenger.sendMessageListTemplate(sender, "usage-cmd-remove", null, true);
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

        if (!container.getOwner().equals(player.getUniqueId()) && !player.hasPermission("united.storage.admin")) {
            Messenger.sendMessageTemplate(player, "error-not-owner", null, true);
            return;
        }

        if (plugin.getDataManager().removeStorageContainer(container)) {
            Messenger.sendMessageTemplate(player, "success-remove-container", null, true);
        } else {
            Messenger.sendMessageTemplate(player, "error-remove-container", null, true);
        }

    }

}
