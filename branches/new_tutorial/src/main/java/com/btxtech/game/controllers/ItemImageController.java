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
import com.btxtech.game.jsre.common.ClientDateUtil;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.ImageHolder;
import com.btxtech.game.services.item.ServerItemTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Component(value = "itemImageController")
public class ItemImageController implements Controller {
    @Autowired
    private ServerItemTypeService serverItemTypeService;

    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        try {
            byte[] imageData;
            String contentType;

            String itemTypeSpriteMapString = httpServletRequest.getParameter(Constants.ITEM_TYPE_SPRITE_MAP_ID);

            if (itemTypeSpriteMapString != null) {
                int itemTypeId = Integer.parseInt(itemTypeSpriteMapString);
                ImageHolder imageHolder = serverItemTypeService.getItemTypeSpriteMap(itemTypeId);
                imageData = imageHolder.getData();
                contentType = imageHolder.getContentType();
            } else {
                httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, Constants.ITEM_TYPE_SPRITE_MAP_ID + " or " + Constants.ITEM_TYPE_ID + " must be given.");
                return null;
            }
            httpServletResponse.setContentLength(imageData.length);
            httpServletResponse.setContentType(contentType);
            httpServletResponse.addDateHeader("Expires", System.currentTimeMillis() + ClientDateUtil.MILLIS_IN_DAY);
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