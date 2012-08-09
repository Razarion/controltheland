package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;
import com.google.gwt.canvas.dom.client.Context2d;


/**
 * User: beat
 * Date: 03.08.12
 * Time: 18:05
 */
public class BoundingBoxVisualisation extends AbstractVisualisation {
    @Override
    protected void drawVisualisation(Context2d context2d, SyncItemArea syncItemArea) {
        context2d.save();
        context2d.setStrokeStyle("#FF0000");
        context2d.beginPath();
        context2d.moveTo(syncItemArea.getCorner1().getX(), syncItemArea.getCorner1().getY());
        context2d.lineTo(syncItemArea.getCorner2().getX(), syncItemArea.getCorner2().getY());
        context2d.lineTo(syncItemArea.getCorner3().getX(), syncItemArea.getCorner3().getY());
        context2d.lineTo(syncItemArea.getCorner4().getX(), syncItemArea.getCorner4().getY());
        context2d.closePath();
        context2d.stroke();
        context2d.restore();
    }
}
