package com.btxtech.game.jsre.common.gameengine.services;

import com.btxtech.game.jsre.client.common.RadarMode;

import java.io.Serializable;
import java.util.Map;

/**
 * User: beat
 * Date: 27.08.12
 * Time: 00:36
 */
public class PlanetInfo implements Serializable {
    public static final int MISSION_PLANET_ID = -1;
    public static final int EDITOR_PLANET_ID = -2;
    private PlanetLiteInfo planetLiteInfo;
    private int maxMoney;
    private int houseSpace;
    private Map<Integer, Integer> itemTypeLimitation;
    private RadarMode radarMode;

    public int getPlanetId() {
        return planetLiteInfo.getPlanetId();
    }

    public void setPlanetIdAndName(int planetId, String name) {
        planetLiteInfo = new PlanetLiteInfo(planetId, name);
    }

    public String getName() {
        return planetLiteInfo.getName();
    }

    public int getMaxMoney() {
        return maxMoney;
    }

    public void setMaxMoney(int maxMoney) {
        this.maxMoney = maxMoney;
    }

    public int getHouseSpace() {
        return houseSpace;
    }

    public void setHouseSpace(int houseSpace) {
        this.houseSpace = houseSpace;
    }

    public void setItemTypeLimitation(Map<Integer, Integer> itemTypeLimitation) {
        this.itemTypeLimitation = itemTypeLimitation;
    }

    public int getLimitation4ItemType(int itemTypeId) {
        Integer limitation = itemTypeLimitation.get(itemTypeId);
        if (limitation != null) {
            return limitation;
        } else {
            return 0;
        }
    }

    public RadarMode getRadarMode() {
        return radarMode;
    }

    public void setRadarMode(RadarMode radarMode) {
        this.radarMode = radarMode;
    }

    public PlanetLiteInfo getPlanetLiteInfo() {
        return planetLiteInfo;
    }
}
