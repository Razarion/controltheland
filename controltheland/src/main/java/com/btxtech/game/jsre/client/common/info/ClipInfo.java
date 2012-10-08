package com.btxtech.game.jsre.client.common.info;

import com.btxtech.game.jsre.client.common.Index;

import java.io.Serializable;

/**
 * User: beat
 * Date: 07.10.12
 * Time: 13:43
 */
public class ClipInfo implements Serializable {
    private int clipId;
    private int spriteMapId;
    private Integer soundId;
    private int frameCount;
    private int frameWidth;
    private int frameHeight;
    private int frameTime;

    public ClipInfo(int clipId) {
        this.clipId = clipId;
    }

    public int getClipId() {
        return clipId;
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

    public boolean hasSoundId() {
        return soundId != null;
    }

    public Integer getSoundId() {
        return soundId;
    }

    public int getSpriteMapId() {
        return spriteMapId;
    }

    public void setSpriteMapId(int spriteMapId) {
        this.spriteMapId = spriteMapId;
    }

    public void setSoundId(Integer soundId) {
        this.soundId = soundId;
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

    public void setFrameTime(int frameTime) {
        this.frameTime = frameTime;
    }

    public Index getSpriteMapOffset(int frame) {
        if (frame >= frameCount) {
            throw new IllegalArgumentException("ClipInfo.getSpriteMapOffset() clipId: " + clipId + " frame: " + frame + " frameCount: " + frameCount + ". Out of range");
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

        ClipInfo clipInfo = (ClipInfo) o;

        return clipId == clipInfo.clipId;

    }

    @Override
    public int hashCode() {
        return clipId;
    }
}
