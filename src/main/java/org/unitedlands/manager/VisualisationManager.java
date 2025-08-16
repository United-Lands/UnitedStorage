package org.unitedlands.manager;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.unitedlands.UnitedStorage;
import org.unitedlands.objects.StorageMode;

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

        String targetAutoParticleName = plugin.getConfig().getString("settings.visualisation.target-auto.particle",
                "HAPPY_VILLAGER");
        int targetAutoParticleCount = plugin.getConfig().getInt("settings.visualisation.target-auto.count", 1);
        double targetAutoParticleOffset = plugin.getConfig().getDouble("settings.visualisation.target-auto.offset", 0);
        Particle targetAutoParticle = Particle.valueOf(targetAutoParticleName);

        String targetManualParticleName = plugin.getConfig().getString("settings.visualisation.target-manual.particle",
                "HAPPY_VILLAGER");
        int targetManualParticleCount = plugin.getConfig().getInt("settings.visualisation.target-manual.count", 1);
        double targetManualParticleOffset = plugin.getConfig().getDouble("settings.visualisation.target-manual.offset",
                0);
        Particle targetManualParticle = Particle.valueOf(targetManualParticleName);

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
                            if (target.getMode() == StorageMode.AUTOMATIC) {
                                spawnFaces(targetAutoParticleCount, targetAutoParticleOffset, targetAutoParticle,
                                        player,
                                        target.getLocation());
                                spawnFaces(targetAutoParticleCount, targetAutoParticleOffset, targetAutoParticle,
                                        player,
                                        target.getLocation2());
                            } else {
                                spawnFaces(targetManualParticleCount, targetManualParticleOffset, targetManualParticle,
                                        player,
                                        target.getLocation());
                                spawnFaces(targetManualParticleCount, targetManualParticleOffset, targetManualParticle,
                                        player,
                                        target.getLocation2());
                            }
                        }

                        var playerOverflows = dataManager.getOverflowContainersForSorter(sorter.getUuid());
                        for (var overflow : playerOverflows) {
                            spawnFaces(overflowParticleCount, overflowParticleOffset, overflowParticle, player,
                                    overflow.getLocation());
                            spawnFaces(overflowParticleCount, overflowParticleOffset, overflowParticle, player,
                                    overflow.getLocation2());
                        }

                        spawnFaces(sorterParticleCount, sorterParticleOffset, sorterParticle, player,
                                sorter.getLocation());
                        spawnFaces(sorterParticleCount, sorterParticleOffset, sorterParticle, player,
                                sorter.getLocation2());
                    }
                }
            }

        };
        displayTask.runTaskTimer(plugin, 0, 20L);

        plugin.getLogger().info("Storage visualisation task started.");

    }

    private void spawnFaces(int particleCount, double particleOffset, Particle particle,
            Player player, Location location) {

        if (location == null)
            return;

        Location[] faces = new Location[6];

        faces[0] = location.clone().add(-particleOffset, 0.5, 0.5);
        faces[1] = location.clone().add(1 + particleOffset, 0.5, 0.5);
        faces[2] = location.clone().add(0.5, 0.5, -particleOffset);
        faces[3] = location.clone().add(0.5, 0.5, 1 + particleOffset);
        faces[4] = location.clone().add(0.5, -particleOffset, 0.5);
        faces[5] = location.clone().add(0.5, 1 + particleOffset, 0.5);

        for (int i = 0; i < faces.length; i++) {
            player.spawnParticle(particle, faces[i], particleCount, 0, 0, 0, 0);
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
