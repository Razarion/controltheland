package com.btxtech.game.jsre.client.dialogs.inventory;

import java.util.Map;

import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MarketDialog extends Dialog {
    private Market market;
    private InventoryInfo inventoryInfo;

    public MarketDialog(InventoryInfo inventoryInfo) {
        super("Market");
        this.inventoryInfo = inventoryInfo;
    }
    
    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        market = new Market();
        dialogVPanel.add(market);
        market.setRazarionAmount(inventoryInfo.getRazarion());
        for (InventoryItemInfo inventoryItemInfo : inventoryInfo.getAllInventoryItemInfos()) {
            if(inventoryItemInfo.hasRazarionCoast()) {
                market.addMarketItem(inventoryItemInfo);
            }
        }
        for (InventoryArtifactInfo inventoryArtifactInfo : inventoryInfo.getAllInventoryArtifactInfos()) {
            if (inventoryArtifactInfo.hasRazarionCoast()) {
                market.addMarketArtifact(inventoryArtifactInfo);
            }
        }
        center();
    }

}
