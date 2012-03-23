package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.ImageHandler;

/**
 * User: beat
 * Date: 05.12.2011
 * Time: 01:10:40
 */
public class ItemTypeImageLoader extends ImageLoader {
    public ItemTypeImageLoader(int itemTypeId, Listener listener) {
        super(listener);
        loadImage(ImageHandler.getItemTypeSpriteMapUrl(itemTypeId));
    }
}
