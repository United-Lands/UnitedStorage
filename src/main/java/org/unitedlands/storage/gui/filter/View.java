package org.unitedlands.storage.gui.filter;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.unitedlands.storage.util.GuiHead;
import org.unitedlands.storage.util.ItemBuilder;

import java.util.List;

import static org.unitedlands.storage.gui.filter.FilterGui.*;
import static org.unitedlands.storage.util.GuiUtils.text;

abstract class View {
    abstract Inventory build(FilterGui gui);
    abstract void handleClick(FilterGui gui, int slot, ClickType click);

    protected void fillNav(Inventory inv, int page, int total) {
        var filler = ItemBuilder.pane(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = PAGE_SIZE; i < inv.getSize(); i++)
            inv.setItem(i, filler);

        if (page > 0)
            inv.setItem(SLOT_PREV, GuiHead.arrow(false));

        if (page < total - 1)
            inv.setItem(SLOT_NEXT, GuiHead.arrow(true));
    }

    protected void addSearchButton(FilterGui gui, Inventory inv) {
        var label = gui.getSearchTerm().isEmpty()
                ? text("No search term set", NamedTextColor.WHITE)
                : text("Active: " + gui.getSearchTerm(), NamedTextColor.WHITE);

        inv.setItem(SLOT_SEARCH, new ItemBuilder(Material.SPYGLASS)
                .name("Search", NamedTextColor.YELLOW)
                .lore(List.of(label, text("Click to change", NamedTextColor.GRAY)))
                .build());
    }
}
