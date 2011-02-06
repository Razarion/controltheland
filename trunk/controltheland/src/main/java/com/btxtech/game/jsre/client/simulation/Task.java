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
import com.btxtech.game.jsre.client.item.ClientItemTypeAccess;
import com.btxtech.game.jsre.client.simulation.hint.Hint;
import com.btxtech.game.jsre.client.simulation.hint.HintFactory;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.common.tutorial.HintConfig;
import com.btxtech.game.jsre.common.tutorial.StepConfig;
import com.btxtech.game.jsre.common.tutorial.TaskConfig;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 13:36:19
 */
public class Task {
    private TaskConfig taskConfig;
    private Step activeStep;
    private Collection<Hint> stepGraphicHints = new ArrayList<Hint>();
    private Collection<Hint> taskGraphicHints = new ArrayList<Hint>();
    private long stepTime;

    public Task(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
        start();
    }

    private void start() {
        ClientItemTypeAccess.getInstance().setAllowedItemTypes(taskConfig.getAllowedItemTypes());
        ClientBase.getInstance().setAccountBalance(taskConfig.getAccountBalance());
        runNextStep();
    }

    public TaskConfig getTaskConfig() {
        return taskConfig;
    }

    public void runNextStep() {
        disposeAllStepHints();
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
        for (HintConfig hintConfig : stepConfig.getGraphicHintConfigs()) {
            Hint hint = HintFactory.createHint(hintConfig);
            if (hintConfig.isCloseOnTaskEnd()) {
                taskGraphicHints.add(hint);
            } else {
                stepGraphicHints.add(hint);
            }
        }
        activeStep = new Step(stepConfig);
    }

    private void disposeAllStepHints() {
        for (Hint stepGraphicHint : stepGraphicHints) {
            stepGraphicHint.dispose();
        }
        stepGraphicHints.clear();
    }

    private void disposeAllTaskHints() {
        for (Hint stepGraphicHint : taskGraphicHints) {
            stepGraphicHint.dispose();
        }
        taskGraphicHints.clear();
    }

    public boolean isFulfilled() {
        return activeStep == null;
    }

    public void cleanup() {
        disposeAllTaskHints();
        disposeAllStepHints();
        activeStep = null;
    }
}
