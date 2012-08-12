package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.common.ImageLoader;
import com.google.gwt.dom.client.ImageElement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: beat
 * Date: 12.08.12
 * Time: 11:47
 */
public abstract class ImageLoaderContainer<T> implements ImageLoader.Listener<T> {
    private Map<T, ImageElement> images = new HashMap<T, ImageElement>();
    private ImageLoader<T> imageLoader;
    private Set<T> currentlyLoading = new HashSet<T>();

    protected abstract String getUrl(T t);

    public ImageElement getImage(T t) {
        ImageElement imageElement = images.get(t);
        if (imageElement != null) {
            return imageElement;
        } else if (currentlyLoading.contains(t)) {
            return null;
        } else {
            if (imageLoader == null) {
                imageLoader = new ImageLoader<T>();
            }
            imageLoader.addImageUrl(getUrl(t), t);
            currentlyLoading.add(t);
            return null;
        }
    }

    public void startLoad() {
        if (imageLoader != null) {
            imageLoader.startLoading(this);
            imageLoader = null;
        }
    }

    @Override
    public void onLoaded(Map<T, ImageElement> imageElements) {
        images.putAll(imageElements);
        currentlyLoading.removeAll(imageElements.keySet());
    }

}
