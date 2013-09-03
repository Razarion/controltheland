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
public class EnergyPacket extends Packet {
    private int generating;
    private int consuming;

    public int getGenerating() {
        return generating;
    }

    public void setGenerating(int generating) {
        this.generating = generating;
    }

    public int getConsuming() {
        return consuming;
    }

    public void setConsuming(int consuming) {
        this.consuming = consuming;
    }

    @Override
    public String toString() {
        return "EnergyPacket: consuming: " + consuming + " generating: " + generating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnergyPacket that = (EnergyPacket) o;

        return consuming == that.consuming && generating == that.generating;

    }

    @Override
    public int hashCode() {
        int result = generating;
        result = 31 * result + consuming;
        return result;
    }
}
