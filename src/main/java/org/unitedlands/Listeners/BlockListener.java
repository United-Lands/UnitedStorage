package org.unitedlands.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.unitedlands.UnitedStorage;
import org.unitedlands.objects.StorageContainer;
import org.unitedlands.objects.StorageContainerType;
import org.unitedlands.util.MessageProvider;
import org.unitedlands.utils.Messenger;

public class BlockListener implements Listener {

    private final UnitedStorage plugin;
    private final MessageProvider messageProvider;

    public BlockListener(UnitedStorage plugin, MessageProvider messageProvider) {
        this.plugin = plugin;
        this.messageProvider = messageProvider;
    }

    @EventHandler(ignoreCancelled = true)
    private void onBlockBreak(BlockBreakEvent event) {
        var block = event.getBlock();
        if (block.getType() != Material.CHEST)
            return;

        var player = event.getPlayer();
        var location = block.getLocation();

        var container = plugin.getDataManager().getStorageContainerAtLocation(location);
        if (container != null) {
            if (container.getType() == StorageContainerType.SORTER) {
                Messenger.sendMessage(player, messageProvider.get("messages.error-cant-break-sorter"), null, messageProvider.get("messages.prefix"));
                event.setCancelled(true);
            } else {
                removeContainer(player, container);
            }

        }
    }

    private void removeContainer(Player player, StorageContainer container) {
        if (plugin.getDataManager().removeStorageContainer(container)) {
            Messenger.sendMessage(player, messageProvider.get("messages.success-remove-container"), null, messageProvider.get("messages.prefix"));
        } else {
            Messenger.sendMessage(player, messageProvider.get("messages.error-remove-container"), null, messageProvider.get("messages.prefix"));
        }
    }

}
