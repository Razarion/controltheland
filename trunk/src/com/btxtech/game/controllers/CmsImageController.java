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

import com.btxtech.game.services.cms.CmsService;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * User: beat
 * Date: May 23, 2009
 * Time: 12:50:37 PM
 */
@Component(value = "cmsImageController")
public class CmsImageController implements Controller {
    public static final String IMG_PARAMETER = "img";
    public static final String IMG_START = "start";
    public static final String CONTROLLER = "/spring/cms";
    @Autowired
    private CmsService cmsService;
    private Log log = LogFactory.getLog(CmsImageController.class);


    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        try {
            String imgParam = httpServletRequest.getParameter(IMG_PARAMETER);
            if (IMG_START.equalsIgnoreCase(imgParam)) {
                byte[] img = cmsService.getHomeCmsInfo().getDbCmsHomeLayout().getBgImage();
                if (img != null) {
                    httpServletResponse.setContentLength(img.length);
                    httpServletResponse.setContentType(cmsService.getHomeCmsInfo().getDbCmsHomeLayout().getBgImageContentType());
                    OutputStream out = httpServletResponse.getOutputStream();
                    out.write(img);
                    out.close();
                }
            } else {
                throw new IllegalArgumentException("Unknown image parameter: " + imgParam);
            }
        } catch (IOException e) {
            // Connection lost -> ignore
        } catch (Exception e) {
            log.error("", e);
            handleBadRequest(httpServletResponse);
            return null;
        }

        return null;
    }

    private void handleBadRequest(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

}