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

import com.btxtech.game.services.base.Base;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

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
    private String level;
    @Column(nullable = false)
    private String sessionId;
    private long startupDuration;
    private long clientTimeStamp;
    private long serverTimeStamp;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @org.hibernate.annotations.IndexColumn(name = "orderIndex", nullable = false, base = 0)
    @JoinColumn(name = "dbStartup", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Collection<DbStartupTask> dbStartupTasks;
    private boolean realGame;
    private String baseName;
    private Integer baseId;

    /**
     * Used by Hibernate
     */
    public DbStartup() {
    }

    public DbStartup(long startupDuration, long clientTimeStamp, DbAbstractLevel abstractLevel, String sessionId, String baseName, Integer baseId) {
        this.clientTimeStamp = clientTimeStamp;
        serverTimeStamp = System.currentTimeMillis();
        this.level = abstractLevel.getName();
        this.sessionId = sessionId;
        this.startupDuration = startupDuration;
        timeStamp = new Date();
        if (abstractLevel instanceof DbRealGameLevel) {
            realGame = true;
        } else if (abstractLevel instanceof DbSimulationLevel) {
            realGame = false;
        } else {
            throw new IllegalArgumentException("Unknown level type: " + abstractLevel);
        }
        this.baseName = baseName;
        this.baseId = baseId;
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

    public long getServerTimeStamp() {
        return serverTimeStamp;
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
