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
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
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
    private Step activeStep;
    private long stepTime;

    public Task(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
        start();
    }

    private void start() {
        ClientBase.getInstance().setAccountBalance(taskConfig.getAccountBalance());
        runNextStep();
    }

    public TaskConfig getTaskConfig() {
        return taskConfig;
    }

    public void runNextStep() {
        StepConfig stepConfig;
        List<StepConfig> stepConfigs = taskConfig.getStepConfigs();
        if (stepConfigs.isEmpty()) {
            activeStep = null;
            return;
        }
        if (activeStep != null) {
            int index = stepConfigs.indexOf(activeStep.getStepConfig());
            index++;
            if (stepConfigs.size() > index) {
                stepConfig = stepConfigs.get(index);
            } else {
                activeStep = null;
                return;
            }
        } else {
            stepConfig = stepConfigs.get(0);
        }
        runNextStep(stepConfig);
    }

    private void runNextStep(StepConfig stepConfig) {
        if (activeStep != null) {
            long time = System.currentTimeMillis();
            ClientUserTracker.getInstance().onStepFinished(activeStep, this, time - stepTime, time);
        }
        stepTime = System.currentTimeMillis();
        activeStep = new Step(stepConfig);
    }

    public boolean isFulfilled() {
        return activeStep == null;
    }

    public void cleanup() {
        activeStep = null;
    }
}
