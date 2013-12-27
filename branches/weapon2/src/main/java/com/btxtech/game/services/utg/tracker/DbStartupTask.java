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

package com.btxtech.game.services.utg.tracker;

import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.utg.DbLevel;
import org.hibernate.annotations.Index;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * User: beat
 * Date: 12.01.2010
 * Time: 22:45:47
 */
@Entity(name = "TRACKER_STARTUP_TASK")
public class DbStartupTask implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Index(name = "TRACKER_STARTUP_INDEX_SESSION")
    private String sessionId;
    @Column(nullable = false)
    private long clientTimeStamp;
    @Column(nullable = false)
    private String task;
    private long duration;
    @Column(length = 50000)
    private String failureText;
    @Index(name = "TRACKER_STARTUP_INDEX_START_UUID")
    private String startUuid;
    private Integer levelTaskId;
    private Integer baseId;
    private String baseName;
    private Date timeStamp;
    private String levelName;
    private String userName;

    /**
     * Used by Hibernate
     */
    DbStartupTask() {
    }

    public DbStartupTask(String sessionId, StartupTaskInfo startupTaskInfo, String startUuid, DbLevel dbLevel, Integer levelTaskId, User user, Integer baseId, String baseName) {
        this.sessionId = sessionId;
        this.startUuid = startUuid;
        this.levelTaskId = levelTaskId;
        this.baseId = baseId;
        clientTimeStamp = startupTaskInfo.getStartTime();
        task = startupTaskInfo.getTaskEnum().getStartupTaskEnumHtmlHelper().getNiceText();
        duration = startupTaskInfo.getDuration();
        failureText = startupTaskInfo.getError();
        levelName = dbLevel.getName();
        userName = user != null ? user.getUsername() : null;
        this.baseName = baseName;
        timeStamp = new Date();
    }

    public Date getClientTimeStamp() {
        return new Date(clientTimeStamp);
    }

    public long getDuration() {
        return duration;
    }

    public String getFailureText() {
        return failureText;
    }

    public String getTask() {
        return task;
    }

    public String getStartUuid() {
        return startUuid;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getBaseName() {
        return baseName;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getLevelName() {
        return levelName;
    }

    public String getUserName() {
        return userName;
    }

    public Integer getLevelTaskId() {
        return levelTaskId;
    }

    public Integer getBaseId() {
        return baseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbStartupTask that = (DbStartupTask) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}