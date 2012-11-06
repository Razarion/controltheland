package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.itemtypeeditor.ItemTypeEditorModel.UpdateListener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.FlexTable;

public class DemolitionItemImagePanel extends Composite implements UpdateListener {

    private static DemolitionItemImagePanelUiBinder uiBinder = GWT.create(DemolitionItemImagePanelUiBinder.class);
    @UiField
    IntegerBox animationFrameField;
    @UiField
    Button animationFrameButton;
    @UiField
    IntegerBox animationDurationField;
    @UiField FlexTable imageTable;

    interface DemolitionItemImagePanelUiBinder extends UiBinder<Widget, DemolitionItemImagePanel> {
    }

    public DemolitionItemImagePanel() {
        initWidget(uiBinder.createAndBindUi(this));
        ItemTypeEditorModel.getInstance().addUpdateListener(this);
    }

    @UiHandler("animationFrameButton")
    void onAnimationFrameButtonClick(ClickEvent event) {
        // TODO frames & duration per step
        ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().setDemolitionAnimationFrames(animationFrameField.getValue());
        ItemTypeEditorModel.getInstance().cutDemolitionToCorrectLength();
    }

    @UiHandler("animationDurationField")
    void onAnimationDurationFieldChange(ChangeEvent event) {
        // TODO frames & duration per step
        ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().setDemolitionAnimationDuration(animationDurationField.getValue());
        ItemTypeEditorModel.getInstance().fireUpdate();
    }

    @Override
    public void onModelUpdate() {
        // TODO frames & duration per step
        animationFrameField.setValue(ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().getDemolitionAnimationFrames());
        animationDurationField.setValue(ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().getDemolitionAnimationDuration());
        imageTable.clear();
        // TODO frames & duration per step
        for (int frame = 0; frame < ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().getDemolitionAnimationFrames(); frame++) {
            for (int angel = 0; angel < ItemTypeEditorModel.getInstance().getBoundingBox().getAngelCount(); angel++) {
                DropImage dropImage = ItemTypeEditorModel.getInstance().getDropImage(angel, ItemTypeEditorModel.getInstance().getCurrentDemolitionStep(), frame, ItemTypeSpriteMap.SyncObjectState.DEMOLITION);
                imageTable.setWidget(frame, angel, dropImage);
            }
        }
    }

}
