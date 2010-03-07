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

package com.btxtech.game.services.connection.impl;

import com.btxtech.game.services.connection.Connection;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.itemTypeAccess.impl.UserItemTypeAccess;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.utg.BrowserDetails;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.WebCommon;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: beat
 * Date: May 31, 2009
 * Time: 8:45:09 PM
 */
public class SessionImpl implements Session, Serializable {
    private Connection connection;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    UserTrackingService userTrackingService;
    private String sessionId;
    private String userAgent;
    private User user;
    private UserItemTypeAccess userItemTypeAccess;

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @PostConstruct
    public void init() {
        sessionId = request.getSession().getId();
        userAgent = request.getHeader("user-agent");
        BrowserDetails browserDetails = new BrowserDetails(sessionId,
                WebCommon.getCookieId(request.getCookies()),
                userAgent,
                request.getHeader("Accept-Language"),
                request.getRemoteAddr(),
                request.getHeader("Referer"));
        userTrackingService.newSession(browserDetails);
    }

    @PreDestroy
    public void destroy() {
        if (user != null) {
            userTrackingService.onUserLoggedOut(user);
        }
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String getUserAgent() {
        return userAgent;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    @Override
    public UserItemTypeAccess getUserItemTypeAccess() {
        return userItemTypeAccess;
    }

    @Override
    public void setUserItemTypeAccess(UserItemTypeAccess userItemTypeAccess) {
        this.userItemTypeAccess = userItemTypeAccess;
    }

    @Override
    public void clearGame() {
        //base = null;
        connection = null;
        user = null;
        userItemTypeAccess = null;
    }
}
