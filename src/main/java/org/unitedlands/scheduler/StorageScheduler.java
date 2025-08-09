package org.unitedlands.scheduler;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.unitedlands.UnitedStorage;

public class StorageScheduler {

    private final UnitedStorage plugin;

    private BukkitTask storageCheckerTask;

    public StorageScheduler(UnitedStorage plugin) {
        this.plugin = plugin;
    }

    public void startChecks() {

        var enabled = plugin.getConfig().getBoolean("settings.enabled", false);
        if (!enabled)
            return;

        plugin.getLogger().info("Starting storage checks...");

        var frequency = plugin.getConfig().getLong("settings.tick-frequency", 10L);
        if (frequency <= 0)
            frequency = 1L;

        storageCheckerTask = new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getStorageManager().checkSorters();
            }
        }.runTaskTimer(plugin, 0L, frequency);
    }

    public void stopChecks() {
        plugin.getLogger().info("Stopping storage checks...");
        storageCheckerTask.cancel();
    }

}
