package org.unitedlands.objects;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Container;

import com.google.gson.annotations.Expose;

public class StorageContainer {

    @Expose
    private UUID uuid;
    @Expose
    private UUID owner;
    @Expose
    private UUID parent;
    @Expose
    private Location location;
    @Expose
    private Location location2;
    @Expose
    private StorageContainerType type;
    @Expose
    private StorageContainerState state = StorageContainerState.DISABLED;
    @Expose
    private Long lastInteractionTime = 0L;

    private Container container;

    public StorageContainer() {

    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public UUID getParent() {
        return parent;
    }

    public void setParent(UUID parent) {
        this.parent = parent;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation2() {
        return location2;
    }

    public void setLocation2(Location location) {
        this.location2 = location;
    }

    public StorageContainerType getType() {
        return type;
    }

    public void setType(StorageContainerType type) {
        this.type = type;
    }

    public boolean isEnabled() {
        return state == StorageContainerState.ENABLED;
    }

    public void setEnabled(boolean enabled) {
        if (enabled)
            state = StorageContainerState.ENABLED;
        else
            state = StorageContainerState.DISABLED;
    }

    public StorageContainerState getState() {
        return state;
    }

    public void setState(StorageContainerState state) {
        this.state = state;
    }

    public Container getContainer() {
        if (container == null && location != null) {
            var block = location.getBlock();
            if (block.getState() instanceof Container containerState) {
                container = containerState;
            }
        }
        return container;
    }

    public Long getLastInteractionTime() {
        return lastInteractionTime;
    }

    public void setLastInteractionTime(Long lastInteractionTime) {
        this.lastInteractionTime = lastInteractionTime;
    }

}
