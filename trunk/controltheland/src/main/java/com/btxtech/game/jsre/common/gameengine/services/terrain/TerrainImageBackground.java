package com.btxtech.game.jsre.common.gameengine.services.terrain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 09.11.2011
 * Time: 13:52:50
 */
public class TerrainImageBackground implements Serializable {
    private Map<Integer, String> backgrounds = new HashMap<Integer, String>();

    public void put(int imageId, String background) {
        backgrounds.put(imageId, background);
    }

    public String get(int imageId) {
        String background = backgrounds.get(imageId);
        if (background == null) {
            return "#FFFFFF";
        } else {
            return background;
        }
    }
}
