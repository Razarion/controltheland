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

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.ImageElement;

/**
 * User: beat
 * Date: 22.12.2009
 * Time: 12:43:55
 */
public class MiniTerrain extends MiniMap implements TerrainListener {
    private static final CssColor WATER_COLOR = CssColor.make(24, 80, 120);
    private static final CssColor LAND_COLOR = CssColor.make(49, 60, 20);
    private static final CssColor NONE_COLOR = CssColor.make(92, 92, 92);
    private static final CssColor LAND_COAST_COLOR = CssColor.make(177, 146, 110);
    private static final CssColor WATER_COAST_COLOR = WATER_COLOR;


    public MiniTerrain(int width, int height) {
        super(width, height);
        getCanvas().getElement().getStyle().setBackgroundColor("#FFFFFF");
        TerrainView.getInstance().getTerrainHandler().addTerrainListener(this);
    }

    @Override
    public void onTerrainChanged() {
        clear();

        double factor = 1.0 / getScale();

        // Draw surface
        for (SurfaceRect surfaceRect : TerrainView.getInstance().getTerrainHandler().getSurfaceRects()) {
            Rectangle tileRectangle = surfaceRect.getTileRectangle();
            SurfaceImage surfaceImage = TerrainView.getInstance().getTerrainHandler().getSurfaceImage(surfaceRect);
            if (surfaceImage != null) {
                setFillColor(surfaceImage.getSurfaceType());
                // If +2 is omitted, there are ugly lines in the MiniTerrain
                getContext2d().fillRect(tileRectangle.getX(), tileRectangle.getY(), tileRectangle.getWidth() + factor, tileRectangle.getHeight() + factor);
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
        for (TerrainImagePosition terrainImagePosition : TerrainView.getInstance().getTerrainHandler().getTerrainImagePositions()) {
            TerrainImage terrainImage = TerrainView.getInstance().getTerrainHandler().getTerrainImage(terrainImagePosition);
            for (int x = 0; x < terrainImage.getTileWidth(); x++) {
                for (int y = 0; y < terrainImage.getTileHeight(); y++) {
                    SurfaceType surfaceType = terrainImage.getSurfaceType(x, y);
                    setFillColor(surfaceType);
                    getContext2d().fillRect(terrainImagePosition.getTileIndex().getX() + x,
                            terrainImagePosition.getTileIndex().getY() + y,
                            1,
                            1);
                }
            }
        }
    }

    private void terrainWithImages() {
        getContext2d().save();
        double scale = Math.min((double) getWidth() / (double) getTerrainSettings().getPlayFieldXSize(),
                (double) getHeight() / (double) getTerrainSettings().getPlayFieldYSize()) / getScale();
        getContext2d().scale(scale, scale);

        for (TerrainImagePosition terrainImagePosition : TerrainView.getInstance().getTerrainHandler().getTerrainImagePositions()) {
            Index absolute = TerrainView.getInstance().getTerrainHandler().getAbsolutIndexForTerrainTileIndex(terrainImagePosition.getTileIndex());
            ImageElement imageElement = TerrainView.getInstance().getTerrainHandler().getTerrainImageElement(terrainImagePosition.getImageId());
            try {
                if (imageElement != null) {
                    getContext2d().drawImage(imageElement,
                            0,
                            0,
                            imageElement.getWidth(),
                            imageElement.getHeight(),
                            absolute.getX(),
                            absolute.getY(),
                            imageElement.getWidth(),
                            imageElement.getHeight());
                }
            } catch (Throwable throwable) {
                GwtCommon.handleException(throwable);
            }
        }
        getContext2d().restore();
    }

    private void setFillColor(SurfaceType surfaceType) {
        switch (surfaceType) {
            case WATER:
                getContext2d().setFillStyle(WATER_COLOR);
                break;
            case LAND:
                getContext2d().setFillStyle(LAND_COLOR);
                break;
            case NONE:
                getContext2d().setFillStyle(NONE_COLOR);
                break;
            case LAND_COAST:
                getContext2d().setFillStyle(LAND_COAST_COLOR);
                break;
            case WATER_COAST:
                getContext2d().setFillStyle(WATER_COAST_COLOR);
                break;
            default:
                throw new IllegalArgumentException(this + " unknown surface type: " + surfaceType);
        }
    }
}