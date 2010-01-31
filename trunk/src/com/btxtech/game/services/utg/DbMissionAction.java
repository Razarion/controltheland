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
import com.btxtech.game.jsre.common.gameengine.services.utg.MissionAction;

/**
 * User: beat
 * Date: 31.01.2010
 * Time: 15:55:52
 */
@Entity(name = "TRACKER_MISSION_ACTION")
public class DbMissionAction {
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
    private String description;
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
        description = missionAction.getDescription();
        task = missionAction.getTask();
    }
}
