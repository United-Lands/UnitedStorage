package org.unitedlands.commands.handlers.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedStorage;
import org.unitedlands.commands.handlers.base.BaseCommandHandler;
import org.unitedlands.util.Formatter;
import org.unitedlands.util.Messenger;
import org.unitedlands.util.Utilities;

public class StorageCmdInfo extends BaseCommandHandler {

    public StorageCmdInfo(UnitedStorage plugin) {
        super(plugin);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            Messenger.sendMessageListTemplate(sender, "usage-cmd-info", null, true);
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
                Messenger.sendMessageListTemplate(player, "sorter-info",
                        Map.of("target-count", targets.size() + "", "overflow-count", overflows.size() + "",
                                "sorter-state", container.getState().toString(), "owner", ownerName),
                        false);
                break;
            case TARGET:
                var targetParent = plugin.getDataManager().getSorterContainer(container.getParent());
                var mode = container.getMode().toString();
                var items = "-";
                if (!container.getFilter().isEmpty())
                {
                    items = String.join(", ", container.getFilter());
                }

                Messenger.sendMessageListTemplate(player, "target-info",
                        Map.of("sorter-loc", Formatter.formatLocation(targetParent.getLocation()), "owner", ownerName,
                        "items", items, "mode", mode),
                        false);
                break;
            case OVERFLOW:
                var overflowParent = plugin.getDataManager().getSorterContainer(container.getParent());
                Messenger.sendMessageListTemplate(player, "overflow-info",
                        Map.of("sorter-loc", Formatter.formatLocation(overflowParent.getLocation()), "owner", ownerName),
                        false);
                break;
        }

    }

}
