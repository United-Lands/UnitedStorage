package org.unitedlands.storage.commands.handlers.storage;

import org.unitedlands.classes.BaseSubcommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.storage.UnitedStorage;

public class StorageCmdItemSubhandler extends BaseSubcommandHandler<UnitedStorage> {

    public StorageCmdItemSubhandler(UnitedStorage plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerSubHandlers() {
        subHandlers.put("add", new StorageCmdItemAdd(plugin, messageProvider));
        subHandlers.put("remove", new StorageCmdItemRemove(plugin, messageProvider));
        subHandlers.put("clear", new StorageCmdItemClear(plugin, messageProvider));
    }

}
