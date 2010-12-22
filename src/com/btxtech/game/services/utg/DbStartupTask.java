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

import com.btxtech.game.jsre.common.StartupTaskInfo;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
    @Column(nullable = false)
    private long clientTimeStamp;
    @Column(nullable = false)
    private String task;
    private long duration;
    @Column(length = 50000)
    private String failureText;
    @ManyToOne(optional = false)
    @JoinColumn(name = "dbStartup", insertable = false, updatable = false, nullable = false)
    private DbStartup dbStartup;

    /**
     * Used by Hibernate
     */
    public DbStartupTask() {
    }

    public DbStartupTask(StartupTaskInfo info, DbStartup dbStartup) {
        this.dbStartup = dbStartup;
        clientTimeStamp = info.getStartTime();
        task = info.getTaskEnum().getStartupTaskEnumHtmlHelper().getNiceText();
        duration = info.getDuration();
        failureText = info.getError();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbStartupTask that = (DbStartupTask) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}