package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 12:44
 */
public class ItemTypeImageLoaderContainer extends ImageLoaderContainer<ItemType> {
    private static final ItemTypeImageLoaderContainer INSTANCE = new ItemTypeImageLoaderContainer();

    public static ItemTypeImageLoaderContainer getInstance() {
        return INSTANCE;
    }

    private ItemTypeImageLoaderContainer() {
    }

    @Override
    protected String getUrl(ItemType itemType) {
        return ImageHandler.getItemTypeSpriteMapUrl(itemType.getId());
    }
}
