package com.btxtech.game.jsre.client.utg.tip.visualization;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.renderer.CanvasElementLibrary;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 13:53
 */
public class ItemInGameTipVisualization implements GameTipVisualization {
    private SyncItem syncItem;
    private int itemRadius;

    public ItemInGameTipVisualization(SyncItem syncItem) {
        this.syncItem = syncItem;
        itemRadius = (int) syncItem.getSyncItemArea().getBoundingBox().getMinRadius();

    }

    @Override
    public Index getArrowHotSpot(Rectangle viewRect) {
        return syncItem.getSyncItemArea().getPosition().sub(viewRect.getStart()).add(itemRadius, 0);
    }

    @Override
    public Index getMousePosition(Rectangle viewRect) {
        return getArrowHotSpot(viewRect).add(CanvasElementLibrary.ARROW_HEIGHT + 10, -CanvasElementLibrary.MOUSE_HEIGHT_TOTAL / 2);
    }

    @Override
    public double getArrowAngel() {
        return -MathHelper.QUARTER_RADIANT;
    }
}