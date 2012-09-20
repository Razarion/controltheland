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
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainHandler;
import com.btxtech.game.jsre.client.terrain.TerrainListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImageBackground;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainTile;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 22.12.2009
 * Time: 12:43:55
 */
public class MiniTerrain extends MiniMap implements TerrainListener {
    private Logger log = Logger.getLogger(MiniTerrain.class.getName());

    public MiniTerrain(int width, int height) {
        super(width, height);
        getCanvas().getElement().getStyle().setBackgroundColor("#000000");
        if (!TerrainView.uglySuppressRadar) {
            TerrainView.getInstance().getTerrainHandler().addTerrainListener(this);
        }
    }

    @Override
    public void onTerrainChanged() {
        draw();
    }

    @Override
    protected void render() {
        if (getScale().isDrawImages()) {
            drawImages(getContext2d());
        } else {
            drawWithoutImages(getContext2d());
        }
    }

    private void drawWithoutImages(final Context2d context2d) {
        final Rectangle tileRect = getTileViewRectangle();
        final int scrollXOffset = getViewOrigin().getX() % Constants.TERRAIN_TILE_WIDTH;
        final int scrollYOffset = getViewOrigin().getY() % Constants.TERRAIN_TILE_HEIGHT;
        final int xTileIncrease = getScale().getTileIncrease();
        final int yTileIncrease = getScale().getTileIncrease();
        final int tileWidth = Constants.TERRAIN_TILE_WIDTH * xTileIncrease;
        final int tileHeight = Constants.TERRAIN_TILE_HEIGHT * yTileIncrease;
        TerrainView.getInstance().getTerrainHandler().iteratorOverAllTerrainTiles(tileRect, new AbstractTerrainService.TerrainTileEvaluator() {
            TerrainHandler terrainHandler = TerrainView.getInstance().getTerrainHandler();
            TerrainImageBackground terrainImageBackground = terrainHandler.getTerrainImageHandler().getTerrainImageBackground();

            @Override
            public void evaluate(int x, int y, TerrainTile terrainTile) {
                if (terrainTile == null) {
                    return;
                }

                int relativeX = terrainHandler.getAbsolutXForTerrainTile(x - tileRect.getX());
                int imageWidth = tileWidth;
                if (relativeX == 0) {
                    imageWidth = tileWidth - scrollXOffset;
                } else {
                    relativeX -= scrollXOffset;
                }

                int relativeY = terrainHandler.getAbsolutYForTerrainTile(y - tileRect.getY());
                int imageHeight = tileHeight;
                if (relativeY == 0) {
                    imageHeight = tileHeight - scrollYOffset;
                } else {
                    relativeY -= scrollYOffset;
                }

                if (terrainTile.isSurface()) {
                    context2d.setFillStyle(terrainHandler.getTerrainImageHandler().getSurfaceImage(terrainTile.getImageId()).getHtmlBackgroundColor());
                } else {
                    SurfaceType surfaceType = terrainHandler.getTerrainImageHandler().getTerrainImage(terrainTile.getImageId()).getSurfaceType(terrainTile.getTileXOffset(), terrainTile.getTileYOffset());
                    context2d.setFillStyle(terrainImageBackground.get(terrainTile.getImageId(), surfaceType));
                }

                try {
                    context2d.fillRect(Math.round((double) relativeX * getScaleValue()) + getXShiftRadarPixel(),
                            Math.round((double) relativeY * getScaleValue()) + getYShiftRadarPixel(),
                            Math.ceil((double) imageWidth * getScaleValue()),
                            Math.ceil((double) imageHeight * getScaleValue()));
                } catch (Throwable t) {
                    log.log(Level.SEVERE, "MiniTerrain.drawWithoutImages() error in canvas fillRect", t);
                }
            }
        }, xTileIncrease, yTileIncrease);
    }

    private void drawImages(final Context2d context2d) {
        final Rectangle tileRect = getTileViewRectangle();
        final int scrollXOffset = getViewOrigin().getX() % Constants.TERRAIN_TILE_WIDTH;
        final int scrollYOffset = getViewOrigin().getY() % Constants.TERRAIN_TILE_HEIGHT;
        final int tileWidth = Constants.TERRAIN_TILE_WIDTH;
        final int tileHeight = Constants.TERRAIN_TILE_HEIGHT;
        TerrainView.getInstance().getTerrainHandler().iteratorOverAllTerrainTiles(tileRect, new AbstractTerrainService.TerrainTileEvaluator() {
            TerrainHandler terrainHandler = TerrainView.getInstance().getTerrainHandler();

            @Override
            public void evaluate(int x, int y, TerrainTile terrainTile) {
                if (terrainTile == null) {
                    return;
                }

                int relativeX = terrainHandler.getAbsolutXForTerrainTile(x - tileRect.getX());
                int imageWidth = tileWidth;
                if (relativeX == 0) {
                    imageWidth = tileWidth - scrollXOffset;
                } else {
                    relativeX -= scrollXOffset;
                }

                int relativeY = terrainHandler.getAbsolutYForTerrainTile(y - tileRect.getY());
                int imageHeight = tileHeight;
                if (relativeY == 0) {
                    imageHeight = tileHeight - scrollYOffset;
                } else {
                    relativeY -= scrollYOffset;
                }

                ImageElement imageElement;
                if (terrainTile.isSurface()) {
                    imageElement = terrainHandler.getTerrainImageHandler().getSurfaceImageElement(terrainTile.getImageId());
                } else {
                    imageElement = terrainHandler.getTerrainImageHandler().getTerrainImageElement(terrainTile.getImageId());
                }
                if (imageElement == null || imageElement.getWidth() == 0 || imageElement.getHeight() == 0) {
                    return;
                }

                int sourceXOffset = terrainHandler.getAbsolutXForTerrainTile(terrainTile.getTileXOffset());
                int sourceYOffset = terrainHandler.getAbsolutYForTerrainTile(terrainTile.getTileYOffset());
                if (relativeX == 0) {
                    sourceXOffset += scrollXOffset;
                }
                if (relativeY == 0) {
                    sourceYOffset += scrollYOffset;
                }

                if (terrainTile.isSurface()) {
                    sourceXOffset = sourceXOffset % imageElement.getWidth();
                    sourceYOffset = sourceYOffset % imageElement.getHeight();
                }

                try {
                    context2d.drawImage(imageElement,
                            sourceXOffset, //the start X position in the source image
                            sourceYOffset, //the start Y position in the source image
                            imageWidth, //the width in the source image you want to sample
                            imageHeight, //the height in the source image you want to sample
                            Math.round((double) relativeX * getScaleValue()) + getXShiftRadarPixel(), //the start X position in the destination image
                            Math.round((double) relativeY * getScaleValue()) + getYShiftRadarPixel(), //the start Y position in the destination image
                            Math.ceil((double) imageWidth * getScaleValue()), //the width of drawn image in the destination
                            Math.ceil((double) imageHeight * getScaleValue()) // the height of the drawn image in the destination
                    );
                } catch (Throwable t) {
                    log.log(Level.SEVERE, "MiniTerrain.drawImages() error in canvas drawImage", t);
                }
            }
        });
    }
}