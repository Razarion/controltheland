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

package com.btxtech.game.jsre.mapview;

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.TopMapPanel;
import com.btxtech.game.jsre.client.cockpit.radar.MiniTerrain;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.territory.ClientTerritoryService;
import com.btxtech.game.jsre.common.Territory;
import com.btxtech.game.jsre.mapeditor.TerrainEditor;
import com.btxtech.game.jsre.mapeditor.TerrainEditorAsync;
import com.btxtech.game.jsre.mapeditor.TerrainInfo;
import com.btxtech.game.jsre.mapview.territory.MiniTerritoryView;
import com.btxtech.game.jsre.mapview.territory.TerritoryCockpit;
import com.btxtech.game.jsre.mapview.territory.TerritoryEditModel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;
import java.util.Collection;

/**
 * User: beat
 * Date: 22.05.2010
 * Time: 15:13:09
 */
public class MapView implements EntryPoint {

    @Override
    public void onModuleLoad() {
        // Setup common
        GwtCommon.setUncaughtExceptionHandler();
        GwtCommon.disableBrowserContextMenuJSNI();

        AbsolutePanel absolutePanel = new AbsolutePanel();
        absolutePanel.setSize("100%", "100%");
        RootPanel.get().add(absolutePanel);

        final TerritoryEditModel territoryEditModel = new TerritoryEditModel();
        TerritoryCockpit cockpit = new TerritoryCockpit(territoryEditModel);
        cockpit.addToParent(absolutePanel, TopMapPanel.Direction.RIGHT_TOP, 30);
        territoryEditModel.setTerritoryCockpit(cockpit);

        final MiniTerrain miniTerrain = new MiniTerrain(RootPanel.get().getOffsetWidth(), RootPanel.get().getOffsetHeight());
        miniTerrain.getElement().getStyle().setZIndex(1);
        absolutePanel.add(miniTerrain, 0, 0);

        final MiniTerritoryView miniTerritoryView = new MiniTerritoryView(RootPanel.get().getOffsetWidth(), RootPanel.get().getOffsetHeight());
        miniTerritoryView.getElement().getStyle().setZIndex(2);
        absolutePanel.add(miniTerritoryView, 0, 0);
        territoryEditModel.setMiniTerritoryView(miniTerritoryView);

        TerrainEditorAsync terrainEditor = GWT.create(TerrainEditor.class);
        territoryEditModel.setTerrainEditorAsync(terrainEditor);
        terrainEditor.getTerrainInfo(new AsyncCallback<TerrainInfo>() {
            @Override
            public void onFailure(Throwable throwable) {
                GwtCommon.handleException(throwable, true);
            }

            @Override
            public void onSuccess(TerrainInfo terrainInfo) {
                TerrainView.getInstance().setupTerrain(terrainInfo.getTerrainSettings(),
                        terrainInfo.getTerrainImagePositions(),
                        terrainInfo.getSurfaceRects(),
                        terrainInfo.getSurfaceImages(),
                        terrainInfo.getTerrainImages());
                miniTerrain.onTerrainSettings(terrainInfo.getTerrainSettings());
                miniTerritoryView.onTerrainSettings(terrainInfo.getTerrainSettings());
                territoryEditModel.setTerrainSettings(terrainInfo.getTerrainSettings());
                miniTerritoryView.drawTiles();
            }
        });
        terrainEditor.getTerritories(new AsyncCallback<Collection<Territory>>() {
            @Override
            public void onFailure(Throwable throwable) {
                GwtCommon.handleException(throwable, true);
            }

            @Override
            public void onSuccess(Collection<Territory> territories) {
                ClientTerritoryService.getInstance().setTerritories(territories);
                territoryEditModel.setTerritories(territories);
            }
        });
    }
}
