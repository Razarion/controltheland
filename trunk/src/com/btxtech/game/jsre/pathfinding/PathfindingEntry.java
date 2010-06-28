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

package com.btxtech.game.jsre.pathfinding;

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.cockpit.radar.MiniTerrain;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.mapeditor.TerrainInfo;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 6:51:16 PM
 */
public class PathfindingEntry implements EntryPoint {
    @Override
    public void onModuleLoad() {
        // Setup common
        GwtCommon.setUncaughtExceptionHandler();
        GwtCommon.disableBrowserContextMenuJSNI();

        AbsolutePanel absolutePanel = new AbsolutePanel();
        absolutePanel.setSize("100%", "100%");
        RootPanel.get().add(absolutePanel);

        final MiniTerrain miniTerrain = new MiniTerrain(RootPanel.get().getOffsetWidth(), RootPanel.get().getOffsetHeight());
        miniTerrain.getElement().getStyle().setZIndex(1);
        absolutePanel.add(miniTerrain, 0, 0);

        PathfindingAsync pathfinding = GWT.create(Pathfinding.class);
        final PathMiniMap pathMiniMap = new PathMiniMap(RootPanel.get().getOffsetWidth(), RootPanel.get().getOffsetHeight(), pathfinding);
        pathMiniMap.getElement().getStyle().setZIndex(2);
        absolutePanel.add(pathMiniMap, 0, 0);

        pathfinding.getTerrainInfo(new AsyncCallback<TerrainInfo>() {
            @Override
            public void onFailure(Throwable throwable) {
                GwtCommon.handleException(throwable);
            }

            @Override
            public void onSuccess(TerrainInfo terrainInfo) {
                TerrainView.getInstance().setupTerrain(terrainInfo.getTerrainSettings(),
                        terrainInfo.getTerrainImagePositions(),
                        terrainInfo.getSurfaceRects(),
                        terrainInfo.getSurfaceImages(),
                        terrainInfo.getTerrainImages());
                miniTerrain.onTerrainSettings(terrainInfo.getTerrainSettings());
                pathMiniMap.onTerrainSettings(terrainInfo.getTerrainSettings());
            }
        });
    }

}