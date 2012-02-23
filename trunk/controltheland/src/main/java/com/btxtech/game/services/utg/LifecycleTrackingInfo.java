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

import com.btxtech.game.services.utg.tracker.DbStartupTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 08.08.2010
 * Time: 14:03:07
 */
public class LifecycleTrackingInfo implements Serializable, Comparable<LifecycleTrackingInfo> {
    private List<DbStartupTask> dbStartupTasks = new ArrayList<DbStartupTask>();
    private String levelTaskName;
    private String sessionId;
    private String startUuid;
    private LifecycleTrackingInfo nextReaGameLifecycleTrackingInfo;

    public LifecycleTrackingInfo(List<DbStartupTask> dbStartupTasks, String levelTaskName) {
        if (dbStartupTasks.isEmpty()) {
            throw new IllegalStateException("Multiple DbStartupTask expected for: " + startUuid + " startups.size(): " + dbStartupTasks.size());
        }
        sessionId = dbStartupTasks.get(0).getSessionId();
        startUuid = dbStartupTasks.get(0).getStartUuid();
        this.dbStartupTasks = dbStartupTasks;
        this.levelTaskName = levelTaskName;
    }

    public List<DbStartupTask> getGameStartups() {
        return dbStartupTasks;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getStartUuid() {
        return startUuid;
    }

    public String getLevel() {
        if (dbStartupTasks.isEmpty()) {
            return "";
        } else {
            return dbStartupTasks.get(0).getLevelName();
        }
    }

    public long getStartServer() {
        if (dbStartupTasks.isEmpty()) {
            return 0;
        } else {
            return dbStartupTasks.get(0).getTimeStamp().getTime();
        }
    }

    public Long getNextStartServer() {
        if (nextReaGameLifecycleTrackingInfo != null) {
            return nextReaGameLifecycleTrackingInfo.getStartServer();
        } else {
            return null;
        }
    }

    public long getStartupDuration() {
        long total = 0;
        for (DbStartupTask dbStartupTask : dbStartupTasks) {
            total += dbStartupTask.getDuration();
        }
        return total;
    }

    public String getBaseName() {
        if (dbStartupTasks.isEmpty()) {
            return null;
        } else {
            return dbStartupTasks.get(0).getBaseName();
        }
    }

    public boolean isRealGame() {
        return getLevelTaskId() == null;
    }

    public String getLevelTaskName() {
        return levelTaskName;
    }

    public Integer getLevelTaskId() {
        for (DbStartupTask dbStartupTask : dbStartupTasks) {
            if (dbStartupTask.getLevelTaskId() != null) {
                return dbStartupTask.getLevelTaskId();
            }
        }
        return null;
    }

    public void setNextReaGameLifecycleTrackingInfo(LifecycleTrackingInfo nextReaGameLifecycleTrackingInfo) {
        this.nextReaGameLifecycleTrackingInfo = nextReaGameLifecycleTrackingInfo;
    }

    public LifecycleTrackingInfo getNextReaGameLifecycleTrackingInfo() {
        return nextReaGameLifecycleTrackingInfo;
    }

    public Integer getBaseId() {
        if (dbStartupTasks.isEmpty()) {
            return null;
        } else {
            return dbStartupTasks.get(0).getBaseId();
        }
    }

    public String getUserName() {
        if (dbStartupTasks.isEmpty()) {
            return null;
        } else {
            return dbStartupTasks.get(0).getUserName();
        }
    }

    @Override
    public int compareTo(LifecycleTrackingInfo o) {
        return (int) (getStartServer() - o.getStartServer());
    }
}
