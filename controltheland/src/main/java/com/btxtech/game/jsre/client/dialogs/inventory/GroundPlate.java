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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import java.util.Map;
import com.google.gwt.user.client.ui.FlexTable;

public class GroundPlate extends Composite implements HasText {
    private static GroundPlateUiBinder uiBinder = GWT.create(GroundPlateUiBinder.class);
    private static final int ARTIFACT_ROWS = 2;
    @UiField
    Image image;
    @UiField
    FlexTable artifacts;
    @UiField
    Button assembleButton;
    private int inventoryItemId;
    private InventoryDialog inventoryDialog;

    interface GroundPlateUiBinder extends UiBinder<Widget, GroundPlate> {
    }

    public GroundPlate(InventoryItemInfo inventoryItemInfo, Map<InventoryArtifactInfo, Integer> ownArtifact, InventoryDialog inventoryDialog) {
        this.inventoryDialog = inventoryDialog;
        initWidget(uiBinder.createAndBindUi(this));
        image.setUrl(ImageHandler.getInventoryItemUrl(inventoryItemInfo.getInventoryItemId()));
        inventoryItemId = inventoryItemInfo.getInventoryItemId();
        boolean isReadyForAssemble = true;
        int artifactNumber = 0;
        for (Map.Entry<InventoryArtifactInfo, Integer> entry : inventoryItemInfo.getArtifacts().entrySet()) {
            Integer ownCount = ownArtifact.get(entry.getKey());
            if (ownCount == null) {
                ownCount = 0;
            }
            if (ownCount < entry.getValue()) {
                isReadyForAssemble = false;
            }
            
            int row = artifactNumber % 2;
            int column = artifactNumber / 2;
            
            artifacts.setWidget(row, column, new ArtifactPlate(ImageHandler.getInventoryArtifactUrl(entry.getKey().getInventoryArtifactId()),
                    ownCount,
                    entry.getValue(),
                    entry.getKey().getHtmlRarenessColor()));
            artifactNumber++;
        }
        assembleButton.setEnabled(isReadyForAssemble);
    }

    public void setText(String text) {
    }

    public String getText() {
        return null;
    }

    @UiHandler("assembleButton")
    void onButtonClick(ClickEvent event) {
        Connection.getInstance().assembleInventoryItem(inventoryItemId, inventoryDialog);
    }
}
