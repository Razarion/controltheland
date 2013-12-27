package com.btxtech.game.jsre.client.utg.tip.visualization;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.MathHelper;

/**
 * User: beat
 * Date: 22.12.13
 * Time: 23:36
 */
public abstract class OverlayTipVisualization implements GameTipVisualization{
    public abstract Index getAbsoluteArrowHotSpot();

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

    @Override
    public double getArrowAngel() {
        return MathHelper.HALF_RADIANT;
    }

}
