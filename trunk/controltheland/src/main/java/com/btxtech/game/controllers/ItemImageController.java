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
            int itemTypeId = Integer.parseInt(httpServletRequest.getParameter(Constants.ITEM_IMAGE_ID));
            int index = Integer.parseInt(httpServletRequest.getParameter(Constants.ITEM_IMAGE_INDEX));
            DbItemTypeImage itemTypeImage = itemService.getItemTypeImage(itemTypeId, index);
            httpServletResponse.setContentLength(itemTypeImage.getData().length);
            httpServletResponse.setContentType(itemTypeImage.getContentType());
            httpServletResponse.addDateHeader("Expires", System.currentTimeMillis() + DateUtil.MILLIS_IN_DAY);
            OutputStream out = httpServletResponse.getOutputStream();
            out.write(itemTypeImage.getData());
            out.close();
        } catch (IOException e) {
            // Connection lost -> ignore
        } catch (Exception e) {
            log.error("", e);
        }

        return null;
    }

}