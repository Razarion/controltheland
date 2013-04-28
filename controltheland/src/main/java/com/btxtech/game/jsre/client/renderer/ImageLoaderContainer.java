package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.common.ImageLoader;
import com.google.gwt.dom.client.ImageElement;

import java.util.ArrayList;
import java.util.Collection;
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
    private Collection<LoadListener> loadListeners = new ArrayList<LoadListener>();

    public interface LoadListener {
        void onLoaded();
    }

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
    public void onLoaded(Map<T, ImageElement> loadedImageElements, Collection<T> failed) {
        images.putAll(loadedImageElements);
        currentlyLoading.removeAll(loadedImageElements.keySet());
        currentlyLoading.removeAll(failed);
        for (LoadListener loadListener : loadListeners) {
            loadListener.onLoaded();
        }
    }

    public void addLoadListener(LoadListener loadListener) {
        loadListeners.add(loadListener);
    }

    public void removeLoadListener(LoadListener loadListener) {
        loadListeners.remove(loadListener);
    }
}
