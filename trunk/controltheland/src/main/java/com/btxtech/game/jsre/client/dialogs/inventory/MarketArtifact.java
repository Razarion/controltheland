package com.btxtech.game.jsre.client.dialogs.inventory;

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.ImageHandler;
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
    private InventoryDialog inventoryDialog;

    interface MarketArtifactUiBinder extends UiBinder<Widget, MarketArtifact> {
    }

    public MarketArtifact(InventoryArtifactInfo inventoryArtifactInfo, int razarion, InventoryDialog inventoryDialog) {
        this.inventoryArtifactInfo = inventoryArtifactInfo;
        this.inventoryDialog = inventoryDialog;
        initWidget(uiBinder.createAndBindUi(this));
        coastLabel.setText("Coast: " + inventoryArtifactInfo.getRazarionCoast());
        artifactNameLabel.setText(inventoryArtifactInfo.getInventoryArtifactName());
        inventoryArtifactId = inventoryArtifactInfo.getInventoryArtifactId();
        image.setUrl(ImageHandler.getInventoryArtifactUrl(inventoryArtifactId));
        handleButtonState(razarion);
    }

    public void handleButtonState(int razarion) {
        buyArtifactButton.setEnabled(razarion >= inventoryArtifactInfo.getRazarionCoast());
    }

    @UiHandler("buyArtifactButton")
    void onClick(ClickEvent e) {
        Connection.getInstance().buyInventoryArtifact(inventoryArtifactId, inventoryDialog);
    }

    public void setText(String text) {
    }

    public String getText() {
        return null;
    }

}
