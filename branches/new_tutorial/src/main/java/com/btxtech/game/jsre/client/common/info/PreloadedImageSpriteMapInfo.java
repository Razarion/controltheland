package com.btxtech.game.jsre.client.common.info;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 07.10.12
 * Time: 23:35
 */
public class PreloadedImageSpriteMapInfo implements Serializable {
    public enum Type {
        EXPLOSION,
        MUZZLE_FLASH,
        DETONATION
    }

    private Map<Type, Integer> imageSpriteMapInfo = new HashMap<Type, Integer>();

    public void add(Type type, int clipId) {
        if (type == null) {
            throw new IllegalArgumentException("PreloadedImageSpriteMapInfo.add() type must be set.");
        }
        imageSpriteMapInfo.put(type, clipId);
    }

    public Map<Type, Integer> getPreloadedImageSpriteMapInfo() {
        return imageSpriteMapInfo;
    }
}
