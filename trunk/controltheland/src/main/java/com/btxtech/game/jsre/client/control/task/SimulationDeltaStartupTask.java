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
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.client.control.StartupTaskEnum;

/**
 * User: beat
 * Date: 04.12.2010
 * Time: 13:26:18
 */
public class SimulationDeltaStartupTask extends GameEngineStartupTask {

    public SimulationDeltaStartupTask(StartupTaskEnum taskEnum) {
        super(taskEnum);
    }

    @Override
    protected void privateStart(DeferredStartup deferredStartup) {
        ClientBase.getInstance().setConnectedToServer4FakedHouseSpace(false);
        SimulationInfo simulationInfo = (SimulationInfo) Connection.getInstance().getGameInfo();
        deltaSetupGameStructure(simulationInfo);
    }
}
