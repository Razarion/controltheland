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
        //TODO remove--------------
        // Artifacts
        //InventoryArtifactInfo inventoryArtifactInfo1 = new InventoryArtifactInfo("Artifat1", 1, "#FFEEEE");
        //InventoryArtifactInfo inventoryArtifactInfo2 = new InventoryArtifactInfo("Artifat2", 2, "#FFFFEE");
        //InventoryArtifactInfo inventoryArtifactInfo3 = new InventoryArtifactInfo("Artifat3", 3, "#FFEEBB");
        //InventoryArtifactInfo inventoryArtifactInfo4 = new InventoryArtifactInfo("Artifat4", 4, "#FFAAEE");
        //InventoryArtifactInfo inventoryArtifactInfo5 = new InventoryArtifactInfo("Artifat5", 5, "#FFFFAA");
        //InventoryArtifactInfo inventoryArtifactInfo6 = new InventoryArtifactInfo("Artifat6", 6, "#FFAABB");
        // Items
        //List<InventoryItemInfo> inventoryItemInfos = new ArrayList<InventoryItemInfo>();
        //Map<InventoryArtifactInfo, Integer> itemArtifacts = new HashMap<InventoryArtifactInfo, Integer>();
        //itemArtifacts.put(inventoryArtifactInfo1, 5);
        //itemArtifacts.put(inventoryArtifactInfo2, 3);
        //itemArtifacts.put(inventoryArtifactInfo3, 3);
        //InventoryItemInfo inventoryItemInfo1 = new InventoryItemInfo("Tesla", 1, itemArtifacts);
        //inventoryItemInfos.add(inventoryItemInfo1);
        // Own Artifacts
        // Map<InventoryArtifactInfo, Integer> ownArtifacts = new HashMap<InventoryArtifactInfo, Integer>();
        // ownArtifacts.put(inventoryArtifactInfo1, 10);
        // ownArtifacts.put(inventoryArtifactInfo2, 1);
        // ownArtifacts.put(inventoryArtifactInfo4, 2);
        // ownArtifacts.put(inventoryArtifactInfo5, 3);
        // Own Items
        //Map<InventoryItemInfo, Integer> ownItems = new HashMap<InventoryItemInfo, Integer>();
        //ownItems.put(inventoryItemInfo1, 3);
        //TODO remove ends--------------
        if (inventory == null) {
            log.warning("InventoryDialog.onItemsReceived() inventory == null");
            return;
        }
        if (inventoryInfo == null) {
            log.warning("InventoryDialog.onItemsReceived() inventoryInfo == null");
            return;
        }
        inventory.setRazarionAmount(inventoryInfo.getRazarion());
        for (Map.Entry<InventoryItemInfo, Integer> entry : inventoryInfo.getOwnInventoryItems().entrySet()) {
            inventory.addItemPlate(entry.getKey(), entry.getValue());
        }

        for (InventoryItemInfo inventoryItemInfo : inventoryInfo.getAllInventoryItemInfos()) {
            if (inventoryItemInfo.hasArtifacts()) {
                inventory.addGroundPlate(inventoryItemInfo, inventoryInfo.getOwnInventoryArtifacts());
            }
        }
    }
}
