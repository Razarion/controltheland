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
    private static final String DEFAULT_COLOR = "#FFFFFF";
    private Map<Integer, Integer> imageGroupMap = new HashMap<Integer, Integer>();
    private Map<Integer, Map<SurfaceType, String>> groupBgMap = new HashMap<Integer, Map<SurfaceType, String>>();

    public void put(int imageId, int groupId, String bgNone, String bgWater, String bgLand, String bgWaterCoast, String bgLandCoast) {
        if (!groupBgMap.containsKey(groupId)) {
            createAndAddGroup(groupId, bgNone, bgWater, bgLand, bgWaterCoast, bgLandCoast);
        }
        imageGroupMap.put(imageId, groupId);
    }

    private void createAndAddGroup(int groupId, String bgNone, String bgWater, String bgLand, String bgWaterCoast, String bgLandCoast) {
        Map<SurfaceType, String> surfaceTypeBackground = new HashMap<SurfaceType, String>();
        if (bgNone != null) {
            surfaceTypeBackground.put(SurfaceType.NONE, bgNone);
        }
        if (bgWater != null) {
            surfaceTypeBackground.put(SurfaceType.WATER, bgWater);
        }
        if (bgLand != null) {
            surfaceTypeBackground.put(SurfaceType.LAND, bgLand);
        }
        if (bgWaterCoast != null) {
            surfaceTypeBackground.put(SurfaceType.COAST, bgWaterCoast);
        }
        if (bgLandCoast != null) {
            surfaceTypeBackground.put(SurfaceType.COAST, bgLandCoast);
        }
        groupBgMap.put(groupId, surfaceTypeBackground);
    }

    public String get(int imageId, SurfaceType surfaceType) {
        Integer groupId = imageGroupMap.get(imageId);
        if (groupId == null) {
            return DEFAULT_COLOR;
        }
        Map<SurfaceType, String> surfaceTypeBackground = groupBgMap.get(groupId);
        if (surfaceTypeBackground == null) {
            return DEFAULT_COLOR;
        }
        String background = surfaceTypeBackground.get(surfaceType);
        if (background != null) {
            return background;
        } else {
            return DEFAULT_COLOR;
        }
    }
}
