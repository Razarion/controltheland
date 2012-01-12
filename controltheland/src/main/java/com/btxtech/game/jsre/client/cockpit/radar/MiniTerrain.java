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

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImageBackground;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.google.gwt.dom.client.ImageElement;

/**
 * User: beat
 * Date: 22.12.2009
 * Time: 12:43:55
 */
public class MiniTerrain extends MiniMap implements TerrainListener {
    public MiniTerrain(int width, int height) {
        super(width, height);
        getCanvas().getElement().getStyle().setBackgroundColor("#000000");
        if (!TerrainView.uglySuppressRadar) {
            TerrainView.getInstance().getTerrainHandler().addTerrainListener(this);
        }
    }

    @Override
    public void onTerrainChanged() {
        clear();

        // Draw surface
        for (SurfaceRect surfaceRect : TerrainView.getInstance().getTerrainHandler().getSurfaceRects()) {
            Rectangle tileRectangle = surfaceRect.getTileRectangle();
            SurfaceImage surfaceImage = TerrainView.getInstance().getTerrainHandler().getSurfaceImage(surfaceRect);
            if (surfaceImage != null) {
                getContext2d().setFillStyle(surfaceImage.getHtmlBackgroundColor());
                getContext2d().setStrokeStyle(surfaceImage.getHtmlBackgroundColor());
                int x = tileRectangle.getX();
                int y = tileRectangle.getY();
                int width = tileRectangle.getWidth();
                int height = tileRectangle.getHeight();

                getContext2d().fillRect(x, y, width, height);
                getContext2d().strokeRect(x, y, width, height);
            }
        }

        // Draw terrain
        if (getTerrainSettings().getTileXCount() > 100) {
            terrainWithoutImages();
        } else {
            terrainWithImages();
        }
    }

    private void terrainWithoutImages() {
        if (Connection.getInstance().getGameInfo() == null) {
            // Does not work in map editor
            return;
        }
        TerrainImageBackground terrainImageBackground = Connection.getInstance().getGameInfo().getTerrainImageBackground();
        for (TerrainImagePosition terrainImagePosition : TerrainView.getInstance().getTerrainHandler().getTerrainImagePositions()) {
            TerrainImage terrainImage = TerrainView.getInstance().getTerrainHandler().getTerrainImage(terrainImagePosition);
            String bgColor = terrainImageBackground.get(terrainImage.getId());
            getContext2d().setFillStyle(bgColor);

            for (int x = 0; x < terrainImage.getTileWidth(); x++) {
                for (int y = 0; y < terrainImage.getTileHeight(); y++) {
                    int startX = terrainImagePosition.getTileIndex().getX() + x;
                    int startY = terrainImagePosition.getTileIndex().getY() + y;
                    getContext2d().fillRect(startX, startY, 1, 1);
                }
            }
        }
    }

    private void terrainWithImages() {
        for (TerrainImagePosition terrainImagePosition : TerrainView.getInstance().getTerrainHandler().getTerrainImagePositions()) {
            TerrainImage terrainImage = TerrainView.getInstance().getTerrainHandler().getTerrainImage(terrainImagePosition);
            ImageElement imageElement = TerrainView.getInstance().getTerrainHandler().getTerrainImageElement(terrainImagePosition.getImageId());
            try {
                if (imageElement != null) {
                    getContext2d().drawImage(imageElement,
                            0,
                            0,
                            imageElement.getWidth(),
                            imageElement.getHeight(),
                            terrainImagePosition.getTileIndex().getX(),
                            terrainImagePosition.getTileIndex().getY(),
                            terrainImage.getTileWidth(),
                            terrainImage.getTileHeight());
                }
            } catch (Throwable throwable) {
                GwtCommon.handleException(throwable);
            }
        }
    }
}