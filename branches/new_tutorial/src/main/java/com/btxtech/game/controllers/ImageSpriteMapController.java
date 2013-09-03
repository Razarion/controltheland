package com.btxtech.game.controllers;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.common.ClientDateUtil;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.ImageHolder;
import com.btxtech.game.services.media.ClipService;
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
 * Date: 14.08.12
 * Time: 11:04
 */
@Component(value = "imageSpriteMapController")
public class ImageSpriteMapController implements Controller {
    @Autowired
    private ClipService clipService;

    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        try {
            int imageSpriteMapId = Integer.parseInt(httpServletRequest.getParameter(Constants.IMAGE_SPRITE_MAP_ID));
            ImageHolder imageHolder = clipService.getImageSpriteMap(imageSpriteMapId);
            httpServletResponse.setContentLength(imageHolder.getData().length);
            httpServletResponse.setContentType(imageHolder.getContentType());
            httpServletResponse.addDateHeader("Expires", System.currentTimeMillis() + ClientDateUtil.MILLIS_IN_DAY);
            OutputStream out = httpServletResponse.getOutputStream();
            out.write(imageHolder.getData());
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
