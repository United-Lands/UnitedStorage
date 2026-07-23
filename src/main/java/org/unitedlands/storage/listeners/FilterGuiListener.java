package org.unitedlands.storage.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.unitedlands.storage.UnitedStorage;
import org.unitedlands.storage.gui.filter.FilterGui;

public class FilterGuiListener implements Listener {

    private final UnitedStorage plugin;

    public FilterGuiListener(UnitedStorage plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof FilterGui gui))
            return;

        event.setCancelled(true);
        if (event.getClickedInventory() != event.getView().getTopInventory() || event.getCurrentItem() == null)
            return;

        gui.handleClick(event.getSlot(), event.getClick());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof FilterGui gui))
            return;

        if (!gui.isAwaitingSearch())
            gui.cleanup();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChatLow(AsyncChatEvent event) {
        var gui = FilterGui.AWAITING_SEARCH.get(event.getPlayer().getUniqueId());
        if (gui == null || !gui.isAwaitingSearch())
            return;

        event.setCancelled(true);
        event.viewers().clear();

        var term   = PlainTextComponentSerializer.plainText().serialize(event.message());
        var player = event.getPlayer();
        var signed = event.signedMessage();

        Bukkit.getScheduler().runTask(plugin, () -> {
            try { player.deleteMessage(signed); } catch (Exception ignored) {}
            gui.applySearch(term);
        });
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChatLegacy(AsyncPlayerChatEvent event) {
        var gui = FilterGui.AWAITING_SEARCH.get(event.getPlayer().getUniqueId());
        if (gui == null || !gui.isAwaitingSearch())
            return;

        event.setCancelled(true);
        event.getRecipients().clear();
    }

}
