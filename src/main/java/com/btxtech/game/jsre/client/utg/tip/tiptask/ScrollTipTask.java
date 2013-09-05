package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.cockpit.quest.QuestVisualisationModel;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainScrollListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.tip.TipSplashPopupInfo;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.client.utg.tip.visualization.TerrainInGameTipVisualization;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 12:53
 */
public class ScrollTipTask extends AbstractTipTask implements TerrainScrollListener {
    private Rectangle region;
    private TipSplashPopupInfo tipSplashPopupInfo;

    public ScrollTipTask(Rectangle region, TipSplashPopupInfo tipSplashPopupInfo) {
        this.region = region;
        this.tipSplashPopupInfo = tipSplashPopupInfo;
    }

    @Override
    public void start() {
        TerrainView.getInstance().addTerrainScrollListener(this);
        QuestVisualisationModel.getInstance().setShowInGameVisualisation(false);
    }

    @Override
    public boolean isFulfilled() {
        return false;
    }

    @Override
    public void cleanup() {
        TerrainView.getInstance().removeTerrainScrollListener(this);
        QuestVisualisationModel.getInstance().setShowInGameVisualisation(true);
    }

    public GameTipVisualization createInGameTip() {
        return new TerrainInGameTipVisualization(region.getCenter());
    }

    @Override
    public TipSplashPopupInfo createSplashTip() {
        return tipSplashPopupInfo;
    }

    @Override
    public void onScroll(int left, int top, int width, int height, int deltaLeft, int deltaTop) {
        if (new Rectangle(left, top, width, height).adjoins(region)) {
            onSucceed();
        }
    }
}
