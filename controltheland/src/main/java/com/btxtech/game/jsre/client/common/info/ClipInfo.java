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

    /**
     * Used by GWT
     */
    ClipInfo() {
    }

    public ClipInfo(int clipId) {
        this.clipId = clipId;
    }

    public int getClipId() {
        return clipId;
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
