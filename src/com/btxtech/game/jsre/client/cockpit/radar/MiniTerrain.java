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
    private boolean first = true;

    public MiniTerrain(int width, int height) {
        super(width, height);
        TerrainView.getInstance().getTerrainHandler().addTerrainListener(this);
    }

    @Override
    public void onTerrainChanged() {
        int playFieldXSize = TerrainView.getInstance().getTerrainHandler().getTerrainSettings().getPlayFieldXSize();
        int playFieldYSize = TerrainView.getInstance().getTerrainHandler().getTerrainSettings().getPlayFieldYSize();
        if (first) {
            double scaleX = (double) getWidth() / (double) playFieldXSize;
            double scaleY = (double) getHeight() / (double) playFieldYSize;
        scale(scaleX, scaleY);
            first = false;
            setCoordSize(playFieldXSize, playFieldYSize);
            }
        clear();
        // Draw terrain background
        setBackgroundColor(new Color(30,100,0));
        //drawBackground(playFieldXSize, playFieldYSize);

        // Draw terrain
        for (TerrainImagePosition terrainImagePosition : TerrainView.getInstance().getTerrainHandler().getTerrainImagePositions()) {
            Index absolute = TerrainView.getInstance().getTerrainHandler().getAbsolutIndexForTerrainTileIndex(terrainImagePosition.getTileIndex());
            ImageElement imageElement = TerrainView.getInstance().getTerrainHandler().getTileImageElement(terrainImagePosition.getImageId());
            if (imageElement != null) {
                drawImage(imageElement, absolute.getX(), absolute.getY());
        }
    }
    }

    private void drawBackground(int playFieldXSize, int playFieldYSize) {
        ImageElement bgImageElement = TerrainView.getInstance().getTerrainHandler().getBackgroundImage();
        if (bgImageElement == null) {
            return;
        }
        int bgTileWidth = bgImageElement.getWidth();
        int bgTileHeight = bgImageElement.getHeight();
        for (int x = 0; x < playFieldXSize / bgTileWidth; x++) {
            for (int y = 0; y < playFieldYSize / bgTileHeight; y++) {
                drawImage(bgImageElement, (bgTileWidth * x), (bgTileHeight * y));
            }
        }
        int bgXRest = playFieldXSize % bgTileWidth;
        int bgYRest = playFieldYSize % bgTileHeight;
        if (bgXRest > 0) {
            for (int y = 0; y < playFieldYSize / bgTileHeight; y++) {
                drawImage(bgImageElement,
                        playFieldXSize - bgXRest,
                        bgTileHeight * y,
                        bgXRest,
                        bgTileHeight);
            }
        }
        if (bgYRest > 0) {
            for (int x = 0; x < playFieldXSize / bgTileWidth; x++) {
                drawImage(bgImageElement,
                        bgTileWidth * x,
                        playFieldYSize - bgYRest,
                        bgXRest,
                        bgYRest);
            }
        }
        if (bgXRest > 0 && bgYRest > 0) {
            drawImage(bgImageElement,
                    playFieldXSize - bgXRest,
                    playFieldYSize - bgYRest,
                    bgXRest,
                    bgYRest);

        }
    }

}