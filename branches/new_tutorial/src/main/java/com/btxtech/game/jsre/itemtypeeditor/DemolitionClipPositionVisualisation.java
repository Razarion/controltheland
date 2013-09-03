package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemClipPosition;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;

public class DemolitionClipPositionVisualisation extends AbstractVisualisation {
    private static final int CROSS_SIDE_LENGTH = 5;
    private ItemClipPosition currentItemClipPosition;

    public DemolitionClipPositionVisualisation(final ItemClipPosition currentItemClipPosition, final DemolitionClipPosition demolitionClipPosition) {
        this.currentItemClipPosition = currentItemClipPosition;
        setStepAndSyncObjectState(ItemTypeEditorModel.getInstance().getCurrentDemolitionStep(), ItemTypeSpriteMap.SyncObjectState.DEMOLITION);
        getCanvas().addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                Index clipPosition = new Index(GwtCommon.correctInt(event.getX()), GwtCommon.correctInt(event.getY()));
                clipPosition = clipPosition.sub(MIDDLE);
                ItemTypeEditorModel.getInstance().setCurrentDemolitionClipPosition(currentItemClipPosition, clipPosition);
                demolitionClipPosition.update();
            }
        });
    }

    @Override
    protected void drawVisualisation(Context2d context2d, SyncItemArea syncItemArea) {
        Index position = ItemTypeEditorModel.getInstance().getCurrentDemolitionClipPosition(currentItemClipPosition);
        if (position == null) {
            return;
        }
        Index crossMiddle = MIDDLE.add(position);
        context2d.setStrokeStyle("#FF0000");
        context2d.setLineWidth(1.0);
        context2d.beginPath();
        context2d.moveTo(crossMiddle.getX() - CROSS_SIDE_LENGTH, crossMiddle.getY());
        context2d.lineTo(crossMiddle.getX() + CROSS_SIDE_LENGTH, crossMiddle.getY());
        context2d.moveTo(crossMiddle.getX(), crossMiddle.getY() - CROSS_SIDE_LENGTH);
        context2d.lineTo(crossMiddle.getX(), crossMiddle.getY() + CROSS_SIDE_LENGTH);
        context2d.stroke();
    }

}
