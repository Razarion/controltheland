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

package com.btxtech.game.services.common;

import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.connection.ServerConnectionService;
import com.btxtech.game.services.planet.ActionService;
import com.btxtech.game.services.planet.BaseService;
import com.btxtech.game.services.planet.CollisionService;
import com.btxtech.game.services.planet.InventoryService;
import com.btxtech.game.services.planet.ResourceService;
import com.btxtech.game.services.planet.ServerEnergyService;
import com.btxtech.game.services.planet.ServerItemService;
import com.btxtech.game.services.planet.ServerTerrainService;

/**
 * User: beat
 * Date: 01.12.2009
 * Time: 23:05:54
 */
public interface ServerPlanetServices extends PlanetServices {
    @Override
    PlanetInfo getPlanetInfo();

    @Override
    ServerItemService getItemService();

    @Override
    BaseService getBaseService();

    @Override
    ServerEnergyService getEnergyService();

    @Override
    CollisionService getCollisionService();

    @Override
    ActionService getActionService();

    @Override
    ServerTerrainService getTerrainService();

    @Override
    BotService getBotService();

    @Override
    InventoryService getInventoryService();

    ResourceService getResourceService();

    @Override
    ServerConnectionService getConnectionService();

    Region getStartRegion();
}
