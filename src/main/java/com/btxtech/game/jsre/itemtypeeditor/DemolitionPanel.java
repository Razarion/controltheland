package com.btxtech.game.jsre.itemtypeeditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DemolitionPanel extends Composite implements ItemTypeEditorModel.UpdateListener {

    private static DemolitionPanelUiBinder uiBinder = GWT.create(DemolitionPanelUiBinder.class);
    @UiField
    IntegerBox demolitionStepField;
    @UiField
    Button demolitionStepButton;
    @UiField
    CaptionPanel demolitionClipPanel;
    @UiField
    Label currentStepLabel;
    @UiField
    CaptionPanel itemImagePanel;
    @UiField
    Button previousStepButton;
    @UiField
    Button nextStepButton;
    @UiField
    Button rotationLeftButton;
    @UiField
    Label currentAngelIndexFiled;
    @UiField
    Button rotationRightButton;

    interface DemolitionPanelUiBinder extends UiBinder<Widget, DemolitionPanel> {
    }

    public DemolitionPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        ItemTypeEditorModel.getInstance().addUpdateListener(this);
        demolitionClipPanel.setContentWidget(new DemolitionClipPanel());
        itemImagePanel.setContentWidget(new DemolitionItemImagePanel());
    }

    @UiHandler("demolitionStepButton")
    void onDemolitionStepButtonClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().setDemolitionStepsLength(demolitionStepField.getValue());
    }

    @Override
    public void onModelUpdate() {
        int demolitionSteps = ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().getDemolitionStepCount();
        demolitionStepField.setValue(demolitionSteps);
        currentAngelIndexFiled.setText((ItemTypeEditorModel.getInstance().getCurrentAngelIndex() + 1) + "/" + (ItemTypeEditorModel.getInstance().getBoundingBox().getAngelCount()));
        if (demolitionSteps == 0) {
            currentStepLabel.setText("-");
            nextStepButton.setEnabled(false);
            previousStepButton.setEnabled(false);
        } else {
            currentStepLabel.setText((ItemTypeEditorModel.getInstance().getCurrentDemolitionStep() + 1) + "/" + (demolitionSteps));
            nextStepButton.setEnabled(true);
            previousStepButton.setEnabled(true);
        }

    }

    @UiHandler("previousStepButton")
    void onPreviousStepButtonClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().previousDemolitionStep();
    }

    @UiHandler("nextStepButton")
    void onNextStepButtonClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().nextDemolitionStep();
    }

    @UiHandler("rotationLeftButton")
    void onRotationLeftButtonClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().increaseCurrentAngelIndex();
    }

    @UiHandler("rotationRightButton")
    void onRotationRightButtonClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().decreaseCurrentAngelIndex();
    }
}
