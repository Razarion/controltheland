package com.btxtech.game.services.gwt;

import com.btxtech.game.jsre.client.common.info.ImageSpriteMapInfo;
import com.btxtech.game.jsre.imagespritemapeditor.ImageSpriteMapAccess;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.media.ClipService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: beat
 * Date: 26.08.2011
 * Time: 13:40:38
 */
public class ImageSpriteMapAccessImpl extends AutowiredRemoteServiceServlet implements ImageSpriteMapAccess {
    @Autowired
    private ClipService clipService;

    @Override
    public ImageSpriteMapInfo loadImageSpriteMapInfo(int imageSpriteMapId) {
        try {
            return clipService.getImageSpriteMapCrud().readDbChild(imageSpriteMapId).createImageSpriteMapInfo();
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
            return null;
        }
    }

    @Override
    public void saveImageSpriteMapInfo(ImageSpriteMapInfo imageSpriteMapInfo, String[] overriddenImages) {
        try {
            clipService.saveImageSpriteMap(imageSpriteMapInfo, overriddenImages);
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }
}
