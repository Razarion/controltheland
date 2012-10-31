/**
 * 
 */
package com.btxtech.game.jsre.client.dialogs.inventory;

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.RegisterDialog;
import com.btxtech.game.jsre.common.PayPalUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author beat
 * 
 */
public class BuyPanel extends VerticalPanel {
    public BuyPanel(InventoryDialog inventoryDialog) {
        add(new HTML("<h2>Buy Razarion via PayPal</h2>"));

        if (Connection.getInstance().isRegistered()) {
            fillBuyOptions();
        } else {
            fillUnregistered(inventoryDialog);
        }
    }

    private void fillBuyOptions() {
        FlexTable flexTable = new FlexTable();
        // 1000 Razarion
        flexTable.setWidget(0, 0, PayPalUtils.createBuyNowButton("PE44JA3J2AZ2J"));
        flexTable.setWidget(0, 1, new HTML("1000 Razarion for 5$"));
        // 2200 Razarion
        flexTable.setWidget(1, 0, PayPalUtils.createBuyNowButton("T6UDKDH59Y43E"));
        flexTable.setWidget(1, 1, new HTML("2200 Razarion for 10$.<br />You get 200 Razarion for free!"));
        // 4600 Razarion
        flexTable.setWidget(2, 0, PayPalUtils.createBuyNowButton("7LSHFG9LM88VL"));
        flexTable.setWidget(2, 1, new HTML("4600 Razarion for 20$.<br />You get 600 Razarion for free!"));
        // 12500 Razarion
        flexTable.setWidget(3, 0, PayPalUtils.createBuyNowButton("YLVYNLXBSJXGY"));
        flexTable.setWidget(3, 1, new HTML("12500 Razarion for 50$.<br />You get 2500 Razarion for free!"));
        add(flexTable);
    }

    private void fillUnregistered(final InventoryDialog inventoryDialog) {
        add(new HTML("Only registered user can buy Razarion."));
        add(new Button("Register", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                inventoryDialog.close();
                DialogManager.showDialog(new RegisterDialog(), DialogManager.Type.PROMPTLY);
            }

        }));
    }

}
