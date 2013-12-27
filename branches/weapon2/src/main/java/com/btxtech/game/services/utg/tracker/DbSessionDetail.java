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

import com.btxtech.game.services.user.DbInvitationInfo;
import com.btxtech.game.services.utg.DbFacebookSource;
import org.hibernate.annotations.Index;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * User: beat
 * Date: 12.01.2010
 * Time: 22:45:47
 */
@Entity(name = "TRACKER_BROWSER_DETAILS")
public class DbSessionDetail implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date timeStamp;
    @Column(nullable = false)
    @Index(name = "TRACKER_BROWSER_DETAILS_INDEX_SESSION")
    private String sessionId;
    @Column(length = 10000)
    private String userAgent;
    private String language;
    private String remoteHost;
    private String remoteAddr;
    private String cookieId;
    private boolean newUser;
    @Column(length = 50000)
    private String referer;
    private boolean javaScriptDetected = false;
    private boolean html5Support;
    @Embedded
    private DbFacebookSource dbFacebookSource;
    @Embedded
    private DbInvitationInfo dbInvitationInfo;

    /**
     * Used by Hibernate
     */
    public DbSessionDetail() {
    }

    public DbSessionDetail(String sessionId, String cookieId, String userAgent, String language, String remoteAddr, String referer, boolean newUser, DbFacebookSource dbFacebookSource, DbInvitationInfo dbInvitationInfo) {
        this.cookieId = cookieId;
        this.newUser = newUser;
        this.dbFacebookSource = dbFacebookSource;
        this.dbInvitationInfo = dbInvitationInfo;
        timeStamp = new Date();
        this.sessionId = sessionId;
        this.userAgent = userAgent;
        this.language = language;
        this.remoteAddr = remoteAddr;
        try {
            InetAddress inetAddress = InetAddress.getByName(remoteAddr);
            remoteHost = inetAddress.getHostName();
        } catch (UnknownHostException e) {
            remoteHost = "???";
        }
        this.referer = referer;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getLanguage() {
        return language;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public String getCookieId() {
        return cookieId;
    }

    public String getReferer() {
        return referer;
    }

    public void setJavaScriptDetected(boolean html5Support) {
        this.html5Support = html5Support;
        javaScriptDetected = true;
    }

    public boolean isJavaScriptDetected() {
        return javaScriptDetected;
    }

    public boolean isHtml5Support() {
        return html5Support;
    }

    public boolean isNewUser() {
        return newUser;
    }

    public DbFacebookSource getDbFacebookSource() {
        return dbFacebookSource;
    }

    public DbInvitationInfo getDbInvitationInfo() {
        return dbInvitationInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbSessionDetail that = (DbSessionDetail) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}