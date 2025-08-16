package org.unitedlands.manager.thirdpartyhandlers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.unitedlands.UnitedStorage;

public class VanillaItemHandler extends BaseItemHandler {

    public VanillaItemHandler(UnitedStorage plugin) {
        super(plugin);
    }

    @Override
    public boolean isItemInInventory(Inventory inventory, ItemStack item) {
        return inventory.contains(item.getType());
    }

    @Override
    public boolean isValidItem(String itemName) {
        var vanillaMaterial = Material.valueOf(itemName);
        if (vanillaMaterial != null)
            return true;
        return false;
    }

    @Override
    public String getFilterName(ItemStack itemStack) {
        return itemStack.getType().toString();
    }

    @Override
    public List<String> getItemList() {
        return Arrays.stream(Material.values())
                .map(Enum::name) // gets the name as a String
                .collect(Collectors.toList());
    }

}
