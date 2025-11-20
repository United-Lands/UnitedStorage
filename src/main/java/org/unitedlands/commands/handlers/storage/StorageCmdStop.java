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

public class StorageCmdStop extends BaseCommandHandler<UnitedStorage> {

    public StorageCmdStop(UnitedStorage plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            Messenger.sendMessage(sender, messageProvider.getList("messages.usage-cmd-stop"), null, messageProvider.get("messages.prefix"));
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

        container.setState(StorageContainerState.DISABLED);
        plugin.getDataManager().saveStorageContainerFile(container);

        Messenger.sendMessage(sender, messageProvider.get("messages.success-sorter-toggle"), Map.of("sorter-state", "<red>disabled</red>"), messageProvider.get("messages.prefix"));
    }

}
