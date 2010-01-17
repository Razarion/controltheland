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

import com.btxtech.game.jsre.common.gameengine.services.utg.UserAction;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User: beat
 * Date: 14.01.2010
 * Time: 18:17:24
 */
@Entity(name = "TRACKER_USER_ACTION")
public class DbUserAction {
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
    private String type;
    private String additionalString;
    private Date clientTimeStampLast;
    private String additionalStringLast;
    private int repeatingCount;

    /**
     * Used by Hibernate
     */
    public DbUserAction() {
    }

    public DbUserAction(UserAction userAction, String sessionId) {
        timeStamp = new Date();
        this.sessionId = sessionId;
        clientTimeStamp = userAction.getTimeStamp();
        type = userAction.getType();
        additionalString = userAction.getAdditionalString();
        clientTimeStampLast = userAction.getTimeStampLast();
        additionalStringLast = userAction.getAdditionalStringLast();
        repeatingCount = userAction.getRepeatingCount();
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Date getClientTimeStamp() {
        return clientTimeStamp;
    }

    public String getType() {
        return type;
    }

    public String getAdditionalString() {
        return additionalString;
    }

    public Date getClientTimeStampLast() {
        return clientTimeStampLast;
    }

    public String getAdditionalStringLast() {
        return additionalStringLast;
    }

    public int getRepeatingCount() {
        return repeatingCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbUserAction that = (DbUserAction) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
