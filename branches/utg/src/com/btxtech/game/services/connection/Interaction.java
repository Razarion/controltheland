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

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;

/**
 * User: beat
 * Date: Jul 26, 2009
 * Time: 11:09:27 AM
 */
@Entity
@Table(name = "USER_INTERACTION")
public class Interaction {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date timeStamp;
    @Column(nullable = false)
    private String sessionId;
    @Column(nullable = false)
    private String interaction;
    @Column(nullable = false)
    private String interactionClass;
    @Column(nullable = false)
    private String userName;

    /**
     * Used by hibernate
     */
    public Interaction() {
    }

    public Interaction(Connection connection, BaseCommand baseCommand) {
        userName = connection.getBase().getName();
        sessionId = connection.getSessionId();
        this.interaction = baseCommand.toString();
        interactionClass = baseCommand.getClass().getName();
        timeStamp = new Date();
    }

    public String getUserName() {
        return userName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Interaction that = (Interaction) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return interaction + " " + userName + " " + sessionId;
    }
}
