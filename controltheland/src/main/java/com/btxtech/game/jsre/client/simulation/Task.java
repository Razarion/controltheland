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
import com.btxtech.game.jsre.common.tutorial.TaskConfig;

import java.util.List;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 13:36:19
 */
public class Task {
    private TaskConfig taskConfig;

    public Task(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
    }

    public void start() {
        ClientBase.getInstance().setAccountBalance(taskConfig.getMoney());
        SimulationConditionServiceImpl.getInstance().activateCondition(taskConfig.getConditionConfig(), ClientBase.getInstance().getSimpleBase(), null);        
    }

    public TaskConfig getTaskConfig() {
        return taskConfig;
    }
}
