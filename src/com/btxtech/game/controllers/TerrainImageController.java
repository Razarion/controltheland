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
import com.btxtech.game.services.terrain.DbTerrainImage;
import com.btxtech.game.services.terrain.TerrainService;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * User: beat
 * Date: May 23, 2009
 * Time: 12:50:37 PM
 */
public class TerrainImageController implements Controller {
    @Autowired
    private TerrainService terrainService;
    private Log log = LogFactory.getLog(TerrainImageController.class);


    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        try {
            String type = httpServletRequest.getParameter(Constants.TERRAIN_IMG_TYPE);
            byte[] imageData;
            String imageContentType;
            if (Constants.TERRAIN_IMG_TYPE_BACKGROUND.equalsIgnoreCase(type)) {
                imageData = terrainService.getTerrainSetting().getBgImageData();
                imageContentType = terrainService.getTerrainSetting().getBgContentType();
            } else if (Constants.TERRAIN_IMG_TYPE_FOREGROUND.equalsIgnoreCase(type)) {
                String strId = httpServletRequest.getParameter(Constants.TERRAIN_IMG_TYPE_IMG_ID);
                int id = Integer.parseInt(strId);
                DbTerrainImage dbTerrainImage = terrainService.getTerrainImage(id);
                imageData = dbTerrainImage.getImageData();
                imageContentType = dbTerrainImage.getContentType();
            } else {
                throw new IllegalArgumentException("Unknown terrain image type: " + type);
            }

            if (imageData == null || imageContentType == null) {
                httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return null;
            }
            httpServletResponse.setContentLength(imageData.length);
            httpServletResponse.setContentType(imageContentType);
            OutputStream out = httpServletResponse.getOutputStream();
            out.write(imageData);
            out.close();
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
