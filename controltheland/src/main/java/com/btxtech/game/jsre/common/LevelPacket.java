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

import com.btxtech.game.jsre.client.common.LevelScope;

/**
 * User: beat
 * Date: 12.05.2010
 * Time: 14:38:58
 */
public class LevelPacket extends Packet {
    private LevelScope levelScope;

    public LevelScope getLevel() {
        return levelScope;
    }

    public void setLevel(LevelScope levelScope) {
        this.levelScope = levelScope;
    }
}
