package org.unitedlands.commands.handlers.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.DoubleChestInventory;
import org.unitedlands.UnitedStorage;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.objects.StorageContainer;
import org.unitedlands.objects.StorageContainerType;
import org.unitedlands.util.Formatter;
import org.unitedlands.utils.Messenger;
import org.unitedlands.util.Utilities;

public class StorageCmdCreate extends BaseCommandHandler<UnitedStorage> {

    public StorageCmdCreate(UnitedStorage plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    private List<String> completions = List.of("sorterchest", "targetchest", "overflowchest");

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return completions;
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            Messenger.sendMessage(sender, messageProvider.getList("messages.usage-cmd-create"), null, messageProvider.get("messages.prefix"));
            return;
        }

        var player = (Player) sender;
        var block = Utilities.getTargetBlock(player, 6);

        if (block == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-no-chest-in-los"), null, messageProvider.get("messages.prefix"));
            return;
        }

        if (!(block.getType() == Material.CHEST)) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-no-chest-in-los"), null, messageProvider.get("messages.prefix"));
            return;
        }

        var location = block.getLocation();

        if (plugin.isUsingTowny()) {
            if (!plugin.getTownyPermissionManager().isPlacementAllowed(player, location)) {
                Messenger.sendMessage(sender, messageProvider.get("messages.error-create-towny-permissions"), null, messageProvider.get("messages.prefix"));
                return;
            }
        }

        var existingContainer = plugin.getDataManager().getStorageContainerAtLocation(location);
        if (existingContainer != null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-container-in-location"), Map.of("storage-type", existingContainer.getType().toString().toLowerCase()), messageProvider.get("messages.prefix"));
            return;
        }

        if (args[0].equalsIgnoreCase("sorterchest")) {

            if (plugin.getDataManager().isSorterInMinimumDistance(location)) {
                Messenger.sendMessage(sender, messageProvider.get("messages.error-sorter-too-close"), null, messageProvider.get("messages.prefix"));
                return;
            }

            handleSorterCreate(player, block, location);
            return;

        } else {

            var maxDistance = plugin.getConfig().getInt("settings.target-max-distance", 0);
            var maxDistanceOverride = Utilities.getPermissionLimit(player, "united.storage.maxdistance");
            if (maxDistanceOverride != -1) {
                maxDistance = maxDistanceOverride;
            }

            var closestSorter = plugin.getDataManager().getSorterInMaximumDistance(location, maxDistance);
            if (closestSorter == null) {
                Messenger.sendMessage(sender, messageProvider.get("messages.error-sorter-too-far"), null, messageProvider.get("messages.prefix"));
                return;
            }

            if (args[0].equalsIgnoreCase("targetchest")) {
                handleTargetCreate(player, block, closestSorter, location);
                return;
            } else if (args[0].equalsIgnoreCase("overflowchest")) {
                handleOverflowCreate(player, block, closestSorter, location);
                return;
            }
        }

        // Fallback
        Messenger.sendMessage(sender, messageProvider.getList("messages.usage-cmd-create"), null, messageProvider.get("messages.prefix"));

    }

    private void handleSorterCreate(Player player, Block block, Location location) {

        var maxSorters = plugin.getConfig().getInt("settings.default-max-sorters-per-player", 0);
        var maxSortersOverride = Utilities.getPermissionLimit(player, "united.storage.maxsorters");
        if (maxSortersOverride != -1) {
            maxSorters = maxSortersOverride;
        }

        if (maxSorters != -1) {
            var playerOwnedSorters = plugin.getDataManager().getSorterContainersByOwner(player.getUniqueId());
            if (playerOwnedSorters != null && playerOwnedSorters.size() >= maxSorters) {
                Messenger.sendMessage(player, messageProvider.get("messages.error-too-many-sorters"), Map.of("max", maxSorters + ""), messageProvider.get("messages.prefix"));
                return;
            }
        }

        var container = new StorageContainer();
        container.setUuid(UUID.randomUUID());
        container.setType(StorageContainerType.SORTER);
        container.setOwner(player.getUniqueId());
        container.setLastInteractionTime(System.currentTimeMillis());

        var inventory = ((Container) block.getState()).getInventory();
        if (inventory instanceof DoubleChestInventory doubleChestInventory) {
            container.setLocation(doubleChestInventory.getLeftSide().getLocation());
            container.setLocation2(doubleChestInventory.getRightSide().getLocation());
        } else {
            container.setLocation(block.getLocation());
        }

        plugin.getDataManager().registerStorageContainer(container);
        plugin.getDataManager().saveStorageContainerFile(container);

        Messenger.sendMessage(player, messageProvider.get("messages.success-create-sorter"), Map.of("sorter-loc", Formatter.formatLocation(container.getLocation())), messageProvider.get("messages.prefix"));
    }

    private void handleTargetCreate(Player player, Block block, StorageContainer closestSorter, Location location) {

        if (!closestSorter.getOwner().equals(player.getUniqueId())) {
            Messenger.sendMessage(player, messageProvider.get("messages.error-not-sorter-owner"), null, messageProvider.get("messages.prefix"));
            return;
        }

        var maxTargets = plugin.getConfig().getInt("settings.default-max-targets-per-sorter", 0);
        var maxTargetsOverride = Utilities.getPermissionLimit(player, "united.storage.maxtargets");
        if (maxTargetsOverride != -1) {
            maxTargets = maxTargetsOverride;
        }

        if (maxTargets != -1) {
            var linkedTargets = plugin.getDataManager().getTargetContainersForSorter(closestSorter.getUuid()).stream()
                    .filter(c -> c.getOwner().equals(player.getUniqueId())).collect(Collectors.toList());
            if (linkedTargets != null && linkedTargets.size() >= maxTargets) {
                Messenger.sendMessage(player, messageProvider.get("messages.error-too-many-targets"), Map.of("max", maxTargets + ""), messageProvider.get("messages.prefix"));
                return;
            }
        }

        var container = new StorageContainer();
        container.setUuid(UUID.randomUUID());
        container.setType(StorageContainerType.TARGET);
        container.setOwner(player.getUniqueId());
        container.setParent(closestSorter.getUuid());

        var inventory = ((Container) block.getState()).getInventory();
        if (inventory instanceof DoubleChestInventory doubleChestInventory) {
            container.setLocation(doubleChestInventory.getLeftSide().getLocation());
            container.setLocation2(doubleChestInventory.getRightSide().getLocation());
        } else {
            container.setLocation(block.getLocation());
        }

        plugin.getDataManager().registerStorageContainer(container);
        plugin.getDataManager().saveStorageContainerFile(container);

        Messenger.sendMessage(player, messageProvider.get("messages.success-create-target"), Map.of("sorter-loc", Formatter.formatLocation(closestSorter.getLocation())), messageProvider.get("messages.prefix"));
    }

    private void handleOverflowCreate(Player player, Block block, StorageContainer closestSorter, Location location) {

        if (!closestSorter.getOwner().equals(player.getUniqueId())) {
            Messenger.sendMessage(player, messageProvider.get("messages.error-not-sorter-owner"), null, messageProvider.get("messages.prefix"));
            return;
        }

        var maxOverflows = plugin.getConfig().getInt("settings.default-max-overflows-per-sorter", 0);
        var maxTargetsOverride = Utilities.getPermissionLimit(player, "united.storage.maxoverflows");
        if (maxTargetsOverride != -1) {
            maxOverflows = maxTargetsOverride;
        }

        if (maxOverflows != -1) {
            var linkedOverflows = plugin.getDataManager().getOverflowContainersForSorter(closestSorter.getUuid())
                    .stream().filter(c -> c.getOwner().equals(player.getUniqueId())).collect(Collectors.toList());
            if (linkedOverflows != null && linkedOverflows.size() >= maxOverflows) {
                Messenger.sendMessage(player, messageProvider.get("messages.error-too-many-overflows"), Map.of("max", maxOverflows + ""), messageProvider.get("messages.prefix"));
                return;
            }
        }

        var container = new StorageContainer();
        container.setUuid(UUID.randomUUID());
        container.setLocation(location);
        container.setType(StorageContainerType.OVERFLOW);
        container.setOwner(player.getUniqueId());
        container.setParent(closestSorter.getUuid());

        var inventory = ((Container) block.getState()).getInventory();
        if (inventory instanceof DoubleChestInventory doubleChestInventory) {
            container.setLocation(doubleChestInventory.getLeftSide().getLocation());
            container.setLocation2(doubleChestInventory.getRightSide().getLocation());
        } else {
            container.setLocation(block.getLocation());
        }

        plugin.getDataManager().registerStorageContainer(container);
        plugin.getDataManager().saveStorageContainerFile(container);

        Messenger.sendMessage(player, messageProvider.get("messages.success-create-overflow"), Map.of("sorter-loc", Formatter.formatLocation(closestSorter.getLocation())), messageProvider.get("messages.prefix"));
    }

}
