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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * User: beat
 * Date: Jul 31, 2009
 * Time: 9:03:13 PM
 */
@Entity(name = "TRACKER_CLIENT_DEBUG")
public class DbClientDebugEntry {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date timeStamp;
    private Date clientTimeStamp;
    private String userAgent;
    private Integer userId;
    @Column(nullable = false)
    private String sessionId;
    @Column(nullable = false, length = 10000)
    private String message;
    private String category;

    /**
     * Used by hibernate
     */
    public DbClientDebugEntry() {
    }

    public DbClientDebugEntry(Date date, Session session, Integer userId, String category, String message) {
        timeStamp = new Date();
        clientTimeStamp = date;
        userAgent = session.getUserAgent();
        sessionId = session.getSessionId();
        this.userId = userId;
        this.category = category;
        this.message = message;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public Date getClientTimeStamp() {
        return clientTimeStamp;
    }

    public String getCategory() {
        return category;
    }

    public String getMessage() {
        return message;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof DbClientDebugEntry)) return false;

        DbClientDebugEntry debugEntry = (DbClientDebugEntry) o;

        return id != null && id.equals(debugEntry.id);

    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }

    public String getFormatMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append("CLIENT LOG ENTRY ---------------------------\n");

        builder.append("Client Time: ");
        builder.append(clientTimeStamp);
        builder.append("\n");

        builder.append("userAgent: ");
        builder.append(userAgent);
        builder.append("\n");

        builder.append("sessionId: ");
        builder.append(sessionId);
        builder.append("\n");

        builder.append("userId: ");
        builder.append(userId);
        builder.append("\n");

        builder.append("category: ");
        builder.append(category);
        builder.append("\n");

        builder.append(message);
        builder.append("\n--------------------------------------------");

        return builder.toString();
    }

}