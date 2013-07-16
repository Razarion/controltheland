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

import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.pages.RazarionPage;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component(value = "statisticsController")
public class StatisticsController implements Controller {
    @Autowired
    private UserTrackingService userTrackingService;
    private Log log = LogFactory.getLog(StatisticsController.class);

    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        try {
            String html5 = httpServletRequest.getParameter(RazarionPage.HTML5_KEY);
            Boolean html5Support = null;
            if (html5.equals((RazarionPage.HTML5_KEY_Y))) {
                html5Support = true;
            } else if (html5.equals((RazarionPage.HTML5_KEY_N))) {
                html5Support = false;
            } else {
                log.warn("StatisticsController: Unknown HTML5 parameter received: " + html5);
            }
            userTrackingService.onJavaScriptDetected(html5Support);

            httpServletResponse.setContentType(LoadScriptCommunicationController.CONTENT_TYPE);
            httpServletResponse.getOutputStream().write(LoadScriptCommunicationController.PIXEL_BYTES);
            httpServletResponse.setContentLength(LoadScriptCommunicationController.PIXEL_BYTES.length);
            httpServletResponse.getOutputStream().close();
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
            httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        return null;
    }

}