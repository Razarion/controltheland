package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.common.info.CommonClipInfo;

/**
 * User: beat
 * Date: 07.10.12
 * Time: 13:51
 */
public class NoSuchImageSpriteMapInfoException extends Exception {
    public NoSuchImageSpriteMapInfoException(int imageSpriteMapId) {
        super("No image sprite map info for id: " + imageSpriteMapId);
    }
}
