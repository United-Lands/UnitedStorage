package org.unitedlands.storage.manager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.unitedlands.storage.objects.StorageContainer;
import org.unitedlands.storage.objects.StorageContainerType;
import org.unitedlands.storage.UnitedStorage;
import org.unitedlands.storage.util.JsonUtils;

public class DataManager {

    private final UnitedStorage plugin;

    private HashMap<UUID, StorageContainer> sorters = new HashMap<>();
    private HashMap<UUID, Set<StorageContainer>> targets = new HashMap<>();
    private HashMap<UUID, Set<StorageContainer>> overflows = new HashMap<>();

    public DataManager(UnitedStorage plugin) {
        this.plugin = plugin;
    }

    public void loadData() {

        sorters = new HashMap<>();
        targets = new HashMap<>();
        overflows = new HashMap<>();

        String directoryPath = File.separator + "containers";
        File directory = new File(plugin.getDataFolder(), directoryPath);

        File[] filesList = directory.listFiles();

        int loadCount = 0;
        if (filesList != null) {
            for (File file : filesList) {
                StorageContainer container = loadStorageContainer(file);
                if (container != null) {
                    registerStorageContainer(container);
                    loadCount++;
                } else {
                    plugin.getLogger().severe("Error loading container file " + file.getName());
                }
            }
        }

        plugin.getLogger().info(loadCount + " storage containers loaded.");

        plugin.getScheduler().startChecks();
    }

    public boolean isSorterInMinimumDistance(Location location) {
        var minDistance = plugin.getConfig().getInt("settings.sorter-min-distance", 0);
        for (var sorter : sorters.values()) {
            if (sorter.getLocation().distanceSquared(location) < minDistance * minDistance)
                return true;
        }
        return false;
    }

    public Set<StorageContainer> getAllStorageContainers() {
        Set<StorageContainer> combined = new HashSet<>();
        combined.addAll(sorters.values());
        for (Set<StorageContainer> set : targets.values()) {
            combined.addAll(set);
        }
        for (Set<StorageContainer> set : overflows.values()) {
            combined.addAll(set);
        }
        return combined;
    }

    public StorageContainer getStorageContainerAtLocation(Location location) {
        for (var sorter : sorters.values()) {
            if (sorter.getLocation().equals(location) || (sorter.getLocation2() != null && sorter.getLocation2().equals(location)))
                return sorter;
        }
        for (var targetList : targets.values()) {
            for (var target : targetList) {
                if (target.getLocation().equals(location) || (target.getLocation2() != null && target.getLocation2().equals(location)))
                    return target;
            }
        }
        for (var overflowList : overflows.values()) {
            for (var overflow : overflowList) {
                if (overflow.getLocation().equals(location) || (overflow.getLocation2() != null && overflow.getLocation2().equals(location)))
                    return overflow;
            }
        }
        return null;
    }

    public StorageContainer getSorterContainer(UUID uuid) {
        return sorters.get(uuid);
    }

    public Set<StorageContainer> getSorterContainersByOwner(UUID uuid) {
        return sorters.values().stream().filter(c -> c.getOwner().equals(uuid)).collect(Collectors.toSet());
    }

    public StorageContainer getSorterContainerAtLocation(Location location) {
        for (var sorter : sorters.values()) {
            if (sorter.getLocation().equals(location) || (sorter.getLocation2() != null && sorter.getLocation2().equals(location)))
                return sorter;
        }
        return null;
    }

    public Set<StorageContainer> getTargetContainersForSorter(UUID sorterId) {
        return targets.getOrDefault(sorterId, new HashSet<>());
    }

    public Set<StorageContainer> getOverflowContainersForSorter(UUID sorterId) {
        return overflows.getOrDefault(sorterId, new HashSet<>());
    }

    public StorageContainer getSorterInMaximumDistance(Location location, int maxDistance) {
        for (var sorter : sorters.values()) {
            if (sorter.getLocation().distanceSquared(location) < maxDistance * maxDistance)
                return sorter;
        }
        return null;
    }

    public void registerStorageContainer(StorageContainer container) {
        if (container.getType() == StorageContainerType.SORTER) {
            sorters.put(container.getUuid(), container);
        } else if (container.getType() == StorageContainerType.TARGET) {
            var targetList = targets.computeIfAbsent(container.getParent(), k -> new HashSet<>());
            targetList.add(container);
        } else if (container.getType() == StorageContainerType.OVERFLOW) {
            var overflowList = overflows.computeIfAbsent(container.getParent(), k -> new HashSet<>());
            overflowList.add(container);
        }
    }

    public boolean removeStorageContainer(StorageContainer container) {

        if (container.getType() == StorageContainerType.SORTER) {

            var linkedTargetChestList = targets.get(container.getUuid());
            if (!(linkedTargetChestList == null) && !linkedTargetChestList.isEmpty()) {
                for (var linkedTargetChest : linkedTargetChestList) {
                    if (!deleteStorageContainerFile(linkedTargetChest))
                        return false;
                }
                targets.remove(container.getUuid());
            }

            var linkedOverflowChestList = overflows.get(container.getUuid());
            if (!(linkedOverflowChestList == null) && !linkedOverflowChestList.isEmpty()) {
                for (var linkedOverflowChest : linkedOverflowChestList) {
                    if (!deleteStorageContainerFile(linkedOverflowChest))
                        return false;
                }
                overflows.remove(container.getUuid());
            }
        }

        unregisterStorageContainer(container);
        return deleteStorageContainerFile(container);
    }

    public void unregisterStorageContainer(StorageContainer container) {
        if (container.getType() == StorageContainerType.SORTER) {
            sorters.remove(container.getUuid());
        } else if (container.getType() == StorageContainerType.TARGET) {
            var targetList = targets.get(container.getParent());
            if (targetList != null)
                targetList.remove(container);
        } else if (container.getType() == StorageContainerType.OVERFLOW) {
            var overflowList = overflows.get(container.getParent());
            if (overflowList != null)
                overflowList.remove(container);
        }
    }

    public boolean saveStorageContainerFile(StorageContainer container) {
        var uuid = container.getUuid();
        var filePath = File.separator + "containers" + File.separator + uuid + ".json";

        File containerFile = new File(plugin.getDataFolder(), filePath);
        if (!containerFile.exists()) {
            containerFile.getParentFile().mkdirs();
            try {
                containerFile.createNewFile();
            } catch (IOException ex) {
                plugin.getLogger().severe(ex.getMessage());
            }
        }

        try {
            JsonUtils.saveObjectToFile(container, containerFile);
            return true;
        } catch (IOException ex) {
            plugin.getLogger().severe(ex.getMessage());
            return false;
        }
    }

    public boolean deleteStorageContainerFile(StorageContainer container) {
        var uuid = container.getUuid();

        String directoryPath = File.separator + "containers";
        File file = new File(plugin.getDataFolder(),
                directoryPath + File.separator + uuid + ".json");

        if (!file.exists()) {
            plugin.getLogger()
                    .warning("Attempted to delete file " + file.getAbsolutePath() + ", but it does not exist.");
            return true;
        }

        try {
            file.delete();
            return true;
        } catch (Exception ex) {
            plugin.getLogger().severe(ex.getMessage());
            return false;
        }
    }

    public StorageContainer loadStorageContainer(File file) {
        try {
            return JsonUtils.loadObjectFromFile(file, StorageContainer.class);
        } catch (IOException ex) {
            plugin.getLogger().severe(ex.getMessage());
            return null;
        }
    }

    public HashMap<UUID, StorageContainer> getSorters() {
        return sorters;
    }

    public HashMap<UUID, Set<StorageContainer>> getTargets() {
        return targets;
    }

    public HashMap<UUID, Set<StorageContainer>> getOverflows() {
        return overflows;
    }

}
