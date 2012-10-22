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

import com.btxtech.game.jsre.client.ClientChatHandler;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.SoundHandler;
import com.btxtech.game.jsre.client.cockpit.ChatCockpit;
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.cockpit.quest.QuestVisualtsationModel;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.btxtech.game.jsre.client.dialogs.RegisterDialog;
import com.btxtech.game.jsre.client.renderer.Renderer;
import com.btxtech.game.jsre.client.simulation.Simulation;

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
        RegisterDialog.showDialogRepeating();
        Simulation.getInstance().start();
        SideCockpit.getInstance().updateItemLimit();
        ClientChatHandler.getInstance().runSimulatedGame(Connection.getInstance(), ChatCockpit.getInstance(), ClientChatHandler.START_DELAY, ClientChatHandler.POLL_DELAY);
        Renderer.getInstance().start();
        SoundHandler.getInstance().start(Connection.getInstance().getGameInfo().getCommonSoundInfo());
    }
}
