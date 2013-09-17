package com.btxtech.game.jsre.client.dialogs.inventory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * User: beat
 * Date: 29.05.12
 * Time: 12:42
 */
public class InventoryInfo implements Serializable {
    private int crystals;
    private Collection<InventoryItemInfo> allInventoryItemInfos;
    private Collection<InventoryArtifactInfo> allInventoryArtifactInfos;
    private Map<InventoryItemInfo, Integer> ownInventoryItems;
    private Map<InventoryArtifactInfo, Integer> ownInventoryArtifacts;

    public int getCrystals() {
        return crystals;
    }

    public void setCrystals(int crystals) {
        this.crystals = crystals;
    }

    public void setAllInventoryItemInfos(Collection<InventoryItemInfo> allInventoryItemInfos) {
        this.allInventoryItemInfos = allInventoryItemInfos;
    }

    public void setOwnInventoryItems(Map<InventoryItemInfo, Integer> ownInventoryItems) {
        this.ownInventoryItems = ownInventoryItems;
    }

    public void setOwnInventoryArtifacts(Map<InventoryArtifactInfo, Integer> ownInventoryArtifacts) {
        this.ownInventoryArtifacts = ownInventoryArtifacts;
    }

    public Collection<InventoryItemInfo> getAllInventoryItemInfos() {
        return allInventoryItemInfos;
    }

    public Map<InventoryItemInfo, Integer> getOwnInventoryItems() {
        return ownInventoryItems;
    }

    public Map<InventoryArtifactInfo, Integer> getOwnInventoryArtifacts() {
        return ownInventoryArtifacts;
    }

    public Collection<InventoryArtifactInfo> getAllInventoryArtifactInfos() {
        return allInventoryArtifactInfos;
    }

    public void setAllInventoryArtifactInfos(Collection<InventoryArtifactInfo> allInventoryArtifactInfos) {
        this.allInventoryArtifactInfos = allInventoryArtifactInfos;
    }
}
