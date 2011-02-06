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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import java.util.Date;
import java.io.Serializable;
import com.btxtech.game.jsre.common.gameengine.services.utg.MissionAction;

/**
 * User: beat
 * Date: 31.01.2010
 * Time: 15:55:52
 */
@Entity(name = "TRACKER_MISSION_ACTION")
@Deprecated
public class DbMissionAction implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date timeStamp;
    @Column(nullable = false)
    private String sessionId;
    @Column(nullable = false)
    private Date clientTimeStamp;
    @Column(nullable = false)
    private String action;
    private String mission;
    private String task;

    /**
     * Used by Hibernate
     */
    public DbMissionAction() {
    }

    public DbMissionAction(MissionAction missionAction, String sessionId) {
        timeStamp = new Date();
        this.sessionId = sessionId;
        clientTimeStamp = missionAction.getTimeStamp();
        action = missionAction.getAction();
        mission = missionAction.getMission();
        task = missionAction.getTask();
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Date getClientTimeStamp() {
        return clientTimeStamp;
    }

    public String getAction() {
        return action;
    }

    public String getMission() {
        return mission;
    }

    public String getTask() {
        return task;
    }
}
