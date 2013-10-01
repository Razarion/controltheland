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

package com.btxtech.game.jsre.regioneditor;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.cockpit.radar.MiniTerrain;
import com.btxtech.game.jsre.client.cockpit.radar.ScaleStep;
import com.btxtech.game.jsre.client.terrain.TerrainListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.TerrainInfo;
import com.btxtech.game.jsre.mapeditor.TerrainEditor;
import com.btxtech.game.jsre.mapeditor.TerrainEditorAsync;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * User: beat
 * Date: 22.05.2010
 * Time: 15:13:09
 */
public class RegionEditor implements EntryPoint {
    private TerrainEditorAsync terrainEditor = GWT.create(TerrainEditor.class);

    @Override
    public void onModuleLoad() {
        // Setup common
        GwtCommon.setUncaughtExceptionHandler();
        GwtCommon.disableBrowserContextMenuJSNI();

        TerrainView.uglySuppressRadar = true;

        String parentType = Window.Location.getParameter(TerrainEditorAsync.ROOT_TYPE);
        if (parentType.equals(TerrainEditorAsync.ROOT_TYPE_MISSION)) {
            int tutorialId = Integer.parseInt(Window.Location.getParameter(TerrainEditorAsync.ROOT_ID));
            terrainEditor.getTutorialTerrainInfo(tutorialId, new AsyncCallback<TerrainInfo>() {
                @Override
                public void onFailure(Throwable throwable) {
                    ClientExceptionHandler.handleException(throwable, true);
                }

                @Override
                public void onSuccess(TerrainInfo terrainInfo) {
                    loadRegionAndStart(terrainInfo);
                }
            });

        } else if (parentType.equals(TerrainEditorAsync.ROOT_TYPE_PLANET)) {
            int planetId = Integer.parseInt(Window.Location.getParameter(TerrainEditorAsync.ROOT_ID));
            terrainEditor.getPlanetTerrainInfo(planetId, new AsyncCallback<TerrainInfo>() {
                @Override
                public void onFailure(Throwable throwable) {
                    ClientExceptionHandler.handleException(throwable, true);
                }

                @Override
                public void onSuccess(TerrainInfo terrainInfo) {
                    loadRegionAndStart(terrainInfo);
                }
            });
        } else {
            throw new IllegalArgumentException("RegionEditor.onModuleLoad() Wrong url parameter");
        }
        System.out.println("Started");
    }

    private void loadRegionAndStart(final TerrainInfo terrainInfo) {
        int regionId = Integer.parseInt(Window.Location.getParameter(TerrainEditorAsync.REGION_ID));
        terrainEditor.loadRegionFromDb(regionId, new AsyncCallback<Region>() {
            @Override
            public void onFailure(Throwable throwable) {
                ClientExceptionHandler.handleException(throwable, true);
            }

            @Override
            public void onSuccess(Region region) {
                start(terrainInfo, region);
            }
        });

    }

    private void start(TerrainInfo terrainInfo, Region region) {
        // Setup mini terrain
        TerrainView.getInstance().setupTerrain(terrainInfo.getTerrainSettings(),
                terrainInfo.getTerrainImagePositions(),
                terrainInfo.getSurfaceRects(),
                terrainInfo.getSurfaceImages(),
                terrainInfo.getTerrainImages(),
                terrainInfo.getTerrainImageBackground());
        final MiniTerrain miniTerrain = new MiniTerrain(RootPanel.get().getOffsetWidth(), RootPanel.get().getOffsetHeight());
        TerrainView.getInstance().getTerrainHandler().addTerrainListener(miniTerrain);
        RootPanel.get().add(miniTerrain.getCanvas(), 0, 0);
        miniTerrain.getCanvas().getElement().getStyle().setZIndex(1);
        // If the images get loaded after miniTerrain.onTerrainChanged() has been called
        TerrainView.getInstance().getTerrainHandler().addTerrainListener(new TerrainListener() {
            @Override
            public void onTerrainChanged() {
                miniTerrain.onTerrainChanged();
            }
        });
        miniTerrain.onTerrainSettings(terrainInfo.getTerrainSettings());
        miniTerrain.onTerrainChanged();
        miniTerrain.setScale(ScaleStep.WHOLE_MAP_MISSION);
        // Setup drawing canvas
        Canvas canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(RootPanel.get().getOffsetWidth());
        canvas.setCoordinateSpaceHeight(RootPanel.get().getOffsetHeight());
        RootPanel.get().add(canvas, 0, 0);
        canvas.getElement().getStyle().setZIndex(2);
        canvas.setTabIndex(1);
        // Setup model
        RegionEditorModel regionEditorModel = new RegionEditorModel(region, terrainInfo, miniTerrain, RootPanel.get().getOffsetWidth(), RootPanel.get().getOffsetHeight());
        // Setup mouse control
        MouseAndKeyRegionEditorControl mouseAndKeyRegionEditorControl = new MouseAndKeyRegionEditorControl(regionEditorModel);
        canvas.addMouseMoveHandler(mouseAndKeyRegionEditorControl);
        canvas.addMouseOutHandler(mouseAndKeyRegionEditorControl);
        canvas.addMouseDownHandler(mouseAndKeyRegionEditorControl);
        canvas.addMouseUpHandler(mouseAndKeyRegionEditorControl);
        canvas.addMouseOverHandler(mouseAndKeyRegionEditorControl);
        canvas.addKeyDownHandler(mouseAndKeyRegionEditorControl);
        // Setup button control
        ButtonRegionEditorControl buttonRegionEditorControl = new ButtonRegionEditorControl(regionEditorModel);
        RootPanel.get().add(buttonRegionEditorControl, 0, 0);
        buttonRegionEditorControl.getElement().getStyle().setZIndex(3);
        // Setup view
        RegionEditorRenderer regionEditorRenderer = new RegionEditorRenderer(regionEditorModel);
        regionEditorRenderer.start(canvas);
    }
}
