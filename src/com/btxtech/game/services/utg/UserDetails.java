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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import java.util.Date;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * User: beat
 * Date: 12.01.2010
 * Time: 22:45:47
 */
@Entity(name = "TRACKER_USER_DETAILS")
public class UserDetails {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date timeStamp;
    @Column(nullable = false)
    private String sessionId;
    private String userAgent;
    private String language;
    private String remoteHost;
    private String remoteAddr;
    private boolean isCrawler;
    private String cookieId;
    private String referer;

    /**
     * Used by Hibernate
     */
    public UserDetails() {
    }

    public UserDetails(String sessionId, String cookieId, String userAgent, String language, String remoteAddr, String Referer) {
        this.cookieId = cookieId;
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
        referer = Referer;
        isCrawler = CrawlerDetection.isCrawler(userAgent, remoteHost);
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

    public boolean isCrawler() {
        return isCrawler;
    }

    public String getCookieId() {
        return cookieId;
    }

    public String getReferer() {
        return referer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDetails that = (UserDetails) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}