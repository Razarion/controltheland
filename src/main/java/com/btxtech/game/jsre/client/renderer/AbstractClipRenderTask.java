package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.common.info.ImageSpriteMapInfo;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;

/**
 * User: beat
 * Date: 31.07.12
 * Time: 19:43
 */
public abstract class AbstractClipRenderTask extends AbstractRenderTask {
    public void renderClip(Context2d context2d, ClipRendererModel clipRendererModel) {
        // Load Image
        ImageSpriteMapInfo imageSpriteMapInfo = clipRendererModel.getImageSpriteMapInfo();
        ImageElement imageElement = ImageSpriteMapContainer.getInstance().getImage(imageSpriteMapInfo);
        if (imageElement == null) {
            ImageSpriteMapContainer.getInstance().startLoad();
            imageSpriteMapInfo = clipRendererModel.getPreLoadedSpriteMapInfo();
            if (imageSpriteMapInfo == null) {
                return;
            }
            imageElement = ImageSpriteMapContainer.getInstance().getImage(imageSpriteMapInfo);
            if (imageElement == null) {
                ClientExceptionHandler.handleExceptionOnlyOnce("AbstractClipRenderTask.renderClip() preloaded clip is not available: " + imageSpriteMapInfo.getId());
                return;
            }
        }
        // Draw
        context2d.save();
        context2d.translate(clipRendererModel.getRelativeImageMiddleX(), clipRendererModel.getRelativeImageMiddleY());
        if (clipRendererModel.isRotated()) {
            context2d.rotate(-clipRendererModel.getRotation());
        }
        context2d.drawImage(imageElement,
                clipRendererModel.getSpriteMapXOffset(), // Source x pos
                clipRendererModel.getSpriteMapYOffset(), // Source y pos
                imageSpriteMapInfo.getFrameWidth(),  // Source width
                imageSpriteMapInfo.getFrameHeight(), // Source height
                -clipRendererModel.getXOffset(),// Canvas x pos
                -clipRendererModel.getYOffset() + (clipRendererModel.getMaxHeight() != null ? imageSpriteMapInfo.getFrameHeight() - clipRendererModel.getMaxHeight() : 0),// Canvas y pos
                imageSpriteMapInfo.getFrameWidth(), // Destination width
                clipRendererModel.getMaxHeight() != null ? clipRendererModel.getMaxHeight() : imageSpriteMapInfo.getFrameHeight() // Destination height
        );

        context2d.restore();
    }
}
