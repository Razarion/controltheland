/**
 *
 */
package com.btxtech.game.jsre.client.dialogs.crystals;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.common.PayPalButton;
import com.btxtech.game.jsre.common.PayPalUtils;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author beat
 */
public class BuyCrystalsPaypalDialog extends AbstractBuyCrystalsDialog {

    private BuyCrystalsPaypalDialog() {
        super(ClientI18nHelper.CONSTANTS.buyCrystalsPaypalDialogTitle());
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        dialogVPanel.add(createHtml(ClientI18nHelper.CONSTANTS.buyCrystalsPaypal(), 16));

        FlexTable flexTable = new FlexTable();

        flexTable.setWidget(0, 0, PayPalUtils.createBuyNowButton(PayPalButton.B_1));
        flexTable.setWidget(0, 1, createHtml(ClientI18nHelper.CONSTANTS.buyCrystalsPaypal1(), 14));

        flexTable.setWidget(1, 0, PayPalUtils.createBuyNowButton(PayPalButton.B_2));
        flexTable.setWidget(1, 1, createHtml(ClientI18nHelper.CONSTANTS.buyCrystalsPaypal2(), 14));

        flexTable.setWidget(2, 0, PayPalUtils.createBuyNowButton(PayPalButton.B_3));
        flexTable.setWidget(2, 1, createHtml(ClientI18nHelper.CONSTANTS.buyCrystalsPaypa3(), 14));

        flexTable.setWidget(3, 0, PayPalUtils.createBuyNowButton(PayPalButton.B_4));
        flexTable.setWidget(3, 1, createHtml(ClientI18nHelper.CONSTANTS.buyCrystalsPaypal4(), 14));

        flexTable.setWidget(4, 0, PayPalUtils.createBuyNowButton(PayPalButton.B_5));
        flexTable.setWidget(4, 1, createHtml(ClientI18nHelper.CONSTANTS.buyCrystalsPaypal5(), 14));
        dialogVPanel.add(flexTable);
    }

    public static void showDialog() {
        if (checkShowDialog(ClientI18nHelper.CONSTANTS.buyCrystalsPaypalDialogTitle(),
                ClientI18nHelper.CONSTANTS.buyCrystalsPaypalOnlyRegisteredVerified(),
                ClientI18nHelper.CONSTANTS.buyCrystalsPaypalOnlyRegistered())) {
            DialogManager.showDialog(new BuyCrystalsPaypalDialog(), DialogManager.Type.STACK_ABLE);
        }
    }
}
