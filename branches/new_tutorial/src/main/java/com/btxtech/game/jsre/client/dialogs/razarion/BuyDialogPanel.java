package com.btxtech.game.jsre.client.dialogs.razarion;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.dialogs.DialogUiBinderWrapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class BuyDialogPanel extends DialogUiBinderWrapper {
    private static BuyDialogPanelUiBinder uiBinder = GWT.create(BuyDialogPanelUiBinder.class);
    @UiField
    Label messageLabel;
    @UiField
    Label costLabel;
    @UiField
    Label balanceLabel;
    private String title;

    interface BuyDialogPanelUiBinder extends UiBinder<Widget, BuyDialogPanel> {
    }

    public BuyDialogPanel(String title, String message, int razarionCost, int razarionBalance) {
        this.title = title;
        initWidget(uiBinder.createAndBindUi(this));
        messageLabel.setText(message);
        costLabel.setText(ClientI18nHelper.CONSTANTS.buyDialogCost(razarionCost));
        balanceLabel.setText(ClientI18nHelper.CONSTANTS.buyDialogbalance(razarionBalance));
    }

    @Override
    public String getDialogTitle() {
        return title;
    }
}
