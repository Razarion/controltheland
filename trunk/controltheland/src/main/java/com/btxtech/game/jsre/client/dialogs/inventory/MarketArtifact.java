package com.btxtech.game.jsre.client.dialogs.inventory;

import com.btxtech.game.jsre.client.ImageHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class MarketArtifact extends Composite implements HasText {
    private static MarketArtifactUiBinder uiBinder = GWT.create(MarketArtifactUiBinder.class);
    @UiField 
    Image image;
    @UiField 
    Label artifactNameLabel;
    @UiField 
    Label coastLabel;
    @UiField 
    Button buyItemButton;

    interface MarketArtifactUiBinder extends UiBinder<Widget, MarketArtifact> {
    }

    public MarketArtifact(InventoryArtifactInfo inventoryArtifactInfo) {
        initWidget(uiBinder.createAndBindUi(this));
        coastLabel.setText("Coast: " + inventoryArtifactInfo.getRazarionCoast());
        artifactNameLabel.setText(inventoryArtifactInfo.getInventoryArtifactName());
        image.setUrl(ImageHandler.getInventoryArtifactUrl(inventoryArtifactInfo.getInventoryArtifactId()));
}

    @UiHandler("buyItemButton")
    void onClick(ClickEvent e) {
    }

    public void setText(String text) {
    }

    public String getText() {
        return null;
    }

}
