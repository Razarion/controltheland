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

package com.btxtech.game.services.mgmt;

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
@Entity(name = "TRACKER_SERVER_DEBUG")
public class DbServerDebugEntry {
    @Id
    @GeneratedValue
    private Integer id;
    private Date timeStamp;
    private String userAgent;
    private String sessionId;
    @Column(length = 10000)
    private String message;
    private String category;
    @Column(length = 5000)
    private String throwableMessage;
    @Column(length = 20000)
    private String stackTrace;
    private String userName;
    private String remoteAddress;
    private String referer;
    private String thread;
    private String requestUri;
    private String queryString;
    private String causePage;

    /**
     * Used by hibernate
     */
    public DbServerDebugEntry() {
    }

    public DbServerDebugEntry(String category, String message, String throwableMessage, String stackTrace) {
        timeStamp = new Date();
        this.category = category;
        this.message = message;
        this.throwableMessage = throwableMessage;
        this.stackTrace = stackTrace;
    }

    public Date getTimeStamp() {
        return timeStamp;
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

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getReferer() {
        return referer;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public String getThread() {
        return thread;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setCausePage(String causePage) {
        this.causePage = causePage;
    }

    public String getCausePage() {
        return causePage;
    }

    public String getThrowableMessage() {
        return throwableMessage;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof DbServerDebugEntry)) return false;

        DbServerDebugEntry debugEntry = (DbServerDebugEntry) o;

        return id != null && id.equals(debugEntry.id);

    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}