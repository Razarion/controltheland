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

package com.btxtech.game.jsre.mapeditor;

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.control.task.SimpleDeferredStartup;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 6:51:16 PM
 */
public class MainTerrainEditor implements EntryPoint {

    @Override
    public void onModuleLoad() {
        // Setup common
        GwtCommon.setUncaughtExceptionHandler();
        GwtCommon.disableBrowserContextMenuJSNI();

        int terrainId = Integer.parseInt(Window.Location.getParameter(TerrainEditorAsync.TERRAIN_SETTING_ID));

        TerrainEditorAsync terrainEditor = GWT.create(TerrainEditor.class);

        // Setup map
        RootPanel.get().add(MapWindow.getAbsolutePanel());
        TerrainView.getInstance().addToParent(MapWindow.getAbsolutePanel());
        TerrainView.getInstance().getCanvas().getElement().getStyle().setZIndex(Constants.Z_INDEX_TERRAIN);
        TerrainView.getInstance().addTerrainScrollListener(MapWindow.getInstance());

        // Setup editor
        final Cockpit cockpit = new Cockpit(terrainEditor, terrainId);
        MapWindow.getAbsolutePanel().add(cockpit, 30, 30);

        // Radar panel
        //MapWindow.getAbsolutePanel().add(RadarPanel.getInstance(), 1, 30);
        //RadarPanel.getInstance().getElement().getStyle().setProperty("left", "");
        //RadarPanel.getInstance().getElement().getStyle().setProperty("right", "30px");
        //RadarPanel.getInstance().setRadarState1(true);
        //RadarPanel.getInstance().updateEnergy(1, 0);

        terrainEditor.getTerrainInfo(terrainId, new AsyncCallback<TerrainInfo>() {
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
                TerrainView.getInstance().getTerrainHandler().loadImagesAndDrawMap(new SimpleDeferredStartup());
                cockpit.fillTerrainImages(terrainInfo.getTerrainImages());
                cockpit.fillSurfaces(terrainInfo.getSurfaceImages());
            }
        });

    }
}
