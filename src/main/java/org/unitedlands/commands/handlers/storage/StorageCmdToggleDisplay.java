package org.unitedlands.commands.handlers.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedStorage;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.util.Messenger;

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
            Messenger.sendMessageListTemplate(sender, "usage-cmd-toggledisplay", null, true);
            return;
        }

        var player = (Player) sender;

        if (!plugin.getVisualisationManager().isViewer(player)) {
            plugin.getVisualisationManager().addViewer(player);
            Messenger.sendMessageTemplate(player, "toggledisplay-info", Map.of("display-state", "§aenabled"), true);
        } else {
            plugin.getVisualisationManager().removeViewer(player);
            Messenger.sendMessageTemplate(player, "toggledisplay-info", Map.of("display-state", "§4disabled"), true);
        }

    }

}
