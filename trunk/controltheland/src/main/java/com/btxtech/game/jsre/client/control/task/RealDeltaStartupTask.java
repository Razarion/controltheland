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

package com.btxtech.game.jsre.client.control.task;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientEnergyService;
import com.btxtech.game.jsre.client.cockpit.Cockpit;
import com.btxtech.game.jsre.client.common.info.RealityInfo;
import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.btxtech.game.jsre.client.item.ClientItemTypeAccess;
import com.btxtech.game.jsre.client.territory.ClientTerritoryService;

/**
 * User: beat
 * Date: 12.12.2010
 * Time: 13:26:18
 */
public class RealDeltaStartupTask extends GameEngineStartupTask {

    public RealDeltaStartupTask(StartupTaskEnum taskEnum) {
        super(taskEnum);
    }

    @Override
    protected void privateStart(DeferredStartup deferredStartup) {
        RealityInfo realityInfo = reloadIfNotCorrectInfoClass(RealityInfo.class, deferredStartup);
        if (realityInfo == null) {
            return;
        }
        deltaSetupGameStructure(realityInfo);
        ClientBase.getInstance().setAllBaseAttributes(realityInfo.getAllBase());
        ClientBase.getInstance().setBase(realityInfo.getBase());
        ClientBase.getInstance().setAccountBalance(realityInfo.getAccountBalance());
        Cockpit.getInstance().setGameInfo(realityInfo);
        ClientItemTypeAccess.getInstance().setAllowedItemTypes(realityInfo.getAllowedItemTypes());
        ClientEnergyService.getInstance().init(true);
        ClientBase.getInstance().setConnectedToServer4FakedHouseSpace(true);
        ClientEnergyService.getInstance().onEnergyPacket(realityInfo.getEnergyGenerating(), realityInfo.getEnergyConsuming());
        ClientTerritoryService.getInstance().setTerritories(realityInfo.getTerritories());
        ClientBase.getInstance().setHouseSpace(realityInfo.getHouseSpace());
        Cockpit.getInstance().enableOnlinePanel(true);
    }

}
