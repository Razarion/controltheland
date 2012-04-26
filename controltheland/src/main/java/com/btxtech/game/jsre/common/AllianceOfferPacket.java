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

/**
 * User: beat Date: 12.05.2010 Time: 14:38:58
 */
public class AllianceOfferPacket extends Packet {
    private String actorUserName;

    public String getActorUserName() {
        return actorUserName;
    }

    public void setActorUserName(String actorUserName) {
        this.actorUserName = actorUserName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AllianceOfferPacket that = (AllianceOfferPacket) o;

        return actorUserName.equals(that.actorUserName);

    }

    @Override
    public int hashCode() {
        return actorUserName.hashCode();
    }

    @Override
    public String toString() {
        return "AllianceOfferPacket{actorUserName='" + actorUserName + "\'}";
    }
}
