package com.btxtech.game.jsre.client.dialogs.inventory;

import com.btxtech.game.jsre.client.ImageHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ItemPlate extends Composite implements HasText {

    private static ItemPlateUiBinder uiBinder = GWT.create(ItemPlateUiBinder.class);
    @UiField
    Label itemNameLabel;
    @UiField
    Label countLabel;
    @UiField
    Image image;

    interface ItemPlateUiBinder extends UiBinder<Widget, ItemPlate> {
    }

    public ItemPlate(InventoryItemInfo inventoryItemInfo, int ownCount) {
        initWidget(uiBinder.createAndBindUi(this));
        itemNameLabel.setText(inventoryItemInfo.getInventoryItemName());
        countLabel.setText("You own: " + ownCount);
        image.setUrl(ImageHandler.getInventoryItemUrl(inventoryItemInfo.getInventoryItemId()));
    }

    public void setText(String text) {
    }

    public String getText() {
        return null;
    }
}
