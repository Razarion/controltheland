package com.btxtech.game.jsre.common;

import com.btxtech.game.jsre.client.common.Constants;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 16.08.2011
 * Time: 12:37:09
 */

/**
 * This is a very tricky class. Different broswers handle the image loading different
 */
public class ImageLoader {
    public interface Listener {
        void onLoaded(ImageElement[] imageElements);
    }

    private ImageElement[] loadedImages;
    private int loadedImageCount;
    private List<String> urls = new ArrayList<String>();
    private Logger log = Logger.getLogger(ImageLoader.class.getName());

    public static void addImageUrlsAndStart(List<String> urls, Listener listener) {
        ImageLoader imageLoader = new ImageLoader();
        for (String url : urls) {
            imageLoader.addImageUrl(url);
        }
        imageLoader.startLoading(listener);
    }

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
        loadedImageCount = 0;
        int imageCount = 0;
        loadedImages = new ImageElement[urls.size()];
        for (String url : urls) {
            final int imageIndex = imageCount++;
            final Image image = new Image();
            image.setUrl(url);
            // "complete" property does not work on every browser
            if (image.getWidth() == 0 || image.getHeight() == 0) {
                // Image must be on page to fire load event.
                image.addLoadHandler(new LoadHandler() {
                    public void onLoad(LoadEvent event) {
                        if (event.getAssociatedType().getName().equals("loaded")) {
                            log.warning("ImageLoader onLoad returned unknown name: " + event.getAssociatedType().getName() + " " + image.getUrl());
                        }
                        image.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
                        onImageLoaded(imageIndex, image, listener);
                    }
                });
                RootPanel.get().add(image, 0, 0);
            } else {
                onImageLoaded(imageIndex, image, listener);
            }
        }
    }

    private void onImageLoaded(int imageIndex, Image image, Listener listener) {
        if (loadedImages[imageIndex] != null) {
            // May happens during hosted mode
            return;
        }
        ImageElement imageElement = (ImageElement) image.getElement().cast();
        loadedImages[imageIndex] = imageElement;
        loadedImageCount++;
        if (listener != null && isLoaded()) {
            listener.onLoaded(loadedImages);
        }
    }

}
