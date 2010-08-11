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

import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User: beat
 * Date: Jul 26, 2009
 * Time: 11:09:27 AM
 */
@Entity(name = "TRACKER_COMMAND")
public class DbCommand implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date niceTimeStamp;
    @Column(nullable = false)
    private long clientTimeStamp;
    @Column(nullable = false)
    private String sessionId;
    @Column(nullable = false, length = 50000)
    private BaseCommand baseCommand;

    /**
     * Used by hibernate
     */
    public DbCommand() {
    }

    public DbCommand(BaseCommand baseCommand, String sessionId) {
        this.sessionId = sessionId;
        clientTimeStamp = baseCommand.getTimeStamp().getTime();
        niceTimeStamp = new Date();
        this.baseCommand = baseCommand;
    }

    public Date getNiceTimeStamp() {
        return niceTimeStamp;
    }

    public long getClientTimeStamp() {
        return clientTimeStamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public BaseCommand getBaseCommand() {
        return baseCommand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbCommand that = (DbCommand) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}