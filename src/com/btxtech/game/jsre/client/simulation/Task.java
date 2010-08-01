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
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.item.ClientItemTypeAccess;
import com.btxtech.game.jsre.client.simulation.condition.AbstractCondition;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.tutorial.StepConfig;
import com.btxtech.game.jsre.common.tutorial.TaskConfig;
import java.util.List;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 13:36:19
 */
public class Task {
    private TaskConfig taskConfig;
    private TutorialGui tutorialGui;
    private Step activeStep;
    private GraphicHint stepGraphicHint;
    private GraphicHint taskGraphicHint;
    private AbstractCondition completionCondition;


    public Task(TaskConfig taskConfig, TutorialGui tutorialGui) {
        this.taskConfig = taskConfig;
        this.tutorialGui = tutorialGui;
        tutorialGui.setTaskText(taskConfig.getDescription());
        start();
    }

    private void start() {
        ClientItemTypeAccess.getInstance().setAllowedItemTypes(taskConfig.getAllowedItemTypes());
        ClientBase.getInstance().setAccountBalance(taskConfig.getAccountBalance());
        if (taskConfig.getGraphicHintConfig() != null) {
            taskGraphicHint = new GraphicHint(taskConfig.getGraphicHintConfig());
        }
        completionCondition = ConditionFactory.createCondition(taskConfig.getCompletionConditionConfig());
        runNextStep();
    }

    public TaskConfig getTaskConfig() {
        return taskConfig;
    }

    public void onOwnSelectionChanged(Group selectedGroup) {
        if (activeStep != null) {
            activeStep.onOwnSelectionChanged(selectedGroup);
            checkForCompletion();
        }
        if (completionCondition != null && completionCondition.isFulfilledSelection(selectedGroup)) {
            taskFinished();
        }
    }

    public void onSendCommand(SyncBaseItem syncItem, BaseCommand baseCommand) {
        if (activeStep != null) {
            activeStep.onSendCommand(syncItem, baseCommand);
            checkForCompletion();
        }
        if (completionCondition != null && completionCondition.isFulfilledSendCommand(syncItem, baseCommand)) {
            taskFinished();
        }
    }

    public void onSyncItemDeactivated(SyncBaseItem deactivatedItem) {
        if (activeStep != null) {
            activeStep.onSyncItemDeactivated(deactivatedItem);
            checkForCompletion();
        }
        if (completionCondition != null && completionCondition.isFulfilledSyncItemDeactivated(deactivatedItem)) {
            taskFinished();
        }
    }

    public void onSyncItemKilled(SyncItem killedItem, SyncBaseItem actor) {
        if (activeStep != null) {
            activeStep.onSyncItemKilled(killedItem, actor);
            checkForCompletion();
        }
        if (completionCondition != null && completionCondition.isFulfilledItemsKilled(killedItem, actor)) {
            taskFinished();
        }
    }

    public void onItemBuilt(SyncBaseItem syncBaseItem) {
        if (activeStep != null) {
            activeStep.onItemBuilt(syncBaseItem);
            checkForCompletion();
        }
        if (completionCondition != null && completionCondition.isFulfilledItemBuilt(syncBaseItem)) {
            taskFinished();
        }
    }

    public void onDeposit() {
        if (activeStep != null) {
            activeStep.onDeposit();
            checkForCompletion();
        }
        if (completionCondition != null && completionCondition.isFulfilledHarvest()) {
            taskFinished();
        }
    }

    private void runNextStep() {
        if (stepGraphicHint != null) {
            stepGraphicHint.dispose();
            stepGraphicHint = null;
        }
        StepConfig stepConfig;
        List<StepConfig> stepConfigs = taskConfig.getStepConfigs();
        if (stepConfigs.isEmpty()) {
            if (completionCondition == null) {
                taskFinished();
            }
            activeStep = null;
            return;
        }
        if (activeStep != null) {
            int index = stepConfigs.indexOf(activeStep.getStepConfig());
            index++;
            if (stepConfigs.size() > index) {
                stepConfig = stepConfigs.get(index);
            } else {
                if (completionCondition == null) {
                    taskFinished();
                }
                activeStep = null;
                return;
            }
        } else {
            stepConfig = stepConfigs.get(0);
        }
        runNextStep(stepConfig);
    }

    private void runNextStep(StepConfig stepConfig) {
        System.out.println("*** Next Step started");
        if (stepConfig.getGraphicHintConfig() != null) {
            stepGraphicHint = new GraphicHint(stepConfig.getGraphicHintConfig());
        }
        activeStep = new Step(stepConfig, tutorialGui);
    }

    private void checkForCompletion() {
        if (activeStep.isFulFilled()) {
            runNextStep();
        }
    }

    private void taskFinished() {
        cleanup();
    }

    private void cleanup() {
        if (taskGraphicHint != null) {
            taskGraphicHint.dispose();
        }
        if (stepGraphicHint != null) {
            stepGraphicHint.dispose();
        }
        activeStep = null;
        completionCondition = null;
    }

    public boolean isFulFilled() {
        return activeStep == null && completionCondition == null;
    }
}
