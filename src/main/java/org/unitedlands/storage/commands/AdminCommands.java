package org.unitedlands.storage.commands;

import org.unitedlands.classes.BaseCommandExecutor;
import org.unitedlands.storage.commands.handlers.admin.AdminCmdPurge;
import org.unitedlands.storage.commands.handlers.admin.AdminCmdReload;
import org.unitedlands.storage.commands.handlers.admin.AdminCmdValidate;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.storage.UnitedStorage;

public class AdminCommands extends BaseCommandExecutor<UnitedStorage> {

    public AdminCommands(UnitedStorage plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerHandlers() {
        handlers.put("reload", new AdminCmdReload(plugin, messageProvider));
        handlers.put("validatedata", new AdminCmdValidate(plugin, messageProvider));
        handlers.put("purge", new AdminCmdPurge(plugin, messageProvider));
    }

}
