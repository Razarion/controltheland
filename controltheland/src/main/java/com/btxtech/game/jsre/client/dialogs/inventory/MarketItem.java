package com.btxtech.game.jsre.client.dialogs.inventory;

import com.btxtech.game.jsre.client.ImageHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Image;

public class MarketItem extends Composite implements HasText {
    private static MarketItemUiBinder uiBinder = GWT.create(MarketItemUiBinder.class);

    interface MarketItemUiBinder extends UiBinder<Widget, MarketItem> {
    }

    @UiField 
    Label coastLabel;
    @UiField 
    Button buyItemButton;
    @UiField
    Label itemNameLabel;
    @UiField 
    Image image;

    public MarketItem(InventoryItemInfo inventoryItemInfo) {
        initWidget(uiBinder.createAndBindUi(this));
        coastLabel.setText("Coast: " + inventoryItemInfo.getRazarionCoast());
        itemNameLabel.setText(inventoryItemInfo.getInventoryItemName());
        image.setUrl(ImageHandler.getInventoryItemUrl(inventoryItemInfo.getInventoryItemId()));
    }

    @UiHandler("buyItemButton")
    void onClick(ClickEvent e) {
    }

    public void setText(String text) {
    }

    public String getText() {
        return null;
    }
    
}
