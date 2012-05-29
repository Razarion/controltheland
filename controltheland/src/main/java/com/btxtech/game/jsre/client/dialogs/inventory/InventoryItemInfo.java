package com.btxtech.game.jsre.client.dialogs.inventory;

import java.io.Serializable;
import java.util.Map;

/**
 * User: beat
 * Date: 29.05.12
 * Time: 00:20
 */
public class InventoryItemInfo implements Serializable {
    private String inventoryItemName;
    private int inventoryItemId;
    private Map<InventoryArtifactInfo, Integer> artifacts;

    /**
     * Used by GWT
     */
    InventoryItemInfo() {
    }

    public InventoryItemInfo(String inventoryItemName, int inventoryItemId, Map<InventoryArtifactInfo, Integer> artifacts) {
        this.inventoryItemName = inventoryItemName;
        this.inventoryItemId = inventoryItemId;
        this.artifacts = artifacts;
    }

    public String getInventoryItemName() {
        return inventoryItemName;
    }

    public int getInventoryItemId() {
        return inventoryItemId;
    }

    public boolean hasArtifacts() {
        return artifacts != null && !artifacts.isEmpty();
    }

    public Map<InventoryArtifactInfo, Integer> getArtifacts() {
        return artifacts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InventoryItemInfo that = (InventoryItemInfo) o;

        return inventoryItemId == that.inventoryItemId;
    }

    @Override
    public int hashCode() {
        return inventoryItemId;
    }
}
