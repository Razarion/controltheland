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
    private Inventory inventory;
    private Logger log = Logger.getLogger(InventoryDialog.class.getName());

    public InventoryDialog() {
        super("Inventory");
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        inventory = new Inventory();
        dialogVPanel.add(inventory);
        Connection.getInstance().loadInventory(this);
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
        inventory.setRazarionAmount(inventoryInfo.getRazarion());
        inventory.clearAllItemPlates();
        for (Map.Entry<InventoryItemInfo, Integer> entry : inventoryInfo.getOwnInventoryItems().entrySet()) {
            inventory.addItemPlate(entry.getKey(), entry.getValue(), this);
        }
        inventory.clearAllGroundPlates();
        for (InventoryItemInfo inventoryItemInfo : inventoryInfo.getAllInventoryItemInfos()) {
            if (inventoryItemInfo.hasArtifacts()) {
                inventory.addGroundPlate(inventoryItemInfo, inventoryInfo.getOwnInventoryArtifacts(), this);
            }
        }
        center();
    }
}
