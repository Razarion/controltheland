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
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.terrain.DbSurfaceImage;
import com.btxtech.game.services.terrain.DbTerrainImage;
import com.btxtech.game.services.terrain.TerrainImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * User: beat
 * Date: May 23, 2009
 * Time: 12:50:37 PM
 */
@Component
public class TerrainImageController implements Controller {
    @Autowired
    private TerrainImageService terrainService;


    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        try {
            String type = httpServletRequest.getParameter(Constants.TERRAIN_IMG_TYPE);
            String strId = httpServletRequest.getParameter(Constants.TERRAIN_IMG_TYPE_IMG_ID);
            int id = Integer.parseInt(strId);
            byte[] imageData;
            String imageContentType;
            if (Constants.TERRAIN_IMG_TYPE_SURFACE.equalsIgnoreCase(type)) {
                DbSurfaceImage dbSurfaceImage = terrainService.getDbSurfaceImage(id);
                imageData = dbSurfaceImage.getImageData();
                imageContentType = dbSurfaceImage.getContentType();
            } else if (Constants.TERRAIN_IMG_TYPE_FOREGROUND.equalsIgnoreCase(type)) {
                DbTerrainImage dbTerrainImage = terrainService.getDbTerrainImage(id);
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
        } catch (IOException e) {
            // Connection lost -> ignore
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
            httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        return null;
    }
}
