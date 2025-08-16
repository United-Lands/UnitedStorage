package org.unitedlands.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.unitedlands.UnitedStorage;
import org.unitedlands.objects.StorageContainer;
import org.unitedlands.objects.StorageContainerType;
import org.unitedlands.util.Messenger;

public class BlockListener implements Listener {

    private final UnitedStorage plugin;

    public BlockListener(UnitedStorage plugin) {
        this.plugin = plugin;
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
                Messenger.sendMessageTemplate(player, "error-cant-break-sorter", null, true);
                event.setCancelled(true);
            } else {
                removeContainer(player, container);
            }

        }
    }

    private void removeContainer(Player player, StorageContainer container) {
        if (plugin.getDataManager().removeStorageContainer(container)) {
            Messenger.sendMessageTemplate(player, "success-remove-container", null, true);
        } else {
            Messenger.sendMessageTemplate(player, "error-remove-container", null, true);
        }
    }

}
