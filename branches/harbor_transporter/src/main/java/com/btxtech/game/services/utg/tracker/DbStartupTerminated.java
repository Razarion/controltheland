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

import org.hibernate.annotations.Index;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * User: beat
 * Date: 12.01.2010
 * Time: 22:45:47
 */
@Entity(name = "TRACKER_STARTUP_TERMINATED")
public class DbStartupTerminated implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Index(name = "TRACKER_STARTUP_TERMINATED_INDEX_SESSION")
    private String sessionId;
    private long totalTime;
    @Index(name = "TRACKER_STARTUP_TERMINATED_INDEX_START_UUID")
    private String startUuid;
    private Integer levelTaskId;
    private boolean successful;

    /**
     * Used by Hibernate
     */
    DbStartupTerminated() {
    }

    public DbStartupTerminated(String sessionId, boolean successful, long totalTime, String startUuid, Integer levelTaskId) {
        this.sessionId = sessionId;
        this.startUuid = startUuid;
        this.levelTaskId = levelTaskId;
        this.successful = successful;
        this.totalTime = totalTime;
    }

    public String getStartUuid() {
        return startUuid;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Integer getLevelTaskId() {
        return levelTaskId;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbStartupTerminated that = (DbStartupTerminated) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}