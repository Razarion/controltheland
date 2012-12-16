package com.btxtech.game.jsre.client.utg.tip.visualization;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 13:53
 */
public class ItemInGameTipVisualization implements GameTipVisualization {
    private SyncItem syncItem;
    private int itemRadius;
    private double relativeOutOfViewArrowAngel;

    public ItemInGameTipVisualization(SyncItem syncItem) {
        this.syncItem = syncItem;
        itemRadius = syncItem.getSyncItemArea().getBoundingBox().getRadius();
    }

    @Override
    public Index getArrowHotSpot(Rectangle viewRect, long timeStamp) {
        return syncItem.getSyncItemArea().getPosition().sub(viewRect.getStart()).add(itemRadius, 0);
    }

    @Override
    public double getArrowAngel() {
        return -MathHelper.QUARTER_RADIANT;
    }

    @Override
    public Index getItemMarkerMiddle(Rectangle viewRect) {
        return (syncItem instanceof SyncBaseItem) ? syncItem.getSyncItemArea().getPosition().sub(viewRect.getStart()) : null;
    }

    @Override
    public Index getRelativeOutOfViewArrowHotSpot(Rectangle viewRect) {
        if (!(syncItem instanceof SyncBaseItem)) {
            return null;
        }
        if (viewRect.contains(syncItem.getSyncItemArea().getPosition())) {
            return null;
        }
        relativeOutOfViewArrowAngel = viewRect.getCenter().getAngleToNord(syncItem.getSyncItemArea().getPosition());

        return viewRect.getCenter().getPointFromAngelToNord(relativeOutOfViewArrowAngel, 200).sub(viewRect.getStart());
    }

    @Override
    public double getRelativeOutOfViewArrowAngel() {
        return relativeOutOfViewArrowAngel;
    }
}
