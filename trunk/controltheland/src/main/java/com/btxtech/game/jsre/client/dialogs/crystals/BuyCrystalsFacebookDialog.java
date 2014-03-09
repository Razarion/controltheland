/**
 *
 */
package com.btxtech.game.jsre.client.dialogs.crystals;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.common.FacebookProducts;
import com.btxtech.game.jsre.common.FacebookUtils;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author beat
 */
public class BuyCrystalsFacebookDialog extends AbstractBuyCrystalsDialog {

    private BuyCrystalsFacebookDialog() {
        super(ClientI18nHelper.CONSTANTS.buyCrystalsFacebookDialogTitle());
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        dialogVPanel.add(createHtml(ClientI18nHelper.CONSTANTS.buyCrystalsFacebook(), 16));

        FlexTable flexTable = new FlexTable();

        flexTable.setCellPadding(8);

        flexTable.setWidget(0, 0, createBuyButton(FacebookProducts.PRODUCT_1));
        flexTable.setWidget(0, 1, createHtml(ClientI18nHelper.CONSTANTS.buyCrystalsFacebook1(), 14));

        flexTable.setWidget(1, 0, createBuyButton(FacebookProducts.PRODUCT_2));
        flexTable.setWidget(1, 1, createHtml(ClientI18nHelper.CONSTANTS.buyCrystalsFacebook2(), 14));

        flexTable.setWidget(2, 0, createBuyButton(FacebookProducts.PRODUCT_3));
        flexTable.setWidget(2, 1, createHtml(ClientI18nHelper.CONSTANTS.buyCrystalsFacebook3(), 14));

        flexTable.setWidget(3, 0, createBuyButton(FacebookProducts.PRODUCT_4));
        flexTable.setWidget(3, 1, createHtml(ClientI18nHelper.CONSTANTS.buyCrystalsFacebook4(), 14));

        flexTable.setWidget(4, 0, createBuyButton(FacebookProducts.PRODUCT_5));
        flexTable.setWidget(4, 1, createHtml(ClientI18nHelper.CONSTANTS.buyCrystalsFacebook5(), 14));
        dialogVPanel.add(flexTable);
    }

    private Widget createBuyButton(final FacebookProducts product) {
        Button button = new Button(ClientI18nHelper.CONSTANTS.buyCrystalsFacebookButton(product.getCrystals()), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                FacebookUtils.showPayDialog(product);
                close();
            }
        });
        button.getElement().getStyle().setWidth(9, Style.Unit.EM);
        button.getElement().getStyle().setHeight(2.1, Style.Unit.EM);
        return button;
    }

    public static void showDialog() {
        if (checkShowDialog(ClientI18nHelper.CONSTANTS.buyCrystalsFacebookDialogTitle(),
                ClientI18nHelper.CONSTANTS.buyCrystalsFacebookOnlyRegisteredVerified(),
                ClientI18nHelper.CONSTANTS.buyCrystalsFacebookOnlyRegistered())) {
            DialogManager.showDialog(new BuyCrystalsFacebookDialog(), DialogManager.Type.STACK_ABLE);
        }
    }
}
