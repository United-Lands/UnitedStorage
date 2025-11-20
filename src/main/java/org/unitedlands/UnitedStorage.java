package org.unitedlands;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.unitedlands.Listeners.BlockListener;
import org.unitedlands.Listeners.StorageListeners;
import org.unitedlands.commands.AdminCommands;
import org.unitedlands.commands.PlayerStorageCommands;
import org.unitedlands.manager.DataManager;
import org.unitedlands.manager.StorageManager;
import org.unitedlands.manager.VisualisationManager;
import org.unitedlands.manager.thirdpartyhandlers.TownyPermissionHandler;
import org.unitedlands.scheduler.StorageScheduler;
import org.unitedlands.util.MessageProvider;

public class UnitedStorage extends JavaPlugin {

    private MessageProvider messageProvider;

    private DataManager dataManager;
    private StorageManager storageManager;
    private VisualisationManager visualisationManager;
    private TownyPermissionHandler townyPermissionManager;
    private StorageScheduler scheduler;

    private boolean usingTowny = false;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        messageProvider = new MessageProvider(getConfig());

        getLogger().info("****************************");
        getLogger().info("    | |__  o _|_ _  _|   ");
        getLogger().info("    |_|| | |  |_(/_(_|   ");
        getLogger().info("     __             _    ");
        getLogger().info("    (_ _|_ _  __ _ (_| _ ");
        getLogger().info("    __) |_(_) | (_|__|(/_");
        getLogger().info("****************************");

        var playerStorageCmds = new PlayerStorageCommands(this, messageProvider);
        getCommand("storage").setTabCompleter(playerStorageCmds);
        getCommand("storage").setExecutor(playerStorageCmds);
        var adminCmds = new AdminCommands(this, messageProvider);
        getCommand("unitedstorage").setExecutor(adminCmds);
        getCommand("unitedstorage").setTabCompleter(adminCmds);

        scheduler = new StorageScheduler(this);
        storageManager = new StorageManager(this, messageProvider);
        visualisationManager = new VisualisationManager(this);

        Plugin towny = Bukkit.getPluginManager().getPlugin("Towny");
        if (towny != null && towny.isEnabled()) {
            getLogger().info("Towny found, adding Towny permission manager.");
            townyPermissionManager = new TownyPermissionHandler(this);
            usingTowny = true;
        } else {
            getLogger().info("Towny not found, disabling Towny permission checks.");
        }

        dataManager = new DataManager(this);
        dataManager.loadData();

        getServer().getPluginManager().registerEvents(new StorageListeners(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this, messageProvider), this);

        getLogger().info("UnitedStorage initialized.");
    }

    @Override
    public void onDisable() {
        getLogger().info("UnitedStorage unloaded.");
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public StorageScheduler getScheduler() {
        return scheduler;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public boolean isUsingTowny() {
        return usingTowny;
    }

    public TownyPermissionHandler getTownyPermissionManager() {
        return townyPermissionManager;
    }

    public VisualisationManager getVisualisationManager() {
        return visualisationManager;
    }

    public MessageProvider getMessageProvider() {
        return messageProvider;
    }

}
