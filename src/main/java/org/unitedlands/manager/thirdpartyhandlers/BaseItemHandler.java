package org.unitedlands.manager.thirdpartyhandlers;

import java.util.List;

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
    public abstract List<String> getItemList();

    @Override
    public abstract boolean isValidItem(String itemName);

    @Override
    public abstract String getFilterName(ItemStack itemStack);

    @Override
    public abstract boolean isItemInInventory(Inventory inventory, ItemStack item);
}
