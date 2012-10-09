package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
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

public class BuildupPanel extends Composite implements ItemTypeEditorModel.UpdateListener {

    private static BuildupPanelUiBinder uiBinder = GWT.create(BuildupPanelUiBinder.class);
    @UiField
    FlexTable imageTable;
    @UiField
    IntegerBox buildupStepFiled;
    @UiField
    IntegerBox animationFrameField;
    @UiField
    IntegerBox animationDurationField;
    @UiField
    Button animationFrameButton;
    @UiField
    Button buildupStepButton;

    interface BuildupPanelUiBinder extends UiBinder<Widget, BuildupPanel> {
    }

    public BuildupPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        ItemTypeEditorModel.getInstance().addUpdateListener(this);
    }

    @Override
    public void onModelUpdate() {
        buildupStepFiled.setValue(ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().getBuildupSteps());
        animationFrameField.setValue(ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().getBuildupAnimationFrames());
        animationDurationField.setValue(ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().getBuildupAnimationDuration());
        imageTable.removeAllRows();
        for (int frame = 0; frame < ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().getBuildupAnimationFrames(); frame++) {
            for (int step = 0; step < ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().getBuildupSteps(); step++) {
                DropImage dropImage = ItemTypeEditorModel.getInstance().getDropImage(0, step, frame, ItemTypeSpriteMap.SyncObjectState.BUILD_UP);
                imageTable.setWidget(frame, step, dropImage);
            }
        }
    }

    @UiHandler("animationDurationField")
    void onAnimationDurationFieldChange(ChangeEvent event) {
        ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().setBuildupAnimationDuration(animationDurationField.getValue());
        ItemTypeEditorModel.getInstance().fireUpdate();
    }

    @UiHandler("animationFrameButton")
    void onAnimationFrameButtonClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().setBuildupAnimationFrames(animationFrameField.getValue());
        ItemTypeEditorModel.getInstance().cutBuildupToCorrectLength();
    }

    @UiHandler("buildupStepButton")
    void onBuildupStepButtonClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().setBuildupSteps(buildupStepFiled.getValue());
        ItemTypeEditorModel.getInstance().cutBuildupToCorrectLength();
    }

}
