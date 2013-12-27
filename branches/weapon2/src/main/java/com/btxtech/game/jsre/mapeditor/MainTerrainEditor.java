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
import com.btxtech.game.jsre.client.TopMapPanel;
import com.btxtech.game.jsre.mapeditor.render.MapEditorRenderer;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
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

        String rootType = Window.Location.getParameter(TerrainEditorAsync.ROOT_TYPE);
        int rootId = Integer.parseInt(Window.Location.getParameter(TerrainEditorAsync.ROOT_ID));

        // Model
        final MapEditorModel mapEditorModel = new MapEditorModel(RootPanel.get().getOffsetWidth(), RootPanel.get().getOffsetHeight());

        // View
        Cockpit cockpit = new Cockpit();
        cockpit.setMapEditorModel(mapEditorModel);
        cockpit.addToParent(RootPanel.get(), TopMapPanel.Direction.LEFT_TOP, 0);
        cockpit.getElement().getStyle().setZIndex(2);
        final Canvas canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(RootPanel.get().getOffsetWidth());
        canvas.setCoordinateSpaceHeight(RootPanel.get().getOffsetHeight());
        RootPanel.get().add(canvas, 0, 0);
        canvas.getElement().getStyle().setZIndex(1);
        canvas.setTabIndex(1);
        canvas.addMouseMoveHandler(mapEditorModel);
        canvas.addMouseDownHandler(mapEditorModel);
        canvas.addMouseUpHandler(mapEditorModel);
        canvas.addKeyDownHandler(mapEditorModel);
        canvas.addKeyUpHandler(mapEditorModel);
        canvas.addMouseOverHandler(mapEditorModel);
        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                canvas.setCoordinateSpaceWidth(event.getWidth());
                canvas.setCoordinateSpaceHeight(event.getHeight());
                mapEditorModel.onCanvasSizeChanged(event.getWidth(), event.getHeight());
            }
        });

        MapEditorRenderer mapEditorRenderer = new MapEditorRenderer();
        mapEditorRenderer.setCanvas(canvas);
        mapEditorRenderer.setMapEditorModel(mapEditorModel);
        MapEditorCursorHandler mapEditorCursorHandler = new MapEditorCursorHandler();
        mapEditorCursorHandler.setCanvas(canvas);
        mapEditorModel.setMapEditorCursorHandler(mapEditorCursorHandler);
        MapEditorRadar mapEditorRadar = new MapEditorRadar();
        mapEditorRadar.addToParent(RootPanel.get(), TopMapPanel.Direction.RIGHT_TOP, 0);
        mapEditorRadar.getElement().getStyle().setZIndex(2);
        mapEditorRadar.setMapEditorModel(mapEditorModel);

        // Data
        TerrainData terrainData = new TerrainData();
        mapEditorModel.setTerrainData(terrainData);
        // Connection
        TerrainEditorConnection terrainEditorConnection = new TerrainEditorConnection();
        cockpit.setTerrainEditorConnection(terrainEditorConnection);
        terrainEditorConnection.setTootType(rootType);
        terrainEditorConnection.setRootId(rootId);
        terrainEditorConnection.setMapEditorModel(mapEditorModel);
        terrainEditorConnection.setCockpit(cockpit);
        terrainEditorConnection.setTerrainData(terrainData);
        terrainEditorConnection.setMapEditorRenderer(mapEditorRenderer);
        terrainEditorConnection.loadTerrain();
    }
}
