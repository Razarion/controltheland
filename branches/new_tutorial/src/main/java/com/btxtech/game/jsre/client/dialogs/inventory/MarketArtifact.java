package com.btxtech.game.jsre.client.dialogs.inventory;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.dialogs.razarion.AffordableCallback;
import com.btxtech.game.jsre.client.dialogs.razarion.RazarionHelper;
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
    Label coastLabel;
    @UiField
    Button buyArtifactButton;
    private int inventoryArtifactId;
    private InventoryArtifactInfo inventoryArtifactInfo;
    private int razarion;
    private InventoryDialog inventoryDialog;

    interface MarketArtifactUiBinder extends UiBinder<Widget, MarketArtifact> {
    }

    public MarketArtifact(InventoryArtifactInfo inventoryArtifactInfo, int razarion, InventoryDialog inventoryDialog) {
        this.inventoryArtifactInfo = inventoryArtifactInfo;
        this.razarion = razarion;
        this.inventoryDialog = inventoryDialog;
        initWidget(uiBinder.createAndBindUi(this));
        coastLabel.setText(ClientI18nHelper.CONSTANTS.cost(inventoryArtifactInfo.getRazarionCoast()));
        artifactNameLabel.setText(inventoryArtifactInfo.getInventoryArtifactName());
        inventoryArtifactId = inventoryArtifactInfo.getInventoryArtifactId();
        image.setUrl(ImageHandler.getInventoryArtifactUrl(inventoryArtifactId));
    }

    @UiHandler("buyArtifactButton")
    void onClick(ClickEvent e) {
        new RazarionHelper(ClientI18nHelper.CONSTANTS.getArtifactItemTitle(),ClientI18nHelper.CONSTANTS.getInventoryArtifactNotEnough(inventoryArtifactInfo.getInventoryArtifactName())) {

            @Override
            protected void askAffordable(AffordableCallback affordableCallback) {
                affordableCallback.onDetermined(inventoryArtifactInfo.getRazarionCoast(), razarion);
            }

            @Override
            protected void onBuySilent(int razarionCost, int razarionBalance) {
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
