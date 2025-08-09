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
import org.unitedlands.commands.handlers.base.BaseCommandHandler;
import org.unitedlands.objects.StorageContainer;
import org.unitedlands.objects.StorageContainerType;
import org.unitedlands.util.Formatter;
import org.unitedlands.util.Messenger;
import org.unitedlands.util.Utilities;

public class StorageCmdCreate extends BaseCommandHandler {

    public StorageCmdCreate(UnitedStorage plugin) {
        super(plugin);
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
            Messenger.sendMessageListTemplate(sender, "usage-cmd-create", null, true);
            return;
        }

        var player = (Player) sender;
        var block = Utilities.getTargetBlock(player, 6);

        if (block == null) {
            Messenger.sendMessageTemplate(player, "error-no-chest-in-los", null, true);
            return;
        }

        if (!(block.getType() == Material.CHEST)) {
            Messenger.sendMessageTemplate(player, "error-no-chest-in-los", null, true);
            return;
        }

        var location = block.getLocation();

        if (plugin.isUsingTowny()) {
            if (!plugin.getTownyPermissionManager().isPlacementAllowed(player, location)) {
                Messenger.sendMessageTemplate(player, "error-create-towny-permissions", null, true);
                return;
            }
        }

        var existingContainer = plugin.getDataManager().getStorageContainerAtLocation(location);
        if (existingContainer != null) {
            Messenger.sendMessageTemplate(player, "error-container-in-location",
                    Map.of("storage-type", existingContainer.getType().toString().toLowerCase()), true);
            return;
        }

        if (args[0].equalsIgnoreCase("sorterchest")) {

            if (plugin.getDataManager().isSorterInMinimumDistance(location)) {
                Messenger.sendMessageTemplate(player, "error-sorter-too-close", null, true);
                return;
            }

            handleSorterCreate(player, block, location);
            return;

        } else {

            var closestSorter = plugin.getDataManager().getSorterInMaximumDistance(location);
            if (closestSorter == null) {
                Messenger.sendMessageTemplate(player, "error-sorter-too-far", null, true);
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
        Messenger.sendMessageListTemplate(sender, "usage-cmd-create", null, true);

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
                Messenger.sendMessageTemplate(player, "error-too-many-sorters", Map.of("max", maxSorters + ""), true);
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

        Messenger.sendMessageTemplate(player, "success-create-sorter", null, true);
    }

    private void handleTargetCreate(Player player, Block block, StorageContainer closestSorter, Location location) {

        if (!closestSorter.getOwner().equals(player.getUniqueId())) {
            Messenger.sendMessageTemplate(player, "error-not-sorter-owner", null, true);
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
                Messenger.sendMessageTemplate(player, "error-too-many-targets", Map.of("max", maxTargets + ""), true);
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

        Messenger.sendMessageTemplate(player, "success-create-target",
                Map.of("sorter-loc", Formatter.formatLocation(closestSorter.getLocation())), true);
    }

    private void handleOverflowCreate(Player player, Block block, StorageContainer closestSorter, Location location) {

        if (!closestSorter.getOwner().equals(player.getUniqueId())) {
            Messenger.sendMessageTemplate(player, "error-not-sorter-owner", null, true);
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
                Messenger.sendMessageTemplate(player, "error-too-many-overflows", Map.of("max", maxOverflows + ""),
                        true);
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

        Messenger.sendMessageTemplate(player, "success-create-overflow",
                Map.of("sorter-loc", Formatter.formatLocation(closestSorter.getLocation())), true);
    }

}
