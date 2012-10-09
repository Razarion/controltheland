package com.btxtech.game.jsre.client.common.info;

import java.io.Serializable;

import com.btxtech.game.jsre.client.common.Index;

public class ImageSpriteMapInfo implements Serializable{
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
        if (frame >= frameCount) {
            throw new IllegalArgumentException("ImageSpriteMapInfo.getSpriteMapOffset() id: " + id + " frame: " + frame + " frameCount: " + frameCount + ". Out of range");
        }
        return new Index(frame * frameWidth, 0);
    }

    /**
     * Return the frame or -1 of time is to long
     *
     * @param time how long this clip is running (in MS)
     * @return the frame or -1 if time is to long
     */
    public int getFrame(long time) {
        int frame = (int) (time / frameTime);
        if (frame < frameCount) {
            return frame;
        } else {
            return -1;
        }
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
