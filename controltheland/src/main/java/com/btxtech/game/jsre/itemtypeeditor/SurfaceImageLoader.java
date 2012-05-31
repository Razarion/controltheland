package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.common.ImageLoader;

/**
 * User: beat
 * Date: 05.12.2011
 * Time: 01:10:40
 */
public class SurfaceImageLoader extends ImageLoader {
    public SurfaceImageLoader(int surfaceImageId) {
        super(null);
        loadImage(ImageHandler.getSurfaceImagesUrl(surfaceImageId));
    }
}
