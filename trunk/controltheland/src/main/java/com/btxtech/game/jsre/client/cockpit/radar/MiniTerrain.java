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
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 22.12.2009
 * Time: 12:43:55
 */
public class MiniTerrain extends MiniMap implements TerrainListener {
    public static boolean UGLY_SUPPRESS_CANVAS_BUFFER_DUE_TO_ANTI_ALIASING_ARTIFACTS = false;
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
        if (UGLY_SUPPRESS_CANVAS_BUFFER_DUE_TO_ANTI_ALIASING_ARTIFACTS) {
            drawUnBuffered();
        } else {
            drawBuffered();
        }
    }

    private void drawBuffered() {
        // Due to the canvas antialiasing artifact problem
        Canvas bufferCanvas = Canvas.createIfSupported();
        if (getTerrainSettings().getTileXCount() > 100) {
            scaleToTile();
            bufferCanvas.setCoordinateSpaceWidth(getTerrainSettings().getTileXCount());
            bufferCanvas.setCoordinateSpaceHeight(getTerrainSettings().getTileYCount());
            drawTileSurfaces(bufferCanvas.getContext2d());
            terrainWithoutImages(bufferCanvas.getContext2d());
        } else {
            scaleToAbsolute();
            bufferCanvas.setCoordinateSpaceWidth(getTerrainSettings().getPlayFieldXSize());
            bufferCanvas.setCoordinateSpaceHeight(getTerrainSettings().getPlayFieldYSize());
            drawAbsoluteSurfaces(bufferCanvas.getContext2d());
            terrainWithImages(bufferCanvas.getContext2d());
        }
        getContext2d().drawImage(bufferCanvas.getCanvasElement(), 0, 0);
    }

    private void drawUnBuffered() {
        if (getTerrainSettings().getTileXCount() > 100) {
            scaleToTile();
            drawTileSurfaces(getContext2d());
            terrainWithoutImages(getContext2d());
        } else {
            scaleToAbsolute();
            drawAbsoluteSurfaces(getContext2d());
            terrainWithImages(getContext2d());
        }
    }

    private void drawTileSurfaces(Context2d bufferContext) {
        for (SurfaceRect surfaceRect : TerrainView.getInstance().getTerrainHandler().getSurfaceRects()) {
            Rectangle tileRectangle = surfaceRect.getTileRectangle();
            SurfaceImage surfaceImage = TerrainView.getInstance().getTerrainHandler().getSurfaceImage(surfaceRect);
            if (surfaceImage != null) {
                bufferContext.setFillStyle(surfaceImage.getHtmlBackgroundColor());
                int x = tileRectangle.getX();
                int y = tileRectangle.getY();
                int width = tileRectangle.getWidth();
                int height = tileRectangle.getHeight();

                bufferContext.fillRect(x, y, width, height);
            }
        }
    }

    private void drawAbsoluteSurfaces(Context2d bufferContext) {
        for (SurfaceRect surfaceRect : TerrainView.getInstance().getTerrainHandler().getSurfaceRects()) {
            Rectangle absoluteRectangle = TerrainView.getInstance().getTerrainHandler().convertToAbsolutePosition(surfaceRect.getTileRectangle());
            SurfaceImage surfaceImage = TerrainView.getInstance().getTerrainHandler().getSurfaceImage(surfaceRect);
            if (surfaceImage != null) {
                bufferContext.setFillStyle(surfaceImage.getHtmlBackgroundColor());
                int x = absoluteRectangle.getX();
                int y = absoluteRectangle.getY();
                int width = absoluteRectangle.getWidth();
                int height = absoluteRectangle.getHeight();

                bufferContext.fillRect(x, y, width, height);
            }
        }
    }

    private void terrainWithoutImages(Context2d bufferContext) {
        List<TerrainImagePosition> terrainImagePositionsLayer2 = new ArrayList<TerrainImagePosition>();
        for (TerrainImagePosition terrainImagePosition : TerrainView.getInstance().getTerrainHandler().getTerrainImagePositions()) {
            switch (terrainImagePosition.getzIndex()) {
                case LAYER_1:
                    terrainWithoutImagesLayer(bufferContext, terrainImagePosition);
                    break;
                case LAYER_2:
                    terrainImagePositionsLayer2.add(terrainImagePosition);
                    break;
                default:
                    log.warning("MiniTerrain.terrainWithoutImages() z Index not supported: " + terrainImagePosition.getzIndex());
            }
        }
        for (TerrainImagePosition terrainImagePosition : terrainImagePositionsLayer2) {
            terrainWithoutImagesLayer(bufferContext, terrainImagePosition);
        }
    }

    private void terrainWithoutImagesLayer(Context2d bufferContext, TerrainImagePosition terrainImagePosition) {
        TerrainImage terrainImage = TerrainView.getInstance().getTerrainHandler().getTerrainImage(terrainImagePosition);
        String bgColor = TerrainView.getInstance().getTerrainHandler().getTerrainImageBackground().get(terrainImage.getId());
        bufferContext.setFillStyle(bgColor);

        for (int x = 0; x < terrainImage.getTileWidth(); x++) {
            for (int y = 0; y < terrainImage.getTileHeight(); y++) {
                int startX = terrainImagePosition.getTileIndex().getX() + x;
                int startY = terrainImagePosition.getTileIndex().getY() + y;
                bufferContext.fillRect(startX, startY, 1, 1);
            }
        }
    }

    private void terrainWithImages(Context2d bufferContext) {
        for (TerrainImagePosition terrainImagePosition : TerrainView.getInstance().getTerrainHandler().getTerrainImagePositions()) {
            Index absoluteIndex = TerrainView.getInstance().getTerrainHandler().getAbsolutIndexForTerrainTileIndex(terrainImagePosition.getTileIndex());
            TerrainImage terrainImage = TerrainView.getInstance().getTerrainHandler().getTerrainImage(terrainImagePosition);
            int absoluteWidth = TerrainView.getInstance().getTerrainHandler().getAbsolutXForTerrainTile(terrainImage.getTileWidth());
            int absoluteHeight = TerrainView.getInstance().getTerrainHandler().getAbsolutYForTerrainTile(terrainImage.getTileHeight());
            ImageElement imageElement = TerrainView.getInstance().getTerrainHandler().getTerrainImageElement(terrainImagePosition.getImageId());
            try {
                if (imageElement != null) {
                    bufferContext.drawImage(imageElement,
                            0,
                            0,
                            imageElement.getWidth(),
                            imageElement.getHeight(),
                            absoluteIndex.getX(),
                            absoluteIndex.getY(),
                            absoluteWidth,
                            absoluteHeight);
                }
            } catch (Throwable throwable) {
                GwtCommon.handleException(throwable);
            }
        }
    }
}