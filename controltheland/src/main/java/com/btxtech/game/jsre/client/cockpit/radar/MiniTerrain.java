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

import com.btxtech.game.jsre.client.terrain.TerrainHandler;
import com.btxtech.game.jsre.client.terrain.TerrainListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImageBackground;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainTile;
import com.google.gwt.canvas.client.Canvas;
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
        super(width, height, true);
        getCanvas().getElement().getStyle().setBackgroundColor("#000000");
        if (!TerrainView.uglySuppressRadar) {
            TerrainView.getInstance().getTerrainHandler().addTerrainListener(this);
        }
    }

    @Override
    public void onTerrainChanged() {
        if (getTerrainSettings() == null) {
            return;
        }
        clear();
        drawBuffered();
    }

    private boolean isScaleToTile() {
        return getTerrainSettings().getTileXCount() > 100
                || getTerrainSettings().getTileYCount() > 100
                || getWidth() < getTerrainSettings().getTileXCount() && getHeight() < getTerrainSettings().getTileYCount();
    }

    private void drawBuffered() {
        // Due to the canvas antialiasing artifact problem
        Canvas bufferCanvas = Canvas.createIfSupported();
        if (isScaleToTile()) {
            scaleToTile();
            bufferCanvas.setCoordinateSpaceWidth(getTerrainSettings().getTileXCount());
            bufferCanvas.setCoordinateSpaceHeight(getTerrainSettings().getTileYCount());
            drawWithoutImages(bufferCanvas.getContext2d());
        } else {
            scaleToAbsolute();
            bufferCanvas.setCoordinateSpaceWidth(getTerrainSettings().getPlayFieldXSize());
            bufferCanvas.setCoordinateSpaceHeight(getTerrainSettings().getPlayFieldYSize());
            drawImages(bufferCanvas.getContext2d());
        }
        getContext2d().drawImage(bufferCanvas.getCanvasElement(), 0, 0);
    }

    private void drawWithoutImages(final Context2d bufferContext) {
        TerrainView.getInstance().getTerrainHandler().iteratorOverAllTerrainTiles(null, new AbstractTerrainService.TerrainTileEvaluator() {
            TerrainHandler terrainHandler = TerrainView.getInstance().getTerrainHandler();
            TerrainImageBackground terrainImageBackground = terrainHandler.getTerrainImageBackground();

            @Override
            public void evaluate(int x, int y, TerrainTile terrainTile) {
                if (terrainTile == null) {
                    return;
                }

                if (terrainTile.isSurface()) {
                    bufferContext.setFillStyle(terrainHandler.getSurfaceImage(terrainTile.getImageId()).getHtmlBackgroundColor());
                } else {
                    SurfaceType surfaceType = terrainHandler.getTerrainImage(terrainTile.getImageId()).getSurfaceType(terrainTile.getTileXOffset(), terrainTile.getTileYOffset());
                    bufferContext.setFillStyle(terrainImageBackground.get(terrainTile.getImageId(), surfaceType));
                }
                try {
                    bufferContext.fillRect(x, y, 1, 1);
                } catch (Throwable t) {
                    log.log(Level.SEVERE, "MiniTerrain.drawWithoutImages() error in canvas fillRect", t);
                }
            }
        });
    }

    private void drawImages(final Context2d context2d) {
        TerrainView.getInstance().getTerrainHandler().iteratorOverAllTerrainTiles(null, new AbstractTerrainService.TerrainTileEvaluator() {
            TerrainHandler terrainHandler = TerrainView.getInstance().getTerrainHandler();
            int tileWidth = terrainHandler.getTerrainSettings().getTileWidth();
            int tileHeight = terrainHandler.getTerrainSettings().getTileHeight();

            @Override
            public void evaluate(int x, int y, TerrainTile terrainTile) {
                if (terrainTile == null) {
                    return;
                }

                ImageElement imageElement;
                if (terrainTile.isSurface()) {
                    imageElement = terrainHandler.getSurfaceImageElement(terrainTile.getImageId());
                } else {
                    imageElement = terrainHandler.getTerrainImageElement(terrainTile.getImageId());
                }
                if (imageElement == null || imageElement.getWidth() == 0 || imageElement.getHeight() == 0) {
                    return;
                }

                int absoluteX = terrainHandler.getAbsolutXForTerrainTile(x);
                int absoluteY = terrainHandler.getAbsolutYForTerrainTile(y);

                int sourceXOffset = terrainHandler.getAbsolutXForTerrainTile(terrainTile.getTileXOffset());
                int sourceYOffset = terrainHandler.getAbsolutYForTerrainTile(terrainTile.getTileYOffset());

                if (terrainTile.isSurface()) {
                    sourceXOffset = sourceXOffset % imageElement.getWidth();
                    sourceYOffset = sourceYOffset % imageElement.getHeight();
                }

                try {
                    context2d.drawImage(imageElement,
                            sourceXOffset, //the start X position in the source image
                            sourceYOffset, //the start Y position in the source image
                            tileWidth, //the width in the source image you want to sample
                            tileHeight, //the height in the source image you want to sample
                            absoluteX, //the start X position in the destination image
                            absoluteY, //the start Y position in the destination image
                            tileWidth, //the width of drawn image in the destination
                            tileHeight // the height of the drawn image in the destination
                    );
                } catch (Throwable t) {
                    log.log(Level.SEVERE, "MiniTerrain.drawImages() error in canvas drawImage", t);
                }
            }
        });
    }
}