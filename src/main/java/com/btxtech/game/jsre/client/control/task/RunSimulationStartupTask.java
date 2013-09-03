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
import com.btxtech.game.jsre.client.ClientMessageIdPacketHandler;
import com.btxtech.game.jsre.client.ClientUserService;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.SoundHandler;
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.cockpit.chat.ChatCockpit;
import com.btxtech.game.jsre.client.cockpit.menu.MenuBarCockpit;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.btxtech.game.jsre.client.renderer.Renderer;
import com.btxtech.game.jsre.client.simulation.Simulation;
import com.btxtech.game.jsre.client.terrain.TerrainView;

/**
 * User: beat Date: 05.12.2010 Time: 17:53:47
 */
public class RunSimulationStartupTask extends AbstractStartupTask {

    public RunSimulationStartupTask(StartupTaskEnum taskEnum) {
        super(taskEnum);
    }

    @Override
    protected void privateStart(DeferredStartup deferredStartup) {
        SideCockpit.getInstance().initMission(((SimulationInfo) Connection.getInstance().getGameInfo()));
        ClientBase.getInstance().setMySimpleGuild(null);
        MenuBarCockpit.getInstance().initSimulated(Connection.getInstance().getGameInfo());
        ClientUserService.getInstance().init();
        Simulation.getInstance().start();
        SideCockpit.getInstance().updateItemLimit();
        ClientMessageIdPacketHandler.getInstance().runSimulatedGame(Connection.getInstance(), ChatCockpit.getInstance(), ClientMessageIdPacketHandler.START_DELAY, ClientMessageIdPacketHandler.POLL_DELAY);
        Renderer.getInstance().start();
        SoundHandler.getInstance().start(Connection.getInstance().getGameInfo().getCommonSoundInfo());
        TerrainView.getInstance().setFocus();
    }
}
