package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.common.MathHelper;
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
        context2d.arc(syncItemArea.getPosition().getX(), syncItemArea.getPosition().getY(), syncItemArea.getBoundingBox().getRadius(), 0, MathHelper.ONE_RADIANT);
        context2d.closePath();
        context2d.stroke();
        context2d.restore();
    }
}
