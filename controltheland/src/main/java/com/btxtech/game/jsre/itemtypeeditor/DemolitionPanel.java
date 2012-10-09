package com.btxtech.game.jsre.itemtypeeditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Widget;

public class DemolitionPanel extends Composite implements ItemTypeEditorModel.UpdateListener {

    private static DemolitionPanelUiBinder uiBinder = GWT.create(DemolitionPanelUiBinder.class);
    @UiField
    FlexTable stepTable;
    @UiField
    IntegerBox demolitionStepField;
    @UiField
    Button demolitionStepButton;
    @UiField
    IntegerBox animationFrameField;
    @UiField
    Button animationFrameButton;
    @UiField
    IntegerBox animationDurationField;

    interface DemolitionPanelUiBinder extends UiBinder<Widget, DemolitionPanel> {
    }

    public DemolitionPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        ItemTypeEditorModel.getInstance().addUpdateListener(this);
    }

    @UiHandler("demolitionStepButton")
    void onDemolitionStepButtonClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().setDemolitionSteps(demolitionStepField.getValue());
        ItemTypeEditorModel.getInstance().cutDemolitionToCorrectLength();
    }

    @UiHandler("animationFrameButton")
    void onAnimationFrameButtonClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().setDemolitionAnimationFrames(animationFrameField.getValue());
        ItemTypeEditorModel.getInstance().cutDemolitionToCorrectLength();
    }

    @UiHandler("animationDurationField")
    void onAnimationDurationFieldChange(ChangeEvent event) {
        ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().setDemolitionAnimationDuration(animationDurationField.getValue());
        ItemTypeEditorModel.getInstance().fireUpdate();
    }

    @Override
    public void onModelUpdate() {
        demolitionStepField.setValue(ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().getDemolitionSteps());
        animationFrameField.setValue(ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().getDemolitionAnimationFrames());
        animationDurationField.setValue(ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().getDemolitionAnimationDuration());
        stepTable.removeAllRows();
        for (int step = 0; step < ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().getDemolitionSteps(); step++) {
            stepTable.setWidget(step, 0, new DemolitionStepImageTable(step));
        }
    }
}
