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

package com.btxtech.game.services.planet;

import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.services.planet.db.DbPlanet;

/**
 * User: beat
 * Date: 08.05.2010
 * Time: 21:57:02
 */
public interface ResourceService {
    void resourceItemDeleted(SyncResourceItem syncResourceItem);

    void activate(DbPlanet dbPlanet);

    void deactivate();

    void reactivate(DbPlanet dbPlanet);
}
