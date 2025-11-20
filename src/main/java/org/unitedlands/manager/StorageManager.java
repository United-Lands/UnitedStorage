package org.unitedlands.manager;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.unitedlands.UnitedLib;
import org.unitedlands.UnitedStorage;
import org.unitedlands.objects.StorageContainer;
import org.unitedlands.objects.StorageContainerState;
import org.unitedlands.objects.StorageMode;
import org.unitedlands.util.Formatter;
import org.unitedlands.util.MessageProvider;
import org.unitedlands.utils.Messenger;
import org.unitedlands.util.Utilities;

public class StorageManager {

    private final UnitedStorage plugin;
    private final MessageProvider messageProvider;

    private DataManager dataManager;

    public StorageManager(UnitedStorage plugin, MessageProvider messageProvider) {
        this.plugin = plugin;
        this.messageProvider = messageProvider;
    }

    public void checkSorters() {

        if (dataManager == null)
            dataManager = plugin.getDataManager();

        var sorters = dataManager.getSorters();

        for (var sorter : sorters.values()) {
            if (!sorter.isEnabled()) {
                continue;
            }

            var container = sorter.getContainer();
            var itemsInSorter = Utilities.getFirstNonAirItem(Utilities.getChestInventory(container));

            if (itemsInSorter.size() > 0) {

                var firstSorterItem = itemsInSorter.entrySet().iterator().next();
                // Move one item stack at a time from the sorter chest
                var firstItemStack = firstSorterItem.getValue();

                ItemStack itemStackToMove = null;
                boolean isEmptyShulkerBox = false;
                boolean fromShulkerBox = false;
                BlockStateMeta sourceShulkerMeta = null;
                ShulkerBox sourceShulkerBox = null;
                int shulkerInventoryIndex = 0;

                // Is the first item in the list a shulker box?
                if (firstItemStack.getType().toString().endsWith("SHULKER_BOX")) {

                    // We encountered a shulker box. We need to determine if we should move it
                    // because it is empty, or if we should sort its contents instead.
                    if (firstItemStack.getItemMeta() instanceof BlockStateMeta meta) {
                        BlockState state = meta.getBlockState();
                        if (state instanceof ShulkerBox shulkerBox) {
                            var itemsInShulker = Utilities.getFirstNonAirItem(shulkerBox.getInventory());
                            if (itemsInShulker.size() > 0) {
                                // The shulker box is not empty. Instead of moving the box itself, move the
                                // first item inside of the box.

                                var firstShulkerItemSet = itemsInShulker.entrySet().iterator().next();

                                fromShulkerBox = true;
                                sourceShulkerMeta = meta;
                                sourceShulkerBox = shulkerBox;
                                shulkerInventoryIndex = firstShulkerItemSet.getKey();

                                itemStackToMove = new ItemStack(firstShulkerItemSet.getValue());
                            } else {
                                // The shulker box is empty. Move it.
                                isEmptyShulkerBox = true;
                                itemStackToMove = new ItemStack(firstItemStack);
                            }
                        } else {
                            plugin.getLogger()
                                    .severe("Critical ShulkerBox meta error in shulker processing in sorter "
                                            + sorter.getUuid());
                        }
                    } else {
                        plugin.getLogger().severe(
                                "Critical BlockStateMeta error in shulker processing in sorter " + sorter.getUuid());
                    }

                } else {
                    // Not a shulker box, move the item normally.
                    itemStackToMove = new ItemStack(firstItemStack);
                }

                itemStackToMove = tryMovingItemStack(sorter, itemStackToMove, isEmptyShulkerBox);

                // See if there is still something left because the overflow was full. If so,
                // put the remaining items back and disable the sorter. Otherwise remove
                // the inital item stack.
                if (itemStackToMove != null) {
                    if (fromShulkerBox) {
                        // We moved an item froma shulker box inside of the sorter. We need to alter the
                        // shulker inventory, not the sorter inventory.
                        sourceShulkerBox.getInventory().setItem(shulkerInventoryIndex, new ItemStack(itemStackToMove));
                        sourceShulkerBox.update();
                        sourceShulkerMeta.setBlockState(sourceShulkerBox);
                        firstItemStack.setItemMeta(sourceShulkerMeta);
                    } else {
                        // We moved a regular item and need to update the sorter inventory.
                        var inventory = Utilities.getChestInventory(container);
                        inventory.setItem(firstSorterItem.getKey(), new ItemStack(itemStackToMove));
                    }
                    disableSorter(sorter);
                } else {
                    if (fromShulkerBox) {
                        // We moved an item from a shulker box inside of the sorter. We need to alter
                        // the shulker inventory, not the sorter inventory.
                        sourceShulkerBox.getInventory().setItem(shulkerInventoryIndex, new ItemStack(Material.AIR));
                        sourceShulkerBox.update();
                        sourceShulkerMeta.setBlockState(sourceShulkerBox);
                        firstItemStack.setItemMeta(sourceShulkerMeta);
                    } else {
                        // We moved a regular item and need to update the sorter inventory.
                        var inventory = Utilities.getChestInventory(container);
                        inventory.setItem(firstSorterItem.getKey(), new ItemStack(Material.AIR));
                    }
                    sorter.setLastInteractionTime(System.currentTimeMillis());
                }
            } else {
                // Disable empty sorters for efficiency after the idle time is up.
                setSorterSleeping(sorter);
            }
        }
    }

    private ItemStack tryMovingItemStack(StorageContainer sorter, ItemStack itemStackToMove,
            boolean isEmptyShulkerBox) {


        var itemFactory = UnitedLib.getInstance().getItemFactory();
        
        // Try to store item in a valid target chest, but skip shulkers (those always go
        // to overflow)
        if (!isEmptyShulkerBox) {
            var targetContainerList = dataManager.getTargetContainersForSorter(sorter.getUuid());
            if (targetContainerList != null && targetContainerList.size() > 0) {
                for (var target : targetContainerList) {
                    var targetContainer = target.getContainer();
                    if (target.getMode() == StorageMode.AUTOMATIC) {
                        if (itemFactory.isItemInInventory(Utilities.getChestInventory(targetContainer),
                                itemStackToMove)) {
                            var inventory = Utilities.getChestInventory(targetContainer);
                            var leftovers = inventory.addItem(itemStackToMove);
                            if (!leftovers.isEmpty()) {
                                itemStackToMove = new ItemStack(leftovers.get(0));
                            } else {
                                itemStackToMove = null;
                                break;
                            }
                        }
                    } else if (target.getMode() == StorageMode.MANUAL) {
                        var filter = target.getFilter();
                        var itemName = itemFactory.getFilterName(itemStackToMove);
                        if (filter.contains(itemName)) {
                            var inventory = Utilities.getChestInventory(targetContainer);
                            var leftovers = inventory.addItem(itemStackToMove);
                            if (!leftovers.isEmpty()) {
                                itemStackToMove = new ItemStack(leftovers.get(0));
                            } else {
                                itemStackToMove = null;
                                break;
                            }
                        }
                    }
                }
            }
        }

        // If items remain, try to store them in the overflow chests
        if (itemStackToMove != null) {
            var overflowContainerList = dataManager.getOverflowContainersForSorter(sorter.getUuid());
            if (overflowContainerList != null && overflowContainerList.size() > 0) {
                for (var overflow : overflowContainerList) {
                    var overflowContainer = overflow.getContainer();
                    var inventory = Utilities.getChestInventory(overflowContainer);
                    var leftovers = inventory.addItem(itemStackToMove);
                    if (!leftovers.isEmpty()) {
                        itemStackToMove = new ItemStack(leftovers.get(0));
                    } else {
                        itemStackToMove = null;
                        break;
                    }
                }
            }
        }
        return itemStackToMove;
    }

    private void setSorterSleeping(StorageContainer sorter) {

        var timeDifference = System.currentTimeMillis() - sorter.getLastInteractionTime();
        var timeBeforeSleep = plugin.getConfig().getLong("settings.idle-time-before-sleep", 0L) * 1000;

        if (timeDifference > timeBeforeSleep) {
            plugin.getLogger().info("Sending sorter " + sorter.getUuid() + " to sleep.");
            sorter.setState(StorageContainerState.SLEEPING);
            plugin.getDataManager().saveStorageContainerFile(sorter);
        }

    }

    private void disableSorter(StorageContainer sorter) {
        plugin.getLogger().info("Disabling sorter " + sorter.getUuid() + " due to overflow.");
        sorter.setState(StorageContainerState.DISABLED);

        UUID ownerId = sorter.getOwner();
        Player owner = Bukkit.getPlayer(ownerId);
        if (owner != null && owner.isOnline()) {
            Messenger.sendMessage(owner, messageProvider.get("messages.error-overflow-full"), Map.of("sorter-loc", Formatter.formatLocation(sorter.getLocation())), messageProvider.get("messages.prefix"));
        }

        plugin.getDataManager().saveStorageContainerFile(sorter);
    }

}
