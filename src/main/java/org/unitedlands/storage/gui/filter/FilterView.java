package org.unitedlands.storage.gui.filter;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.unitedlands.storage.util.ItemBuilder;

import java.util.List;

import static org.unitedlands.storage.gui.filter.FilterGui.*;
import static org.unitedlands.storage.util.GuiUtils.*;

final class FilterView extends View {

    @Override
    Inventory build(FilterGui gui) {
        var filter = gui.buildableFilter();
        int page   = gui.getFilterPage();
        int total  = gui.getFilterPageCount();

        var inv = Bukkit.createInventory(gui, 54,
                gradient("Filter", NamedTextColor.GOLD, NamedTextColor.YELLOW)
                        .append(text(" (" + filter.size() + " items)", NamedTextColor.GRAY)));

        int start = page * PAGE_SIZE;
        for (int i = 0; i < PAGE_SIZE && (start + i) < filter.size(); i++) {
            inv.setItem(i, ItemBuilder.forMaterial(filter.get(start + i))
                    .lore(text("Right-click to remove", NamedTextColor.RED))
                    .build());
        }

        fillNav(inv, page, total);

        inv.setItem(SLOT_CENTER, new ItemBuilder(Material.CHEST)
                .name("Browse Items", NamedTextColor.AQUA)
                .lore(text("Add items from all available materials", NamedTextColor.GRAY))
                .build());

        if (!gui.isFilterEmpty()) {
            inv.setItem(SLOT_ACTION, new ItemBuilder(Material.TNT)
                    .name("Clear All", NamedTextColor.RED)
                    .lore(text("Remove all filter items", NamedTextColor.GRAY))
                    .build());
        }

        addSearchButton(gui, inv);
        return inv;
    }

    @Override
    void handleClick(FilterGui gui, int slot, ClickType click) {
        switch (slot) {
            case SLOT_PREV   -> gui.prevFilterPage();
            case SLOT_NEXT   -> gui.nextFilterPage();
            case SLOT_CENTER -> gui.openView(BROWSE);
            case SLOT_SEARCH -> gui.promptSearch();
            case SLOT_ACTION -> { if (!gui.isFilterEmpty()) gui.openView(CONFIRM); }

            default -> {
                if (slot >= PAGE_SIZE || !click.isRightClick()) return;
                var filter = gui.buildableFilter();
                int idx    = gui.getFilterPage() * PAGE_SIZE + slot;

                if (idx < filter.size()) {
                    gui.removeFilterItem(filter.get(idx));
                    gui.openView(FILTER);
                }
            }
        }
    }
}
