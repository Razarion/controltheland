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

/**
 * User: beat
 * Date: 26.05.2010
 * Time: 17:25:05
 */
package com.btxtech.game.jsre.client.territory;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.itemType.LauncherType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.territory.impl.AbstractTerritoryServiceImpl;

public class ClientTerritoryService extends AbstractTerritoryServiceImpl {
    private static final ClientTerritoryService INSTANCE = new ClientTerritoryService();

    public static ClientTerritoryService getInstance() {
        return INSTANCE;
    }

    private ClientTerritoryService() {
    }

    @Override
    protected AbstractTerrainService getTerrainService() {
        return TerrainView.getInstance().getTerrainHandler();
    }
}
