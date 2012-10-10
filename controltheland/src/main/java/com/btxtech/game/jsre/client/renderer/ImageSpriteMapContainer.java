package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.info.ClipInfo;
import com.btxtech.game.jsre.client.common.info.ImageSpriteMapInfo;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 12:44
 */
public class ImageSpriteMapContainer extends ImageLoaderContainer<ImageSpriteMapInfo> {
    private static final ImageSpriteMapContainer INSTANCE = new ImageSpriteMapContainer();

    public static ImageSpriteMapContainer getInstance() {
        return INSTANCE;
    }

    private ImageSpriteMapContainer() {
    }

    @Override
    protected String getUrl(ImageSpriteMapInfo imageSpriteMapInfo) {
        return ImageHandler.getImageSpriteMapUrl(imageSpriteMapInfo.getId());
    }
}
