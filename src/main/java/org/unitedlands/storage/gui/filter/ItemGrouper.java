package org.unitedlands.storage.gui.filter;

import org.bukkit.inventory.ItemStack;
import org.unitedlands.UnitedLib;

import java.util.*;

public final class ItemGrouper {

    private ItemGrouper() {}

    private static LinkedHashMap<String, ItemStack> cache = null;

    private static final Set<String> MODIFIERS = Set.of(
        "SMOOTH", "CHISELED", "CUT", "POLISHED", "CRACKED", "MOSSY",
        "CARVED", "STRIPPED", "WAXED", "INFESTED", "COBBLED", "DEAD",
        "POTTED", "WET", "DRIED", "SUSPICIOUS", "EXPOSED", "WEATHERED",
        "OXIDIZED", "LIGHT"
    );

    private static LinkedHashMap<String, ItemStack> buildCache() {
        var factory = UnitedLib.getInstance().getItemFactory();
        var names   = new ArrayList<>(factory.getItemList());
        Collections.sort(names);

        var map = new LinkedHashMap<String, ItemStack>(names.size());
        for (var name : names) {
            var lower = name.toLowerCase();
            if (lower.contains("icon") || lower.contains("stage"))
                continue;

            var stack = factory.getItemStack(name, 1);
            if (stack != null && stack.getItemMeta() != null)
                map.put(name, stack);
        }
        return map;
    }

    private static LinkedHashMap<String, ItemStack> getCache() {
        if (cache == null) cache = buildCache();
        return cache;
    }

    public static ItemStack getCachedStack(String name) {
        return getCache().get(name);
    }

    public static List<String> getAllItems() {
        return new ArrayList<>(getCache().keySet());
    }

    public static List<String> filtered(String searchTerm) {
        if (searchTerm.isEmpty()) return getAllItems();
        var lower = searchTerm.toLowerCase();
        return getCache().keySet().stream()
                .filter(n -> n.toLowerCase().contains(lower))
                .toList();
    }

    public static String groupKeyword(String itemName) {
        for (var seg : itemName.split("_")) {
            if (!MODIFIERS.contains(seg)) return seg;
        }
        return itemName.split("_")[0];
    }

    public static boolean containsKeyword(String itemName, String keyword) {
        for (var seg : itemName.split("_")) {
            if (seg.equals(keyword)) return true;
        }
        return false;
    }
}
