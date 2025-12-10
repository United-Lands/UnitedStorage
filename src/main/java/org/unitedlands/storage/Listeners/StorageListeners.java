package org.unitedlands.storage.Listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.unitedlands.storage.objects.StorageContainerState;
import org.unitedlands.storage.UnitedStorage;

public class StorageListeners implements Listener {

    private final UnitedStorage plugin;

    public StorageListeners(UnitedStorage plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (event.getHand() != EquipmentSlot.HAND)
            return;

        var block = event.getClickedBlock();
        if (block == null || block.getType() != Material.CHEST)
            return;

        var sorter = plugin.getDataManager().getSorterContainerAtLocation(block.getLocation());
        if (sorter == null)
            return;

        if (sorter.getState() == StorageContainerState.SLEEPING) {
            sorter.setLastInteractionTime(System.currentTimeMillis());
            sorter.setState(StorageContainerState.ENABLED);
            plugin.getDataManager().saveStorageContainerFile(sorter);
            
            plugin.getLogger().info("Waking up sorter " + sorter.getUuid());
        }
    }
}
