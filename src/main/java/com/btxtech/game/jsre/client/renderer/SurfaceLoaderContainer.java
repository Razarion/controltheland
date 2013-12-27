package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.ImageHandler;

/**
 * User: beat
 * Date: 17.09.12
 * Time: 15:46
 */
public class SurfaceLoaderContainer extends ImageLoaderContainer<Integer> {
    private static final SurfaceLoaderContainer INSTANCE = new SurfaceLoaderContainer();

    /**
     * Singleton
     */
    private SurfaceLoaderContainer() {
    }

    public static SurfaceLoaderContainer getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getUrl(Integer integer) {
        return ImageHandler.getSurfaceImagesUrl(integer);
    }
}
