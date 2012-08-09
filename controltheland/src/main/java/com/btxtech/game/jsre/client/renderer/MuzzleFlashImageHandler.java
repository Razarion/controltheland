package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.common.ImageLoader;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
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
public class MuzzleFlashImageHandler implements ImageLoader.Listener<BaseItemType> {
    private Map<BaseItemType, ImageElement> images = new HashMap<BaseItemType, ImageElement>();
    private ImageLoader<BaseItemType> imageLoader;
    private Set<BaseItemType> currentlyLoading = new HashSet<BaseItemType>();

    public ImageElement getImage(BaseItemType baseItemType) {
        ImageElement imageElement = images.get(baseItemType);
        if (imageElement != null) {
            return imageElement;
        } else if(currentlyLoading.contains(baseItemType)) {
            return null;
        } else {
            if (imageLoader == null) {
                imageLoader = new ImageLoader<BaseItemType>();
            }
            imageLoader.addImageUrl(ImageHandler.getMuzzleFlashImageUrl(baseItemType), baseItemType);
            currentlyLoading.add(baseItemType);
            return null;
        }
    }

    public void startLoad() {
       if(imageLoader != null) {
           imageLoader.startLoading(this);
       }
    }

    @Override
    public void onLoaded(Map<BaseItemType, ImageElement> imageElements) {
        images.putAll(imageElements);
        currentlyLoading.removeAll(imageElements.keySet());
    }
}
