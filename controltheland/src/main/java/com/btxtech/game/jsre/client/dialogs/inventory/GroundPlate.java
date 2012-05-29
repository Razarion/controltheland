package com.btxtech.game.jsre.client.dialogs.inventory;

import com.btxtech.game.jsre.client.ImageHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import java.util.Map;

public class GroundPlate extends Composite implements HasText {

    private static GroundPlateUiBinder uiBinder = GWT.create(GroundPlateUiBinder.class);
    @UiField
    Image image;
    @UiField
    HorizontalPanel artifacts;

    interface GroundPlateUiBinder extends UiBinder<Widget, GroundPlate> {
    }

    public GroundPlate(InventoryItemInfo inventoryItemInfo, Map<InventoryArtifactInfo, Integer> ownArtifact) {
        initWidget(uiBinder.createAndBindUi(this));
        image.setUrl(ImageHandler.getInventoryItemUrl(inventoryItemInfo.getInventoryItemId()));
        for (Map.Entry<InventoryArtifactInfo, Integer> entry : inventoryItemInfo.getArtifacts().entrySet()) {
            Integer ownCount = ownArtifact.get(entry.getKey());
            if (ownCount == null) {
                ownCount = 0;
            }
            artifacts.add(new ArtifactPlate(ImageHandler.getInventoryArtifactUrl(entry.getKey().getInventoryArtifactId()),
                    ownCount,
                    entry.getValue(),
                    entry.getKey().getHtmlRarenessColor()));
        }
    }

    public void setText(String text) {
    }

    public String getText() {
        return null;
    }
}
