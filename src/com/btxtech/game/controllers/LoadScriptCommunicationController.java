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

package com.btxtech.game.controllers;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.services.connection.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

@Component(value = "loadScriptCommunicationController")
public class LoadScriptCommunicationController implements Controller {
    @Autowired
    private Session session;
    private Log log = LogFactory.getLog(LoadScriptCommunicationController.class);

    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        try {
            log.error("------------------LoadScriptCommunication---------------------------------");
            log.error(httpServletRequest.getParameter(Constants.ERROR_KEY));
            log.error("User Agent: " + session.getUserAgent());
            log.error("Session Id: " + session.getSessionId());
            return null;
        } catch (Exception e) {
            log.error("", e);
            throw e;
        }
    }

}