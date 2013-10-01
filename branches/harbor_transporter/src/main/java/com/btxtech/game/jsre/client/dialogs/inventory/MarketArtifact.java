package com.btxtech.game.jsre.client.dialogs.inventory;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.dialogs.crystals.AffordableCallback;
import com.btxtech.game.jsre.client.dialogs.crystals.CrystalHelper;
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

public class MarketArtifact extends Composite implements HasText {
    private static MarketArtifactUiBinder uiBinder = GWT.create(MarketArtifactUiBinder.class);
    @UiField
    Image image;
    @UiField
    Label artifactNameLabel;
    @UiField
    Label costLabel;
    @UiField
    Button buyArtifactButton;
    private int inventoryArtifactId;
    private InventoryArtifactInfo inventoryArtifactInfo;
    private int crystals;
    private InventoryDialog inventoryDialog;

    interface MarketArtifactUiBinder extends UiBinder<Widget, MarketArtifact> {
    }

    public MarketArtifact(InventoryArtifactInfo inventoryArtifactInfo, int crystals, InventoryDialog inventoryDialog) {
        this.inventoryArtifactInfo = inventoryArtifactInfo;
        this.crystals = crystals;
        this.inventoryDialog = inventoryDialog;
        initWidget(uiBinder.createAndBindUi(this));
        costLabel.setText(ClientI18nHelper.CONSTANTS.cost(inventoryArtifactInfo.getCrystalCost()));
        artifactNameLabel.setText(inventoryArtifactInfo.getInventoryArtifactName());
        inventoryArtifactId = inventoryArtifactInfo.getInventoryArtifactId();
        image.setUrl(ImageHandler.getInventoryArtifactUrl(inventoryArtifactId));
        image.getElement().getStyle().setBackgroundColor(inventoryArtifactInfo.getHtmlRarenessColor());
    }

    @UiHandler("buyArtifactButton")
    void onClick(ClickEvent e) {
        new CrystalHelper(ClientI18nHelper.CONSTANTS.getArtifactItemTitle(),ClientI18nHelper.CONSTANTS.getInventoryArtifactNotEnough(inventoryArtifactInfo.getInventoryArtifactName())) {

            @Override
            protected void askAffordable(AffordableCallback affordableCallback) {
                affordableCallback.onDetermined(inventoryArtifactInfo.getCrystalCost(), crystals);
            }

            @Override
            protected void onBuySilent(int crystalCost, int crystalBalance) {
                Connection.getInstance().buyInventoryArtifact(inventoryArtifactId, inventoryDialog.getFilterPlanetId(), inventoryDialog.isFilterLevel(), inventoryDialog);
            }
        };


    }

    public void setText(String text) {
    }

    public String getText() {
        return null;
    }

}
