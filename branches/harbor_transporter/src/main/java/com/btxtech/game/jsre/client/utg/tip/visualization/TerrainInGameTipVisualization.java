package com.btxtech.game.jsre.client.utg.tip.visualization;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.MathHelper;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 13:53
 */
public class TerrainInGameTipVisualization implements GameTipVisualization {
    private Index absolutePosition;

    public TerrainInGameTipVisualization(Index absolutePosition) {
        this.absolutePosition = absolutePosition;
    }

    @Override
    public Index getArrowHotSpot(Rectangle viewRect, long timeStamp) {
        return absolutePosition.sub(viewRect.getStart());
    }

    public Index getTerrainPosition(Rectangle viewRect) {
        return absolutePosition.sub(viewRect.getStart());
    }

    @Override
    public double getArrowAngel() {
        return -MathHelper.QUARTER_RADIANT;
    }

    @Override
    public Index getItemMarkerMiddle(Rectangle viewRect) {
        return null;
    }

    @Override
    public Index getRelativeOutOfViewArrowHotSpot(Rectangle viewRect) {
        return null;
    }

    @Override
    public double getRelativeOutOfViewArrowAngel() {
        return 0;
    }
}
