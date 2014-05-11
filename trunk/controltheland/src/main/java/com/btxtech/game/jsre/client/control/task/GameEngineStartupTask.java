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

import com.btxtech.game.jsre.client.ClientClipHandler;
import com.btxtech.game.jsre.client.ClientEnergyService;
import com.btxtech.game.jsre.client.ClientUserService;
import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.btxtech.game.jsre.client.item.ItemTypeContainer;
import com.btxtech.game.jsre.client.terrain.TerrainView;

/**
 * User: beat
 * Date: 04.12.2010
 * Time: 13:13:08
 */
public abstract class GameEngineStartupTask extends AbstractStartupTask {

    public GameEngineStartupTask(StartupTaskEnum taskEnum) {
        super(taskEnum);
    }

    protected void setupGameStructure(GameInfo gameInfo) {
        ClientUserService.getInstance().setSimpleUser(gameInfo.getSimpleUser());
        ClientEnergyService.getInstance().init();
        TerrainView.getInstance().setupTerrain(gameInfo.getTerrainSettings(),
                gameInfo.getTerrainImagePositions(),
                gameInfo.getSurfaceRects(),
                gameInfo.getSurfaceImages(),
                gameInfo.getTerrainImages(),
                gameInfo.getTerrainImageBackground());
        gameInfo.freePositionAndRects();
        ItemTypeContainer.getInstance().setItemTypes(gameInfo.getItemTypes());
        ClientClipHandler.getInstance().init(gameInfo);
    }

    protected void deltaSetupGameStructure(GameInfo gameInfo) {
        ClientUserService.getInstance().setSimpleUser(gameInfo.getSimpleUser());
        ClientEnergyService.getInstance().init();
        TerrainView.getInstance().deltaSetupTerrain(gameInfo.getTerrainSettings(),
                gameInfo.getTerrainImagePositions(),
                gameInfo.getSurfaceRects(),
                gameInfo.getSurfaceImages(),
                gameInfo.getTerrainImages(),
                gameInfo.getTerrainImageBackground());
        gameInfo.freePositionAndRects();
        ItemTypeContainer.getInstance().addDeltaItemTypes(gameInfo.getItemTypes());
        ClientClipHandler.getInstance().init(gameInfo);
    }
}
