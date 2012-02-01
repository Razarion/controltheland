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
import com.btxtech.game.jsre.client.collision.ClientCollisionService;
import com.btxtech.game.jsre.client.control.ClientRunner;
import com.btxtech.game.jsre.client.control.StartupScreen;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.simulation.SimulationConditionServiceImpl;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.territory.ClientTerritoryService;
import com.btxtech.game.jsre.client.utg.ClientLevelHandler;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.action.CommonActionService;
import com.btxtech.game.jsre.common.gameengine.services.base.AbstractBaseService;
import com.btxtech.game.jsre.common.gameengine.services.bot.CommonBotService;
import com.btxtech.game.jsre.common.gameengine.services.collision.CommonCollisionService;
import com.btxtech.game.jsre.common.gameengine.services.connection.ConnectionService;
import com.btxtech.game.jsre.common.gameengine.services.energy.EnergyService;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.territory.AbstractTerritoryService;
import com.btxtech.game.jsre.common.gameengine.services.utg.CommonUserGuidanceService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.utg.ConditionService;

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
            ClientUserTracker.getInstance().trackSyncInfo(syncItem);
        }

        @Override
        public GameEngineMode getGameEngineMode() {
            return Connection.getInstance().getGameEngineMode();
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
    public AbstractTerritoryService getTerritoryService() {
        return ClientTerritoryService.getInstance();
    }

    @Override
    public CommonBotService getBotService() {
        return ClientBotService.getInstance();
    }

    @Override
    public CommonUserGuidanceService getCommonUserGuidanceService() {
        return ClientLevelHandler.getInstance();
    }

    @Override
    public ConditionService getConditionService() {
        return SimulationConditionServiceImpl.getInstance();
    }

    public ClientRunner getClientRunner() {
        return clientRunner;
    }
}
