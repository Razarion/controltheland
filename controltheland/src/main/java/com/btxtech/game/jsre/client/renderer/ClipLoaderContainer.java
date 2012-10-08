package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.info.ClipInfo;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 12:44
 */
public class ClipLoaderContainer extends ImageLoaderContainer<ClipInfo> {
    private static final ClipLoaderContainer INSTANCE = new ClipLoaderContainer();

    public static ClipLoaderContainer getInstance() {
        return INSTANCE;
    }

    private ClipLoaderContainer() {
    }

    @Override
    protected String getUrl(ClipInfo itemType) {
        return ImageHandler.getClipSpriteMapUrl(itemType);
    }
}
