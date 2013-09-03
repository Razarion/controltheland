package com.btxtech.game.jsre.client.dialogs.razarion;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.DialogUiBinderWrapper;
import com.btxtech.game.jsre.client.dialogs.incentive.InviteFriendsDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class HowToGetRazarionPanel extends DialogUiBinderWrapper {
    private static HowToGetRazarionPanelUiBinder uiBinder = GWT.create(HowToGetRazarionPanelUiBinder.class);
    @UiField
    Label messageLabel;
    @UiField
    Label costLabel;
    @UiField
    Label balanceLabel;
    @UiField
    Button buyButton;
    @UiField
    Button buyInvite;

    private String title;

    interface HowToGetRazarionPanelUiBinder extends UiBinder<Widget, HowToGetRazarionPanel> {
    }

    public HowToGetRazarionPanel(String title) {
        this(title, null, 0, 0);
    }

    public HowToGetRazarionPanel(String title, String message, int razarionCost, int razarionBalance) {
        this.title = title;
        initWidget(uiBinder.createAndBindUi(this));
        if (message != null) {
            messageLabel.setText(message);
            costLabel.setText(ClientI18nHelper.CONSTANTS.buyDialogCost(razarionCost));
            balanceLabel.setText(ClientI18nHelper.CONSTANTS.buyDialogbalance(razarionBalance));
        } else {
            messageLabel.setVisible(false);
            costLabel.setVisible(false);
            balanceLabel.setVisible(false);
        }
    }

    @Override
    public String getDialogTitle() {
        return title;
    }

    @UiHandler("buyButton")
    void onBuyButtonClick(ClickEvent event) {
        close();
        DialogManager.showDialog(new BuyRazarionPaypalDialog(), DialogManager.Type.STACK_ABLE);
    }

    @UiHandler("buyInvite")
    void onBuyInviteClick(ClickEvent event) {
        close();
        InviteFriendsDialog.showDialog();
    }

}
