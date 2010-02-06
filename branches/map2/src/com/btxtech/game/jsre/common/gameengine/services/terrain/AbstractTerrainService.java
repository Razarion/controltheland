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

package com.btxtech.game.jsre.common.gameengine.services.terrain;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.TerrainListener;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;

/**
 * User: beat
 * Date: 05.02.2010
 * Time: 22:28:01
 */
public class AbstractTerrainService implements TerrainService {
    private List<TerrainImagePosition> terrainImagePositions = new ArrayList<TerrainImagePosition>();
    private Map<Integer, TerrainImage> terrainImages = new HashMap<Integer, TerrainImage>();
    private ArrayList<TerrainListener> terrainListeners = new ArrayList<TerrainListener>();
    private TerrainSettings terrainSettings;

    public List<TerrainImagePosition> getTerrainImagePositions() {
        return terrainImagePositions;
    }

    public void setTerrainImagePositions(List<TerrainImagePosition> terrainImagePositions) {
        this.terrainImagePositions = terrainImagePositions;
    }

    protected void addTerrainImagePosition(TerrainImagePosition terrainImagePosition) {
        terrainImagePositions.add(terrainImagePosition);
    }

    protected void removeTerrainImagePosition(TerrainImagePosition terrainImagePosition) {
        terrainImagePositions.remove(terrainImagePosition);
    }

    public void setTerrainSettings(TerrainSettings terrainSettings) {
        this.terrainSettings = terrainSettings;
    }

    public TerrainSettings getTerrainSettings() {
        return terrainSettings;
    }

    public void addTerrainListener(TerrainListener terrainListener) {
        terrainListeners.add(terrainListener);
    }

    protected void fireTerrainChanged() {
        for (TerrainListener terrainListener : terrainListeners) {
            terrainListener.onTerrainChanged();
        }
    }

    public void setupTerrainImages(Collection<TerrainImage> terrainImages) {
        this.terrainImages.clear();
        for (TerrainImage terrainImage : terrainImages) {
            this.terrainImages.put(terrainImage.getId(), terrainImage);
        }
    }

    public List<TerrainImagePosition> getTerrainImagesInRegion(Rectangle absolutePxRectangle) {
        ArrayList<TerrainImagePosition> result = new ArrayList<TerrainImagePosition>();
        if (terrainSettings == null || terrainImagePositions == null) {
            return result;
        }
        Rectangle tileRect = convertToTilePosition(absolutePxRectangle);
        for (TerrainImagePosition terrainImagePosition : terrainImagePositions) {
            if (tileRect.adjoinsEclusive(getTerrainImagePositionRectangle(terrainImagePosition))) {
                result.add(terrainImagePosition);
            }
        }
        return result;
    }

    public Rectangle getTerrainImagePositionRectangle(TerrainImagePosition terrainImagePosition) {
        TerrainImage terrainImage = getTerrainImage(terrainImagePosition);
        return new Rectangle(terrainImagePosition.getTileIndex().getX(),
                terrainImagePosition.getTileIndex().getY(),
                terrainImage.getTileWidth(),
                terrainImage.getTileHeight());
    }

    public TerrainImage getTerrainImage(TerrainImagePosition terrainImagePosition) {
        TerrainImage terrainImage = terrainImages.get(terrainImagePosition.getImageId());
        if (terrainImage == null) {
            throw new IllegalArgumentException(this + " getTerrainImagePosRect(): image id does not exit: " + terrainImagePosition.getImageId());
        }
        return terrainImage;
    }

    public TerrainImagePosition getTerrainImagePosition(int absoluteX, int absoluteY) {
        if (terrainSettings == null || terrainImagePositions == null) {
            return null;
        }
        Index tileIndex = getTerrainTileIndexForAbsPosition(absoluteX, absoluteY);
        for (TerrainImagePosition terrainImagePosition : terrainImagePositions) {
            if (getTerrainImagePositionRectangle(terrainImagePosition).containsExclusive(tileIndex)) {
                return terrainImagePosition;
            }
        }
        return null;
    }

    public Index getTerrainTileIndexForAbsPosition(int x, int y) {
        return new Index(x / terrainSettings.getTileWidth(), y / terrainSettings.getTileHeight());
    }

    public int getTerrainTileIndexForAbsXPosition(int x) {
        return x / terrainSettings.getTileWidth();
    }

    public int getTerrainTileIndexForAbsYPosition(int y) {
        return y / terrainSettings.getTileHeight();
    }

    public Index getTerrainTileIndexForAbsPosition(Index absolutePos) {
        return new Index(absolutePos.getX() / terrainSettings.getTileWidth(), absolutePos.getY() / terrainSettings.getTileHeight());
    }

    public Index getAbsolutIndexForTerrainTileIndex(Index tileIndex) {
        return new Index(tileIndex.getX() * terrainSettings.getTileWidth(), tileIndex.getY() * terrainSettings.getTileHeight());
    }

    public Index getAbsolutIndexForTerrainTileIndex(int xTile, int yTile) {
        return new Index(xTile * terrainSettings.getTileWidth(), yTile * terrainSettings.getTileHeight());
    }

    public int getAbsolutXForTerrainTile(int xTile) {
        return xTile * terrainSettings.getTileWidth();
    }

    public int getAbsolutYForTerrainTile(int yTile) {
        return yTile * terrainSettings.getTileHeight();
    }

    public Rectangle convertToTilePosition(Rectangle rectangle) {
        Index start = getTerrainTileIndexForAbsPosition(rectangle.getStart());
        Index end = getTerrainTileIndexForAbsPosition(rectangle.getEnd());
        return new Rectangle(start, end);
    }

    public Rectangle convertToAbsolutePosition(Rectangle rectangle) {
        Index start = getAbsolutIndexForTerrainTileIndex(rectangle.getStart());
        Index end = getAbsolutIndexForTerrainTileIndex(rectangle.getEnd());
        return new Rectangle(start, end);
    }

        @Override
    // TODO move zp
    public List<Index> setupPathToDestination(Index start, Index destionation, int range) {
        Index destination = start.getPointWithDistance(range, destionation);
        ArrayList<Index> path = new ArrayList<Index>();
        path.add(destination);
        return path;
    }

    @Override
    // TODO move zp
    public List<Index> setupPathToDestination(Index start, Index destination) {
        ArrayList<Index> path = new ArrayList<Index>();
        path.add(destination);
        return path;
    }

    @Override
    // TODO move zp
    public boolean isFree(Index posititon, ItemType itemType) {
        // TODO
        return true;
    }

    @Deprecated
    // TODO move zp
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

}
