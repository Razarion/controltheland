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

package com.btxtech.game.jsre.common.gameengine.services.territory;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.Territory;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import java.util.Collection;

/**
 * User: beat
 * Date: 26.05.2010
 * Time: 13:36:56
 */
public interface AbstractTerritoryService {
    Collection<Territory> getTerritories();

    boolean isAllowed(Index position, SyncBaseItem syncBaseItem);

    boolean isAllowed(Index position, int itemTypeId);
}
