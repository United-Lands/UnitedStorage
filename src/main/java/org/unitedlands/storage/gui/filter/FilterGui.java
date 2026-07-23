package org.unitedlands.storage.gui.filter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.unitedlands.storage.UnitedStorage;
import org.unitedlands.storage.objects.StorageContainer;
import org.unitedlands.utils.Messenger;

import org.unitedlands.storage.util.ItemBuilder;

import java.util.*;

public class FilterGui implements InventoryHolder {

    public static final Map<UUID, FilterGui> AWAITING_SEARCH = new HashMap<>();

    static final int PAGE_SIZE   = 45;
    static final int SLOT_PREV   = 45;
    static final int SLOT_ACTION = 47;
    static final int SLOT_CENTER = 49;
    static final int SLOT_SEARCH = 51;
    static final int SLOT_NEXT   = 53;

    static final View FILTER  = new FilterView();
    static final View BROWSE  = new BrowseView();
    static final View CONFIRM = new ConfirmView();

    private final UnitedStorage    plugin;
    private final Player           player;
    private final StorageContainer container;

    private View    currentView    = FILTER;
    private int     filterPage     = 0;
    private int     browsePage     = 0;
    private String  searchTerm     = "";
    private boolean awaitingSearch = false;

    private Inventory openInventory;

    private FilterGui(UnitedStorage plugin, Player player, StorageContainer container) {
        this.plugin    = plugin;
        this.player    = player;
        this.container = container;
    }

    @Override
    public @NotNull Inventory getInventory() { return openInventory; }

    public static void open(UnitedStorage plugin, Player player, StorageContainer container) {
        AWAITING_SEARCH.remove(player.getUniqueId());
        var gui = new FilterGui(plugin, player, container);
        gui.openView(FILTER);
    }

    public void openView(View view) {
        currentView   = view;
        filterPage    = Math.max(0, Math.min(filterPage, getFilterPageCount() - 1));
        browsePage    = Math.max(0, Math.min(browsePage, getBrowsePageCount() - 1));
        openInventory = view.build(this);
        player.openInventory(openInventory);
    }

    public void handleClick(int slot, ClickType clickType) {
        currentView.handleClick(this, slot, clickType);
    }

    public void prevFilterPage() { if (filterPage > 0)                        { filterPage--; openView(FILTER); } }
    public void nextFilterPage() { if (filterPage < getFilterPageCount() - 1) { filterPage++; openView(FILTER); } }
    public void prevBrowsePage() { if (browsePage > 0)                        { browsePage--; openView(BROWSE); } }
    public void nextBrowsePage() { if (browsePage < getBrowsePageCount() - 1) { browsePage++; openView(BROWSE); } }

    public void addFilterItem(String name)    { container.addFilterItem(name); save(); }
    public void removeFilterItem(String name) { container.removeFilterItem(name); save(); }

    public void addFilterGroup(String keyword) {
        ItemGrouper.getAllItems().stream()
                .filter(i -> ItemGrouper.containsKeyword(i, keyword))
                .forEach(container::addFilterItem);
        save();
    }

    public void clearFilter() { container.clearFilter(); save(); }

    public void promptSearch() {
        awaitingSearch = true;
        player.closeInventory();
        var timeout = plugin.getConfig().getInt("filter-time-out", 20);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!AWAITING_SEARCH.containsKey(player.getUniqueId())) return;
            awaitingSearch = false;
            AWAITING_SEARCH.remove(player.getUniqueId());
            Messenger.sendMessage(player, plugin.getMessageProvider().get("messages.search-timeout"), null, plugin.getMessageProvider().get("messages.prefix"));
        }, timeout * 20L);
        AWAITING_SEARCH.put(player.getUniqueId(), this);
        Messenger.sendMessage(player, plugin.getMessageProvider().get("messages.search-prompt"), null, plugin.getMessageProvider().get("messages.prefix"));
    }

    public void applySearch(String term) {
        awaitingSearch = false;
        AWAITING_SEARCH.remove(player.getUniqueId());
        searchTerm = term.equals(".") ? "" : term;
        browsePage = 0;
        openView(BROWSE);
    }

    public int     getFilterPage()      { return filterPage; }
    public int     getBrowsePage()      { return browsePage; }
    public String  getSearchTerm()      { return searchTerm; }
    public boolean isFilterEmpty()      { return container.getFilter().isEmpty(); }
    public int     getFilterCount()     { return container.getFilter().size(); }
    public boolean isInFilter(String n) { return container.getFilter().contains(n); }
    public boolean isAwaitingSearch()   { return awaitingSearch; }

    public int getFilterPageCount() {
        return Math.max(1, (int) Math.ceil(buildableFilter().size() / (double) PAGE_SIZE));
    }

    public int getBrowsePageCount() {
        return Math.max(1, (int) Math.ceil(buildableItems().size() / (double) PAGE_SIZE));
    }

    public List<String> getSortedFilter() {
        var list = new ArrayList<>(container.getFilter());
        Collections.sort(list);
        return list;
    }

    List<String> buildableFilter() {
        return getSortedFilter().stream()
                .filter(name -> ItemBuilder.forMaterial(name) != null)
                .toList();
    }

    List<String> buildableItems() {
        return ItemGrouper.filtered(searchTerm).stream()
                .filter(name -> ItemBuilder.forMaterial(name) != null)
                .toList();
    }

    public void cleanup() { AWAITING_SEARCH.remove(player.getUniqueId()); }

    private void save() { plugin.getDataManager().saveStorageContainerFile(container); }
}
