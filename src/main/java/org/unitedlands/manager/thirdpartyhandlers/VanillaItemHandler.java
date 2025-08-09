package org.unitedlands.manager.thirdpartyhandlers;

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
    
}
