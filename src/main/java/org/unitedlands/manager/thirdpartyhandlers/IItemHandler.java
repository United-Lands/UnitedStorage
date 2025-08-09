package org.unitedlands.manager.thirdpartyhandlers;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface IItemHandler {
    boolean isItemInInventory(Inventory inventory, ItemStack item);
}
