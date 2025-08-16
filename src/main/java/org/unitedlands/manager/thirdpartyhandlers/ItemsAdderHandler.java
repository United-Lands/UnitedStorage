package org.unitedlands.manager.thirdpartyhandlers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.unitedlands.UnitedStorage;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;

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

    @Override
    public boolean isValidItem(String itemName) {
        CustomStack customItem = CustomStack.getInstance(itemName);
        if (customItem != null) {
            return true;
        } else {
            try {
                var vanillaMaterial = Material.valueOf(itemName);
                if (vanillaMaterial != null)
                    return true;
            } catch (Exception ignore) {
                return false;
            }
        }

        return false;
    }

    @Override
    public String getFilterName(ItemStack itemStack) {
        CustomStack customItem = CustomStack.byItemStack(itemStack);
        if (customItem != null) {
            return customItem.getNamespacedID();
        } else {
            return itemStack.getType().toString();
        }
    }

    @Override
    public List<String> getItemList() {
        var items = Arrays.stream(Material.values())
                    .map(Enum::name) // gets the name as a String
                    .collect(Collectors.toList());
        var customItems = ItemsAdder.getAllItems().stream().filter(i -> !i.getNamespace().startsWith("_")).map(i -> i.getNamespacedID()).collect(Collectors.toList());
        items.addAll(customItems);
        return items;
    }

}
