package org.unitedlands.commands.handlers.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedStorage;
import org.unitedlands.commands.handlers.base.BaseCommandHandler;
import org.unitedlands.util.Messenger;

public class StorageCmdToggleDisplay extends BaseCommandHandler {

    public StorageCmdToggleDisplay(UnitedStorage plugin) {
        super(plugin);
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
