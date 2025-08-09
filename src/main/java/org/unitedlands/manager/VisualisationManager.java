package org.unitedlands.manager;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.unitedlands.UnitedStorage;

public class VisualisationManager {

    private final UnitedStorage plugin;

    private Set<Player> viewers = new HashSet<>();

    private BukkitRunnable displayTask = null;

    public VisualisationManager(UnitedStorage plugin) {
        this.plugin = plugin;
    }

    public boolean isViewer(Player player) {
        return viewers.contains(player);
    }

    public void addViewer(Player player) {
        if (!viewers.contains(player))
            viewers.add(player);

        if (displayTask == null)
            startVisualisation();
    }

    public void removeViewer(Player player) {
        if (viewers.contains(player)) {
            viewers.remove(player);
            if (viewers.isEmpty())
                stopVisualisation();
        }
    }

    public void startVisualisation() {

        if (viewers.isEmpty())
            return;

        String sorterParticleName = plugin.getConfig().getString("settings.visualisation.sorter.particle",
                "HAPPY_VILLAGER");
        int sorterParticleCount = plugin.getConfig().getInt("settings.visualisation.sorter.count", 1);
        double sorterParticleOffset = plugin.getConfig().getDouble("settings.visualisation.sorter.offset", 0);
        Particle sorterParticle = Particle.valueOf(sorterParticleName);

        String targetParticleName = plugin.getConfig().getString("settings.visualisation.target.particle",
                "HAPPY_VILLAGER");
        int targetParticleCount = plugin.getConfig().getInt("settings.visualisation.target.count", 1);
        double targetParticleOffset = plugin.getConfig().getDouble("settings.visualisation.target.offset", 0);
        Particle targetParticle = Particle.valueOf(targetParticleName);

        String overflowParticleName = plugin.getConfig().getString("settings.visualisation.overflow.particle",
                "HAPPY_VILLAGER");
        int overflowParticleCount = plugin.getConfig().getInt("settings.visualisation.overflow.count", 1);
        double overflowParticleOffset = plugin.getConfig().getDouble("settings.visualisation.overflow.offset", 0);
        Particle overflowParticle = Particle.valueOf(overflowParticleName);

        displayTask = new BukkitRunnable() {
            @Override
            public void run() {

                // Room visualisations

                for (Player player : viewers) {

                    var dataManager = plugin.getDataManager();
                    var playerSorters = dataManager.getSorterContainersByOwner(player.getUniqueId());

                    for (var sorter : playerSorters) {
                        var playerTargets = dataManager.getTargetContainersForSorter(sorter.getUuid());
                        for (var target : playerTargets) {

                            spawnCorners(targetParticleCount, targetParticleOffset, targetParticle, player,
                                    target.getLocation());
                            spawnCorners(targetParticleCount, targetParticleOffset, targetParticle, player,
                                    target.getLocation2());
                        }

                        var playerOverflows = dataManager.getOverflowContainersForSorter(sorter.getUuid());
                        for (var overflow : playerOverflows) {
                            spawnCorners(overflowParticleCount, overflowParticleOffset, overflowParticle, player,
                                    overflow.getLocation());
                            spawnCorners(overflowParticleCount, overflowParticleOffset, overflowParticle, player,
                                    overflow.getLocation2());
                        }

                        spawnCorners(sorterParticleCount, sorterParticleOffset, sorterParticle, player,
                                sorter.getLocation());
                        spawnCorners(sorterParticleCount, sorterParticleOffset, sorterParticle, player,
                                sorter.getLocation2());
                    }
                }
            }

        };
        displayTask.runTaskTimer(plugin, 0, 20L);

        plugin.getLogger().info("Storage visualisation task started.");

    }

    private void spawnCorners(int targetParticleCount, double targetParticleOffset, Particle targetParticle,
            Player player, Location location) {

        if (location == null)
            return;

        Location[] corners = new Location[8];

        corners[0] = location.clone().add(1, 0, 0);
        corners[1] = location.clone().add(0, 1, 0);
        corners[2] = location.clone().add(0, 0, 1);
        corners[3] = location.clone().add(1, 1, 0);
        corners[4] = location.clone().add(0, 1, 1);
        corners[5] = location.clone().add(1, 0, 1);
        corners[6] = location.clone().add(1, 1, 1);
        corners[7] = location.clone();

        for (int i = 0; i < corners.length; i++) {
            player.spawnParticle(targetParticle, corners[i], targetParticleCount,
                    targetParticleOffset,
                    targetParticleOffset, targetParticleOffset, 0);
        }

    }

    public void stopVisualisation() {
        if (displayTask != null) {
            displayTask.cancel();
            displayTask = null;
        }
        plugin.getLogger().info("Storage visualisation task stopped.");
    }

}
