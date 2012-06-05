package com.btxtech.game.jsre.client.dialogs.inventory;

import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.Map;

public class Inventory extends Composite implements HasText {

    private static InventoryUiBinder uiBinder = GWT.create(InventoryUiBinder.class);
    @UiField
    Label razarionAmount;
    @UiField
    Button button;
    @UiField
    FlowPanel itemPlates;
    @UiField
    FlowPanel groundPlates;
    private InventoryDialog inventoryDialog;

    interface InventoryUiBinder extends UiBinder<Widget, Inventory> {
    }

    public Inventory(InventoryDialog inventoryDialog) {
        this.inventoryDialog = inventoryDialog;
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setText(String text) {
    }

    public String getText() {
        return null;
    }

    public void setRazarionAmount(int amount) {
        razarionAmount.setText("Razarion: " + amount);
    }

    public void addItemPlate(InventoryItemInfo inventoryItemInfo, int ownCount) {
        itemPlates.add(new ItemPlate(inventoryItemInfo, ownCount, inventoryDialog));
    }

    public void clearAllItemPlates() {
        itemPlates.clear();
    }

    public void addGroundPlate(InventoryItemInfo inventoryItemInfo, Map<InventoryArtifactInfo, Integer> ownArtifact) {
        groundPlates.add(new GroundPlate(inventoryItemInfo, ownArtifact, inventoryDialog));
    }

    public void clearAllGroundPlates() {
        groundPlates.clear();
    }

    @UiHandler("button")
    void onButtonClick(ClickEvent event) {
        inventoryDialog.close();
        DialogManager.showDialog(new MarketDialog(inventoryDialog.getInventoryInfo()), DialogManager.Type.QUEUE_ABLE);
    }
}
