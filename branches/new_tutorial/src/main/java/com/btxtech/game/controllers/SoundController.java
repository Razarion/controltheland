package com.btxtech.game.controllers;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.common.ClientDateUtil;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.media.DbSound;
import com.btxtech.game.services.media.SoundService;
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
 * Date: 14.08.12
 * Time: 11:04
 */
@Component(value = "soundController")
public class SoundController implements Controller {
    @Autowired
    private SoundService soundService;
    private Log log = LogFactory.getLog(SoundController.class);

    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        try {
            int soundId = Integer.parseInt(httpServletRequest.getParameter(Constants.SOUND_ID));
            String codec = httpServletRequest.getParameter(Constants.SOUND_CODEC);
            DbSound dbSound = soundService.getSoundLibraryCrud().readDbChild(soundId);
            byte[] data;
            switch (codec) {
                case Constants.SOUND_CODEC_TYPE_MP3: {
                    data = dbSound.getDataMp3();
                    break;
                }
                case Constants.SOUND_CODEC_TYPE_OGG: {
                    data = dbSound.getDataOgg();
                    break;
                }
                default:
                    throw new IllegalArgumentException("SoundResource: Codec:" + codec + " id: " + soundId);
            }
            if (data == null) {
                log.warn("Sound: " + dbSound + " does not have data for Codec: " + codec);
                httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
            httpServletResponse.setContentLength(data.length);
            httpServletResponse.setContentType(codec);
            httpServletResponse.addDateHeader("Expires", System.currentTimeMillis() + ClientDateUtil.MILLIS_IN_DAY);
            httpServletResponse.addHeader("Content-Range", "bytes 0-" + (data.length - 1) + "/" + data.length);
            httpServletResponse.addHeader("Accept-Ranges", "bytes");
            OutputStream out = httpServletResponse.getOutputStream();
            out.write(data);
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
