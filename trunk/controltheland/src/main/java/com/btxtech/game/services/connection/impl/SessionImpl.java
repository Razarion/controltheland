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
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.BrowserDetails;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.WebCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * User: beat
 * Date: May 31, 2009
 * Time: 8:45:09 PM
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionImpl implements Session, Serializable {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    UserTrackingService userTrackingService;
    @Autowired
    UserService userService;
    private String sessionId;
    private String cookieId;
    private String userAgent;
    private boolean javaScriptDetected = false;
    private BrowserDetails browserDetails;
    private UserState userState;
    private Connection connection;

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
        if (userState != null) {
            userService.onSessionTimedOut(userState, sessionId);
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

    public HttpServletRequest getRequest() {
        return request;
    }

    @Override
    public UserState getUserState() {
        return userState;
    }

    @Override
    public void setUserState(UserState userState) {
        this.userState = userState;
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
}
