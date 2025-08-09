package org.unitedlands.manager.thirdpartyhandlers;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.unitedlands.UnitedStorage;

public abstract class BaseItemHandler implements IItemHandler {

    @SuppressWarnings("unused")
    protected final UnitedStorage plugin;

    public BaseItemHandler(UnitedStorage plugin) {
        this.plugin = plugin;
    }

    @Override
    public abstract boolean isItemInInventory(Inventory inventory, ItemStack item);
}
