package org.unitedlands.storage;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.unitedlands.storage.Listeners.BlockListener;
import org.unitedlands.storage.Listeners.StorageListeners;
import org.unitedlands.storage.commands.AdminCommands;
import org.unitedlands.storage.commands.PlayerStorageCommands;
import org.unitedlands.storage.manager.DataManager;
import org.unitedlands.storage.manager.StorageManager;
import org.unitedlands.storage.manager.VisualisationManager;
import org.unitedlands.storage.manager.thirdpartyhandlers.TownyPermissionHandler;
import org.unitedlands.storage.scheduler.StorageScheduler;
import org.unitedlands.storage.util.MessageProvider;
import org.unitedlands.utils.Logger;

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

        Logger.log("****************************", "UnitedStorage");
        Logger.log("    | |__  o _|_ _  _|   ", "UnitedStorage");
        Logger.log("    |_|| | |  |_(/_(_|   ", "UnitedStorage");
        Logger.log("     __             _    ", "UnitedStorage");
        Logger.log("    (_ _|_ _  __ _ (_| _ ", "UnitedStorage");
        Logger.log("    __) |_(_) | (_|__|(/_", "UnitedStorage");
        Logger.log("****************************", "UnitedStorage");

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
            Logger.log("Towny found, adding Towny permission manager.", "UnitedStorage");
            townyPermissionManager = new TownyPermissionHandler(this);
            usingTowny = true;
        } else {
            Logger.log("Towny not found, disabling Towny permission checks.", "UnitedStorage");
        }

        dataManager = new DataManager(this);
        dataManager.loadData();

        getServer().getPluginManager().registerEvents(new StorageListeners(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this, messageProvider), this);

        Logger.log("UnitedStorage initialized.", "UnitedStorage");
    }

    @Override
    public void onDisable() {
        Logger.log("UnitedStorage unloaded.", "UnitedStorage");
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
