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

package com.btxtech.game.services.utg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: beat
 * Date: 08.08.2010
 * Time: 14:03:07
 */
public class LifecycleTrackingInfo implements Serializable {
    private List<DbStartupTask> dbStartupTasks = new ArrayList<DbStartupTask>();
    private Date start;
    private long startupDuration;
    private String sessionId;
    private Date end;
    private String userStage;

    public LifecycleTrackingInfo(String sessionId, DbStartup startup) {
        this.sessionId = sessionId;
        start = new Date(startup.getClientTimeStamp());
        dbStartupTasks = (List<DbStartupTask>) startup.getGameStartupTasks();
        startupDuration = startup.getStartupDuration();
        userStage = startup.getLevel();
    }

    public Date getStart() {
        return start;
    }

    public List<DbStartupTask> getGameStartups() {
        return dbStartupTasks;
    }

    public long getStartupDuration() {
        return startupDuration;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getUserStage() {
        return userStage;
    }
}
