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

import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.SimpleBase;
import java.util.Collection;

/**
 * User: beat
 * Date: 12.03.2010
 * Time: 23:47:24
 */
public class OnlineBaseUpdate extends Packet {
    private Collection<SimpleBase> onlineBases;

    public Collection<SimpleBase> getOnlineBases() {
        return onlineBases;
    }

    public void setOnlineBases(Collection<SimpleBase> onlineBases) {
        this.onlineBases = onlineBases;
    }
}
