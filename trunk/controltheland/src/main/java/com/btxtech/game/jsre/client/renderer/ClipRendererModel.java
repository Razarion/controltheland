package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.ClientClipHandler;
import com.btxtech.game.jsre.client.NoSuchImageSpriteMapInfoException;
import com.btxtech.game.jsre.client.SoundHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.info.ClipInfo;
import com.btxtech.game.jsre.client.common.info.ImageSpriteMapInfo;
import com.btxtech.game.jsre.client.common.info.PreloadedImageSpriteMapInfo;

/**
 * User: beat
 * Date: 14.10.12
 * Time: 16:43
 */
public class ClipRendererModel {
    private int frame = -1;
    private long startTime;
    private Index absoluteClipMiddle;
    private Rectangle absoluteViewRect;
    private Index relativeImageMiddle;
    private boolean insideViewRect;
    private boolean playing;
    private ImageSpriteMapInfo imageSpriteMapInfo;
    private Index spriteMapOffset;
    private boolean rotated;
    private double rotation;
    private int xOffset;
    private int yOffset;
    private boolean loop;
    private boolean noYMiddle;
    private Integer maxHeight;
    private ImageSpriteMapInfo preLoadedSpriteMapInfo;

    protected void initAndPlaySound(long timeStamp, ClipInfo clipInfo, Index absoluteMiddle, double rotation, boolean loop) throws NoSuchImageSpriteMapInfoException {
        this.loop = loop;
        startTime = timeStamp;
        imageSpriteMapInfo = ClientClipHandler.getInstance().getImageSpriteMapInfo(clipInfo.getSpriteMapId());
        setAbsoluteMiddle(absoluteMiddle, rotation);
        xOffset = imageSpriteMapInfo.getFrameWidth() / 2;
        if (noYMiddle) {
            yOffset = imageSpriteMapInfo.getFrameHeight();
        } else {
            yOffset = imageSpriteMapInfo.getFrameHeight() / 2;
        }
        SoundHandler.getInstance().playClipSound(clipInfo);
    }

    protected void setPreLoadedSpriteMapInfo(PreloadedImageSpriteMapInfo.Type preloaded) throws NoSuchImageSpriteMapInfoException {
        preLoadedSpriteMapInfo = ClientClipHandler.getInstance().getPreloadedImageSpriteMapInfo(preloaded);
    }

    protected void setNoYMiddle() {
        noYMiddle = true;
    }

    protected void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void prepareRender(long timeStamp, Rectangle viewRect) {
        insideViewRect = viewRect.adjoins(absoluteViewRect);
        playing = false;
        if (!insideViewRect) {
            return;
        }

        int newFrame = imageSpriteMapInfo.getFrame(timeStamp - startTime);
        playing = newFrame >= 0;
        if (!playing) {
            if (loop) {
                playing = true;
                newFrame = 0;
                startTime = timeStamp;
            } else {
                return;
            }
        }
        relativeImageMiddle = absoluteClipMiddle.sub(viewRect.getStart());
        if (newFrame == frame) {
            return;
        }
        frame = newFrame;
        spriteMapOffset = imageSpriteMapInfo.getSpriteMapOffset(frame);
    }

    protected void stop() {
        playing = false;
        insideViewRect = false;
    }

    protected void setAbsoluteMiddle(Index absoluteMiddle, double rotation) {
        absoluteClipMiddle = absoluteMiddle;
        absoluteViewRect = Rectangle.generateRectangleFromMiddlePoint(absoluteMiddle, imageSpriteMapInfo.getFrameWidth(), imageSpriteMapInfo.getFrameHeight());
        if (rotation != 0.0) {
            rotated = true;
            this.rotation = rotation;
        }
    }

    public boolean isPlaying() {
        return playing;
    }

    public boolean isInViewRect() {
        return insideViewRect;
    }

    public int getFrame() {
        return frame;
    }

    public ImageSpriteMapInfo getImageSpriteMapInfo() {
        return imageSpriteMapInfo;
    }

    public int getSpriteMapXOffset() {
        return spriteMapOffset.getX();
    }

    public int getSpriteMapYOffset() {
        return spriteMapOffset.getY();
    }

    public int getRelativeImageMiddleX() {
        return relativeImageMiddle.getX();
    }

    public int getRelativeImageMiddleY() {
        return relativeImageMiddle.getY();
    }

    public int getXOffset() {
        return xOffset;
    }

    public int getYOffset() {
        return yOffset;
    }

    public boolean isRotated() {
        return rotated;
    }

    public double getRotation() {
        return rotation;
    }

    public Integer getMaxHeight() {
        return maxHeight;
    }

    public ImageSpriteMapInfo getPreLoadedSpriteMapInfo() {
        return preLoadedSpriteMapInfo;
    }
}
