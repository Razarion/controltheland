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
 * User: beat Date: 29.06.2012 Time: 14:38:58
 */
public class XpPacket extends Packet {
    private int xp;
    private int xp2LevelUp;

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getXp2LevelUp() {
        return xp2LevelUp;
    }

    public void setXp2LevelUp(int xp2LevelUp) {
        this.xp2LevelUp = xp2LevelUp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XpPacket xpPacket = (XpPacket) o;

        return xp == xpPacket.xp && xp2LevelUp == xpPacket.xp2LevelUp;
    }

    @Override
    public int hashCode() {
        int result = xp;
        result = 31 * result + xp2LevelUp;
        return result;
    }

    @Override
    public String toString() {
        return "XpPacket{" +
                "xp=" + xp +
                ", xp2LevelUp=" + xp2LevelUp +
                '}';
    }
}
