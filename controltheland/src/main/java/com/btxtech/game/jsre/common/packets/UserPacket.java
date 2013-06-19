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


import com.btxtech.game.jsre.client.common.info.SimpleUser;

/**
 * User: beat
 * Date: 14.05.2013
 * Time: 14:38:58
 */
public class UserPacket extends Packet {
    private SimpleUser simpleUser;

    public SimpleUser getSimpleUser() {
        return simpleUser;
    }

    public void setSimpleUser(SimpleUser simpleUser) {
        this.simpleUser = simpleUser;
    }

    @Override
    public String toString() {
        return "UserPacket{SimpleUser=" + simpleUser + '}';
    }
}
