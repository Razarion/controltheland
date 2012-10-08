package com.btxtech.game.jsre.client.common.info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 07.10.12
 * Time: 23:35
 */
public class CommonClipInfo implements Serializable {
    public enum Type {
        EXPLOSION
    }
    private Map<Type, List<Integer>> commonClips = new HashMap<Type, List<Integer>>();

    public void add(Type type, int clipId) {
        if(type == null) {
            throw new IllegalArgumentException("CommonClipInfo.add() type must be set.");
        }
        List<Integer> clipIds = commonClips.get(type);
        if (clipIds == null) {
            clipIds = new ArrayList<Integer>();
            commonClips.put(type, clipIds);
        }
        clipIds.add(clipId);
    }

    public Map<Type, List<Integer>> getCommonClips() {
        return commonClips;
    }
}
