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

package com.btxtech.game.jsre.common;

import java.io.Serializable;

/**
 * User: beat
 * Date: Aug 5, 2009
 * Time: 2:03:06 PM
 */
public class SimpleBase implements Serializable {
    public static final int ITEM_TYPE_EDITOR_MY = -1;
    public static final int ITEM_TYPE_EDITOR_ENEMY = -2;
    public static final int FAKE_BASE_START_POINT = -3;

    private int baseId;
    private int planetId;

    /**
     * Used by GWT
     */
    SimpleBase() {
    }

    public SimpleBase(int baseId, int planetId) {
        this.baseId = baseId;
        this.planetId = planetId;
    }

    public int getBaseId() {
        return baseId;
    }

    public int getPlanetId() {
        return planetId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleBase that = (SimpleBase) o;

        return baseId == that.baseId && planetId == that.planetId;
    }

    @Override
    public int hashCode() {
        int result = baseId;
        result = 31 * result + planetId;
        return result;
    }

    @Override
    public String toString() {
        return "Base Id: " + baseId + " Planet Id: " + planetId;
    }

    public static SimpleBase createFakeUser(int planetId) {
        return new SimpleBase(SimpleBase.FAKE_BASE_START_POINT, planetId);
    }
}
