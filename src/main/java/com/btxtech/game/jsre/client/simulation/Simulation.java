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
import com.btxtech.game.jsre.client.utg.tip.GameTipManager;
import com.btxtech.game.jsre.client.utg.tip.dialog.TipManager;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.jsre.common.perfmon.TimerPerfmon;
import com.btxtech.game.jsre.common.tutorial.GameFlow;
import com.btxtech.game.jsre.common.tutorial.ItemTypeAndPosition;
import com.btxtech.game.jsre.common.tutorial.TaskConfig;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.google.gwt.user.client.Timer;

import java.util.List;

/**
 * User: beat
 * Date: 17.07.2010
 * Time: 17:21:24
 */
public class Simulation implements ConditionServiceListener<SimpleBase, Void>, ClientBase.OwnBaseDestroyedListener {
    private static final Simulation SIMULATION = new Simulation();
    private SimulationInfo simulationInfo;
    private Task activeTask;
    private long taskTime;
    private long tutorialTime;
    private TutorialConfig tutorialConfig;

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
        SimulationConditionServiceImpl.getInstance().setConditionServiceListener(this);
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

    private void processPreparation(TaskConfig taskConfig) {
        if (taskConfig.isClearGame()) {
            GameCommon.clearGame();
        }
        ClientBase.getInstance().createOwnSimulationBaseIfNotExist(tutorialConfig.getOwnBaseName());
        ClientPlanetServices.getInstance().setPlanetInfo(taskConfig.createPlanetInfo());
        ClientUserGuidanceService.getInstance().setLevel(taskConfig.createLevelScope(simulationInfo.getLevelNumber()));
        if (taskConfig.hasBots()) {
            ClientBotService.getInstance().setBotConfigs(taskConfig.getBotConfigs());
            ClientBotService.getInstance().start();
        }

        for (ItemTypeAndPosition itemTypeAndPosition : taskConfig.getOwnItems()) {
            try {
                ItemContainer.getInstance().createSimulationSyncObject(itemTypeAndPosition);
            } catch (NoSuchItemTypeException e) {
                ClientExceptionHandler.handleException(e);
            }
        }

        if (taskConfig.getScroll() != null) {
            TerrainView.getInstance().moveAbsolute(taskConfig.getScroll());
        }
    }

    private void runNextTask(Task closedTask) {
        TaskConfig taskConfig;
        List<TaskConfig> tasks = simulationInfo.getTutorialConfig().getTasks();
        if (tasks.isEmpty()) {
            tutorialFinished();
            return;
        }
        if (closedTask != null) {
            int index = tasks.indexOf(closedTask.getTaskConfig());
            index++;
            if (tasks.size() > index) {
                taskConfig = tasks.get(index);
            } else {
                tutorialFinished();
                return;
            }
        } else {
            taskConfig = tasks.get(0);
        }
        processPreparation(taskConfig);
        taskTime = System.currentTimeMillis();
        activeTask = new Task(taskConfig);
        activeTask.start();
    }

    private void tutorialFinished() {
        activeTask = null;
        long time = System.currentTimeMillis();
        ClientUserTracker.getInstance().onTutorialFinished(simulationInfo.getLevelTaskId(), time - tutorialTime, time, new ParametrisedRunnable<GameFlow>() {
            @Override
            public void run(GameFlow gameFlow) {
                ClientUserGuidanceService.getInstance().onTutorialFlow(gameFlow);
            }
        });
    }

    public void cleanup() {
        activeTask = null;
        SimulationConditionServiceImpl.getInstance().setConditionServiceListener(null);
        SimulationConditionServiceImpl.getInstance().deactivateAll();
        SimulationConditionServiceImpl.getInstance().stopUpdateTimer();
        simulationInfo = null;
    }

    @Override
    public void conditionPassed(SimpleBase actor, Void identifier) {
        if (!ClientBase.getInstance().isMyOwnBase(actor)) {
            throw new IllegalStateException("Received conditionPassed for unexpected base: " + actor);
        }
        long time = System.currentTimeMillis();
        ClientUserTracker.getInstance().onTaskFinished(simulationInfo.getLevelTaskId(), activeTask, time - taskTime, time);
        GameTipManager.getInstance().stop();
        runNextTask(activeTask);
    }

    @Override
    public void onOwnBaseDestroyed() {
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
}
