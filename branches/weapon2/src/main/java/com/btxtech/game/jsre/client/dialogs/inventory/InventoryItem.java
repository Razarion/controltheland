package com.btxtech.game.jsre.client.dialogs.inventory;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.cockpit.CockpitMode;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.MessageDialog;
import com.btxtech.game.jsre.client.item.ItemTypeContainer;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
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

import java.util.logging.Level;
import java.util.logging.Logger;

public class InventoryItem extends Composite implements HasText {
    private static InventoryItemUiBinder uiBinder = GWT.create(InventoryItemUiBinder.class);
    @UiField
    Label itemNameLabel;
    @UiField
    Label countLabel;
    @UiField
    Image image;
    @UiField
    Button useItemButton;
    private InventoryItemInfo inventoryItemInfo;
    private Dialog dialog;
    private Logger log = Logger.getLogger(InventoryItem.class.getName());

    interface InventoryItemUiBinder extends UiBinder<Widget, InventoryItem> {
    }

    public InventoryItem(InventoryItemInfo inventoryItemInfo, int ownCount, Dialog dialog) {
        this.inventoryItemInfo = inventoryItemInfo;
        this.dialog = dialog;
        initWidget(uiBinder.createAndBindUi(this));
        itemNameLabel.setText(inventoryItemInfo.getInventoryItemName());
        countLabel.setText(ClientI18nHelper.CONSTANTS.youOwn(ownCount));
        image.setUrl(ImageHandler.getInventoryItemUrl(inventoryItemInfo.getInventoryItemId()));
    }

    public void setText(String text) {
    }

    public String getText() {
        return null;
    }

    @UiHandler("useItemButton")
    void onButtonClick(ClickEvent event) {
        dialog.close();
        if (inventoryItemInfo.hasBaseItemTypeId()) {
            BaseItemType baseItemType;
            try {
                baseItemType = (BaseItemType) ItemTypeContainer.getInstance().getItemType(inventoryItemInfo.getBaseItemTypeId());
                if (ClientBase.getInstance().isLevelLimitation4ItemTypeExceeded(baseItemType, inventoryItemInfo.getItemCount(), ClientBase.getInstance().getSimpleBase())) {
                    DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.useItem(), ClientI18nHelper.CONSTANTS.useItemLimit(ClientI18nHelper.getLocalizedString(baseItemType.getI18Name()))), DialogManager.Type.STACK_ABLE);
                } else if (ClientBase.getInstance().isHouseSpaceExceeded(ClientBase.getInstance().getSimpleBase(), baseItemType, inventoryItemInfo.getItemCount())) {
                    DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.useItem(), ClientI18nHelper.CONSTANTS.useItemHouseSpace()), DialogManager.Type.STACK_ABLE);
                } else {
                    CockpitMode.getInstance().setInventoryItemPlacer(new InventoryItemPlacer(inventoryItemInfo));
                }
            } catch (NoSuchItemTypeException e) {
                log.log(Level.SEVERE, "ItemPlate.onButtonClick()", e);
            }
        } else {
            if (ClientBase.getInstance().isDepositResourceAllowed(inventoryItemInfo.getGoldAmount())) {
                Connection.getInstance().useInventoryItem(inventoryItemInfo.getInventoryItemId(), null);
            } else {
                DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.useItem(), ClientI18nHelper.CONSTANTS.useItemMoney()), DialogManager.Type.STACK_ABLE);
            }
        }
    }
}
