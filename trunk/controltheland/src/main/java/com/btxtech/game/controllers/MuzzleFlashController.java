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
import com.btxtech.game.services.item.itemType.DbItemTypeImageData;
import com.btxtech.game.services.item.itemType.DbItemTypeSoundData;
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

/**
 * User: beat
 * Date: May 23, 2009
 * Time: 12:50:37 PM
 */
@Component(value = "muzzleFlashController")
public class MuzzleFlashController implements Controller {
    @Autowired
    private ItemService itemService;
    private Log log = LogFactory.getLog(MuzzleFlashController.class);

    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        try {
            int itemTypeId = Integer.parseInt(httpServletRequest.getParameter(Constants.ITEM_IMAGE_ID));
            String type = httpServletRequest.getParameter(Constants.TYPE);
            if (Constants.TYPE_IMAGE.equals(type)) {
                DbItemTypeImageData image = itemService.getMuzzleFlashImage(itemTypeId);
                if (image.getData() == null) {
                    throw new IllegalStateException("No image data for itemTypeId: " + itemTypeId);
                }
                httpServletResponse.setContentLength(image.getData().length);
                httpServletResponse.setContentType(image.getContentType());
                httpServletResponse.addDateHeader("Expires", System.currentTimeMillis() + DateUtil.MILLIS_IN_DAY);
                OutputStream out = httpServletResponse.getOutputStream();
                out.write(image.getData());
                out.close();
            } else if (Constants.TYPE_SOUND.equals(type)) {
                DbItemTypeSoundData sound = itemService.getMuzzleFlashSound(itemTypeId);
                String contentType = httpServletRequest.getParameter(Constants.CODEC);
                byte[] data;
                if (Constants.CODEC_TYPE_MP3.equals(contentType)) {
                    data = sound.getDataMp3();
                } else if (Constants.CODEC_TYPE_OGG.equals(contentType)) {
                    data = sound.getDataOgg();
                } else {
                    throw new IllegalArgumentException("Unsupported sound content type: " + contentType);
                }
                if (data == null) {
                    throw new IllegalStateException("No sound data for itemTypeId: " + itemTypeId + " contentType: " + contentType);
                }
                httpServletResponse.setContentLength(data.length);
                httpServletResponse.setContentType(contentType);
                httpServletResponse.addDateHeader("Expires", System.currentTimeMillis() + DateUtil.MILLIS_IN_DAY);
                httpServletResponse.addHeader("Content-Range", "bytes 0-" + (data.length - 1) + "/" + data.length);
                httpServletResponse.addHeader("Accept-Ranges", "bytes");
                OutputStream out = httpServletResponse.getOutputStream();
                out.write(data);
                out.close();
            }
        } catch (IOException e) {
            // Connection lost -> ignore
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

}