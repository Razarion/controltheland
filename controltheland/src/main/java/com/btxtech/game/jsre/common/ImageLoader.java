package com.btxtech.game.jsre.common;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;

import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 16.08.2011
 * Time: 12:37:09
 */
public abstract class ImageLoader {
    private Map<Integer, ImageElement> images = new HashMap<Integer, ImageElement>();
    private int imageCount = 0;
    private Listener listener;

    public ImageLoader() {
    }

    public ImageLoader(Listener listener) {
        this.listener = listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public boolean isLoaded() {
        return images.size() >= imageCount;
    }

    public ImageElement getImage(int imageNr) {
        return images.get(imageNr);
    }

    protected void loadImage(String url) {
        final int imageNr = imageCount;
        imageCount++;
        // init image
        final Image image = new Image();
        image.addLoadHandler(new LoadHandler() {
            public void onLoad(LoadEvent event) {
                ImageElement imageElement = (ImageElement) image.getElement().cast();
                images.put(imageNr, imageElement);
                if (isLoaded() && listener != null) {
                    listener.onLoaded();
                }
            }
        });
        image.setUrl(url);

        if(image.getHeight() > 0) {
            // TODO if more than one image -> this will call the listener too early
            ImageElement imageElement = (ImageElement) image.getElement().cast();
            images.put(imageNr, imageElement);
            if (isLoaded() && listener != null) {
                listener.onLoaded();
            }
        }
    }

    public interface Listener {
        void onLoaded();
    }
}
