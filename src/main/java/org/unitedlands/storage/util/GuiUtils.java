package org.unitedlands.storage.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class GuiUtils {

    private GuiUtils() {}

    public static Component text(String t, NamedTextColor c) {
        return Component.text(t, c).decoration(TextDecoration.ITALIC, false);
    }

    public static Component text(String t, TextColor c) {
        return Component.text(t, c).decoration(TextDecoration.ITALIC, false);
    }

    public static Component gradient(String t, TextColor from, TextColor to) {
        return MiniMessage.miniMessage().deserialize("<gradient:" + from.asHexString() + ":" + to.asHexString() + ">" + t);
    }

    public static String formatName(String raw) {
        return Arrays.stream(raw.split("_"))
                .map(w -> w.isEmpty() ? "" : Character.toUpperCase(w.charAt(0)) + w.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
