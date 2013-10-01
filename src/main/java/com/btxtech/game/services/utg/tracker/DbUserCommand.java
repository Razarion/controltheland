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

import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.services.connection.Connection;
import org.hibernate.annotations.Index;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * User: beat
 * Date: Jul 26, 2009
 * Time: 11:09:27 AM
 */
@Entity(name = "TRACKER_USER_COMMAND")
public class DbUserCommand implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date timeStamp;
    private long timeStampMs;
    @Column(nullable = false)
    private long clientTimeStamp;
    @Column(nullable = false)
    @Index(name = "TRACKER_USER_COMMAND_INDEX_SESSION")    
    private String sessionId;
    @Column(nullable = false, length = 50000)
    private String interaction;
    @Column(nullable = false)
    private String interactionClass;
    @Column(nullable = false)
    private String baseName;

    /**
     * Used by hibernate
     */
    public DbUserCommand() {
    }

    public DbUserCommand(Connection connection, BaseCommand baseCommand, String baseName) {
        this.baseName = baseName;
        sessionId = connection.getSessionId();
        this.interaction = baseCommand.toString();
        interactionClass = baseCommand.getClass().getName();
        clientTimeStamp = baseCommand.getTimeStamp();
        timeStamp = new Date();
        timeStampMs = timeStamp.getTime();
    }

    public String getBaseName() {
        return baseName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getInteraction() {
        return interaction;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public long getClientTimeStamp() {
        return clientTimeStamp;
    }

    public String getInteractionClass() {
        return interactionClass;
    }

    public long getTimeStampMs() {
        return timeStampMs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbUserCommand that = (DbUserCommand) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return interaction + " " + baseName + " " + sessionId;
    }
}
