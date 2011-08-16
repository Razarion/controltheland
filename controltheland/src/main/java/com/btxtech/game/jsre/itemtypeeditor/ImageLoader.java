package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.ImageHandler;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * User: beat
 * Date: 16.08.2011
 * Time: 12:37:09
 */
public class ImageLoader {
    private ImageElement[] images;
    private int imageLoadedCount;
    private int imageCount;
    private Listener listener;

    public ImageLoader(int itemTypeId, int imageCount, Listener listener) {
        this.imageCount = imageCount;
        this.listener = listener;
        images = new ImageElement[imageCount];
        for (int i = 1; i <= imageCount; i++) {
            String url = ImageHandler.getItemTYpeUrl(itemTypeId, i);
            loadImage(url, i);
        }
    }

    public boolean isLoaded() {
        return imageLoadedCount >= imageCount;
    }

    public ImageElement getImage(int imageNr) {
        return images[imageNr];
    }

    private void loadImage(String url, final int imageNr) {
        // init image
        final Image image = new Image(url);
        image.addLoadHandler(new LoadHandler() {
            public void onLoad(LoadEvent event) {
                ImageElement imageElement = (ImageElement) image.getElement().cast();
                images[imageNr - 1] = imageElement;
                imageLoadedCount++;
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
