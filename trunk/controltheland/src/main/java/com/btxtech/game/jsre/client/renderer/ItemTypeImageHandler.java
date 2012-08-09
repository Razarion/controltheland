package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.common.ImageLoader;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.google.gwt.dom.client.ImageElement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 12:44
 */
public class ItemTypeImageHandler implements ImageLoader.Listener<ItemType> {
    private static final ItemTypeImageHandler INSTANCE = new ItemTypeImageHandler();
    private Map<ItemType, ImageElement> images = new HashMap<ItemType, ImageElement>();
    private ImageLoader<ItemType> imageLoader;
    private Set<ItemType> currentlyLoading = new HashSet<ItemType>();

    public static ItemTypeImageHandler getInstance() {
        return INSTANCE;
    }

    private ItemTypeImageHandler() {
    }

    public ImageElement getImage(ItemType itemType) {
        ImageElement imageElement = images.get(itemType);
        if (imageElement != null) {
            return imageElement;
        } else if (currentlyLoading.contains(itemType)) {
            return null;
        } else {
            if (imageLoader == null) {
                imageLoader = new ImageLoader<ItemType>();
            }
            imageLoader.addImageUrl(ImageHandler.getItemTypeSpriteMapUrl(itemType.getId()), itemType);
            currentlyLoading.add(itemType);
            return null;
        }
    }

    public void startLoad() {
        if (imageLoader != null) {
            imageLoader.startLoading(this);
        }
    }

    @Override
    public void onLoaded(Map<ItemType, ImageElement> imageElements) {
        images.putAll(imageElements);
        currentlyLoading.removeAll(imageElements.keySet());
    }
}
