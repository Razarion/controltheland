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

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.TerrainListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.widgetideas.graphics.client.Color;

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
        int playFieldXSize = TerrainView.getInstance().getTerrainHandler().getTerrainSettings().getPlayFieldXSize();
        int playFieldYSize = TerrainView.getInstance().getTerrainHandler().getTerrainSettings().getPlayFieldYSize();
        double scaleX = (double) getWidth() / (double) playFieldXSize;
        double scaleY = (double) getHeight() / (double) playFieldYSize;
        resize(playFieldXSize, playFieldYSize);
        scale(scaleX, scaleY);
        clear();

        // Draw terrain background
        setBackgroundColor(new Color(30, 100, 0));

        // Draw terrain
        for (TerrainImagePosition terrainImagePosition : TerrainView.getInstance().getTerrainHandler().getTerrainImagePositions()) {
            Index absolute = TerrainView.getInstance().getTerrainHandler().getAbsolutIndexForTerrainTileIndex(terrainImagePosition.getTileIndex());
            ImageElement imageElement = TerrainView.getInstance().getTerrainHandler().getTileImageElement(terrainImagePosition.getImageId());
            if (imageElement != null) {
                drawImage(imageElement, absolute.getX(), absolute.getY());
            }
        }
    }

}