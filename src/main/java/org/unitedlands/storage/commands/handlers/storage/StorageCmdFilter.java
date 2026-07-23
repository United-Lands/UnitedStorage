package org.unitedlands.storage.commands.handlers.storage;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.storage.UnitedStorage;
import org.unitedlands.storage.gui.filter.FilterGui;
import org.unitedlands.storage.objects.StorageContainerType;
import org.unitedlands.storage.util.Utilities;
import org.unitedlands.utils.Messenger;

public class StorageCmdFilter extends BaseCommandHandler<UnitedStorage> {

    public StorageCmdFilter(UnitedStorage plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        var player = (Player) sender;

        var block = Utilities.getTargetBlock(player, 6);
        if (block == null || block.getType() != Material.CHEST) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-no-chest-in-los"), null, messageProvider.get("messages.prefix"));
            return;
        }

        var container = plugin.getDataManager().getStorageContainerAtLocation(block.getLocation());
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

        FilterGui.open(plugin, player, container);
    }
}
