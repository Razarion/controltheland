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

package com.btxtech.game.jsre.common.gameengine.services.itemTypeAccess;

import com.btxtech.game.jsre.common.Packet;
import java.util.Collection;

/**
 * User: beat
 * Date: 18.12.2009
 * Time: 21:45:28
 */
public class ItemTypeAccessSyncInfo extends Packet {
    private Collection<Integer> allowedItemTypes;

    public Collection<Integer> getAllowedItemTypes() {
        return allowedItemTypes;
    }

    public void setAllowedItemTypes(Collection<Integer> allowedItemTypes) {
        this.allowedItemTypes = allowedItemTypes;
    }
}
