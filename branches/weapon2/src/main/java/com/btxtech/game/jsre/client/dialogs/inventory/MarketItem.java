package com.btxtech.game.jsre.client.dialogs.inventory;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.dialogs.crystals.AffordableCallback;
import com.btxtech.game.jsre.client.dialogs.crystals.CrystalHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MarketItem extends Composite implements HasText {
    private static MarketItemUiBinder uiBinder = GWT.create(MarketItemUiBinder.class);
    private InventoryItemInfo inventoryItemInfo;
    private int crystals;
    private InventoryDialog inventoryDialog;
    private int inventoryItemId;

    interface MarketItemUiBinder extends UiBinder<Widget, MarketItem> {
    }

    @UiField
    Label costLabel;
    @UiField
    Button buyItemButton;
    @UiField
    Label itemNameLabel;
    @UiField
    Image image;

    public MarketItem(InventoryItemInfo inventoryItemInfo, int crystals, InventoryDialog inventoryDialog) {
        this.inventoryItemInfo = inventoryItemInfo;
        this.crystals = crystals;
        this.inventoryDialog = inventoryDialog;
        initWidget(uiBinder.createAndBindUi(this));
        costLabel.setText(ClientI18nHelper.CONSTANTS.cost(inventoryItemInfo.getCrystalCost()));
        itemNameLabel.setText(inventoryItemInfo.getInventoryItemName());
        inventoryItemId = inventoryItemInfo.getInventoryItemId();
        image.setUrl(ImageHandler.getInventoryItemUrl(inventoryItemId));
    }

    @UiHandler("buyItemButton")
    void onClick(ClickEvent e) {
        new CrystalHelper(ClientI18nHelper.CONSTANTS.getInventoryItemTitle(),ClientI18nHelper.CONSTANTS.getInventoryItemNotEnough(inventoryItemInfo.getInventoryItemName())) {

            @Override
            protected void askAffordable(AffordableCallback affordableCallback) {
                affordableCallback.onDetermined(inventoryItemInfo.getCrystalCost(), crystals);
            }

            @Override
            protected void onBuySilent(int crystalCost, int crystalBalance) {
                Connection.getInstance().buyInventoryItem(inventoryItemId, inventoryDialog.getFilterPlanetId(), inventoryDialog.isFilterLevel(), inventoryDialog);
            }
        };
    }

    public void setText(String text) {
    }

    public String getText() {
        return null;
    }

}
