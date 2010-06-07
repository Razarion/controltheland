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

package com.btxtech.game.jsre.mapview.territory;

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.Territory;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.mapeditor.TerrainEditorAsync;
import com.btxtech.game.jsre.mapview.common.GeometricalUtil;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 26.05.2010
 * Time: 11:14:37
 */
public class TerritoryEditModel {
    public static final String TERRITORY_TO_EDIT = "territory";
    private MiniTerritoryView miniTerritoryView;
    private TerrainSettings terrainSettings;
    private Territory territory;
    private TerrainEditorAsync terrainEditor;
    private TerritoryCockpit territoryCockpit;

    public void save() {
        territoryCockpit.disableSaveButton();
        miniTerritoryView.getTiles();
        ArrayList<Rectangle> territoryRectangles = GeometricalUtil.separateIntoRectangles(miniTerritoryView.getTiles(), terrainSettings);
        territory.setTerritoryTileRegions(territoryRectangles);
        terrainEditor.saveTerritory(territory, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                territoryCockpit.enableSaveButton();
                GwtCommon.handleException(caught, true);
            }

            @Override
            public void onSuccess(Void result) {
                territoryCockpit.enableSaveButton();
            }
        });
    }

    public void setMiniTerritoryView(MiniTerritoryView miniTerritoryView) {
        this.miniTerritoryView = miniTerritoryView;
    }

    public void setTerrainSettings(TerrainSettings terrainSettings) {
        this.terrainSettings = terrainSettings;
    }

    public void setTerrainEditorAsync(TerrainEditorAsync terrainEditor) {
        this.terrainEditor = terrainEditor;
    }

    public void setTerritoryCockpit(TerritoryCockpit territoryCockpit) {
        this.territoryCockpit = territoryCockpit;
    }

    public void setTerritories(Collection<Territory> territories) {
        territoryCockpit.setTerritoryName("???");
        String territoryName = Window.Location.getParameter(TERRITORY_TO_EDIT);
        if (territoryName == null || territoryName.trim().isEmpty()) {
            return;
        }
        for (Territory territory : territories) {
            if (territory.getName().equals(territoryName)) {
                this.territory = territory;
                territoryCockpit.setTerritoryName(territory.getName());
                break;
            }
        }
        miniTerritoryView.setTerritory(territory);
    }
}
