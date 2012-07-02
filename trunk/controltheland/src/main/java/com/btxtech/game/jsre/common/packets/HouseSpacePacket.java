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

package com.btxtech.game.jsre.common.packets;

/**
 * User: beat
 * Date: 24.12.2009
 * Time: 13:14:24
 */
public class HouseSpacePacket extends Packet {
    private int houseSpace;

    public int getHouseSpace() {
        return houseSpace;
    }

    public void setHouseSpace(int houseSpace) {
        this.houseSpace = houseSpace;
    }

    @Override
    public String toString() {
        return "HouseSpacePacket: houseSpace: " + houseSpace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HouseSpacePacket that = (HouseSpacePacket) o;

        return houseSpace == that.houseSpace;
    }

    @Override
    public int hashCode() {
        return houseSpace;
    }
}
