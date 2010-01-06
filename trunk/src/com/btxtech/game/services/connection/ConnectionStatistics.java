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

package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.common.SimpleBase;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * User: beat
 * Date: Jul 26, 2009
 * Time: 12:56:17 PM
 */
@Entity
@Table(name = "CONNECTION_STATISTICS")
public class ConnectionStatistics {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date timeStamp;
    @Column(nullable = false)
    private String sessionId;
    @Column(nullable = false)
    private double ticksPerSecond;
    @Column(nullable = false)
    private String userName;

    public ConnectionStatistics(SimpleBase simpleBase, String sessionId, double ticksPerSecond) {
        this.sessionId = sessionId;
        userName = simpleBase.getName();
        this.ticksPerSecond = ticksPerSecond;
        timeStamp = new Date();
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public double getTicksPerSecond() {
        return ticksPerSecond;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectionStatistics that = (ConnectionStatistics) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
