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

public class RuntimePanel extends Composite implements UpdateListener {

    private static RuntimePanelUiBinder uiBinder = GWT.create(RuntimePanelUiBinder.class);
    @UiField
    FlexTable imageTable;
    @UiField
    Button animationFrameButton;
    @UiField
    IntegerBox animationFrameField;
    @UiField
    IntegerBox animationDurationField;

    interface RuntimePanelUiBinder extends UiBinder<Widget, RuntimePanel> {
    }

    public RuntimePanel() {
        initWidget(uiBinder.createAndBindUi(this));
        ItemTypeEditorModel.getInstance().addUpdateListener(this);
    }

    @UiHandler("animationFrameButton")
    void onApplyButtonClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().setRuntimeAnimationFrames(animationFrameField.getValue());
        ItemTypeEditorModel.getInstance().cutRuntimeToCorrectLength();
    }

    @UiHandler("animationDurationField")
    void onAnimationDurationFieldChange(ChangeEvent event) {
        ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().setRuntimeAnimationDuration(animationDurationField.getValue());
        ItemTypeEditorModel.getInstance().fireUpdate();
    }

    @Override
    public void onModelUpdate() {
        animationFrameField.setValue(ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().getRuntimeAnimationFrames());
        animationDurationField.setValue(ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().getRuntimeAnimationDuration());
        imageTable.removeAllRows();
        for (int frame = 0; frame < ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().getRuntimeAnimationFrames(); frame++) {
            for (int angel = 0; angel < ItemTypeEditorModel.getInstance().getBoundingBox().getAngelCount(); angel++) {
                DropImage dropImage = ItemTypeEditorModel.getInstance().getDropImage(angel, 0, frame, ItemTypeSpriteMap.SyncObjectState.RUN_TIME);
                imageTable.setWidget(frame, angel, dropImage);
            }
        }
    }
}
