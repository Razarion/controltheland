/**
 *
 */
package com.btxtech.game.jsre.client.dialogs.crystals;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.ClientUserService;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.MessageDialog;
import com.btxtech.game.jsre.common.PayPalButton;
import com.btxtech.game.jsre.common.PayPalUtils;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author beat
 */
public class BuyCrystalsPaypalDialog extends Dialog {

    private BuyCrystalsPaypalDialog() {
        super(ClientI18nHelper.CONSTANTS.buyCrystalsDialogTitle());
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        dialogVPanel.add(createHtml(ClientI18nHelper.CONSTANTS.buyCrystalsPaypal(), 16));

        FlexTable flexTable = new FlexTable();
        // 1000 crystals
        flexTable.setWidget(0, 0, PayPalUtils.createBuyNowButton(PayPalButton.B_1000));
        flexTable.setWidget(0, 1, createHtml(ClientI18nHelper.CONSTANTS.buyCrystalsPaypal1000(), 14));
        // 2200 crystals
        flexTable.setWidget(1, 0, PayPalUtils.createBuyNowButton(PayPalButton.B_2200));
        flexTable.setWidget(1, 1, createHtml(ClientI18nHelper.CONSTANTS.buyCrystalsPaypal2200(), 14));
        // 4600 crystals
        flexTable.setWidget(2, 0, PayPalUtils.createBuyNowButton(PayPalButton.B_4600));
        flexTable.setWidget(2, 1, createHtml(ClientI18nHelper.CONSTANTS.buyCrystalsPaypal4600(), 14));
        // 12500 crystals
        flexTable.setWidget(3, 0, PayPalUtils.createBuyNowButton(PayPalButton.B_12500));
        flexTable.setWidget(3, 1, createHtml(ClientI18nHelper.CONSTANTS.buyCrystalsPaypal12500(), 14));
        dialogVPanel.add(flexTable);
    }

    private HTML createHtml(String htmlString, int fontSite) {
        HTML html = new HTML(htmlString);
        html.getElement().getStyle().setColor("#C7C4BB");
        html.getElement().getStyle().setFontSize(fontSite, Style.Unit.PX);
        html.getElement().getStyle().setProperty("fontFamily", "Arial, Helvetica, sans-serif");
        return html;
    }

    public static void showDialog() {
        if (ClientUserService.getInstance().isRegisteredAndVerified()) {
            DialogManager.showDialog(new BuyCrystalsPaypalDialog(), DialogManager.Type.STACK_ABLE);
        } else if (ClientUserService.getInstance().isRegistered()) {
            DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.buyCrystalsDialogTitle(), ClientI18nHelper.CONSTANTS.buyCrystalsPaypalOnlyRegisteredVerified()), DialogManager.Type.STACK_ABLE);
        } else {
            DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.buyCrystalsDialogTitle(), ClientI18nHelper.CONSTANTS.buyCrystalsPaypalOnlyRegistered(), true), DialogManager.Type.STACK_ABLE);
        }
    }
}
