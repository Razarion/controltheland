package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.utg.tip.GameTipManager;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 13:19
 */
public abstract class AbstractTipTask {
    private GameTipManager gameTipManager;

    public void setGameTipManager(GameTipManager gameTipManager) {
        this.gameTipManager = gameTipManager;
    }

    public abstract GameTipVisualization createInGameTip() throws NoSuchItemTypeException;

    public abstract boolean isFulfilled();

    public abstract void start();

    public abstract void cleanup();

    protected void onFailed() {
        cleanup();
        gameTipManager.onTaskFailed();
    }

    protected void onSucceed() {
        cleanup();
        gameTipManager.onSucceed();
    }
}
