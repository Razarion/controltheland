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
import java.util.Collection;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import org.hibernate.annotations.Cascade;

/**
 * User: beat
 * Date: 04.08.2010
 * Time: 20:08:25
 */
@Entity(name = "TRACKER_STARTUP")
public class DbStartup implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date timeStamp;
    private String userStage;
    @Column(nullable = false)
    private String sessionId;
    private long startupDuration;
    private long clientTimeStamp;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @org.hibernate.annotations.IndexColumn(name = "orderIndex", nullable = false, base = 0)
    @JoinColumn(name = "dbStartup", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private Collection<DbStartupTask> dbStartupTasks;

    /**
     * Used by Hibernate
     */
    public DbStartup() {
    }

    public DbStartup(long startupDuration, long clientTimeStamp, DbUserStage userStage, String sessionId) {
        this.clientTimeStamp = clientTimeStamp;
        this.userStage = userStage.getName();
        this.sessionId = sessionId;
        this.startupDuration = startupDuration;
        timeStamp = new Date();
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public long getStartupDuration() {
        return startupDuration;
    }

    public long getClientTimeStamp() {
        return clientTimeStamp;
    }

    public Collection<DbStartupTask> getGameStartupTasks() {
        return dbStartupTasks;
    }

    public void addGameStartupTasks(DbStartupTask dbStartupTask) {
        if (dbStartupTasks == null) {
            dbStartupTasks = new ArrayList<DbStartupTask>();
        }
        dbStartupTasks.add(dbStartupTask);
    }

    public String getUserStage() {
        return userStage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbStartup)) return false;

        DbStartup that = (DbStartup) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
