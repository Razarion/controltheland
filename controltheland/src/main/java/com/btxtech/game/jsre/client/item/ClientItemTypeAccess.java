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

package com.btxtech.game.jsre.client.item;

import com.btxtech.game.jsre.common.gameengine.services.itemTypeAccess.ItemTypeAccess;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * User: beat
 * Date: 18.12.2009
 * Time: 21:16:32
 */
public class ClientItemTypeAccess implements ItemTypeAccess {
    private static final ClientItemTypeAccess INSTANCE = new ClientItemTypeAccess();
    private Set<Integer> allowedItemTypes = Collections.emptySet();

    /**
     * Singleton
     */
    private ClientItemTypeAccess() {
    }

    public static ClientItemTypeAccess getInstance() {
        return INSTANCE;
    }


    @Override
    public boolean isAllowed(int itemTypeId) {
        return allowedItemTypes.contains(itemTypeId);
    }

    public Collection<Integer> getAllowedItemTypes() {
        return allowedItemTypes;
    }

    public void setAllowedItemTypes(Collection<Integer> allowedItemTypes) {
        if (allowedItemTypes != null) {
            this.allowedItemTypes = new HashSet<Integer>(allowedItemTypes);
        } else {
            this.allowedItemTypes = Collections.emptySet();
        }
    }

}
