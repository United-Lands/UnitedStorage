package org.unitedlands.storage.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.unitedlands.UnitedLib;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public final class ItemBuilder {

    private ItemStack item;
    private ItemMeta  meta;
    private boolean   glow;

    public ItemBuilder(Material mat) {
        item = new ItemStack(mat);
        meta = item.getItemMeta();
        if (meta == null) {
            item = new ItemStack(Material.PAPER);
            meta = item.getItemMeta();
        }
    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
        if (meta == null) {
            this.item = new ItemStack(Material.PAPER);
            this.meta = this.item.getItemMeta();
        }
    }

    public ItemBuilder name(String label, TextColor color) {
        meta.displayName(GuiUtils.text(label, color));
        return this;
    }

    public ItemBuilder name(Component component) {
        meta.displayName(component.decoration(TextDecoration.ITALIC, false));
        return this;
    }

    public ItemBuilder lore(Component... lines) {
        meta.lore(Stream.of(lines)
            .map(c -> c.decoration(TextDecoration.ITALIC, false)).toList());
        return this;
    }

    public ItemBuilder lore(List<Component> lines) {
        meta.lore(lines.stream()
            .map(c -> c.decoration(TextDecoration.ITALIC, false)).toList());
        return this;
    }

    public ItemBuilder glow() { this.glow = true; return this; }
    public ItemBuilder glow(boolean when) { if (when) this.glow = true; return this; }

    public ItemStack build() {
        item.setItemMeta(meta);
        if (glow) {
            item.addUnsafeEnchantment(Enchantment.EFFICIENCY, 1);
            var m = item.getItemMeta();
            m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(m);
        }
        return item;
    }

    public static ItemStack pane(Material mat) {
        return new ItemBuilder(mat).name(Component.empty()).build();
    }

    public static ItemBuilder forMaterial(String materialName) {
        var item = UnitedLib.getInstance().getItemFactory().getItemStack(materialName, 1);
        if (item == null)
            return null;

        return new ItemBuilder(item);
    }

    public static ItemStack skull(String textureUrl, Component displayName, Material fallback) {
        try {
            var skull    = new ItemStack(Material.PLAYER_HEAD);
            var sm       = (SkullMeta) skull.getItemMeta();
            var profile  = Bukkit.createProfile(UUID.randomUUID(), null);
            var textures = profile.getTextures();

            textures.setSkin(URI.create(textureUrl).toURL());
            profile.setTextures(textures);

            sm.setPlayerProfile(profile);
            sm.displayName(displayName.decoration(TextDecoration.ITALIC, false));

            skull.setItemMeta(sm);
            return skull;
        } catch (Exception e) {
            return new ItemBuilder(fallback).name(displayName).build();
        }
    }
}
