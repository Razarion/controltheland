package com.btxtech.game.jsre.client.common.info;

import com.btxtech.game.jsre.client.common.Index;

import java.io.Serializable;

public class ImageSpriteMapInfo implements Serializable {
    private int id;
    private int frameCount;
    private int frameWidth;
    private int frameHeight;
    private int frameTime;

    /**
     * Used by GWT
     */
    ImageSpriteMapInfo() {
    }

    public ImageSpriteMapInfo(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }

    public void setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
    }

    public void setFrameHeight(int frameHeight) {
        this.frameHeight = frameHeight;
    }

    public int getFrameTime() {
        return frameTime;
    }

    public void setFrameTime(int frameTime) {
        this.frameTime = frameTime;
    }

    public Index getSpriteMapOffset(int frame) {
        if (frame < 0 || frame >= frameCount) {
            throw new IllegalArgumentException("ImageSpriteMapInfo.getSpriteMapOffset() id: " + id + " frame: " + frame + " frameCount: " + frameCount + ". Out of range");
        }
        return new Index(frame * frameWidth, 0);
    }

    /**
     * Return the frame or -1 of time is too big
     *
     * @param time how long this clip is running (in MS)
     * @return the frame or -1 if time is too big
     */
    public int getFrame(long time) {
        int frame = (int) (time / frameTime);
        if (frame < frameCount) {
            return frame;
        } else {
            return -1;
        }
    }

    /**
     * Return the frame. If time is too big, it starts over
     *
     * @param time how long this clip is running (in MS)
     * @return the frame
     */
    public int getFrameInfinite(long time) {
        int frame = (int) (time / frameTime);
        return frame % frameCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageSpriteMapInfo imageSpriteMapInfo = (ImageSpriteMapInfo) o;

        return id == imageSpriteMapInfo.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

}
