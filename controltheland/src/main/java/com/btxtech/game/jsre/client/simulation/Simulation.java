/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.jsre.client.simulation;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ClientPlanetServices;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameCommon;
import com.btxtech.game.jsre.client.ParametrisedRunnable;
import com.btxtech.game.jsre.client.bot.ClientBotService;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.client.control.GameStartupSeq;
import com.btxtech.game.jsre.client.control.StartupScreen;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.ClientUserGuidanceService;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.client.utg.tip.StorySplashPopup;
import com.btxtech.game.jsre.client.utg.tip.dialog.TipManager;
import com.btxtech.game.jsre.client.utg.tip.tiptask.AbstractTipTask;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.jsre.common.perfmon.TimerPerfmon;
import com.btxtech.game.jsre.common.tutorial.AbstractTaskConfig;
import com.btxtech.game.jsre.common.tutorial.GameFlow;
import com.btxtech.game.jsre.common.tutorial.ItemTypeAndPosition;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.google.gwt.user.client.Timer;

import java.util.List;

/**
 * User: beat
 * Date: 17.07.2010
 * Time: 17:21:24
 */
// TODO noob protection, if unit is send near bot
public class Simulation implements ClientBase.OwnBaseDestroyedListener {
    private static final int PRAISE_DELAY = 3000;
    private static final Simulation SIMULATION = new Simulation();
    private SimulationInfo simulationInfo;
    private AbstractTask activeAbstractTask;
    private long taskTime;
    private long tutorialTime;
    private TutorialConfig tutorialConfig;
    private StorySplashPopup praiseSplashPopup;

    /**
     * Singleton
     */
    private Simulation() {
    }

    public static Simulation getInstance() {
        return SIMULATION;
    }

    public void start() {
        simulationInfo = (SimulationInfo) Connection.getInstance().getGameInfo();
        tutorialConfig = simulationInfo.getTutorialConfig();
        if (tutorialConfig == null) {
            return;
        }
        ClientBase.getInstance().setOwnBaseDestroyedListener(this);
        tutorialTime = System.currentTimeMillis();
        if (tutorialConfig.isEventTracking()) {
            ClientUserTracker.getInstance().startEventTracking();
        }
        if (tutorialConfig.isShowTip()) {
            TipManager.getInstance().activate();
        }
        TerrainView.getInstance().getTerrainScrollHandler().setScrollDisabled(tutorialConfig.isDisableScroll());
        runNextTask(null);
    }

    private void processPreparation(AbstractTaskConfig abstractTaskConfig) {
        if (abstractTaskConfig.isClearGame()) {
            GameCommon.clearGame();
        }
        ClientBase.getInstance().createOwnSimulationBaseIfNotExist(tutorialConfig.getOwnBaseName());
        ClientPlanetServices.getInstance().setPlanetInfo(abstractTaskConfig.createPlanetInfo());
        ClientUserGuidanceService.getInstance().setLevel(abstractTaskConfig.createLevelScope(simulationInfo.getLevelNumber()));

        if (abstractTaskConfig.hasBotIdsToStop()) {
            for (Integer botId : abstractTaskConfig.getBotIdsToStop()) {
                ClientBotService.getInstance().killBot(botId);
            }
        }

        if (abstractTaskConfig.hasBots()) {
            ClientBotService.getInstance().startBots(abstractTaskConfig.getBotConfigs());
        }

        for (ItemTypeAndPosition itemTypeAndPosition : abstractTaskConfig.getOwnItems()) {
            try {
                ItemContainer.getInstance().createSimulationSyncObject(itemTypeAndPosition);
            } catch (NoSuchItemTypeException e) {
                ClientExceptionHandler.handleException(e);
            }
        }

        if (abstractTaskConfig.getScroll() != null) {
            TerrainView.getInstance().moveAbsolute(abstractTaskConfig.getScroll());
        }
    }

    private void runNextTask(AbstractTask closedAbstractTask) {
        AbstractTaskConfig abstractTaskConfig;
        List<AbstractTaskConfig> tasks = tutorialConfig.getTasks();
        if (tasks.isEmpty()) {
            tutorialFinished();
            return;
        }
        if (closedAbstractTask != null) {
            int index = tasks.indexOf(closedAbstractTask.getAbstractTaskConfig());
            index++;
            if (tasks.size() > index) {
                abstractTaskConfig = tasks.get(index);
            } else {
                tutorialFinished();
                return;
            }
        } else {
            abstractTaskConfig = tasks.get(0);
        }
        processPreparation(abstractTaskConfig);
        taskTime = System.currentTimeMillis();
        activeAbstractTask = createTask(abstractTaskConfig);
        activeAbstractTask.start();
    }

    private void tutorialFinished() {
        if (activeAbstractTask != null) {
            activeAbstractTask.cleanup();
        }
        activeAbstractTask = null;
        long time = System.currentTimeMillis();
        ClientUserTracker.getInstance().onTutorialFinished(simulationInfo.getLevelTaskId(), time - tutorialTime, time, new ParametrisedRunnable<GameFlow>() {
            @Override
            public void run(GameFlow gameFlow) {
                ClientUserGuidanceService.getInstance().onTutorialFlow(gameFlow);
            }
        });
    }

    public void cleanup() {
        if (activeAbstractTask != null) {
            activeAbstractTask.cleanup();
        }
        activeAbstractTask = null;
        simulationInfo = null;
    }

    public void onTaskSucceeded() {
        long time = System.currentTimeMillis();
        ClientUserTracker.getInstance().onTaskFinished(simulationInfo.getLevelTaskId(), activeAbstractTask, time - taskTime, time);
        activeAbstractTask.cleanup();
        if (hasPraisePopup()) {
            startPraisePopup();
            Timer deferredTimer = new Timer() {

                @Override
                public void run() {
                    hidePraisePopup();
                    runNextTask(activeAbstractTask);
                }
            };
            deferredTimer.schedule(PRAISE_DELAY);
        } else {
            runNextTask(activeAbstractTask);
        }
    }

    @Override
    public void onOwnBaseDestroyed() {
        // TODO was soll genau geschehen wenn basis zerstört wird
        long time = System.currentTimeMillis();
        ClientUserTracker.getInstance().onTutorialFailed(simulationInfo.getLevelTaskId(), time - tutorialTime, time);
        Timer timer = new TimerPerfmon(PerfmonEnum.SIMULATION) {
            @Override
            public void runPerfmon() {
                StartupScreen.getInstance().fadeOutAndStart(GameStartupSeq.WARM_SIMULATED);
            }
        };
        timer.schedule(1000);
    }

    public Integer getLevelTaskId() {
        if (simulationInfo != null) {
            return simulationInfo.getLevelTaskId();
        } else {
            return null;
        }
    }


    private AbstractTask createTask(AbstractTaskConfig abstractTaskConfig) {
        AbstractTask abstractTask = abstractTaskConfig.createTask();
        abstractTask.setSimulation(this);
        return abstractTask;
    }

    private void startPraisePopup() {
        praiseSplashPopup = new StorySplashPopup(activeAbstractTask.getPraiseSplashPopupInfo());
    }

    private boolean hasPraisePopup() {
        return activeAbstractTask.getPraiseSplashPopupInfo() != null;
    }

    private void hidePraisePopup() {
        if (praiseSplashPopup != null) {
            praiseSplashPopup.fadeOut();
            praiseSplashPopup = null;
        }
    }

    public void onTipTaskChanged(AbstractTipTask currentTipTask) {
        activeAbstractTask.onTipTaskChanged(currentTipTask);
    }

    public void onTipTaskConversion() {
        activeAbstractTask.onTipTaskConversion();
    }

    public void onTipTaskPoorConversion() {
        activeAbstractTask.onTaskPoorConversion();
    }
}
