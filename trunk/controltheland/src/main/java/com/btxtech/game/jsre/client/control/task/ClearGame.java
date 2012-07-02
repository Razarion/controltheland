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
import com.btxtech.game.jsre.client.GameCommon;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.btxtech.game.jsre.client.simulation.Simulation;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.client.utg.tip.TipManager;

/**
 * User: beat
 * Date: 11.12.2010
 * Time: 12:50:50
 */
public class ClearGame extends AbstractStartupTask {

    public ClearGame(StartupTaskEnum taskEnum) {
        super(taskEnum);
    }

    @Override
    protected void privateStart(DeferredStartup deferredStartup) {
        ClientChatHandler.getInstance().stop();
        ClientUserTracker.getInstance().stopEventTracking();
        Connection.getInstance().stopSyncInfoPoll();
        GameCommon.clearGame();
        Simulation.getInstance().cleanup();
        SelectionHandler.getInstance().setSellMode(false);
        SideCockpit.getInstance().getCockpitMode().clearLaunchMode();
        SideCockpit.getInstance().getCockpitMode().clearUnloadMode();
        TipManager.getInstance().deactivate();
        RadarPanel.getInstance().cleanup();
        TerrainView.getInstance().moveAbsolute(new Index(0, 0));
    }
}
