package org.unitedlands.manager.thirdpartyhandlers;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.unitedlands.UnitedStorage;

import dev.lone.itemsadder.api.CustomStack;

public class ItemsAdderHandler extends BaseItemHandler {

    public ItemsAdderHandler(UnitedStorage plugin) {
        super(plugin);
    }

    @Override
    public boolean isItemInInventory(Inventory inventory, ItemStack item) {
        CustomStack customItem = CustomStack.byItemStack(item);
        for (var contentItem : inventory.getContents()) {
            if (contentItem == null || contentItem.getType() == Material.AIR)
                continue;
            var contentCustomItem = CustomStack.byItemStack(contentItem);
            if (customItem != null && contentCustomItem != null) {
                if (customItem.getNamespacedID().equals(contentCustomItem.getNamespacedID()))
                    return true;
            } else if (customItem == null && contentCustomItem == null) {
                if (item.getType() == contentItem.getType())
                    return true;
            }
        }
        return false;
    }

}
