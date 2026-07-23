package org.unitedlands.storage.gui.filter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.unitedlands.storage.util.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.unitedlands.storage.gui.filter.FilterGui.*;
import static org.unitedlands.storage.util.GuiUtils.*;

final class BrowseView extends View {

    @Override
    Inventory build(FilterGui gui) {
        var items = gui.buildableItems();
        int page = gui.getBrowsePage();
        int total = gui.getBrowsePageCount();
        var label = gui.getSearchTerm().isEmpty() ? "all" : gui.getSearchTerm();

        var inv = Bukkit.createInventory(gui, 54,
                gradient("Browse", NamedTextColor.DARK_AQUA, NamedTextColor.AQUA)
                        .append(text(" [" + label + "]", NamedTextColor.GRAY)));

        int start = page * PAGE_SIZE;
        for (int i = 0; i < PAGE_SIZE && (start + i) < items.size(); i++) {
            var name = items.get(start + i);
            boolean inF = gui.isInFilter(name);

            List<Component> lore = new ArrayList<>();
            if (inF)
                lore.add(text("✔ Already in filter", NamedTextColor.GREEN));

            lore.add(text("Left-click  — add this item", NamedTextColor.GRAY));
            lore.add(text("Right-click — add all " + ItemGrouper.groupKeyword(name) + " items", NamedTextColor.YELLOW));

            inv.setItem(i, ItemBuilder.forMaterial(name).lore(lore).glow(inF).build());
        }

        fillNav(inv, page, total);
        inv.setItem(SLOT_ACTION, new ItemBuilder(Material.RED_DYE)
                .name("Back to Filter", NamedTextColor.RED)
                .build());

        addSearchButton(gui, inv);
        return inv;
    }

    @Override
    void handleClick(FilterGui gui, int slot, ClickType click) {
        switch (slot) {
            case SLOT_PREV -> gui.prevBrowsePage();
            case SLOT_NEXT -> gui.nextBrowsePage();
            case SLOT_ACTION -> gui.openView(FILTER);
            case SLOT_SEARCH -> gui.promptSearch();

            default -> {
                if (slot >= PAGE_SIZE) return;

                var items = gui.buildableItems();
                int idx = gui.getBrowsePage() * PAGE_SIZE + slot;
                if (idx >= items.size()) return;

                var name = items.get(idx);
                if (click.isRightClick())
                    gui.addFilterGroup(ItemGrouper.groupKeyword(name));
                else
                    gui.addFilterItem(name);

                gui.openView(BROWSE);
            }
        }
    }
}
