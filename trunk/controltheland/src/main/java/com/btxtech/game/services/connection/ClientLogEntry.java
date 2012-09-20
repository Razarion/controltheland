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
@Entity(name = "TRACKER_CLIENT_LOG_ENTRY")
public class ClientLogEntry {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date timeStamp;
    private Date clientTimeStamp;
    private String userAgent;
    private String baseName;
    @Column(nullable = false)
    private String sessionId;
    @Column(nullable = false, length = 10000)
    private String message;

    public ClientLogEntry(String message, Date date, Session session, String baseName) {
        this.message = message;
        timeStamp = new Date();
        clientTimeStamp = date;
        userAgent = session.getUserAgent();
        sessionId = session.getSessionId();
        try {
            this.baseName = baseName;
        } catch (Exception e) {
            this.baseName = "???";
        }
    }

    /**
     * Used by hibernat
     */
    public ClientLogEntry() {
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getBaseName() {
        return baseName;
    }

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ClientLogEntry)) return false;

        ClientLogEntry logEntry = (ClientLogEntry) o;

        return !(id != null ? !id.equals(logEntry.id) : logEntry.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
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

        builder.append("baseName: ");
        builder.append(baseName);
        builder.append("\n");

        builder.append(message);
        builder.append("\n--------------------------------------------");

        return builder.toString();
    }

}