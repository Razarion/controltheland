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

import com.btxtech.game.jsre.client.StartupTask;
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
    private List<GameStartup> gameStartups = new ArrayList<GameStartup>();
    private Date start;
    private Date end;
    private Long duration;
    private Long startupDuration;
    private String sessionId;
    private boolean isTutorial;

    public LifecycleTrackingInfo(String SessionId, GameStartup gameStartup) {
        sessionId = SessionId;
        gameStartups.add(gameStartup);
        if (!StartupTask.isFirstTask(gameStartup.getState())) {
            throw new IllegalArgumentException("gameStartup must be first task");
        }
        start = gameStartup.getClientTimeStamp();
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public List<GameStartup> getGameStartups() {
        return gameStartups;
    }

    public void setStartupDuration(long startupDuration) {
        this.startupDuration = startupDuration;
    }

    public boolean hasTotalStartupDurtaion() {
        return startupDuration != null;
    }

    public long getStartupDuration() {
        return startupDuration;
    }

    public boolean hasDuration() {
        return duration != null;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
        end = new Date(start.getTime() + duration);
    }

    public String getUserName() {
        if (gameStartups.isEmpty()) {
            return "???";
        } else {
            if (gameStartups.get(0).getUserName() != null) {
                return gameStartups.get(0).getUserName();
            } else {
                return "not registered";
            }
        }
    }

    public String getBaseName() {
        if (gameStartups.isEmpty()) {
            return "???";
        } else {
            return gameStartups.get(0).getBaseName();
        }
    }

    public String getSessionId() {
        return sessionId;
    }

    public boolean isTutorial() {
        return isTutorial;
    }

    public void handleTutorial() {
        isTutorial = UserTrackingService.TUTORIAL_MARKER.equals(gameStartups.get(gameStartups.size() - 1).getBaseName());
    }
}
