package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DemolitionStepImageTable extends Composite {

    private static DemolitionStepImageTableUiBinder uiBinder = GWT.create(DemolitionStepImageTableUiBinder.class);
    @UiField
    Label stepLabel;
    @UiField
    FlexTable imageTable;


    interface DemolitionStepImageTableUiBinder extends UiBinder<Widget, DemolitionStepImageTable> {
    }

    public DemolitionStepImageTable(int step) {
        initWidget(uiBinder.createAndBindUi(this));
        stepLabel.setText("Demolition Step: " + step);
        imageTable.clear();
        for (int frame = 0; frame < ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().getDemolitionAnimationFrames(); frame++) {
            for (int angel = 0; angel < ItemTypeEditorModel.getInstance().getBoundingBox().getAngelCount(); angel++) {
                DropImage dropImage = ItemTypeEditorModel.getInstance().getDropImage(angel, step, frame, ItemTypeSpriteMap.SyncObjectState.DEMOLITION);
                imageTable.setWidget(frame, angel, dropImage);
            }
        }
    }
}
