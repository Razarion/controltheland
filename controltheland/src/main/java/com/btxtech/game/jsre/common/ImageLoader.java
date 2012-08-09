package com.btxtech.game.jsre.common;

import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.btxtech.game.jsre.common.perfmon.TimerPerfmon;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import org.apache.commons.collections.map.HashedMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 16.08.2011
 * Time: 12:37:09
 */

/**
 * This is a very tricky class. Different browsers handle the image loading different
 */
public class ImageLoader<T> {
    public interface Listener<T> {
        void onLoaded(Map<T, ImageElement> imageElements);
    }

    private Map<T, ImageElement> loadedImages;
    private int loadedImageCount = 0;
    private List<String> urls = new ArrayList<String>();
    private List<T> userObjects = new ArrayList<T>();
    private Logger log = Logger.getLogger(ImageLoader.class.getName());
    private Timer timer;

    public boolean isLoaded() {
        return loadedImageCount >= urls.size();
    }

    public ImageElement getImage(T userObject) {
        return loadedImages.get(userObject);
    }

    public void addImageUrl(String url, T userObject) {
        urls.add(url);
        userObjects.add(userObject);
    }

    public int getUrlSize() {
        return urls.size();
    }

    public void startLoading(final Listener<T> listener) {
        loadedImageCount = 0;
        int imageCount = 0;
        loadedImages = new HashMap<T, ImageElement>();
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
                        onImageLoaded(userObjects.get(imageIndex), image, listener);
                        RootPanel.get().remove(image);
                    }
                });
                RootPanel.get().add(image, 0, 0);
            } else {
                onImageLoaded(userObjects.get(imageIndex), image, listener);
            }
        }
        if (!isLoaded()) {
            startTimer();
        }
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new TimerPerfmon(PerfmonEnum.IMAGE_LOADER) {

            @Override
            public void runPerfmon() {
                timer = null;
                checkProgress();
            }
        };
        timer.schedule(10000);
    }

    private void onImageLoaded(T userObject, Image image, Listener<T> listener) {
// TODO needed???        if (userObjects.contains(userObject)) {
// TODO needed???              // May happens during hosted mode
// TODO needed???              return;
// TODO needed???          }
        ImageElement imageElement = (ImageElement) image.getElement().cast();
        loadedImages.put(userObject, imageElement);
        loadedImageCount++;
        if (isLoaded()) {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            if (listener != null) {
                listener.onLoaded(loadedImages);
            }
        }
    }

    private void checkProgress() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("----- Images still not loaded: ");
        stringBuilder.append(loadedImageCount);
        stringBuilder.append("/");
        stringBuilder.append(urls.size());
        stringBuilder.append('\n');

        for (int i = 0, userObjectsSize = userObjects.size(); i < userObjectsSize; i++) {
            T userObject = userObjects.get(i);
            if(!loadedImages.containsKey(userObject)) {
                stringBuilder.append(urls.get(i));
                stringBuilder.append('\n');
            }
        }
        log.warning(stringBuilder.toString());
        startTimer();
    }
}
