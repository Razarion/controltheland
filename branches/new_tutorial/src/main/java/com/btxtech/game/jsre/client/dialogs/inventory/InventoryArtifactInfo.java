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
    private Integer razarionCoast;

    /**
     * Used by GWT
     */
    InventoryArtifactInfo() {
    }

    public InventoryArtifactInfo(String inventoryArtifactName, int inventoryArtifactId, String htmlRarenessColor, Integer razarionCoast) {
        this.inventoryArtifactId = inventoryArtifactId;
        this.inventoryArtifactName = inventoryArtifactName;
        this.htmlRarenessColor = htmlRarenessColor;
        this.razarionCoast = razarionCoast;
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

    public boolean hasRazarionCoast() {
        return razarionCoast != null;
    }
    
    public int getRazarionCoast() {
        return razarionCoast;
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
}
