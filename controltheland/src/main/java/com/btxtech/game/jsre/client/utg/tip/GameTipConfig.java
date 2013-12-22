package com.btxtech.game.jsre.client.utg.tip;

import com.btxtech.game.jsre.client.common.Index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 17:50
 */
public class GameTipConfig implements Serializable {
    public enum Tip {
        BUILD,
        FABRICATE,
        GET_RESOURCE,
        MOVE,
        ATTACK,
        SCROLL,
        WATCH_QUEST,
        LOAD_CONTAINER;

        public static List<Tip> getValuesIncludingNull() {
            List<Tip> tips = new ArrayList<Tip>(Arrays.asList(values()));
            tips.add(null);
            return tips;
        }
    }

    private Tip tip;
    private int actor;
    private int target;
    private int toBeBuiltId;
    private int resourceId;
    private Index terrainPositionHint;

    public Tip getTip() {
        return tip;
    }

    public void setTip(Tip tip) {
        this.tip = tip;
    }

    public int getActor() {
        return actor;
    }

    public void setActor(int actor) {
        this.actor = actor;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getToBeBuiltId() {
        return toBeBuiltId;
    }

    public void setToBeBuiltId(int toBeBuiltId) {
        this.toBeBuiltId = toBeBuiltId;
    }

    public Index getTerrainPositionHint() {
        return terrainPositionHint;
    }

    public void setTerrainPositionHint(Index terrainPositionHint) {
        this.terrainPositionHint = terrainPositionHint;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }
}
