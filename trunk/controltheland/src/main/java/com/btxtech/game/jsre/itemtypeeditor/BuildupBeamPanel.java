package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemClipPosition;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class BuildupBeamPanel extends Composite implements ItemTypeEditorModel.UpdateListener {

    private static BuildupBeamPanelUiBinder uiBinder = GWT.create(BuildupBeamPanelUiBinder.class);
    @UiField
    Button rotationLeftButton;
    @UiField
    Label currentAngelIndexFiled;
    @UiField
    Button rotationRightButton;
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
    private BuildupBeamClipPositionVisualisation visualisation;

    interface BuildupBeamPanelUiBinder extends UiBinder<Widget, BuildupBeamPanel> {
    }

    public BuildupBeamPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        ItemTypeEditorModel.getInstance().addUpdateListener(this);
        visualisation = new BuildupBeamClipPositionVisualisation(this);
        positionPanel.setWidget(visualisation.getCanvas());
        visualisation.onModelUpdate();
    }

    @Override
    public void onModelUpdate() {
        currentAngelIndexFiled.setText((ItemTypeEditorModel.getInstance().getCurrentAngelIndex() + 1) + "/" + (ItemTypeEditorModel.getInstance().getBoundingBox().getAngelCount()));
        ItemClipPosition buildupItemClipPosition = ItemTypeEditorModel.getInstance().getBuildupItemClipPosition();
        if (buildupItemClipPosition != null) {
            clipIdBox.setEnabled(true);
            clipIdBox.setValue(buildupItemClipPosition.getClipId());
            xPositionBox.setEnabled(true);
            xPositionBox.setValue(ItemTypeEditorModel.getInstance().getCurrentBuildupClipPosition().getX());
            xPosPlusButton.setEnabled(true);
            xPosMinusButton.setEnabled(true);
            yPositionBox.setEnabled(true);
            yPositionBox.setValue(ItemTypeEditorModel.getInstance().getCurrentBuildupClipPosition().getY());
            yPosPlusButton.setEnabled(true);
            yPosMinusButton.setEnabled(true);
            visualisation.onModelUpdate();
        } else {
            clipIdBox.setEnabled(false);
            clipIdBox.setValue(null);
            xPositionBox.setEnabled(false);
            xPositionBox.setValue(null);
            xPosPlusButton.setEnabled(false);
            xPosMinusButton.setEnabled(false);
            yPositionBox.setEnabled(false);
            yPositionBox.setValue(null);
            yPosPlusButton.setEnabled(false);
            yPosMinusButton.setEnabled(false);
        }

    }

    @UiHandler("rotationLeftButton")
    void onRotationLeftButtonClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().increaseCurrentAngelIndex();
    }

    @UiHandler("rotationRightButton")
    void onRotationRightButtonClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().decreaseCurrentAngelIndex();
    }

    @UiHandler("clipIdBox")
    void onClipIdBoxChange(ChangeEvent event) {
        ItemTypeEditorModel.getInstance().getBuildupItemClipPosition().setClipId(clipIdBox.getValue());
        onModelUpdate();
    }

    @UiHandler("xPositionBox")
    void onXPositionBoxChange(ChangeEvent event) {
        Index position = ItemTypeEditorModel.getInstance().getCurrentBuildupClipPosition();
        position.setX(xPositionBox.getValue());
        onModelUpdate();
    }

    @UiHandler("xPosPlusButton")
    void onXPosPlusButtonClick(ClickEvent event) {
        Index position = ItemTypeEditorModel.getInstance().getCurrentBuildupClipPosition();
        position.setX(position.getX() + 1);
        onModelUpdate();
    }

    @UiHandler("xPosMinusButton")
    void onXPosMinusButtonClick(ClickEvent event) {
        Index position = ItemTypeEditorModel.getInstance().getCurrentBuildupClipPosition();
        position.setX(position.getX() - 1);
        onModelUpdate();
    }

    @UiHandler("yPositionBox")
    void onYPositionBoxChange(ChangeEvent event) {
        Index position = ItemTypeEditorModel.getInstance().getCurrentBuildupClipPosition();
        position.setY(yPositionBox.getValue());
        onModelUpdate();
    }

    @UiHandler("yPosPlusButton")
    void onYPosPlusButtonClick(ClickEvent event) {
        Index position = ItemTypeEditorModel.getInstance().getCurrentBuildupClipPosition();
        position.setY(position.getY() + 1);
        onModelUpdate();
    }

    @UiHandler("yPosMinusButton")
    void onYPosMinusButtonClick(ClickEvent event) {
        Index position = ItemTypeEditorModel.getInstance().getCurrentBuildupClipPosition();
        position.setY(position.getY() - 1);
        onModelUpdate();
    }
}
