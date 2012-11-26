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
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserTrackingService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component(value = "loadScriptCommunicationController")
public class LoadScriptCommunicationController implements Controller {
    public static final byte[] PIXEL_BYTES = org.apache.wicket.util.crypt.Base64.decodeBase64("R0lGODlhAQABAPAAAAAAAAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==".getBytes());
    public static final String CONTENT_TYPE = "image/gif";
    @Autowired
    private UserTrackingService userTrackingService;
    @Autowired
    private UserService userService;
    private Log log = LogFactory.getLog(LoadScriptCommunicationController.class);

    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        try {
            String message = httpServletRequest.getParameter(Constants.ERROR_KEY);
            String pathName = httpServletRequest.getParameter(Constants.ERROR_PATH_NAME);
            if (ClientUserTracker.WINDOW_CLOSE.equals(message)) {
                String startUuid = httpServletRequest.getParameter(ClientUserTracker.START_UUID);
                userTrackingService.trackWindowsClosed(startUuid);
            } else {
                log.error("------------------LoadScriptCommunication---------------------------------");
                ExceptionHandler.logParameters(log, userService);
                log.error("Path name: " + pathName);
                log.error(message);
            }

            httpServletResponse.setContentType(CONTENT_TYPE);
            httpServletResponse.getOutputStream().write(PIXEL_BYTES);
            httpServletResponse.setContentLength(PIXEL_BYTES.length);
            httpServletResponse.getOutputStream().close();
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
            httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        return null;
    }

}