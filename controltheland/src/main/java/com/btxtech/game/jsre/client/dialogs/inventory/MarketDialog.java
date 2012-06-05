package com.btxtech.game.jsre.client.dialogs.inventory;

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MarketDialog extends Dialog {
    private static MarketDialog staticInstance;
    private Market market;
    private InventoryInfo inventoryInfo;

    public MarketDialog(InventoryInfo inventoryInfo) {
        super("Market");
        this.inventoryInfo = inventoryInfo;
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        market = new Market(this);
        dialogVPanel.add(market);
        market.setRazarionAmount(inventoryInfo.getRazarion());
        for (InventoryItemInfo inventoryItemInfo : inventoryInfo.getAllInventoryItemInfos()) {
            if (inventoryItemInfo.hasRazarionCoast()) {
                market.addMarketItem(inventoryItemInfo, inventoryInfo.getRazarion());
            }
        }
        for (InventoryArtifactInfo inventoryArtifactInfo : inventoryInfo.getAllInventoryArtifactInfos()) {
            if (inventoryArtifactInfo.hasRazarionCoast()) {
                market.addMarketArtifact(inventoryArtifactInfo, inventoryInfo.getRazarion());
            }
        }
        center();
        staticInstance = this;
    }

    public void updateRazarion(int razarion) {
        market.updateRazarion(razarion);
    }

    @Override
    public void close() {
        staticInstance = null;
        super.close();
    }

    public static void onBoxPicket() {
        if (staticInstance != null) {
            Connection.getInstance().loadRazarion(staticInstance);
        }
    }
}
