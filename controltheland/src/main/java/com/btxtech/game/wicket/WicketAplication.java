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

package com.btxtech.game.wicket;

import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.wicket.pages.PageExpired;
import com.btxtech.game.wicket.pages.cms.Home;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: beat
 * Date: May 31, 2009
 * Time: 9:51:34 PM
 */
@Component
public class WicketAplication extends WebApplication {
    @Autowired
    private Session session;
    @Autowired
    private MgmtService mgmtService;
    private String configurationType;
    private Log log = LogFactory.getLog(WicketAplication.class);

    @Override
    protected void init() {
        addComponentInstantiationListener(new SpringComponentInjector(this));
    }

    public Class<Home> getHomePage() {
        return Home.class;
    }

    @Override
    public String getConfigurationType() {
        if (configurationType == null) {
            if (mgmtService.isTestMode()) {
                configurationType = Application.DEVELOPMENT;
            } else {
                configurationType = Application.DEPLOYMENT;
            }
        }
        return configurationType;
    }

    public final RequestCycle newRequestCycle(final Request request, final Response response) {
        if (mgmtService.isTestMode()) {
            return super.newRequestCycle(request, response);
        } else {
            return new MyRequestCycle(this, (WebRequest) request, response);
        }
    }

    public final class MyRequestCycle extends WebRequestCycle {

        public MyRequestCycle(final WebApplication application, final WebRequest request, final Response response) {
            super(application, request, response);
        }

        @Override
        public final Page onRuntimeException(final Page cause, final RuntimeException e) {
            if (e instanceof PageExpiredException) {
                log.error("------------------PageExpiredException---------------------------------");
                log.error(e.getMessage());
                log.error("URL: " + getRequest().getURL());
                log.error("Page: " + cause);
                log.error("User Agent: " + session.getUserAgent());
                // TODO log.error("User: " + session.getUser());
                log.error("Session Id: " + session.getSessionId());
                return new PageExpired();                
            } else {
                log.error("", e);
                return new Home();                
            }
        }
    }

}

