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
import com.btxtech.game.jsre.client.ClientPlanetServices;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.btxtech.game.jsre.client.unlock.ClientUnlockServiceImpl;
import com.btxtech.game.jsre.client.utg.ClientUserGuidanceService;

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
        RealGameInfo realGameInfo = (RealGameInfo) Connection.getInstance().getGameInfo();
        deltaSetupGameStructure(realGameInfo);
        setCommon(realGameInfo);
        ClientPlanetServices.getInstance().setPlanetInfo(realGameInfo.getPlanetInfo());
        ClientUnlockServiceImpl.getInstance().setUnlockContainer(realGameInfo.getUnlockContainer());
    }

    public static void setCommon(RealGameInfo realGameInfo) {
        ClientBase.getInstance().setAllBaseAttributes(realGameInfo.getAllBase());
        ClientBase.getInstance().setBase(realGameInfo.getBase());
        ClientBase.getInstance().setAccountBalance(realGameInfo.getAccountBalance());
        ClientBase.getInstance().setConnectedToServer4FakedHouseSpace(true);
        ClientEnergyService.getInstance().onEnergyPacket(realGameInfo.getEnergyGenerating(), realGameInfo.getEnergyConsuming());
        ClientBase.getInstance().setHouseSpace(realGameInfo.getHouseSpace());
    }
}
