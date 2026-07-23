package org.unitedlands.storage.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class GuiHead {

    private GuiHead() {
    }

    private static final String ARROW_LEFT = "http://textures.minecraft.net/texture/cdc9e4dcfa4221a1fadc1b5b2b11d8beeb57879af1c42362142bae1edd5";
    private static final String ARROW_RIGHT = "http://textures.minecraft.net/texture/956a3618459e43b287b22b7e235ec699594546c6fcd6dc84bfca4cf30ab9311";

    private static final String CONFIRM = "http://textures.minecraft.net/texture/4312ca4632def5ffaf2eb0d9d7cc7b55a50c4e3920d90372aab140781f5dfbc4";
    private static final String DECLINE = "http://textures.minecraft.net/texture/beb588b21a6f98ad1ff4e085c552dcb050efc9cab427f46048f18fc803475f7";

    public static ItemStack arrow(boolean forward) {
        return ItemBuilder.skull(
                forward ? ARROW_RIGHT : ARROW_LEFT,
                GuiUtils.text(forward ? "Next Page" : "Previous Page", NamedTextColor.WHITE),
                forward ? Material.SPECTRAL_ARROW : Material.ARROW
        );
    }

    public static ItemStack decision(boolean confirm) {
        return ItemBuilder.skull(
                confirm ? CONFIRM : DECLINE,
                GuiUtils.text(confirm ? "✔ Confirm" : "✖ Cancel", confirm ? NamedTextColor.GREEN : NamedTextColor.RED),
                confirm ? Material.GREEN_WOOL : Material.RED_WOOL
        );
    }

    public static ItemStack fromUrl(String textureUrl, Component displayName, Material fallback) {
        return ItemBuilder.skull(textureUrl, displayName, fallback);
    }
}
