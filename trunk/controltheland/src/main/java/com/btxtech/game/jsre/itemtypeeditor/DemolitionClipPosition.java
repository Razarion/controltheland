package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemClipPosition;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.SimplePanel;

public class DemolitionClipPosition extends Composite {

    private static DemolitionClipPositionUiBinder uiBinder = GWT.create(DemolitionClipPositionUiBinder.class);
    @UiField
    IntegerBox clipIdBox;
    @UiField
    IntegerBox xPositionBox;
    @UiField
    Button xPosPlusButton;
    @UiField
    Button xPosMinusButton;
    @UiField
    IntegerBox yPositionBox;
    @UiField
    Button yPosPlusButton;
    @UiField
    Button yPosMinusButton;
    @UiField
    SimplePanel positionPanel;
    private DemolitionClipPositionVisualisation visualisation;
    private ItemClipPosition itemClipPosition;

    interface DemolitionClipPositionUiBinder extends UiBinder<Widget, DemolitionClipPosition> {
    }

    public DemolitionClipPosition(ItemClipPosition itemClipPosition) {
        this.itemClipPosition = itemClipPosition;
        initWidget(uiBinder.createAndBindUi(this));
        visualisation = new DemolitionClipPositionVisualisation(itemClipPosition, this);
        positionPanel.setWidget(visualisation.getCanvas());
        visualisation.onModelUpdate();
        update();
    }

    @UiHandler("clipIdBox")
    void onClipIdBoxChange(ChangeEvent event) {
        itemClipPosition.setClipId(clipIdBox.getValue());
        update();
    }

    @UiHandler("xPositionBox")
    void onXPositionBoxChange(ChangeEvent event) {
        Index position = ItemTypeEditorModel.getInstance().getCurrentDemolitionClipPosition(itemClipPosition);
        position.setX(xPositionBox.getValue());
        update();
    }

    @UiHandler("xPosPlusButton")
    void onXPosPlusButtonClick(ClickEvent event) {
        Index position = ItemTypeEditorModel.getInstance().getCurrentDemolitionClipPosition(itemClipPosition);
        position.setX(position.getX() + 1);
        update();
    }

    @UiHandler("xPosMinusButton")
    void onXPosMinusButtonClick(ClickEvent event) {
        Index position = ItemTypeEditorModel.getInstance().getCurrentDemolitionClipPosition(itemClipPosition);
        position.setX(position.getX() - 1);
        update();
    }

    @UiHandler("yPositionBox")
    void onYPositionBoxChange(ChangeEvent event) {
        Index position = ItemTypeEditorModel.getInstance().getCurrentDemolitionClipPosition(itemClipPosition);
        position.setY(yPositionBox.getValue());
        update();
    }

    @UiHandler("yPosPlusButton")
    void onYPosPlusButtonClick(ClickEvent event) {
        Index position = ItemTypeEditorModel.getInstance().getCurrentDemolitionClipPosition(itemClipPosition);
        position.setY(position.getY() + 1);
        update();
    }

    @UiHandler("yPosMinusButton")
    void onYPosMinusButtonClick(ClickEvent event) {
        Index position = ItemTypeEditorModel.getInstance().getCurrentDemolitionClipPosition(itemClipPosition);
        position.setY(position.getY() - 1);
        update();
    }

    public void update() {
        clipIdBox.setValue(itemClipPosition.getClipId());
        Index position = ItemTypeEditorModel.getInstance().getCurrentDemolitionClipPosition(itemClipPosition);
        if (position != null) {
            xPositionBox.setValue(position.getX());
            yPositionBox.setValue(position.getY());
            visualisation.onModelUpdate();
        } else {
            xPositionBox.setValue(null);
            yPositionBox.setValue(null);
        }
    }
}
