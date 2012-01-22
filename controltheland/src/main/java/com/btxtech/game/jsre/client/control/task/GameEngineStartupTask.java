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

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.ClientLevelHandler;
import com.google.gwt.user.client.Window;

/**
 * User: beat
 * Date: 04.12.2010
 * Time: 13:13:08
 */
public abstract class GameEngineStartupTask extends AbstractStartupTask {

    public GameEngineStartupTask(StartupTaskEnum taskEnum) {
        super(taskEnum);
    }

    protected <T extends GameInfo> T reloadIfNotCorrectInfoClass(Class<T> infoClass, DeferredStartup deferredStartup) {
        GameInfo gameInfo = Connection.getInstance().getGameInfo();
        // GWT only supports a few java.lang.Class members
        if (gameInfo instanceof RealGameInfo && infoClass.equals(RealGameInfo.class)) {
            // noinspection unchecked
            return (T) gameInfo;
        } else if (gameInfo instanceof SimulationInfo && infoClass.equals(SimulationInfo.class)) {
            // noinspection unchecked
            return (T) gameInfo;
        } else {
            // The level on the game may has changed but the browser caches the old Startup
            // Set deferred to prevent starting any new tasks
            deferredStartup.setDeferred();
            // Reload the whole page
            Window.Location.reload();
            return null;
        }
    }

    protected void setupGameStructure(GameInfo gameInfo) {
        Connection.getInstance().setRegistered(gameInfo.isRegistered());
        TerrainView.getInstance().setupTerrain(gameInfo.getTerrainSettings(),
                gameInfo.getTerrainImagePositions(),
                gameInfo.getSurfaceRects(),
                gameInfo.getSurfaceImages(),
                gameInfo.getTerrainImages());
        ItemContainer.getInstance().setItemTypes(gameInfo.getItemTypes());
    }

    protected void deltaSetupGameStructure(GameInfo gameInfo) {
        Connection.getInstance().setRegistered(gameInfo.isRegistered());
        TerrainView.getInstance().deltaSetupTerrain(gameInfo.getTerrainSettings(),
                gameInfo.getTerrainImagePositions(),
                gameInfo.getSurfaceRects(),
                gameInfo.getSurfaceImages(),
                gameInfo.getTerrainImages());
        ItemContainer.getInstance().addDeltaItemTypes(gameInfo.getItemTypes());
    }
}
