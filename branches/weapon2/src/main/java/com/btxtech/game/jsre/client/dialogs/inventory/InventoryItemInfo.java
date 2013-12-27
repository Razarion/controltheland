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
    private Integer baseItemTypeId;
    private int itemCount;
    private int itemFreeRange;
    private int goldAmount;
    private Integer crystalCost;

    /**
     * Used by GWT
     */
    InventoryItemInfo() {
    }

    public InventoryItemInfo(String inventoryItemName, int inventoryItemId, Map<InventoryArtifactInfo, Integer> artifacts, Integer baseItemTypeId, int itemCount, int itemFreeRange, int goldAmount, Integer crystalCost) {
        this.inventoryItemName = inventoryItemName;
        this.inventoryItemId = inventoryItemId;
        this.artifacts = artifacts;
        this.baseItemTypeId = baseItemTypeId;
        this.itemCount = itemCount;
        this.itemFreeRange = itemFreeRange;
        this.goldAmount = goldAmount;
        this.crystalCost = crystalCost;
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

    public int getBaseItemTypeId() {
        return baseItemTypeId;
    }

    public boolean hasBaseItemTypeId() {
        return baseItemTypeId != null;
    }

    public int getItemCount() {
        return itemCount;
    }

    public int getItemFreeRange() {
        return itemFreeRange;
    }

    public int getGoldAmount() {
        return goldAmount;
    }
    
    public boolean hasCrystalCost() {
        return crystalCost != null;
    }

    public int getCrystalCost() {
        return crystalCost;
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
