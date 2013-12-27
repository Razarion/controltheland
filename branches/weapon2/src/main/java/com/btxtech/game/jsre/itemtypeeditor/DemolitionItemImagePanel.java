package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.itemtypeeditor.ItemTypeEditorModel.UpdateListener;
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

public class DemolitionItemImagePanel extends Composite implements UpdateListener {

    private static DemolitionItemImagePanelUiBinder uiBinder = GWT.create(DemolitionItemImagePanelUiBinder.class);
    @UiField
    IntegerBox animationFrameField;
    @UiField
    Button animationFrameButton;
    @UiField
    IntegerBox animationDurationField;
    @UiField
    FlexTable imageTable;

    interface DemolitionItemImagePanelUiBinder extends UiBinder<Widget, DemolitionItemImagePanel> {
    }

    public DemolitionItemImagePanel() {
        initWidget(uiBinder.createAndBindUi(this));
        ItemTypeEditorModel.getInstance().addUpdateListener(this);
    }

    @UiHandler("animationFrameButton")
    void onAnimationFrameButtonClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().setCurrentDemolitionAnimationFrames(animationFrameField.getValue());
    }

    @UiHandler("animationDurationField")
    void onAnimationDurationFieldChange(ChangeEvent event) {
        ItemTypeEditorModel.getInstance().setCurrentDemolitionAnimationDuration(animationDurationField.getValue());
        onModelUpdate();
    }

    @Override
    public void onModelUpdate() {
        imageTable.removeAllRows();
        if (ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().getDemolitionStepCount() > 0) {
            animationFrameField.setValue(ItemTypeEditorModel.getInstance().getCurrentDemolitionAnimationFrames());
            animationDurationField.setValue(ItemTypeEditorModel.getInstance().getCurrentDemolitionAnimationDuration());
            animationFrameField.setEnabled(true);
            animationDurationField.setEnabled(true);
            for (int frame = 0; frame < ItemTypeEditorModel.getInstance().getCurrentDemolitionAnimationFrames(); frame++) {
                for (int angel = 0; angel < ItemTypeEditorModel.getInstance().getBoundingBox().getAngelCount(); angel++) {
                    DropImage dropImage = ItemTypeEditorModel.getInstance().getDropImage(angel, ItemTypeEditorModel.getInstance().getCurrentDemolitionStep(), frame, ItemTypeSpriteMap.SyncObjectState.DEMOLITION);
                    imageTable.setWidget(frame, angel, dropImage);
                }
            }
        } else {
            animationFrameField.setValue(null);
            animationDurationField.setValue(null);
            animationFrameField.setEnabled(false);
            animationDurationField.setEnabled(false);
        }
    }

}
