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

import com.btxtech.game.services.tutorial.TutorialService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: beat
 * Date: May 23, 2009
 * Time: 12:50:37 PM
 */
@Component(value = "tutorialResourceHintController")
@Deprecated
public class TutorialResourceHintController implements Controller {
    @Autowired
    private TutorialService tutorialService;
    private Log log = LogFactory.getLog(TutorialResourceHintController.class);


    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        // TODO handle Finish Image
//        try {
//            String imgParam = httpServletRequest.getParameter(Constants.TUTORIAL_RESOURCE_ID);
//            int imgId = Integer.parseInt(imgParam);
//            DbResourceHintConfig dbResourceHintConfig = tutorialService.getDbResourceHintConfig(imgId);
//            httpServletResponse.setContentLength(dbResourceHintConfig.getData().length);
//            httpServletResponse.setContentType(dbResourceHintConfig.getContentType());
//            OutputStream out = httpServletResponse.getOutputStream();
//            out.write(dbResourceHintConfig.getData());
//            out.close();
//        } catch (IOException e) {
//            // Connection lost -> ignore
//        } catch (Exception e) {
//            log.error("", e);
//            handleBadRequest(httpServletResponse);
//        }
        return null;
    }
}