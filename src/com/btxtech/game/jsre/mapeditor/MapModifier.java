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
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.TerrainMouseButtonListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.TerrainUtil;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;

/**
 * User: beat
 * Date: Sep 3, 2009
 * Time: 6:26:18 PM
 */
public class MapModifier implements TerrainMouseButtonListener {
    private GameEditorAsync terrainAsync;
    private TileSelector tileSelector;
    private Button button;

    public MapModifier(GameEditorAsync terrainAsync, TileSelector tileSelector, Button button) {
        this.terrainAsync = terrainAsync;
        this.tileSelector = tileSelector;
        this.button = button;
    }

    @Override
    public void onMouseDown(int absoluteX, int absoluteY, MouseDownEvent mouseDownEvent) {
        flipTile(absoluteX, absoluteY);
    }

    private void flipTile(int absoluteX, int absoluteY) {
        int tileId = tileSelector.getSelection();
        if (tileId < 0) {
            return;
        }
        Index tileIndex = TerrainUtil.getTerrainTileIndexForAbsPosition(absoluteX, absoluteY);
        TerrainView terrainView = TerrainView.getInstance();
        terrainView.getTerrainHandler().getTerrainField()[tileIndex.getX()][tileIndex.getY()] = tileId;
        terrainView.getTerrainHandler().loadTilesAndDrawMap();
    }

    public void saveMap() {
        int[][] filed = TerrainView.getInstance().getTerrainHandler().getTerrainField();
        button.setEnabled(false);
        terrainAsync.setTerrainField(filed, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable throwable) {
                button.setEnabled(true);
                GwtCommon.handleException(throwable);
            }

            @Override
            public void onSuccess(Void aVoid) {
                button.setEnabled(true);
            }

        });
    }
}
