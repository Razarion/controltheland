package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.common.info.PreloadedImageSpriteMapInfo;

/**
 * User: beat
 * Date: 07.10.12
 * Time: 13:51
 */
public class NoSuchImageSpriteMapInfoException extends Exception {
    public NoSuchImageSpriteMapInfoException(int imageSpriteMapId) {
        super("No image sprite map info for id: " + imageSpriteMapId);
    }

    public NoSuchImageSpriteMapInfoException(PreloadedImageSpriteMapInfo.Type preloaded) {
        super("No preloaded image sprite map info for: " + preloaded);
    }
}
