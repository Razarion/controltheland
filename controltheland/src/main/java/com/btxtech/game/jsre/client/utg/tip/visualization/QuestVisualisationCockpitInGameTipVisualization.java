package com.btxtech.game.jsre.client.utg.tip.visualization;

import com.btxtech.game.jsre.client.cockpit.quest.QuestVisualisationCockpit;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.MathHelper;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 13:53
 */
public class QuestVisualisationCockpitInGameTipVisualization implements GameTipVisualization {
    private static final int LEFT_DISTANCE = 100;
    private static final int TOP_DISTANCE = 50;

    public QuestVisualisationCockpitInGameTipVisualization() {
    }

    @Override
    public Index getArrowHotSpot(Rectangle viewRect, long timeStamp) {
        int distance = (LEFT_DISTANCE - (int) (LEFT_DISTANCE * (timeStamp & 1000) / 1000.0)) / 2;
        return new Index(QuestVisualisationCockpit.getInstance().getAbsoluteLeft() - distance, TOP_DISTANCE);
    }

    @Override
    public double getArrowAngel() {
        return -MathHelper.THREE_QUARTER_RADIANT;
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
