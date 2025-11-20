package org.unitedlands.commands;

import org.unitedlands.UnitedStorage;
import org.unitedlands.classes.BaseCommandExecutor;
import org.unitedlands.commands.handlers.admin.AdminCmdPurge;
import org.unitedlands.commands.handlers.admin.AdminCmdReload;
import org.unitedlands.commands.handlers.admin.AdminCmdValidate;
import org.unitedlands.interfaces.IMessageProvider;

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
