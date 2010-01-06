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

import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainService;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.TerrainUtil;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.GwtCommon;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.widgetideas.graphics.client.ImageLoader;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: beat
 * Date: 30.11.2009
 * Time: 22:58:26
 */
public class TerrainHandler implements TerrainService {
    private int tileXCount = 50;
    private int tileYCount = 50;
    private int terrainWidth;
    private int terrainHeight;
    private int[][] terrainField;
    private Collection<Integer> passableTerrainTileIds;
    private HashMap<Integer, ImageElement> terrainTileImages = new HashMap<Integer, ImageElement>();    
    private ArrayList<TerrainListener> terrainListeners = new ArrayList<TerrainListener>();

    public void setupTerrain(int[][] terrainField, Collection<Integer> passableTerrainTileIds) {
        this.terrainField = terrainField;
        this.passableTerrainTileIds = passableTerrainTileIds;

        tileXCount = terrainField.length;
        tileYCount = terrainField[0].length;
        terrainWidth = Constants.TILE_WIDTH * tileXCount;
        terrainHeight = Constants.TILE_HEIGHT * tileYCount;

        loadTilesAndDrawMap();
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
        return true;
    }

    public boolean isTerrainPassable(Index absolutePos) {
        if (absolutePos == null) {
            return false;
        }
        Index tilePos = TerrainUtil.getTerrainTileIndexForAbsPosition(absolutePos);
        if (tilePos.getX() >= tileXCount || tilePos.getY() >= tileYCount) {
            return false;
        }

        int tileId = terrainField[tilePos.getX()][tilePos.getY()];
        return passableTerrainTileIds.contains(tileId);
    }

    public int getTileXCount() {
        return tileXCount;
    }

    public int getTileYCount() {
        return tileYCount;
    }

    public int getTerrainWidth() {
        return terrainWidth;
    }

    public int getTerrainHeight() {
        return terrainHeight;
    }

    public int getTileId(int x, int y) {
        return terrainField[x][y];
    }

    public boolean isLoaded() {
        return terrainField != null;
    }

    public int[][] getTerrainField() {
        return terrainField;
    }

    public ImageElement getTileImageElement(int tileId) {
        return terrainTileImages.get(tileId);
    }

    public void addTerrainListener(TerrainListener terrainListener) {
       terrainListeners.add(terrainListener);
    }

    public void loadTilesAndDrawMap() {
        // Get used tiles
        final ArrayList<Integer> usedTileIds = new ArrayList<Integer>();
        for (int x = 0; x < getTileXCount(); x++) {
            for (int y = 0; y < getTileYCount(); y++) {
                int tileId = getTileId(x, y);
                if (!usedTileIds.contains(tileId)) {
                    usedTileIds.add(tileId);
                }
            }
        }

        // Load all images
        final ArrayList<String> urls = new ArrayList<String>();
        for (int tileId : usedTileIds) {
            String url = ImageHandler.getTerrainImageUrl(tileId);
            urls.add(url);
        }

        ImageLoader.loadImages(urls.toArray(new String[urls.size()]), new ImageLoader.CallBack() {

            @Override
            public void onImagesLoaded(ImageElement[] imageElements) {
                try {
                    for (int i = 0; i < imageElements.length; i++) {
                        terrainTileImages.put(usedTileIds.get(i), imageElements[i]);
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
}
