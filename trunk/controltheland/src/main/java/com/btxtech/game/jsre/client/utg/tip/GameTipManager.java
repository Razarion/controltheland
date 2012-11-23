package com.btxtech.game.jsre.client.utg.tip;

import com.btxtech.game.jsre.client.utg.tip.tiptask.AbstractTipTask;
import com.btxtech.game.jsre.client.utg.tip.tiptask.TipTaskFactory;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.client.utg.tip.visualization.ItemCockpitGameOverlayTipVisualization;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;

import java.util.List;
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
    private ItemCockpitGameOverlayTipVisualization overlayVisualization;
    private List<AbstractTipTask> tasks;
    private int currentTaskIndex;
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
            tasks = TipTaskFactory.create(this, gameTipConfig);
            startTipTask(0);
        } catch (Exception e) {
            log.log(Level.SEVERE, "GameTipManager.start()", e);
        }
    }

    public void stop() {
        try {
            if (tasks == null) {
                return;
            }
            AbstractTipTask abstractTipTask = tasks.get(currentTaskIndex);
            if (!abstractTipTask.isFulfilled()) {
                abstractTipTask.cleanup();
            }
            cleanupVisualization();
            tasks = null;
        } catch (Exception e) {
            log.log(Level.SEVERE, "GameTipManager.stop()", e);
        }
    }

    private void startTipTask(int currentTaskIndex) throws NoSuchItemTypeException {
        this.currentTaskIndex = currentTaskIndex;
        AbstractTipTask currentTipTask = tasks.get(this.currentTaskIndex);
        currentTipTask.start();
        startVisualization(currentTipTask.createInGameTip());
    }

    public GameTipVisualization getGameTipVisualization() {
        return gameTipVisualization;
    }

    public ItemCockpitGameOverlayTipVisualization getOverlayVisualization() {
        return overlayVisualization;
    }

    public void onTaskFailed() {
        try {
            cleanupVisualization();
            backtrackTask(currentTaskIndex - 1);
        } catch (Exception e) {
            log.log(Level.SEVERE, "GameTipManager.onTaskFailed()", e);
        }
    }

    private void backtrackTask(int taskIndex) throws NoSuchItemTypeException {
        if (taskIndex < 0) {
            startTipTask(0);
        } else {
            AbstractTipTask task = tasks.get(taskIndex);
            if (task.isFulfilled()) {
                startTipTask(taskIndex + 1);
            } else {
                backtrackTask(taskIndex - 1);
            }
        }
    }

    public void onSucceed() {
        try {
            cleanupVisualization();
            int nextTaskIndex = currentTaskIndex + 1;
            if (nextTaskIndex >= tasks.size()) {
                return;
            }
            startTipTask(nextTaskIndex);
        } catch (Exception e) {
            log.log(Level.SEVERE, "GameTipManager.onSucceed()", e);
        }
    }

    private void startVisualization(GameTipVisualization visualization) {
        if (visualization instanceof ItemCockpitGameOverlayTipVisualization) {
            overlayVisualization = (ItemCockpitGameOverlayTipVisualization) visualization;
            overlayTipPanel.create(overlayVisualization.getAbsoluteBuildupPositionHotSpot());
        } else {
            gameTipVisualization = visualization;
        }
    }

    private void cleanupVisualization() {
        if (overlayVisualization != null) {
            overlayTipPanel.close();
            overlayVisualization = null;
        }
        gameTipVisualization = null;
    }

}
