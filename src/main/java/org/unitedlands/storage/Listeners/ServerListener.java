package org.unitedlands.storage.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.unitedlands.storage.UnitedStorage;

public class ServerListener implements Listener {
    private final UnitedStorage plugin;

    public ServerListener(UnitedStorage plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getVisualisationManager().removeViewer(event.getPlayer());
    }
}
