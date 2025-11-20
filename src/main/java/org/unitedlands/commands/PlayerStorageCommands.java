package org.unitedlands.commands;

import org.unitedlands.UnitedStorage;
import org.unitedlands.classes.BaseCommandExecutor;
import org.unitedlands.commands.handlers.storage.StorageCmdCreate;
import org.unitedlands.commands.handlers.storage.StorageCmdInfo;
import org.unitedlands.commands.handlers.storage.StorageCmdItemSubhandler;
import org.unitedlands.commands.handlers.storage.StorageCmdRemove;
import org.unitedlands.commands.handlers.storage.StorageCmdStart;
import org.unitedlands.commands.handlers.storage.StorageCmdStop;
import org.unitedlands.commands.handlers.storage.StorageCmdToggleDisplay;
import org.unitedlands.interfaces.IMessageProvider;

public class PlayerStorageCommands extends BaseCommandExecutor<UnitedStorage> {


    public PlayerStorageCommands(UnitedStorage plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerHandlers() {
        handlers.put("create", new StorageCmdCreate(plugin, messageProvider));
        handlers.put("remove", new StorageCmdRemove(plugin, messageProvider));
        handlers.put("info", new StorageCmdInfo(plugin, messageProvider));
        handlers.put("start", new StorageCmdStart(plugin, messageProvider));
        handlers.put("stop", new StorageCmdStop(plugin, messageProvider));
        handlers.put("toggledisplay", new StorageCmdToggleDisplay(plugin, messageProvider));
        handlers.put("targetitem", new StorageCmdItemSubhandler(plugin, messageProvider));
    }

   
}
