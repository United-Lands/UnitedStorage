package org.unitedlands.storage.gui.filter;

import org.unitedlands.UnitedLib;

import java.util.*;
import java.util.stream.Collectors;

public final class ItemGrouper {

    private ItemGrouper() {}

    private static List<String> cachedItems = null;

    private static final Set<String> MODIFIERS = Set.of(
        "SMOOTH", "CHISELED", "CUT", "POLISHED", "CRACKED", "MOSSY",
        "CARVED", "STRIPPED", "WAXED", "INFESTED", "COBBLED", "DEAD",
        "POTTED", "WET", "DRIED", "SUSPICIOUS", "EXPOSED", "WEATHERED",
        "OXIDIZED", "LIGHT"
    );

    public static List<String> getAllItems() {
        if (cachedItems == null) {
            cachedItems = new ArrayList<>(UnitedLib.getInstance().getItemFactory().getItemList());
            Collections.sort(cachedItems);
        }
        return cachedItems;
    }

    public static List<String> filtered(String searchTerm) {
        if (searchTerm.isEmpty()) return getAllItems();
        var lower = searchTerm.toLowerCase();
        return getAllItems().stream()
                .filter(n -> n.toLowerCase().contains(lower))
                .collect(Collectors.toList());
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
