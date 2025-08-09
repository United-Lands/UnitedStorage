package org.unitedlands.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.util.RayTraceResult;

public class Utilities {

    public static Block getTargetBlock(Player player, double maxDistance) {
        RayTraceResult result = player.rayTraceBlocks(maxDistance);
        if (result != null && result.getHitBlock() != null) {
            return result.getHitBlock();
        }
        return null;
    }

    public static LinkedHashMap<Integer, ItemStack> getFirstNonAirItem(Inventory inventory) {
        LinkedHashMap<Integer, ItemStack> result = new LinkedHashMap<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            var item = inventory.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                result.put(i, item);
                return result;
            }
        }
        return result;
    }

    public static int getPermissionLimit(Player player, String permission) {
        int limit = -1;
        if (!permission.endsWith("."))
            permission += ".";
        for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
            String perm = permInfo.getPermission();
            if (perm.startsWith(permission)) {
                try {
                    int value = Integer.parseInt(perm.substring(permission.length()));
                    if (value > limit)
                        limit = value;
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return limit;
    }

    public static boolean canFitItemSafely(Inventory sourceInventory, ItemStack item) {
        Inventory temp = Bukkit.createInventory(null, sourceInventory.getSize());
        temp.setContents(sourceInventory.getContents());
        Map<Integer, ItemStack> leftovers = temp.addItem(item.clone());
        return leftovers.isEmpty();
    }
}
