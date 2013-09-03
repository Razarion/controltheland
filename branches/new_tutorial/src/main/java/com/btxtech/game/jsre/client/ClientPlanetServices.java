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

package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.bot.ClientBotService;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.collision.ClientCollisionService;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.action.CommonActionService;
import com.btxtech.game.jsre.common.gameengine.services.base.AbstractBaseService;
import com.btxtech.game.jsre.common.gameengine.services.bot.CommonBotService;
import com.btxtech.game.jsre.common.gameengine.services.collision.CommonCollisionService;
import com.btxtech.game.jsre.common.gameengine.services.connection.CommonConnectionService;
import com.btxtech.game.jsre.common.gameengine.services.energy.EnergyService;
import com.btxtech.game.jsre.common.gameengine.services.inventory.CommonInventoryService;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;

/**
 * User: beat
 * Date: 30.11.2009
 * Time: 22:50:22
 */
public class ClientPlanetServices implements PlanetServices {
    private static ClientPlanetServices INSTANCE = new ClientPlanetServices();
    private PlanetInfo planetInfo;

    private CommonInventoryService dummyInventoryService = new CommonInventoryService() {
        @Override
        public void onSyncBoxItemPicked(SyncBoxItem box, SyncBaseItem picker) {
            // Do nothing here
        }
    };

    public static ClientPlanetServices getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private ClientPlanetServices() {
    }

    @Override
    public PlanetInfo getPlanetInfo() {
        return planetInfo;
    }

    public void setPlanetInfo(PlanetInfo planetInfo) {
        this.planetInfo = planetInfo;
        RadarPanel.getInstance().setLevelRadarMode(planetInfo.getRadarMode());
    }

    @Override
    public ItemService getItemService() {
        return ItemContainer.getInstance();
    }

    @Override
    public AbstractTerrainService getTerrainService() {
        return TerrainView.getInstance().getTerrainHandler();
    }

    @Override
    public AbstractBaseService getBaseService() {
        return ClientBase.getInstance();
    }

    @Override
    public EnergyService getEnergyService() {
        return ClientEnergyService.getInstance();
    }

    @Override
    public CommonCollisionService getCollisionService() {
        return ClientCollisionService.getInstance();
    }

    @Override
    public CommonActionService getActionService() {
        return ActionHandler.getInstance();
    }

    @Override
    public CommonBotService getBotService() {
        return ClientBotService.getInstance();
    }

    @Override
    public CommonInventoryService getInventoryService() {
        return dummyInventoryService;
    }

    @Override
    public CommonConnectionService getConnectionService() {
        return Connection.getInstance();
    }
}
