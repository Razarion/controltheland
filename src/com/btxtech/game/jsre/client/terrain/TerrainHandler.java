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

package com.btxtech.game.jsre.client.terrain;

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.widgetideas.graphics.client.ImageLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * User: beat
 * Date: 30.11.2009
 * Time: 22:58:26
 */
public class TerrainHandler implements TerrainService {
    private HashMap<Integer, ImageElement> terrainTileImages = new HashMap<Integer, ImageElement>();
    private ArrayList<TerrainListener> terrainListeners = new ArrayList<TerrainListener>();
    private TerrainSettings terrainSettings;
    private ImageElement backgroundImage;
    private List<TerrainImagePosition> terrainImagePositions;

    public void setupTerrain(TerrainSettings terrainSettings) {
        this.terrainSettings = terrainSettings;
        loadBackgroundAndDrawMap();
    }

    public void setupTerrainImages(List<TerrainImagePosition> terrainImagePositions) {
        this.terrainImagePositions = terrainImagePositions;
        loadImagesAndDrawMap();
    }

    public TerrainSettings getTerrainSettings() {
        return terrainSettings;
    }

    @Override
    public List<Index> setupPathToDestination(Index start, Index destionation, int range) {
        Index destination = start.getPointWithDistance(range, destionation);
        ArrayList<Index> path = new ArrayList<Index>();
        path.add(destination);
        return path;
    }

    @Override
    public List<Index> setupPathToDestination(Index start, Index destination) {
        ArrayList<Index> path = new ArrayList<Index>();
        path.add(destination);
        return path;
    }

    @Override
    public boolean isFree(Index posititon, ItemType itemType) {
        // TODO
        return true;
    }

    @Deprecated
    public boolean isTerrainPassable(Index absolutePos) {
        // TODO
        /* if (absolutePos == null) {
            return false;
        }
        Index tilePos = TerrainUtil.getTerrainTileIndexForAbsPosition(absolutePos);
        if (tilePos.getX() >= tileXCount || tilePos.getY() >= tileYCount) {
            return false;
        }

        int tileId = terrainField[tilePos.getX()][tilePos.getY()];
        return passableTerrainTileIds.contains(tileId);*/
        return true;
    }

    @Deprecated
    public int getTileXCount() {
        return 0;
    }

    @Deprecated
    public int getTileYCount() {
        return 0;
    }

    @Deprecated
    public int getTerrainWidth() {
        return 0;
    }

    @Deprecated
    public int getTerrainHeight() {
        return 0;
    }

    @Deprecated
    public int getTileId(int x, int y) {
        return 0;
    }

    @Deprecated
    public int[][] getTerrainField() {
        return null;
    }

    public ImageElement getTileImageElement(int tileId) {
        ImageElement imageElement = terrainTileImages.get(tileId);
        if (imageElement == null) {
            loadImagesAndDrawMap();
            return null;
        }
        return imageElement;
    }

    public void addTerrainListener(TerrainListener terrainListener) {
        terrainListeners.add(terrainListener);
    }

    public ImageElement getBackgroundImage() {
        return backgroundImage;
    }

    private void loadBackgroundAndDrawMap() {
        ImageLoader.loadImages(new String[]{ImageHandler.getTerrainBackgroundUrl()}, new ImageLoader.CallBack() {

            @Override
            public void onImagesLoaded(ImageElement[] imageElements) {
                try {
                    backgroundImage = imageElements[0];
                    for (TerrainListener terrainListener : terrainListeners) {
                        terrainListener.onTerrainChanged();
                    }
                } catch (Throwable throwable) {
                    GwtCommon.handleException(throwable);
                }
            }
        });
    }

    private void loadImagesAndDrawMap() {
        ArrayList<String> urls = new ArrayList<String>();
        for (TerrainImagePosition terrainImagePosition : terrainImagePositions) {
            urls.add(ImageHandler.getTerrainImageUrl(terrainImagePosition.getImageId()));
        }
        ImageLoader.loadImages(urls.toArray(new String[urls.size()]), new ImageLoader.CallBack() {

            @Override
            public void onImagesLoaded(ImageElement[] imageElements) {
                try {
                    for (int i = 0; i < imageElements.length; i++) {
                        terrainTileImages.put(terrainImagePositions.get(i).getImageId(), imageElements[i]);
                    }
                    for (TerrainListener terrainListener : terrainListeners) {
                        terrainListener.onTerrainChanged();
                    }
                } catch (Throwable throwable) {
                    GwtCommon.handleException(throwable);
                }
            }
        });
    }

    public List<TerrainImagePosition> getTerrainImagesInRegion(Rectangle absolutePxRectangle) {
        ArrayList<TerrainImagePosition> result = new ArrayList<TerrainImagePosition>();
        if (terrainSettings == null || terrainImagePositions == null) {
            return result;
        }
        Rectangle tileRect = convertToTilePosition(absolutePxRectangle);
        for (TerrainImagePosition terrainImagePosition : terrainImagePositions) {
            if (tileRect.contains(terrainImagePosition.getTileIndex())) {
                result.add(terrainImagePosition);
            }
        }
        return result;
    }

    public void addNewTerrainImage(int absX, int absY, int imageId) {
        Index index = getTerrainTileIndexForAbsPosition(absX, absY);
        terrainImagePositions.add(new TerrainImagePosition(index, imageId));
        for (TerrainListener terrainListener : terrainListeners) {
            terrainListener.onTerrainChanged();
        }
    }


    // TODO also used on the server -> move to super class
    public Index getTerrainTileIndexForAbsPosition(int x, int y) {
        return new Index(x / terrainSettings.getTileWidth(), y / terrainSettings.getTileHeight());
    }

    // TODO also used on the server -> move to super class
    public Index getTerrainTileIndexForAbsPosition(Index absolutePos) {
        return new Index(absolutePos.getX() / terrainSettings.getTileWidth(), absolutePos.getY() / terrainSettings.getTileHeight());
    }

    // TODO also used on the server -> move to super class
    public Index getAbsolutIndexForTerrainTileIndex(Index tileIndex) {
        return new Index(tileIndex.getX() * terrainSettings.getTileWidth(), tileIndex.getY() * terrainSettings.getTileHeight());
    }

    // TODO also used on the server -> move to super class
    public Index getAbsolutIndexForTerrainTileIndex(int xTile, int yTile) {
        return new Index(xTile * terrainSettings.getTileWidth(), yTile * terrainSettings.getTileHeight());
    }

    // TODO also used on the server -> move to super class
    public Rectangle convertToTilePosition(Rectangle rectangle) {
        Index start = getTerrainTileIndexForAbsPosition(rectangle.getStart());
        Index end = getTerrainTileIndexForAbsPosition(rectangle.getEnd());
        return new Rectangle(start, end);
    }
}
