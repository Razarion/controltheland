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
public class Level implements Serializable {
    private String name;
    private String html;
    private boolean realGame;
    private int maxMoney;
    private Map<Integer, Integer> itemTypeLimitation;
    private int houseSpace;

    /**
     * Used by GWT
     */
    public Level() {
    }

    public Level(String name, String html, boolean realGame, int maxMoney, Map<Integer, Integer> itemTypeLimitation, int houseSpace) {
        this.name = name;
        this.html = html;
        this.realGame = realGame;
        this.maxMoney = maxMoney;
        this.itemTypeLimitation = itemTypeLimitation;
        this.houseSpace = houseSpace;
    }

    public String getName() {
        return name;
    }

    public String getHtml() {
        return html;
    }

    public boolean isRealGame() {
        return realGame;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Level)) return false;

        Level level = (Level) o;

        return !(name != null ? !name.equals(level.name) : level.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    public int getHouseSpace() {
        return houseSpace;
    }
}
