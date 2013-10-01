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

import com.btxtech.game.jsre.client.control.ClientRunner;
import com.btxtech.game.jsre.client.control.StartupScreen;
import com.btxtech.game.jsre.client.item.ItemTypeContainer;
import com.btxtech.game.jsre.client.simulation.SimulationConditionServiceImpl;
import com.btxtech.game.jsre.client.unlock.ClientUnlockServiceImpl;
import com.btxtech.game.jsre.client.utg.ClientUserGuidanceService;
import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemTypeService;
import com.btxtech.game.jsre.common.gameengine.services.unlock.UnlockService;
import com.btxtech.game.jsre.common.gameengine.services.utg.CommonUserGuidanceService;
import com.btxtech.game.jsre.common.utg.ConditionService;

/**
 * User: beat
 * Date: 30.11.2009
 * Time: 22:50:22
 */
public class ClientGlobalServices implements GlobalServices {
    private static ClientGlobalServices INSTANCE = new ClientGlobalServices();
    private ClientRunner clientRunner = new ClientRunner();

    public static ClientGlobalServices getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private ClientGlobalServices() {
    }

    public void connectStartupListeners() {
        clientRunner.addStartupProgressListener(StartupScreen.getInstance());
        clientRunner.addStartupProgressListener(Connection.getInstance());
    }

    @Override
    public ItemTypeService getItemTypeService() {
        return ItemTypeContainer.getInstance();
    }

    @Override
    public CommonUserGuidanceService getCommonUserGuidanceService() {
        return ClientUserGuidanceService.getInstance();
    }

    @Override
    public ConditionService getConditionService() {
        return SimulationConditionServiceImpl.getInstance();
    }

    public ClientRunner getClientRunner() {
        return clientRunner;
    }

    @Override
    public UnlockService getUnlockService() {
        return ClientUnlockServiceImpl.getInstance();
    }
}
