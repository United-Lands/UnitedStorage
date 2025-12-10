package org.unitedlands.storage.commands.handlers.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.storage.UnitedStorage;
import org.unitedlands.utils.Messenger;

public class StorageCmdToggleDisplay extends BaseCommandHandler<UnitedStorage> {

    public StorageCmdToggleDisplay(UnitedStorage plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            Messenger.sendMessage(sender, messageProvider.getList("messages.usage-cmd-toggledisplay"), null, messageProvider.get("messages.prefix"));
            return;
        }

        var player = (Player) sender;

        if (!plugin.getVisualisationManager().isViewer(player)) {
            plugin.getVisualisationManager().addViewer(player);
            Messenger.sendMessage(sender, messageProvider.getList("messages.toggledisplay-info"), Map.of("display-state", "<green>enabled</green>"), messageProvider.get("messages.prefix"));
        } else {
            plugin.getVisualisationManager().removeViewer(player);
            Messenger.sendMessage(sender, messageProvider.getList("messages.toggledisplay-info"), Map.of("display-state", "<red>disabled</red>"), messageProvider.get("messages.prefix"));
        }

    }

}
