package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.utg.tip.GameTipManager;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.client.utg.tip.visualization.QuestVisualisationCockpitInGameTipVisualization;

/**
 * User: beat
 * Date: 09.11.12
 * Time: 12:53
 */
@Deprecated
public class WatchQuestVisualisationCockpitTipTask extends AbstractTipTask {
    @Override
    public void start() {
        // Ignore
    }

    @Override
    public boolean isFulfilled() {
        return false;
    }

    @Override
    public void cleanup() {
        // Ignore
    }

    public GameTipVisualization createInGameTip() {
        return new QuestVisualisationCockpitInGameTipVisualization();
    }
}
