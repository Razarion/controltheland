package com.btxtech.game.jsre.client.dialogs.inventory;

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.Map;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 26.05.12
 * Time: 00:05
 */
public class InventoryDialog extends Dialog {
    private static InventoryDialog staticInstance;
    private Inventory inventory;
    private Logger log = Logger.getLogger(InventoryDialog.class.getName());
    private InventoryInfo inventoryInfo;

    public InventoryDialog() {
        super("Inventory");
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        inventory = new Inventory(this);
        dialogVPanel.add(inventory);
        Connection.getInstance().loadInventory(this);
        staticInstance = this;
    }

    public void onItemsReceived(InventoryInfo inventoryInfo) {
        if (inventory == null) {
            log.warning("InventoryDialog.onItemsReceived() inventory == null");
            return;
        }
        if (inventoryInfo == null) {
            log.warning("InventoryDialog.onItemsReceived() inventoryInfo == null");
            return;
        }
        this.inventoryInfo = inventoryInfo;
        inventory.setRazarionAmount(inventoryInfo.getRazarion());
        inventory.clearAllItemPlates();
        for (Map.Entry<InventoryItemInfo, Integer> entry : inventoryInfo.getOwnInventoryItems().entrySet()) {
            inventory.addItemPlate(entry.getKey(), entry.getValue());
        }
        inventory.clearAllGroundPlates();
        for (InventoryItemInfo inventoryItemInfo : inventoryInfo.getAllInventoryItemInfos()) {
            if (inventoryItemInfo.hasArtifacts()) {
                inventory.addGroundPlate(inventoryItemInfo, inventoryInfo.getOwnInventoryArtifacts());
            }
        }
        center();
    }

    @Override
    public void close() {
        super.close();
        staticInstance = null;
    }

    public static void onBoxPicket() {
        if (staticInstance != null) {
            Connection.getInstance().loadInventory(staticInstance);
        }
    }

    public InventoryInfo getInventoryInfo() {
        return inventoryInfo;
    }
}
