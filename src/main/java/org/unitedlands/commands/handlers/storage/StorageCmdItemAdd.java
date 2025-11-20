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
import org.unitedlands.objects.StorageContainerType;
import org.unitedlands.utils.Messenger;
import org.unitedlands.util.Utilities;

public class StorageCmdItemAdd extends BaseCommandHandler<UnitedStorage> {

    public StorageCmdItemAdd(UnitedStorage plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return plugin.getItemHandler().getItemList();
        }
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            Messenger.sendMessage(sender, messageProvider.getList("messages.usage-cmd-item-add"), null, messageProvider.get("messages.prefix"));
            return;
        }
        
        var player = (Player) sender;

        var itemName = args[0];
        if (!plugin.getItemHandler().isValidItem(itemName))
        {
            Messenger.sendMessage(sender, messageProvider.get("messages.warning-unknown-item"), null, messageProvider.get("messages.prefix"));
        }

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

        container.addFilterItem(itemName);
        plugin.getDataManager().saveStorageContainerFile(container);

        Messenger.sendMessage(sender, messageProvider.get("messages.success-item-add"), Map.of("material", itemName), messageProvider.get("messages.prefix"));
    }

}
