package org.unitedlands.storage.manager;

import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.unitedlands.storage.UnitedStorage;
import org.unitedlands.storage.objects.StorageContainer;
import org.unitedlands.storage.objects.StorageMode;
import org.unitedlands.storage.util.ParticleSettings;

public class VisualisationManager {

    private final UnitedStorage plugin;

    private Set<Player> viewers = new HashSet<>();
    private BukkitTask displayTask = null;

    public VisualisationManager(UnitedStorage plugin) {
        this.plugin = plugin;
    }

    public boolean isViewer(Player player) {
        return viewers.contains(player);
    }

    public void addViewer(Player player) {
        viewers.add(player);

        if (displayTask == null)
            startVisualisation();
    }

    public void removeViewer(Player player) {
        viewers.remove(player);

        if (viewers.isEmpty())
            stopVisualisation();
    }

    public void startVisualisation() {

        if (viewers.isEmpty())
            return;

        var config = plugin.getConfig();

        var particlesSorter       = ParticleSettings.load(config, "settings.visualisation.sorter");
        var particlesTargetAuto   = ParticleSettings.load(config, "settings.visualisation.target-auto");
        var particlesTargetManual = ParticleSettings.load(config, "settings.visualisation.target-manual");
        var particlesOverflow     = ParticleSettings.load(config, "settings.visualisation.overflow");

        this.displayTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> viewers.forEach(player -> {

            var dataManager = plugin.getDataManager();

            dataManager.getSorterContainersByOwner(player.getUniqueId()).forEach(sorter -> {

                dataManager.getTargetContainersForSorter(sorter.getUuid()).forEach(target -> {
                    var particle = target.getMode() == StorageMode.AUTOMATIC
                            ? particlesTargetAuto
                            : particlesTargetManual;

                    spawnContainer(player, particle, target);
                });

                dataManager.getOverflowContainersForSorter(sorter.getUuid()).forEach(overflow -> {
                    spawnContainer(player, particlesOverflow, overflow);
                });

                spawnContainer(player, particlesSorter, sorter);
            });

        }), 0, 20L);

        plugin.getLogger().info("Storage visualisation task started.");

    }

    public void stopVisualisation() {
        if (displayTask != null) {
            displayTask.cancel();
            displayTask = null;
        }

        plugin.getLogger().info("Storage visualisation task stopped.");
    }

    private void spawnFaces(ParticleSettings settings, Player player, Location location) {
        if (location == null)
            return;

        var particle = settings.particle();
        var count    = settings.count();
        var offset   = settings.offset();

        var faces = new Location[6];

        faces[0] = location.clone().add(-offset, 0.5, 0.5);
        faces[1] = location.clone().add(1 + offset, 0.5, 0.5);
        faces[2] = location.clone().add(0.5, 0.5, -offset);
        faces[3] = location.clone().add(0.5, 0.5, 1 + offset);
        faces[4] = location.clone().add(0.5, -offset, 0.5);
        faces[5] = location.clone().add(0.5, 1 + offset, 0.5);

        for (Location face : faces) {
            player.spawnParticle(particle, face, count, 0, 0, 0, 0);
        }

    }

    private void spawnContainer(Player player, ParticleSettings settings, StorageContainer container) {
        spawnFaces(settings, player, container.getLocation());
        spawnFaces(settings, player, container.getLocation2());
    }

}
