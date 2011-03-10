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
import com.btxtech.game.jsre.client.control.ClientRunner;
import com.btxtech.game.jsre.client.control.StartupScreen;
import com.btxtech.game.jsre.client.item.ClientItemTypeAccess;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.territory.ClientTerritoryService;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.action.CommonActionService;
import com.btxtech.game.jsre.common.gameengine.services.base.AbstractBaseService;
import com.btxtech.game.jsre.common.gameengine.services.collision.CommonCollisionService;
import com.btxtech.game.jsre.common.gameengine.services.connection.ConnectionService;
import com.btxtech.game.jsre.common.gameengine.services.energy.EnergyService;
import com.btxtech.game.jsre.common.gameengine.services.itemTypeAccess.ItemTypeAccess;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.territory.AbstractTerritoryService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

/**
 * User: beat
 * Date: 30.11.2009
 * Time: 22:50:22
 */
public class ClientServices implements Services {
    private static ClientServices INSTANCE = new ClientServices();
    private ClientRunner clientRunner = new ClientRunner();
    private ConnectionService dummyConnectionService = new ConnectionService() {
        @Override
        public void sendSyncInfo(SyncItem syncItem) {
            // Ignore
        }
    };

    public static ClientServices getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private ClientServices() {
    }

    public void connectStartupListeners() {
        clientRunner.addStartupProgressListener(StartupScreen.getInstance());
        clientRunner.addStartupProgressListener(Connection.getInstance());
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
    public ConnectionService getConnectionService() {
        return dummyConnectionService;
    }

    @Override
    public ItemTypeAccess getItemTypeAccess() {
        return ClientItemTypeAccess.getInstance();
    }

    @Override
    public EnergyService getEnergyService() {
        return EnergyHandler.getInstance();
    }

    @Override
    public CommonCollisionService getCollisionService() {
        return ItemContainer.getInstance();
    }

    @Override
    public CommonActionService getActionService() {
        return ActionHandler.getInstance();
    }

    @Override
    public AbstractTerritoryService getTerritoryService() {
        return ClientTerritoryService.getInstance();
    }

    public ClientRunner getClientRunner() {
        return clientRunner;
    }
}
