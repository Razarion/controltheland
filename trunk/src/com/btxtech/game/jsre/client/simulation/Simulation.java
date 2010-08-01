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
import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.InfoPanel;
import com.btxtech.game.jsre.client.OnlineBasePanel;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionListener;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.tutorial.ItemTypeAndPosition;
import com.btxtech.game.jsre.common.tutorial.TaskConfig;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import java.util.List;

/**
 * User: beat
 * Date: 17.07.2010
 * Time: 17:21:24
 */
public class Simulation implements SelectionListener {
    private static final Simulation SIMULATION = new Simulation();
    private SimulationInfo simulationInfo;
    private Task activeTask;
    private TutorialGui tutorialGui;
    private long taskTime;
    private long tutorialTime;

    /**
     * Singleton
     */
    private Simulation() {
        SelectionHandler.getInstance().addSelectionListener(this);
    }

    public static Simulation getInstance() {
        return SIMULATION;
    }

    public void start() {
        if (Connection.getInstance().getGameInfo() instanceof SimulationInfo) {
            simulationInfo = (SimulationInfo) Connection.getInstance().getGameInfo();
            TutorialConfig tutorialConfig = simulationInfo.getTutorialConfig();
            if (tutorialConfig == null) {
                return;
            }
            tutorialGui = new TutorialGui();
            ClientBase.getInstance().setBase(tutorialConfig.getOwnBase());
            tutorialTime = System.currentTimeMillis();
            runNextTask(activeTask);
        }
    }

    private void processPreparation(TaskConfig taskConfig) {
        if (taskConfig.isClearGame()) {
            clearGame();
        }

        OnlineBasePanel.getInstance().setVisible(taskConfig.isOnlineBoxVisible());
        InfoPanel.getInstance().setVisible(taskConfig.isInfoBoxVisible());
        RadarPanel.getInstance().setVisible(taskConfig.isScrollingAllowed());
        MapWindow.getInstance().setScrollingAllowed(taskConfig.isScrollingAllowed());
        InfoPanel.getInstance().getScrollHome().setEnabled(taskConfig.isScrollingAllowed());

        for (ItemTypeAndPosition itemTypeAndPosition : taskConfig.getOwnItems()) {
            try {
                ItemContainer.getInstance().createSimulationSyncObject(itemTypeAndPosition);
            } catch (NoSuchItemTypeException e) {
                GwtCommon.handleException(e);
            }
        }

        if (taskConfig.getScroll() != null) {
            TerrainView.getInstance().moveAbsolute(taskConfig.getScroll());
        }
    }

    private void clearGame() {
        ItemContainer.getInstance().clear();
        ActionHandler.getInstance().clear();
        SelectionHandler.getInstance().clearSelection();
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
        activeTask = new Task(taskConfig, tutorialGui);
    }

    private void tutorialFinished() {
        activeTask = null;
        ClientUserTracker.getInstance().onTutorialFinished(System.currentTimeMillis() - tutorialTime);
        Window.Location.reload();
    }


    private void checkForTaskCompletion() {
        if (activeTask.isFulFilled()) {
            ClientUserTracker.getInstance().onTaskFinished(activeTask, System.currentTimeMillis() - taskTime);
            if (activeTask.getTaskConfig().getFinishedText() == null
                    || activeTask.getTaskConfig().getFinishedText().isEmpty()
                    || activeTask.getTaskConfig().getFinishedTextDuration() <= 0) {
                runNextTask(activeTask);
            } else {
                final Task closedTask = activeTask;
                activeTask = null;
                tutorialGui.showFinishedText(closedTask.getTaskConfig().getFinishedText());
                Timer timer = new Timer() {
                    @Override
                    public void run() {
                        runNextTask(closedTask);
                    }
                };
                timer.schedule(closedTask.getTaskConfig().getFinishedTextDuration());
            }
        }

    }

    @Override
    public void onTargetSelectionChanged(ClientSyncItemView selection) {
        // Ignore
    }

    @Override
    public void onSelectionCleared() {
        // Ignore
    }

    @Override
    public void onOwnSelectionChanged(Group selectedGroup) {
        if (activeTask != null) {
            activeTask.onOwnSelectionChanged(selectedGroup);
            checkForTaskCompletion();
        }
    }

    public void onSendCommand(SyncBaseItem syncItem, BaseCommand baseCommand) {
        if (activeTask != null) {
            activeTask.onSendCommand(syncItem, baseCommand);
            checkForTaskCompletion();
        }
    }

    public void onSyncItemDeactivated(SyncBaseItem deactivatedItem) {
        if (activeTask != null) {
            activeTask.onSyncItemDeactivated(deactivatedItem);
            checkForTaskCompletion();
        }
    }

    public void onSyncItemKilled(SyncItem killedItem, SyncBaseItem actor) {
        if (activeTask != null) {
            activeTask.onSyncItemKilled(killedItem, actor);
            checkForTaskCompletion();
        }
    }

    public void onItemBuilt(SyncBaseItem syncBaseItem) {
        if (activeTask != null) {
            activeTask.onItemBuilt(syncBaseItem);
            checkForTaskCompletion();
        }
    }

    public void onDeposit() {
        if (activeTask != null) {
            activeTask.onDeposit();
            checkForTaskCompletion();
        }
    }
}
