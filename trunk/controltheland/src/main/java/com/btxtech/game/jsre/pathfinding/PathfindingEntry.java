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

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.TopMapPanel;
import com.btxtech.game.jsre.client.cockpit.radar.MiniTerrain;
import com.btxtech.game.jsre.client.terrain.TerrainListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.mapeditor.TerrainEditorAsync;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
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

        int terrainId = Integer.parseInt(Window.Location.getParameter(TerrainEditorAsync.TERRAIN_SETTING_ID));

        TerrainView.uglySuppressRadar = true;

        AbsolutePanel absolutePanel = new AbsolutePanel();
        absolutePanel.setSize("100%", "100%");
        RootPanel.get().add(absolutePanel);

        final MiniTerrain miniTerrain = new MiniTerrain(RootPanel.get().getOffsetWidth(), RootPanel.get().getOffsetHeight());
        miniTerrain.getCanvas().getElement().getStyle().setZIndex(1);
        absolutePanel.add(miniTerrain.getCanvas(), 0, 0);

        PathfindingAsync pathfinding = GWT.create(Pathfinding.class);
        final PathMiniMap pathMiniMap = new PathMiniMap(RootPanel.get().getOffsetWidth(), RootPanel.get().getOffsetHeight());
        pathMiniMap.getCanvas().getElement().getStyle().setZIndex(3);
        absolutePanel.add(pathMiniMap.getCanvas(), 0, 0);

        PathfindingCockpit pathfindingCockpit = new PathfindingCockpit(pathMiniMap, miniTerrain);
        pathfindingCockpit.addToParent(absolutePanel, TopMapPanel.Direction.RIGHT_TOP, 20);
        pathfindingCockpit.getElement().getStyle().setZIndex(4);

        pathfinding.getTerrainInfo(terrainId, new AsyncCallback<com.btxtech.game.jsre.common.TerrainInfo>() {
            @Override
            public void onFailure(Throwable throwable) {
                ClientExceptionHandler.handleException(throwable);
            }

            @Override
            public void onSuccess(com.btxtech.game.jsre.common.TerrainInfo terrainInfo) {
                TerrainView.getInstance().setupTerrain(terrainInfo.getTerrainSettings(),
                        terrainInfo.getTerrainImagePositions(),
                        terrainInfo.getSurfaceRects(),
                        terrainInfo.getSurfaceImages(),
                        terrainInfo.getTerrainImages(),
                        terrainInfo.getTerrainImageBackground());
                // If the images get loaded after miniTerrain.onTerrainChanged() has been called
                TerrainView.getInstance().getTerrainHandler().addTerrainListener(new TerrainListener() {
                    @Override
                    public void onTerrainChanged() {
                        miniTerrain.onTerrainChanged();
                    }
                });
                miniTerrain.onTerrainSettings(terrainInfo.getTerrainSettings());
                miniTerrain.onTerrainChanged();
                pathMiniMap.onTerrainSettings(terrainInfo.getTerrainSettings());
            }
        });
    }

}