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
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.info.ImageSpriteMapInfo;
import com.btxtech.game.jsre.client.renderer.ImageLoaderContainer;
import com.btxtech.game.jsre.client.renderer.ImageSpriteMapContainer;
import com.btxtech.game.jsre.client.renderer.SurfaceLoaderContainer;
import com.btxtech.game.jsre.client.renderer.TerrainImageLoaderContainer;
import com.btxtech.game.jsre.client.terrain.TerrainHandler;
import com.btxtech.game.jsre.client.terrain.TerrainListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImageBackground;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainTile;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainUtil;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 22.12.2009
 * Time: 12:43:55
 */
public class MiniTerrain extends MiniMap implements TerrainListener, ImageLoaderContainer.LoadListener {
    private Logger log = Logger.getLogger(MiniTerrain.class.getName());

    public MiniTerrain(int width, int height) {
        super(width, height);
        getCanvas().getElement().getStyle().setBackgroundColor("#000000");
        if (!TerrainView.uglySuppressRadar) {
            TerrainView.getInstance().getTerrainHandler().addTerrainListener(this);
            TerrainImageLoaderContainer.getInstance().addLoadListener(this);
            SurfaceLoaderContainer.getInstance().addLoadListener(this);
        }
    }

    @Override
    public void onTerrainChanged() {
        draw();
    }

    @Override
    protected void render() {
        final Rectangle tileRect = getTileViewRectangle();
        MiniMapRenderDetails miniMapRenderDetails = new MiniMapRenderDetails(tileRect);

        if (miniMapRenderDetails.isDrawImages()) {
            drawImages(getContext2d(), tileRect);
        } else {
            drawWithoutImages(getContext2d(), tileRect, miniMapRenderDetails);
        }
    }

    private void drawWithoutImages(final Context2d context2d, final Rectangle tileRect, MiniMapRenderDetails miniMapRenderDetails) {
        final int scrollXOffset = getViewOrigin().getX() % Constants.TERRAIN_TILE_WIDTH;
        final int scrollYOffset = getViewOrigin().getY() % Constants.TERRAIN_TILE_HEIGHT;
        final int xTileIncrease = miniMapRenderDetails.getTileIncrease();
        final int yTileIncrease = miniMapRenderDetails.getTileIncrease();
        final int tileWidth = Constants.TERRAIN_TILE_WIDTH * xTileIncrease;
        final int tileHeight = Constants.TERRAIN_TILE_HEIGHT * yTileIncrease;
        TerrainView.getInstance().getTerrainHandler().iteratorOverAllTerrainTiles(tileRect, new AbstractTerrainService.TerrainTileEvaluator() {
            TerrainHandler terrainHandler = TerrainView.getInstance().getTerrainHandler();
            TerrainImageBackground terrainImageBackground = terrainHandler.getCommonTerrainImageService().getTerrainImageBackground();

            @Override
            public void evaluate(int x, int y, TerrainTile terrainTile) {
                if (terrainTile == null) {
                    return;
                }

                int relativeX = TerrainUtil.getAbsolutXForTerrainTile(x - tileRect.getX());
                int imageWidth = tileWidth;
                if (relativeX == 0) {
                    imageWidth = tileWidth - scrollXOffset;
                } else {
                    relativeX -= scrollXOffset;
                }

                int relativeY = TerrainUtil.getAbsolutYForTerrainTile(y - tileRect.getY());
                int imageHeight = tileHeight;
                if (relativeY == 0) {
                    imageHeight = tileHeight - scrollYOffset;
                } else {
                    relativeY -= scrollYOffset;
                }

                if (terrainTile.isSurface()) {
                    context2d.setFillStyle(terrainHandler.getCommonTerrainImageService().getSurfaceImage(terrainTile.getImageId()).getHtmlBackgroundColor());
                } else {
                    SurfaceType surfaceType = terrainHandler.getCommonTerrainImageService().getTerrainImage(terrainTile.getImageId()).getSurfaceType(terrainTile.getTileXOffset(), terrainTile.getTileYOffset());
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

    private void drawImages(final Context2d context2d, final Rectangle tileRect) {
        final int scrollXOffset = getViewOrigin().getX() % Constants.TERRAIN_TILE_WIDTH;
        final int scrollYOffset = getViewOrigin().getY() % Constants.TERRAIN_TILE_HEIGHT;
        final int tileWidth = Constants.TERRAIN_TILE_WIDTH;
        final int tileHeight = Constants.TERRAIN_TILE_HEIGHT;
        final TerrainHandler terrainHandler = TerrainView.getInstance().getTerrainHandler();
        final TerrainImageBackground terrainImageBackground = terrainHandler.getCommonTerrainImageService().getTerrainImageBackground();
        TerrainView.getInstance().getTerrainHandler().iteratorOverAllTerrainTiles(tileRect, new AbstractTerrainService.TerrainTileEvaluator() {

            @Override
            public void evaluate(int x, int y, TerrainTile terrainTile) {
                int relativeX = TerrainUtil.getAbsolutXForTerrainTile(x - tileRect.getX());
                int imageWidth = tileWidth;
                if (relativeX == 0) {
                    imageWidth = tileWidth - scrollXOffset;
                } else {
                    relativeX -= scrollXOffset;
                }

                int relativeY = TerrainUtil.getAbsolutYForTerrainTile(y - tileRect.getY());
                int imageHeight = tileHeight;
                if (relativeY == 0) {
                    imageHeight = tileHeight - scrollYOffset;
                } else {
                    relativeY -= scrollYOffset;
                }

                ImageElement imageElement = null;
                ImageSpriteMapInfo imageSpriteMapInfo = null;
                if (terrainTile == null) {
                    imageElement = null;
                } else if (terrainTile.isSurface()) {
                    if (terrainTile.hasImageSpriteMapInfo()) {
                        imageSpriteMapInfo = terrainTile.getImageSpriteMapInfo();
                    } else {
                        imageElement = SurfaceLoaderContainer.getInstance().getImage(terrainTile.getImageId());
                    }
                } else {
                    if (terrainTile.hasImageSpriteMapInfo()) {
                        imageSpriteMapInfo = terrainTile.getImageSpriteMapInfo();
                    }
                    imageElement = TerrainImageLoaderContainer.getInstance().getImage(terrainTile.getImageId());
                }
                boolean fallbackNeeded = true;
                // Render clip below image
                if (imageSpriteMapInfo != null) {
                    ImageElement clipImageElement = ImageSpriteMapContainer.getInstance().getImage(imageSpriteMapInfo);
                    // Check if image is loaded
                    if (clipImageElement != null && clipImageElement.getWidth() != 0 && clipImageElement.getHeight() != 0) {
                        int clipTileXCount = TerrainUtil.getTerrainTileIndexForAbsXPosition(imageSpriteMapInfo.getFrameWidth());
                        int clipTileYCount = TerrainUtil.getTerrainTileIndexForAbsYPosition(imageSpriteMapInfo.getFrameHeight());
                        int sourceClipXOffset = TerrainUtil.getAbsolutXForTerrainTile(x % clipTileXCount);
                        int sourceClipYOffset = TerrainUtil.getAbsolutYForTerrainTile(y % clipTileYCount);
                        if (relativeX == 0) {
                            sourceClipXOffset += scrollXOffset;
                        }
                        if (relativeY == 0) {
                            sourceClipYOffset += scrollYOffset;
                        }
                        Index sourceClipOffset = imageSpriteMapInfo.getSpriteMapOffset(imageSpriteMapInfo.getFrame(0));
                        sourceClipXOffset += sourceClipOffset.getX();
                        sourceClipYOffset += sourceClipOffset.getY();

                        context2d.drawImage(clipImageElement,
                                sourceClipXOffset, // Source x pos
                                sourceClipYOffset, // Source y pos
                                imageWidth, //the width in the source image you want to sample
                                imageHeight, //the height in the source image you want to sample
                                Math.round((double) relativeX * getScaleValue()) + getXShiftRadarPixel(), //the start X position in the destination image
                                Math.round((double) relativeY * getScaleValue()) + getYShiftRadarPixel(), //the start Y position in the destination image
                                Math.ceil((double) imageWidth * getScaleValue()), //the width of drawn image in the destination
                                Math.ceil((double) imageHeight * getScaleValue()) // the height of the drawn image in the destination
                        );
                        fallbackNeeded = false;
                    } else {
                        ImageSpriteMapContainer.getInstance().startLoad();
                    }
                }

                // Render overlay image. Check if image is loaded
                if (imageElement != null && imageElement.getWidth() != 0 && imageElement.getHeight() != 0) {
                    int sourceXOffset = TerrainUtil.getAbsolutXForTerrainTile(terrainTile.getTileXOffset());
                    int sourceYOffset = TerrainUtil.getAbsolutYForTerrainTile(terrainTile.getTileYOffset());
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
                        fallbackNeeded = false;
                    } catch (Throwable t) {
                        log.log(Level.SEVERE, "MiniTerrain.drawImages() error in canvas drawImage", t);
                    }
                }
                if (fallbackNeeded) {
                    // Image is not loaded or no image
                    if (terrainTile != null) {
                        if (terrainTile.isSurface()) {
                            context2d.setFillStyle(terrainHandler.getCommonTerrainImageService().getSurfaceImage(terrainTile.getImageId()).getHtmlBackgroundColor());
                        } else {
                            SurfaceType surfaceType = terrainHandler.getCommonTerrainImageService().getTerrainImage(terrainTile.getImageId()).getSurfaceType(terrainTile.getTileXOffset(), terrainTile.getTileYOffset());
                            context2d.setFillStyle(terrainImageBackground.get(terrainTile.getImageId(), surfaceType));
                        }
                    } else {
                        // No Image
                        context2d.setFillStyle("#000000");
                    }
                    context2d.fillRect(relativeX, relativeY, imageWidth, imageHeight);
                }
            }
        });
        TerrainImageLoaderContainer.getInstance().startLoad();
        SurfaceLoaderContainer.getInstance().startLoad();
    }

    @Override
    public void onLoaded() {
        draw();
    }
}