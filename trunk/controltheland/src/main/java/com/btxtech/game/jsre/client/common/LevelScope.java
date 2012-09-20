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
    private Integer planetId;
    private int levelId;
    private int number;
    private Map<Integer, Integer> itemTypeLimitation;
    private int xp2LevelUp;

    /**
     * Used by GWT
     */
    public LevelScope() {
    }

    public LevelScope(Integer planetId, int levelId, int number, Map<Integer, Integer> itemTypeLimitation, int xp2LevelUp) {
        this.planetId = planetId;
        this.levelId = levelId;
        this.number = number;
        this.itemTypeLimitation = itemTypeLimitation;
        this.xp2LevelUp = xp2LevelUp;
    }

    public Integer getPlanetId() {
        return planetId;
    }

    public boolean hasPlanet() {
        return planetId != null;
    }

    public int getNumber() {
        return number;
    }

    public int getLimitation4ItemType(int itemTypeId) {
        Integer limitation = itemTypeLimitation.get(itemTypeId);
        if (limitation != null) {
            return limitation;
        } else {
            return 0;
        }
    }

    public int getXp2LevelUp() {
        return xp2LevelUp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LevelScope that = (LevelScope) o;

        return levelId == that.levelId;

    }

    @Override
    public int hashCode() {
        return levelId;
    }

    @Override
    public String toString() {
        return "LevelScope{levelId=" + levelId + ", number=" + number + ", planetId=" + planetId + '}';
    }
}
