package com.btxtech.game.jsre.itemtypeeditor;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

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

    public ImageLoader(Listener listener) {
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
        final Image image = new Image(url);
        image.addLoadHandler(new LoadHandler() {
            public void onLoad(LoadEvent event) {
                ImageElement imageElement = (ImageElement) image.getElement().cast();
                images.put(imageNr, imageElement);
                if (isLoaded() && listener != null) {
                    listener.onLoaded();
                }
            }
        });
        image.setVisible(false);
        RootPanel.get().add(image); // image must be on page to fire load
    }

    public interface Listener {
        void onLoaded();
    }
}
