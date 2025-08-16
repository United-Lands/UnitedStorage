package org.unitedlands.manager.thirdpartyhandlers;

import java.util.List;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface IItemHandler {
    List<String> getItemList();
    boolean isValidItem(String itemName);
    String getFilterName(ItemStack itemStack);
    boolean isItemInInventory(Inventory inventory, ItemStack item);
}
