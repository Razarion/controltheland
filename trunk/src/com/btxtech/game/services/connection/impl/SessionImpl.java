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
import com.btxtech.game.services.market.impl.UserItemTypeAccess;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.utg.BrowserDetails;
import com.btxtech.game.services.utg.DbUserStage;
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
    private String cookieId;
    private String userAgent;
    private User user;
    private UserItemTypeAccess userItemTypeAccess;
    private boolean javaScriptDetected = false;
    private BrowserDetails browserDetails;
    private DbUserStage dbUserStage;

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
        cookieId = WebCommon.getCookieId(request.getCookies());
        browserDetails = new BrowserDetails(sessionId,
                cookieId,
                userAgent,
                request.getHeader("Accept-Language"),
                request.getRemoteAddr(),
                request.getHeader("Referer"));
        userTrackingService.saveBrowserDetails(browserDetails);
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
    public String getCookieId() {
        return cookieId;
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
        connection = null;
        user = null;
        userItemTypeAccess = null;
    }

    @Override
    public void onJavaScriptDetected() {
        if (javaScriptDetected) {
            return;
        }
        javaScriptDetected = true;
        browserDetails.setJavaScriptDetected();
        userTrackingService.saveBrowserDetails(browserDetails);

    }

    @Override
    public boolean isJavaScriptDetected() {
        return javaScriptDetected;
    }

    @Override
    public DbUserStage getUserStage() {
        return dbUserStage;
    }

    @Override
    public void setDbUserStage(DbUserStage dbUserStage) {
        this.dbUserStage = dbUserStage;
    }
}
