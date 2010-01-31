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

package com.btxtech.game.jsre.common.gameengine.services.utg;

import java.io.Serializable;
import java.util.Date;

/**
 * User: beat
 * Date: 31.01.2010
 * Time: 15:18:45
 */
public class MissionAction implements Serializable {
    public static final String MISSION_START = "MISSION_START";
    public static final String TASK_START = "TASK_START";
    public static final String MISSION_COMPLETED = "MISSION_COMPLETED";

    private String action;
    private String description;
    private String task;
    private Date timeStamp;

    /**
     * Used by GWT
     */
    public MissionAction() {
    }

    public MissionAction(String action, String description, String task) {
        this.action = action;
        this.description = description;
        this.task = task;
        timeStamp = new Date();
    }

    public String getAction() {
        return action;
    }

    public String getDescription() {
        return description;
    }

    public String getTask() {
        return task;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }
}
