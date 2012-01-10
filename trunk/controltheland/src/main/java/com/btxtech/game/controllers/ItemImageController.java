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
import com.btxtech.game.services.common.DateUtil;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBuildupStep;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private ItemService itemService;
    private Log log = LogFactory.getLog(ItemImageController.class);

    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        try {
            byte[] imageData;
            String contentType;
            int itemTypeId = Integer.parseInt(httpServletRequest.getParameter(Constants.ITEM_TYPE_ID));
            String type = httpServletRequest.getParameter(Constants.TYPE);
            if (type == null || type.trim().isEmpty()) {
                // Default is item image
                int index = Integer.parseInt(httpServletRequest.getParameter(Constants.ITEM_IMAGE_INDEX));
                DbItemTypeImage itemTypeImage = itemService.getItemTypeImage(itemTypeId, index);
                imageData = itemTypeImage.getData();
                contentType = itemTypeImage.getContentType();
            } else if (type.equals(Constants.TYPE_BUILDUP_STEP)) {
                int buildupStepId = Integer.parseInt(httpServletRequest.getParameter(Constants.ITEM_IMAGE_BUILDUP_STEP));
                DbBuildupStep dbBuildupStep = itemService.getDbBuildupStep(itemTypeId, buildupStepId);
                imageData = dbBuildupStep.getData();
                contentType = dbBuildupStep.getContentType();
            } else {
                throw new IllegalArgumentException("Type is not known: " + type);
            }
            httpServletResponse.setContentLength(imageData.length);
            httpServletResponse.setContentType(contentType);
            httpServletResponse.addDateHeader("Expires", System.currentTimeMillis() + DateUtil.MILLIS_IN_DAY);
            OutputStream out = httpServletResponse.getOutputStream();
            out.write(imageData);
            out.close();

        } catch (IOException e) {
            // Connection lost -> ignore
        } catch (Exception e) {
            log.error("", e);
        }

        return null;
    }

}