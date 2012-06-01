package com.btxtech.game.jsre.common;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 16.08.2011
 * Time: 12:37:09
 */
public class ImageLoader {
    public interface Listener {
        void onLoaded(ImageElement[] imageElements);
    }

    private ImageElement[] loadedImages;
    private int loadedImageCount;
    private List<String> urls = new ArrayList<String>();

    public ImageLoader() {
    }

    public boolean isLoaded() {
        return loadedImageCount >= urls.size();
    }

    public ImageElement getImage(int imageNr) {
        return loadedImages[imageNr];
    }

    public void addImageUrl(String url) {
        urls.add(url);
    }

    public void startLoading(final Listener listener) {
        int imageCount = 0;
        loadedImages = new ImageElement[urls.size()];
        for (String url : urls) {
            final int imageIndex = imageCount++;
            final Image image = new Image();
            // init image
            image.addLoadHandler(new LoadHandler() {
                public void onLoad(LoadEvent event) {
                    ImageElement imageElement = (ImageElement) image.getElement().cast();
                    loadedImages[imageIndex] = imageElement;
                    loadedImageCount++;
                    if (listener != null && isLoaded()) {
                        listener.onLoaded(loadedImages);
                    }
                }
            });

            image.setUrl(url);
            image.setVisible(false);
            RootPanel.get().add(image); // image must be on page to fire load

            // Image was already loaded
            if (image.getHeight() > 0) {
                ImageElement imageElement = (ImageElement) image.getElement().cast();
                loadedImages[imageIndex] = imageElement;
                loadedImageCount++;
                if (isLoaded() && listener != null) {
                    listener.onLoaded(loadedImages);
                }
            }
        }
    }
}
