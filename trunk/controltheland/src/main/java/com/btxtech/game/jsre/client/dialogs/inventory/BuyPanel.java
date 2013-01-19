/**
 *
 */
package com.btxtech.game.jsre.client.dialogs.inventory;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.RegisterDialog;
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
public class BuyPanel extends VerticalPanel {
    public BuyPanel(InventoryDialog inventoryDialog) {
        add(createHtml(ClientI18nHelper.CONSTANTS.buyRazarionPaypal(), 16));

        if (Connection.getInstance().isRegistered()) {
            fillBuyOptions();
        } else {
            fillUnregistered(inventoryDialog);
        }
    }

    private HTML createHtml(String htmlString, int fontSite) {
        HTML html = new HTML(htmlString);
        html.getElement().getStyle().setColor("#C7C4BB");
        html.getElement().getStyle().setFontSize(fontSite, Style.Unit.PX);
        html.getElement().getStyle().setProperty("fontFamily", "Arial, Helvetica, sans-serif");
        return html;
    }

    private void fillBuyOptions() {
        FlexTable flexTable = new FlexTable();
        // 1000 Razarion
        flexTable.setWidget(0, 0, PayPalUtils.createBuyNowButton(PayPalButton.B_1000));
        flexTable.setWidget(0, 1, createHtml(ClientI18nHelper.CONSTANTS.buyRazarionPaypal1000(), 14));
        // 2200 Razarion
        flexTable.setWidget(1, 0, PayPalUtils.createBuyNowButton(PayPalButton.B_2200));
        flexTable.setWidget(1, 1, createHtml(ClientI18nHelper.CONSTANTS.buyRazarionPaypal2200(), 14));
        // 4600 Razarion
        flexTable.setWidget(2, 0, PayPalUtils.createBuyNowButton(PayPalButton.B_4600));
        flexTable.setWidget(2, 1, createHtml(ClientI18nHelper.CONSTANTS.buyRazarionPaypal4600(), 14));
        // 12500 Razarion
        flexTable.setWidget(3, 0, PayPalUtils.createBuyNowButton(PayPalButton.B_12500));
        flexTable.setWidget(3, 1, createHtml(ClientI18nHelper.CONSTANTS.buyRazarionPaypal12500(), 14));
        add(flexTable);
    }

    private void fillUnregistered(final InventoryDialog inventoryDialog) {
        add(new HTML(ClientI18nHelper.CONSTANTS.buyRazarionPaypalOnlyRegistered()));
        add(new Button(ClientI18nHelper.CONSTANTS.register(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                inventoryDialog.close();
                DialogManager.showDialog(new RegisterDialog(), DialogManager.Type.PROMPTLY);
            }

        }));
    }

}
