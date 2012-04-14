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
    private int number;
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

    public LevelScope(int number, int maxMoney, Map<Integer, Integer> itemTypeLimitation, int houseSpace, double itemSellFactor, RadarMode radarMode) {
        this.number = number;
        this.maxMoney = maxMoney;
        this.itemTypeLimitation = itemTypeLimitation;
        this.houseSpace = houseSpace;
        this.itemSellFactor = itemSellFactor;
        this.radarMode = radarMode;
    }

    public int getNumber() {
        return number;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LevelScope that = (LevelScope) o;

        return houseSpace == that.houseSpace
                && Double.compare(that.itemSellFactor, itemSellFactor) == 0
                && maxMoney == that.maxMoney
                && number == that.number
                && !(itemTypeLimitation != null ? !itemTypeLimitation.equals(that.itemTypeLimitation) : that.itemTypeLimitation != null)
                && radarMode == that.radarMode;
    }

    @Override
    public int hashCode() {
        int result;
        result = number;
        result = 31 * result + maxMoney;
        result = 31 * result + (itemTypeLimitation != null ? itemTypeLimitation.hashCode() : 0);
        result = 31 * result + houseSpace;
        long temp = (long) itemSellFactor;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (radarMode != null ? radarMode.hashCode() : 0);
        return result;
    }
}
