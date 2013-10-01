package com.btxtech.game.controllers;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.common.ClientDateUtil;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.ImageHolder;
import com.btxtech.game.services.planet.PlanetSystemService;
import org.apache.commons.lang.ArrayUtils;
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
@Component(value = "imageController")
public class ImageController implements Controller {
    @Autowired
    private PlanetSystemService planetSystemService;

    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        try {
            String imageType = httpServletRequest.getParameter(Constants.IMAGE_TYPE_KEY);
            if (imageType == null) {
                throw new IllegalArgumentException(Constants.IMAGE_TYPE_KEY + " must be set");
            }
            ImageHolder imageHolder;
            switch (imageType) {
                case Constants.IMAGE_TYPE_VALUE_STAR_MAP: {
                    int planetId = Integer.parseInt(httpServletRequest.getParameter(Constants.IMAGE_ID));
                    imageHolder = planetSystemService.getStarMapImage(planetId);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Can not handle image type: " + imageType);
            }
            if (ArrayUtils.isEmpty(imageHolder.getData())) {
                throw new IllegalArgumentException("No image for " + httpServletRequest.getQueryString());
            }
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
