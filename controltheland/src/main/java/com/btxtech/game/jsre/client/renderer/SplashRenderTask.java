package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.cockpit.SplashImage;
import com.btxtech.game.jsre.client.cockpit.SplashManager;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;

import java.util.Collection;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 11:46
 */
public class SplashRenderTask extends AbstractRenderTask {
    private Context2d context2d;
    private ImageLoaderContainer<String> imageLoaderContainer = new ImageLoaderContainer<String>() {

        @Override
        protected String getUrl(String imageName) {
            return ImageHandler.getSplashImageUrl(imageName);
        }
    };

    public SplashRenderTask(Context2d context2d) {
        this.context2d = context2d;
    }

    @Override
    public void render(long timeStamp, Collection<SyncItem> itemsInView, Rectangle viewRect, final Rectangle tileViewRect) {
        SplashImage splashImage = SplashManager.getInstance().getCurrentSplash(timeStamp);
        if (splashImage == null) {
            return;
        }
        ImageElement imageElement = imageLoaderContainer.getImage(splashImage.getSplashImageName());
        if (imageElement != null) {
            context2d.drawImage(imageElement,(viewRect.getWidth() - imageElement.getWidth()) / 2,(viewRect.getHeight() - imageElement.getHeight()) / 2);
        } else {
            imageLoaderContainer.startLoad();
        }
    }
}
