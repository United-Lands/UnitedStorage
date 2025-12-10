package org.unitedlands.storage.commands.handlers.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.storage.UnitedStorage;
import org.unitedlands.storage.util.Formatter;
import org.unitedlands.utils.Messenger;
import org.unitedlands.storage.util.Utilities;

public class StorageCmdInfo extends BaseCommandHandler<UnitedStorage> {

    public StorageCmdInfo(UnitedStorage plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            Messenger.sendMessage(sender, messageProvider.getList("messages.usage-cmd-info"), null, messageProvider.get("messages.prefix"));
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

        String ownerName = null;
        var ownerPlayer = Bukkit.getOfflinePlayer(container.getOwner());
        if (ownerPlayer != null) {
            ownerName = ownerPlayer.getName();
        }

        if (ownerName == null)
            ownerName = "-";

        switch (container.getType()) {
            case SORTER:
                var targets = plugin.getDataManager().getTargetContainersForSorter(container.getUuid());
                var overflows = plugin.getDataManager().getOverflowContainersForSorter(container.getUuid());
                Messenger.sendMessage(sender, messageProvider.getList("messages.sorter-info"), Map.of("target-count", targets.size() + "", "overflow-count", overflows.size() + "",
                                "sorter-state", container.getState().toString(), "owner", ownerName));
                break;
            case TARGET:
                var targetParent = plugin.getDataManager().getSorterContainer(container.getParent());
                var mode = container.getMode().toString();
                var items = "-";
                if (!container.getFilter().isEmpty())
                {
                    items = String.join(", ", container.getFilter());
                }
                Messenger.sendMessage(sender, messageProvider.getList("messages.target-info"), Map.of("sorter-loc", Formatter.formatLocation(targetParent.getLocation()), "owner", ownerName,
                        "items", items, "mode", mode));
                break;
            case OVERFLOW:
                var overflowParent = plugin.getDataManager().getSorterContainer(container.getParent());
                Messenger.sendMessage(sender, messageProvider.getList("messages.overflow-info"), Map.of("sorter-loc", Formatter.formatLocation(overflowParent.getLocation()), "owner", ownerName));
                break;
        }

    }

}
