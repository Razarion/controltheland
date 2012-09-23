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

package com.btxtech.game.jsre.common.gameengine.services;

import com.btxtech.game.jsre.common.gameengine.services.action.CommonActionService;
import com.btxtech.game.jsre.common.gameengine.services.base.AbstractBaseService;
import com.btxtech.game.jsre.common.gameengine.services.bot.CommonBotService;
import com.btxtech.game.jsre.common.gameengine.services.collision.CommonCollisionService;
import com.btxtech.game.jsre.common.gameengine.services.connection.CommonConnectionService;
import com.btxtech.game.jsre.common.gameengine.services.energy.EnergyService;
import com.btxtech.game.jsre.common.gameengine.services.inventory.CommonInventoryService;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;

/**
 * User: beat
 * Date: 18.11.2009
 * Time: 22:00:04
 */
public interface PlanetServices {
    PlanetInfo getPlanetInfo();

    ItemService getItemService();

    AbstractTerrainService getTerrainService();

    AbstractBaseService getBaseService();

    EnergyService getEnergyService();

    CommonCollisionService getCollisionService();

    CommonActionService getActionService();

    CommonBotService getBotService();

    CommonInventoryService getInventoryService();

    CommonConnectionService getConnectionService();
}
