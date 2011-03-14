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

package com.btxtech.game.services.territory;

import com.btxtech.game.jsre.common.Territory;
import com.btxtech.game.jsre.common.gameengine.services.territory.AbstractTerritoryService;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.CrudServiceHelper;

import java.util.List;

/**
 * User: beat
 * Date: 23.05.2010
 * Time: 14:56:42
 */
public interface TerritoryService extends AbstractTerritoryService {
    CrudRootServiceHelper<DbTerritory> getDbTerritoryCrudServiceHelper();

    void saveDbTerritory(List<DbTerritory> dbTerritories);

    List<DbTerritory> getDbTerritories();

    void saveTerritory(Territory territory);

    void activate();
}
