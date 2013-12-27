package com.btxtech.game.jsre.client.utg.tip;

import com.btxtech.game.jsre.client.simulation.Simulation;
import com.btxtech.game.jsre.client.utg.tip.tiptask.AbstractTipTask;
import com.btxtech.game.jsre.client.utg.tip.tiptask.TipTaskContainer;
import com.btxtech.game.jsre.client.utg.tip.tiptask.TipTaskFactory;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.client.utg.tip.visualization.OverlayTipVisualization;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 21.08.12
 * Time: 22:48
 */
public class GameTipManager {
    private static final GameTipManager INSTANCE = new GameTipManager();
    private GameTipVisualization gameTipVisualization;
    private OverlayTipVisualization overlayTipVisualization;
    private TipTaskContainer tipTaskContainer;
    private OverlayTipPanel overlayTipPanel = new OverlayTipPanel();
    private static Logger log = Logger.getLogger(GameTipManager.class.getName());

    public static GameTipManager getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private GameTipManager() {
    }

    public void start(GameTipConfig gameTipConfig) {
        try {
            if (gameTipConfig == null) {
                return;
            }
            tipTaskContainer = TipTaskFactory.create(this, gameTipConfig);
            startTipTask();
        } catch (Exception e) {
            log.log(Level.SEVERE, "GameTipManager.start()", e);
        }
    }

    public void stop() {
        try {
            if (tipTaskContainer == null) {
                return;
            }
            tipTaskContainer.cleanup();
            cleanupVisualization();
            tipTaskContainer = null;
        } catch (Exception e) {
            log.log(Level.SEVERE, "GameTipManager.stop()", e);
        }
    }

    private void startTipTask() throws NoSuchItemTypeException {
        AbstractTipTask currentTipTask = tipTaskContainer.getCurrentTask();
        if (currentTipTask.isFulfilled()) {
            tipTaskContainer.next();
            currentTipTask = tipTaskContainer.getCurrentTask();
        }
        currentTipTask.start();
        Simulation.getInstance().onTipTaskChanged(currentTipTask);
        startVisualization(currentTipTask.createInGameTip());
    }

    public GameTipVisualization getGameTipVisualization() {
        return gameTipVisualization;
    }

    public OverlayTipVisualization getOverlayTipVisualization() {
        return overlayTipVisualization;
    }

    public void onTaskFailed() {
        try {
            cleanupVisualization();
            tipTaskContainer.backtrackTask();
            startTipTask();
        } catch (Exception e) {
            log.log(Level.SEVERE, "GameTipManager.onTaskFailed()", e);
        }
    }

    public void onSucceed() {
        try {
            cleanupVisualization();
            tipTaskContainer.next();
            if (!tipTaskContainer.hasTip()) {
                tipTaskContainer.activateFallback();
                if (!tipTaskContainer.hasTip()) {
                    tipTaskContainer = null;
                    return;
                }
            }
            startTipTask();
        } catch (Exception e) {
            log.log(Level.SEVERE, "GameTipManager.onSucceed()", e);
        }
    }

    private void startVisualization(GameTipVisualization visualization) {
        if (visualization instanceof OverlayTipVisualization) {
            overlayTipVisualization = (OverlayTipVisualization) visualization;
            overlayTipPanel.create(overlayTipVisualization.getAbsoluteArrowHotSpot());
        } else {
            gameTipVisualization = visualization;
        }
    }

    private void cleanupVisualization() {
        if (overlayTipVisualization != null) {
            overlayTipPanel.close();
            overlayTipVisualization = null;
        }
        gameTipVisualization = null;
    }
}
