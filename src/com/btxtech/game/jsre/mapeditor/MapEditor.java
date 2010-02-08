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
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 6:51:16 PM
 */
public class MapEditor implements EntryPoint {

    @Override
    public void onModuleLoad() {
        // Setup common
        GwtCommon.setUncaughtExceptionHandler();
        GwtCommon.disableBrowserContextMenuJSNI();
        GameEditorAsync gameEditor = GWT.create(GameEditor.class);

        // Setup map
        RootPanel.get().add(MapWindow.getAbsolutePanel());
        TerrainView.getInstance().addToParent(MapWindow.getAbsolutePanel());
        TerrainView.getInstance().getCanvas().getElement().getStyle().setZIndex(Constants.Z_INDEX_TERRAIN);
        TerrainView.getInstance().addTerrainScrollListener(MapWindow.getInstance());

        // Setup editor
        final Cockpit cockpit = new Cockpit(gameEditor);
        MapWindow.getAbsolutePanel().add(cockpit, 30, 30);
        MapModifier mapModifier = new MapModifier(cockpit);
        cockpit.setMapModifier(mapModifier);

        // Radar panel
        MapWindow.getAbsolutePanel().add(RadarPanel.getInstance(), 1, 30);
        RadarPanel.getInstance().getElement().getStyle().setProperty("left", "");
        RadarPanel.getInstance().getElement().getStyle().setProperty("right", "30px");
        RadarPanel.getInstance().setRadarState(true);
        RadarPanel.getInstance().updateEnergy(1, 0);

        gameEditor.getEditorInfo(new AsyncCallback<EditorInfo>() {
            @Override
            public void onFailure(Throwable throwable) {
                GwtCommon.handleException(throwable);
            }

            @Override
            public void onSuccess(EditorInfo editorInfo) {
                TerrainView.getInstance().setupTerrain(editorInfo.getTerrainSettings(),
                        editorInfo.getTerrainImagePositions(),
                        editorInfo.getTerrainImages());
                cockpit.setupTerrainImages(editorInfo.getTerrainImages());
            }
        });

        MapWindow.getInstance().setTerrainMouseMoveListener(mapModifier);
            }
            }
