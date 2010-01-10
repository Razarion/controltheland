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

package com.btxtech.game.jsre.client.cockpit.radar;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.terrain.TerrainListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.google.gwt.dom.client.ImageElement;

/**
 * User: beat
 * Date: 22.12.2009
 * Time: 12:43:55
 */
public class MiniTerrain extends MiniMap implements TerrainListener {
    public MiniTerrain(int width, int height) {
        super(width, height);
        TerrainView.getInstance().getTerrainHandler().addTerrainListener(this);
    }

    @Override
    public void onTerrainChanged() {
        int xCount = TerrainView.getInstance().getTerrainHandler().getTileXCount();
        int yCount = TerrainView.getInstance().getTerrainHandler().getTileYCount();
        double scaleX = (double) getWidth() / (double) TerrainView.getInstance().getTerrainHandler().getTerrainWidth();
        double scaleY = (double) getHeight() / (double) TerrainView.getInstance().getTerrainHandler().getTerrainHeight();
        scale(scaleX, scaleY);
        for (int x = 0; x < xCount; x++) {
            for (int y = 0; y < yCount; y++) {
                int tileId = TerrainView.getInstance().getTerrainHandler().getTerrainField()[x][y];
                ImageElement imageElement = TerrainView.getInstance().getTerrainHandler().getTileImageElement(tileId);
                if (imageElement != null) {
                    drawImage(imageElement, Constants.TILE_WIDTH * x, Constants.TILE_HEIGHT * y);
                }
            }
        }
    }

}