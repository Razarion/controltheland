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

import com.btxtech.game.services.utg.tracker.DbStartup;
import com.btxtech.game.services.utg.tracker.DbStartupTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 08.08.2010
 * Time: 14:03:07
 */
public class LifecycleTrackingInfo implements Serializable {
    private List<DbStartupTask> dbStartupTasks = new ArrayList<DbStartupTask>();
    private long startServer;
    private long startClient;
    private long startupDuration;
    private String sessionId;
    private Long nextStartServer;
    private Long nextStartClient;
    private String level;
    private boolean realGame;
    private String baseName;
    private Integer baseId;


    public LifecycleTrackingInfo(String sessionId, DbStartup startup) {
        this.sessionId = sessionId;
        startServer = startup.getServerTimeStamp();
        startClient = startup.getClientTimeStamp();
        dbStartupTasks = startup.getGameStartupTasks();
        startupDuration = startup.getStartupDuration();
        level = startup.getLevel();
        realGame = startup.isRealGame();
        baseName = startup.getBaseName();
        baseId = startup.getBaseId();
    }

    public long getStartClient() {
        return startClient;
    }

    public long getStartServer() {
        return startServer;
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

    public void setNext(DbStartup nextStartup) {
        nextStartServer = nextStartup.getServerTimeStamp();
        nextStartClient = nextStartup.getClientTimeStamp();
    }

    public Long getNextStartServer() {
        return nextStartServer;
    }

    public Long getNextStartClient() {
        return nextStartClient;
    }

    public String getLevel() {
        return level;
    }

    public boolean isRealGame() {
        return realGame;
    }

    public String getBaseName() {
        return baseName;
    }

    public Integer getBaseId() {
        return baseId;
    }
}
