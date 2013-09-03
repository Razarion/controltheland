package com.btxtech.game.jsre.common.gameengine.services;

import com.btxtech.game.jsre.client.common.RadarMode;

import java.io.Serializable;
import java.util.Map;

/**
 * User: beat
 * Date: 27.08.12
 * Time: 00:36
 */
public class PlanetLiteInfo implements Serializable {
    private int planetId;
    private String name;
    private Integer unlockRazarion;

    /**
     * Used by GWT
     */
    PlanetLiteInfo() {
    }

    public PlanetLiteInfo(int planetId, String name, Integer unlockRazarion) {
        this.planetId = planetId;
        this.name = name;
        this.unlockRazarion = unlockRazarion;
    }

    public int getPlanetId() {
        return planetId;
    }

    public String getName() {
        return name;
    }

    public Integer getUnlockRazarion() {
        return unlockRazarion;
    }

    public boolean isUnlockNeeded() {
        return unlockRazarion != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlanetLiteInfo that = (PlanetLiteInfo) o;

        return planetId == that.planetId;

    }

    @Override
    public int hashCode() {
        return planetId;
    }

    @Override
    public String toString() {
        return "PlanetLiteInfo{" +
                "planetId=" + planetId +
                ", name='" + name + '\'' +
                ", unlockRazarion=" + unlockRazarion +
                '}';
    }
}
