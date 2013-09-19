package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.cockpit.quest.QuestVisualisationModel;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.client.utg.tip.visualization.TerrainInGameTipVisualization;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 12:53
 */
public class ScrollTipTask extends AbstractTipTask {
    private Index terrainPositionHint;

    public ScrollTipTask(Index terrainPositionHint) {
        this.terrainPositionHint = terrainPositionHint;
    }

    @Override
    public void internalStart() {
        QuestVisualisationModel.getInstance().setShowInGameVisualisation(false);
    }

    @Override
    public boolean isFulfilled() {
        return false;
    }

    @Override
    public void internalCleanup() {
        QuestVisualisationModel.getInstance().setShowInGameVisualisation(true);
    }

    @Override
    public String getTaskText() {
        // TODO use i18n
        return "Scrolle auf dem Planeten herum um das Datencenter zu finden. Benutze dazu die Pfeiltasten oder bewege die Maus zum Bildschirmrand. Oben links ist das Radar.";
    }

    public GameTipVisualization createInGameTip() {
        return new TerrainInGameTipVisualization(terrainPositionHint);
    }
}
