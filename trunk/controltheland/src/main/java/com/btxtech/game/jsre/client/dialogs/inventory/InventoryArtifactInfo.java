package com.btxtech.game.jsre.client.dialogs.inventory;

import java.io.Serializable;

/**
 * User: beat
 * Date: 29.05.12
 * Time: 01:01
 */
public class InventoryArtifactInfo implements Serializable {
    private String inventoryArtifactName;
    private int inventoryArtifactId;
    private String htmlRarenessColor;
    private Integer crystalCost;

    /**
     * Used by GWT
     */
    InventoryArtifactInfo() {
    }

    public InventoryArtifactInfo(String inventoryArtifactName, int inventoryArtifactId, String htmlRarenessColor, Integer crystalCost) {
        this.inventoryArtifactId = inventoryArtifactId;
        this.inventoryArtifactName = inventoryArtifactName;
        this.htmlRarenessColor = htmlRarenessColor;
        this.crystalCost = crystalCost;
   }

    public int getInventoryArtifactId() {
        return inventoryArtifactId;
    }

    public String getInventoryArtifactName() {
        return inventoryArtifactName;
    }

    public String getHtmlRarenessColor() {
        return htmlRarenessColor;
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

        InventoryArtifactInfo that = (InventoryArtifactInfo) o;

        return inventoryArtifactId == that.inventoryArtifactId;

    }

    @Override
    public int hashCode() {
        return inventoryArtifactId;
    }

    @Override
    public String toString() {
        return "InventoryArtifactInfo{" +
                "htmlRarenessColor='" + htmlRarenessColor + '\'' +
                ", inventoryArtifactName='" + inventoryArtifactName + '\'' +
                ", inventoryArtifactId=" + inventoryArtifactId +
                ", crystalCost=" + crystalCost +
                '}';
    }
}
