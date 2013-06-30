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

import com.btxtech.game.services.cms.EditMode;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.connection.Connection;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.services.utg.tracker.DbSessionDetail;
import com.btxtech.game.wicket.WicketAuthenticatedWebSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.RequestCycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * User: beat
 * Date: May 31, 2009
 * Time: 8:45:09 PM
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionImpl implements Session, Serializable {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    UserTrackingService userTrackingService;
    @Autowired
    UserService userService;
    private String sessionId;
    private String userAgent;
    private boolean javaScriptDetected = false;
    private DbSessionDetail dbSessionDetail;
    private UserState userState;
    private Connection connection;
    private Log log = LogFactory.getLog(SessionImpl.class);
    private EditMode editMode;
    private boolean html5Support = true;
    private FacebookSignedRequest facebookSignedRequest;
    private String trackingCookieId;

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
        boolean isNewUserTracking = false;
        try {
            RequestCycle requestCycle = RequestCycle.get();
            if (requestCycle != null) {
                WicketAuthenticatedWebSession wicketSession = (WicketAuthenticatedWebSession) WicketAuthenticatedWebSession.get();
                trackingCookieId = wicketSession.getTrackingCookieId();
                isNewUserTracking = wicketSession.isNewUserTracking();
            } else {
                // TODO remove when error found
                log.error("----------------------Unknown request----------------------------");
                log.error("Thread: " + Thread.currentThread().getName());
                log.error("User Agent: " + userAgent);
                log.error("Session Id: " + sessionId);
                log.error("IP: " + request.getRemoteAddr());
                try {
                    InetAddress inetAddress = InetAddress.getByName(request.getRemoteAddr());
                    log.error("Remote Host: " + inetAddress.getHostName());
                } catch (UnknownHostException e) {
                    // Ignore
                }
                log.error("Referer: " + request.getHeader("Referer"));
                log.error("Request URI: " + request.getRequestURI());
                log.error("Query String: " + request.getQueryString());
                if(request.getCookies() != null) {
                    for (Cookie cookie : request.getCookies()) {
                        log.error("Cookie: " + cookie.getName() + "=" + cookie.getValue());
                    }
                }
                log.error("-----------------------------------------------------------------");
            }
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
        dbSessionDetail = new DbSessionDetail(sessionId,
                trackingCookieId,
                userAgent,
                request.getHeader("Accept-Language"),
                request.getRemoteAddr(),
                request.getHeader("Referer"),
                isNewUserTracking);
        userTrackingService.saveBrowserDetails(dbSessionDetail);
    }

    @PreDestroy
    public void destroy() {
        try {
            if (userState != null) {
                userService.onSessionTimedOut(userState);
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String getTrackingCookieId() {
        return trackingCookieId;
    }

    @Override
    public String getUserAgent() {
        return userAgent;
    }

    @Override
    @Deprecated
    public HttpServletRequest getRequest() {
        // This is not the current request. This request is from the start() method.
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
    public void onJavaScriptDetected(Boolean html5Support) {
        if (javaScriptDetected) {
            return;
        }
        javaScriptDetected = true;
        // Return true if html5Support == null the detection may be went wrong an a HTML5 ready browser
        this.html5Support = html5Support == null || html5Support;
        dbSessionDetail.setJavaScriptDetected(this.html5Support);
        userTrackingService.saveBrowserDetails(dbSessionDetail);
    }

    @Override
    public boolean isJavaScriptDetected() {
        return javaScriptDetected;
    }

    @Override
    public boolean isHtml5Support() {
        return html5Support;
    }

    public Boolean getHtml5Support() {
        return html5Support;
    }

    public void setHtml5Support(Boolean html5Support) {
        this.html5Support = html5Support;
    }

    @Override
    public EditMode getEditMode() {
        return editMode;
    }

    @Override
    public void setEditMode(EditMode editMode) {
        this.editMode = editMode;
    }

    @Override
    public FacebookSignedRequest getFacebookSignedRequest() {
        return facebookSignedRequest;
    }

    @Override
    public void setFacebookSignedRequest(FacebookSignedRequest facebookSignedRequest) {
        this.facebookSignedRequest = facebookSignedRequest;
    }
}
