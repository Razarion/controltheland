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
import com.btxtech.game.jsre.client.cockpit.quest.QuestVisualisationModel;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.tip.GameTipManager;
import com.btxtech.game.jsre.common.packets.LevelTaskPacket;
import com.btxtech.game.jsre.common.tutorial.AutomatedTaskConfig;
import com.btxtech.game.jsre.common.tutorial.TaskConfig;
import com.google.gwt.user.client.Timer;

/**
 * User: beat Date: 18.07.2010 Time: 13:36:19
 */
public class Task {
    private TaskConfig taskConfig;
    private Timer timer;

    public Task(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
    }

    public void start() {
        if (taskConfig instanceof AutomatedTaskConfig) {
            startAutomatedTask();
        } else {
            startConditionTask();
        }
    }

    private void startAutomatedTask() {
        final Index destination = ((AutomatedTaskConfig) taskConfig).getScrollToPosition();
        timer = new Timer() {
            @Override
            public void run() {
                // TODO make smoother with elapsed time
                Index currentPosition = TerrainView.getInstance().getViewOrigin();
                if (currentPosition.equals(destination)) {
                    timer.cancel();
                    timer = null;
                    Simulation.getInstance().onTaskTipCompleted();
                }
                TerrainView.getInstance().moveAbsolute(currentPosition.getPointWithDistance(20, destination, false));
            }
        };
        timer.scheduleRepeating(25);
    }

    private void startConditionTask() {
        ClientBase.getInstance().setAccountBalance(taskConfig.getMoney());
        new Timer() {
            @Override
            public void run() {
                // TODO: timer wegmachen
                // TODO: Activate the condition after the items have been created and deactivated in the ActionHandler
                // TODO: Prevent condition trigger due to deactivation of items

                // TODO make special scroll task (oder so)
                if (taskConfig.getConditionConfig() != null) {
                    SimulationConditionServiceImpl.getInstance().activateCondition(taskConfig.getConditionConfig(), ClientBase.getInstance().getSimpleBase(), null);
                    LevelTaskPacket levelTaskPacket = new LevelTaskPacket();
                    levelTaskPacket.setQuestInfo(new QuestInfo(taskConfig.getName(),
                            null,
                            taskConfig.getConditionConfig().getAdditionalDescription(),
                            null,
                            0,
                            0,
                            0,
                            QuestInfo.Type.MISSION,
                            taskConfig.getConditionConfig().getRadarPositionHint(),
                            taskConfig.getConditionConfig().isHideQuestProgress(),
                            null));
                    levelTaskPacket.setQuestProgressInfo(SimulationConditionServiceImpl.getInstance().getQuestProgressInfo(ClientBase.getInstance().getSimpleBase(), null));
                    QuestVisualisationModel.getInstance().setLevelTask(levelTaskPacket);
                }
                // TODO make special scroll task (oder so)
                GameTipManager.getInstance().start(taskConfig.getGameTipConfig());
            }
        }.schedule(200);
    }

    public TaskConfig getTaskConfig() {
        return taskConfig;
    }
}
