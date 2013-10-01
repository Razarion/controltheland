package com.btxtech.game.jsre.common.gameengine.services;

import java.io.Serializable;

/**
 * User: beat
 * Date: 27.08.12
 * Time: 00:36
 */
public class PlanetLiteInfo implements Serializable {
    private int planetId;
    private String name;
    private Integer unlockCrystals;

    /**
     * Used by GWT
     */
    PlanetLiteInfo() {
    }

    public PlanetLiteInfo(int planetId, String name, Integer unlockCrystals) {
        this.planetId = planetId;
        this.name = name;
        this.unlockCrystals = unlockCrystals;
    }

    public int getPlanetId() {
        return planetId;
    }

    public String getName() {
        return name;
    }

    public Integer getUnlockCrystals() {
        return unlockCrystals;
    }

    public boolean isUnlockNeeded() {
        return unlockCrystals != null;
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
                ", unlockCrystals=" + unlockCrystals +
                '}';
    }
}
