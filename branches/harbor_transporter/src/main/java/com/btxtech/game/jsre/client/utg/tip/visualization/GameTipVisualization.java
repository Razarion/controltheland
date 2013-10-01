package com.btxtech.game.jsre.client.utg.tip.visualization;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;

/**
 * User: beat
 * Date: 21.08.12
 * Time: 22:51
 */
public interface GameTipVisualization {
    double getArrowAngel();

    Index getArrowHotSpot(Rectangle viewRect, long timeStamp);

    Index getItemMarkerMiddle(Rectangle viewRect);

    Index getRelativeOutOfViewArrowHotSpot(Rectangle viewRect);

    double getRelativeOutOfViewArrowAngel();
}
