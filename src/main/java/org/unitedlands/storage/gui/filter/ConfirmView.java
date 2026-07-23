package org.unitedlands.storage.gui.filter;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.unitedlands.storage.util.GuiHead;
import org.unitedlands.storage.util.ItemBuilder;

import static org.unitedlands.storage.gui.filter.FilterGui.FILTER;
import static org.unitedlands.storage.util.GuiUtils.*;

final class ConfirmView extends View {

    @Override
    Inventory build(FilterGui gui) {
        int count = gui.getFilterCount();
        var inv   = Bukkit.createInventory(gui, 27, gradient("⚠ Clear Filter", NamedTextColor.DARK_RED, NamedTextColor.RED));

        var red  = ItemBuilder.pane(Material.RED_STAINED_GLASS_PANE);
        for (int i = 0; i < 9; i++) inv.setItem(i, red);
        for (int i = 18; i < 27; i++) inv.setItem(i, red);

        var gray = ItemBuilder.pane(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 9; i < 17; i++) inv.setItem(i, gray);

        inv.setItem(10, GuiHead.decision(false));

        inv.setItem(13, new ItemBuilder(Material.BOOK)
                .name("Clear Filter?", NamedTextColor.WHITE)
                .lore(text("This will remove all " + count + " item" + (count == 1 ? "" : "s") + " from the filter.", NamedTextColor.GRAY))
                .build());

        inv.setItem(16, GuiHead.decision(true));

        return inv;
    }

    @Override
    void handleClick(FilterGui gui, int slot, ClickType click) {
        if (slot == 10)      gui.openView(FILTER);
        else if (slot == 16) { gui.clearFilter(); gui.openView(FILTER); }
    }
}
