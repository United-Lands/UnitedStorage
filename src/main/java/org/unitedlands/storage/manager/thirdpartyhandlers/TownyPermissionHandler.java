package org.unitedlands.storage.manager.thirdpartyhandlers;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.unitedlands.storage.UnitedStorage;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;

public class TownyPermissionHandler {

    private final UnitedStorage plugin;

    public TownyPermissionHandler(UnitedStorage plugin) {
        this.plugin = plugin;
    }

    public boolean isPlacementAllowed(Player player, Location location) {

        TownyAPI townyAPI = TownyAPI.getInstance();

        boolean placeInWilderness = plugin.getConfig().getBoolean("settings.towny.place-in-wilderness", false);
        boolean placeInOwnTown = plugin.getConfig().getBoolean("settings.towny.place-in-own-town", true);
        boolean placeInTrustedTown = plugin.getConfig().getBoolean("settings.towny.place-in-trusted-town", true);
        boolean placeInUntrustedTown = plugin.getConfig().getBoolean("settings.towny.place-in-untrusted-town", false);

        if (townyAPI.isWilderness(location)) {
            return placeInWilderness;
        }

        TownBlock townBlock = townyAPI.getTownBlock(location);
        if (townBlock == null)
            return false;

        try {
            Town town = townBlock.getTown();
            Resident resident = townyAPI.getResident(player);

            if (resident == null)
                return false;

            // Player owns this town
            if (resident.hasTown() && resident.getTown().equals(town)) {
                return placeInOwnTown;
            }

            // Player is trusted in town
            if (town.hasTrustedResident(resident)) {
                return placeInTrustedTown;
            }

            // Trusted in plot
            if (townBlock.hasTrustedResident(resident)) {
                return placeInTrustedTown;
            }

            // Trusted (friend of plot owner)
            if (townBlock.hasResident() && townBlock.getResident().hasFriend(resident)) {
                return placeInTrustedTown;
            }

            // Untrusted
            return placeInUntrustedTown;

        } catch (TownyException e) {
            return false;
        }
    }
}
