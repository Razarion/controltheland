package com.btxtech.game.jsre.imagespritemapeditor;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.ImageSpriteMapInfo;
import com.btxtech.game.jsre.client.renderer.ImageSpriteMapContainer;
import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;

public class ImageSpriteMapEditorRenderer {
    private AnimationScheduler.AnimationCallback renderCallback;
    private Canvas canvas;
    private ImageSpriteMapEditorModel imageSpriteMapEditorModel;
    private long startTime;
    private boolean loop;

    public ImageSpriteMapEditorRenderer() {
        canvas = Canvas.createIfSupported();
        renderCallback = new AnimationScheduler.AnimationCallback() {
            @Override
            public void execute(double timestamp) {
                // timestamp can not be converted to a long in google chrome
                try {
                    doRender(System.currentTimeMillis());
                } catch (Exception e) {
                    ClientExceptionHandler.handleException(e);
                } finally {
                    if (renderCallback != null) {
                        AnimationScheduler.get().requestAnimationFrame(renderCallback, canvas.getElement());
                    }
                }
            }
        };
        startTime = System.currentTimeMillis();
        AnimationScheduler.get().requestAnimationFrame(renderCallback, canvas.getElement());
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setImageSpriteMapEditorModel(ImageSpriteMapEditorModel imageSpriteMapEditorModel) {
        this.imageSpriteMapEditorModel = imageSpriteMapEditorModel;
    }

    private void doRender(long timeStamp) {
        if (imageSpriteMapEditorModel == null) {
            return;
        }
        ImageSpriteMapInfo imageSpriteMapInfo = imageSpriteMapEditorModel.getImageSpriteMapInfo();
        if (imageSpriteMapInfo == null) {
            return;
        }
        long playTime = timeStamp - startTime;

        int frame = imageSpriteMapInfo.getFrame(playTime);
        if (frame < 0) {
            if (loop) {
                play();
            }
            return;
        }
        canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
        if (imageSpriteMapEditorModel.isFrameOverriden(frame)) {
            Image image = new Image(imageSpriteMapEditorModel.getOverriddenImage(frame));
            canvas.getContext2d().drawImage(ImageElement.as(image.getElement()), 0, 0);
        } else {
            ImageElement imageElement = ImageSpriteMapContainer.getInstance().getImage(imageSpriteMapInfo);
            if (imageElement != null) {
                Index offset = imageSpriteMapInfo.getSpriteMapOffset(frame);
                if (offset.getX() + imageSpriteMapInfo.getFrameWidth() <= imageElement.getWidth() && offset.getY() + imageSpriteMapInfo.getFrameHeight() <= imageElement.getHeight()) {
                    canvas.getContext2d().drawImage(imageElement,
                            offset.getX(), // Source x pos
                            offset.getY(), // Source y pos
                            imageSpriteMapInfo.getFrameWidth(), // Source width
                            imageSpriteMapInfo.getFrameHeight(), // Source height
                            0,// Canvas y pos
                            0,// Canvas y pos
                            imageSpriteMapInfo.getFrameWidth(), // Destination width
                            imageSpriteMapInfo.getFrameHeight() // Destination height
                    );
                }
            }
            ImageSpriteMapContainer.getInstance().startLoad();
        }
    }

    public void updateSize(int width, int height) {
        canvas.setCoordinateSpaceWidth(width);
        canvas.setCoordinateSpaceHeight(height);
    }

    public void play() {
        startTime = System.currentTimeMillis();
    }

    public void stop() {
        startTime = -1;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

}
