/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.jsre.client.common;

import java.io.Serializable;
import java.util.Map;

/**
 * User: beat
 * Date: 17.05.2010
 * Time: 18:48:18
 */
public class LevelScope implements Serializable {
    private String levelName;
    private int maxMoney;
    private Map<Integer, Integer> itemTypeLimitation;
    private int houseSpace;
    private double itemSellFactor;
    private RadarMode radarMode;

    /**
     * Used by GWT
     */
    public LevelScope() {
    }

    public LevelScope(String levelName, int maxMoney, Map<Integer, Integer> itemTypeLimitation, int houseSpace, double itemSellFactor, RadarMode radarMode) {
        this.levelName = levelName;
        this.maxMoney = maxMoney;
        this.itemTypeLimitation = itemTypeLimitation;
        this.houseSpace = houseSpace;
        this.itemSellFactor = itemSellFactor;
        this.radarMode = radarMode;
    }

    public String getLevelName() {
        return levelName;
    }

    public int getMaxMoney() {
        return maxMoney;
    }

    public int getLimitation4ItemType(int itemTypeId) {
        Integer limitation = itemTypeLimitation.get(itemTypeId);
        if (limitation != null) {
            return limitation;
        } else {
            return 0;
        }
    }

    public int getHouseSpace() {
        return houseSpace;
    }

    public double getItemSellFactor() {
        return itemSellFactor;
    }

    public RadarMode getRadarMode() {
        return radarMode;
    }
}
