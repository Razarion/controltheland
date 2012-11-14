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

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainListener;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 05.02.2010
 * Time: 22:28:01
 */
public abstract class AbstractTerrainServiceImpl implements AbstractTerrainService {
    private TerrainTile[][] terrainTileField;
    private ArrayList<TerrainListener> terrainListeners = new ArrayList<TerrainListener>();
    private TerrainSettings terrainSettings;
    private Logger log = Logger.getLogger(AbstractTerrainServiceImpl.class.getName());

    protected abstract CommonTerrainImageService getCommonTerrainImageService();

    @Override
    public TerrainTile[][] getTerrainTileField() {
        return terrainTileField;
    }

    @Override
    public void iteratorOverAllTerrainTiles(Rectangle tileRect, TerrainTileEvaluator terrainTileEvaluator) {
        iteratorOverAllTerrainTiles(tileRect, terrainTileEvaluator, 1, 1);
    }

    @Override
    public void iteratorOverAllTerrainTiles(Rectangle tileRect, TerrainTileEvaluator terrainTileEvaluator, int xIncrease, int yIncrease) {
        if (terrainSettings == null || terrainTileField == null) {
            return;
        }
        if (tileRect == null) {
            tileRect = new Rectangle(0, 0, terrainSettings.getTileXCount(), terrainSettings.getTileYCount());
        }
        int startX = tileRect.getX();
        if (startX < 0) {
            startX = 0;
        }
        int startY = tileRect.getY();
        if (startY < 0) {
            startY = 0;
        }
        int endX = tileRect.getEndX();
        if(endX > terrainSettings.getTileXCount()) {
            endX = terrainSettings.getTileXCount();
        }
        int endY = tileRect.getEndY();
        if(endY > terrainSettings.getTileYCount()) {
            endY = terrainSettings.getTileYCount();
        }

        for (int x = startX; x < endX; x += xIncrease) {
            for (int y = startY; y < endY; y += yIncrease) {
                try {
                    TerrainTile terrainTile = terrainTileField[x][y];
                    terrainTileEvaluator.evaluate(x, y, terrainTile);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "AbstractTerrainServiceImpl.iteratorOverAllTerrainTiles()", e);
                }
            }
        }

    }

    public void setTerrainSettings(TerrainSettings terrainSettings) {
        this.terrainSettings = terrainSettings;
    }

    @Override
    public TerrainSettings getTerrainSettings() {
        return terrainSettings;
    }

    @Override
    public void addTerrainListener(TerrainListener terrainListener) {
        terrainListeners.add(terrainListener);
    }

    public void fireTerrainChanged() {
        for (TerrainListener terrainListener : terrainListeners) {
            terrainListener.onTerrainChanged();
        }
    }

    public Collection<SurfaceType> getSurfaceTypeTilesInRegion(Rectangle absRectangle) {
        ArrayList<SurfaceType> surfaceTypes = new ArrayList<SurfaceType>();
        Rectangle tileRect = convertToTilePositionRoundUp(absRectangle);

        for (int x = tileRect.getX(); x < tileRect.getEndX(); x++) {
            for (int y = tileRect.getY(); y < tileRect.getEndY(); y++) {
                surfaceTypes.add(getSurfaceType(new Index(x, y)));
            }

        }
        return surfaceTypes;
    }

    @Override
    @Deprecated
    public Index getTerrainTileIndexForAbsPosition(int x, int y) {
        return new Index(x / Constants.TERRAIN_TILE_WIDTH, y / Constants.TERRAIN_TILE_HEIGHT);
    }

    @Override
    @Deprecated
    public int getTerrainTileIndexForAbsXPosition(int x) {
        return x / Constants.TERRAIN_TILE_WIDTH;
    }

    @Override
    @Deprecated
    public int getTerrainTileIndexForAbsYPosition(int y) {
        return y / Constants.TERRAIN_TILE_HEIGHT;
    }

    @Override
    @Deprecated
    public Index getTerrainTileIndexForAbsPosition(Index absolutePos) {
        return new Index(absolutePos.getX() / Constants.TERRAIN_TILE_WIDTH, absolutePos.getY() / Constants.TERRAIN_TILE_HEIGHT);
    }

    @Override
    @Deprecated
    public Index getAbsolutIndexForTerrainTileIndex(Index tileIndex) {
        return new Index(tileIndex.getX() * Constants.TERRAIN_TILE_WIDTH, tileIndex.getY() * Constants.TERRAIN_TILE_HEIGHT);
    }

    @Override
    @Deprecated
    public Index getTerrainTileIndexForAbsPositionRoundUp(Index absolutePos) {
        return new Index((int) Math.ceil((double) absolutePos.getX() / (double) Constants.TERRAIN_TILE_WIDTH),
                (int) Math.ceil((double) absolutePos.getY() / (double) Constants.TERRAIN_TILE_HEIGHT));
    }

    @Override
    @Deprecated
    public Index getAbsolutIndexForTerrainTileIndex(int xTile, int yTile) {
        return new Index(xTile * Constants.TERRAIN_TILE_WIDTH, yTile * Constants.TERRAIN_TILE_HEIGHT);
    }

    @Override
    @Deprecated
    public int getAbsolutXForTerrainTile(int xTile) {
        return xTile * Constants.TERRAIN_TILE_WIDTH;
    }

    @Override
    @Deprecated
    public int getAbsolutYForTerrainTile(int yTile) {
        return yTile * Constants.TERRAIN_TILE_HEIGHT;
    }

    @Override
    @Deprecated
    public Rectangle convertToTilePosition(Rectangle rectangle) {
        Index start = getTerrainTileIndexForAbsPosition(rectangle.getStart());
        Index end = getTerrainTileIndexForAbsPosition(rectangle.getEnd());
        return new Rectangle(start, end);
    }

    @Override
    @Deprecated
    public Rectangle convertToTilePositionRoundUp(Rectangle rectangle) {
        Index start = getTerrainTileIndexForAbsPosition(rectangle.getStart());
        Index end = getTerrainTileIndexForAbsPositionRoundUp(rectangle.getEnd());
        return new Rectangle(start, end);
    }

    @Override
    @Deprecated
    public Rectangle convertToAbsolutePosition(Rectangle rectangle) {
        Index start = getAbsolutIndexForTerrainTileIndex(rectangle.getStart());
        Index end = getAbsolutIndexForTerrainTileIndex(rectangle.getEnd());
        return new Rectangle(start, end);
    }

    @Override
    public boolean isFree(Index middlePoint, int itemFreeWidth, int itemFreeHeight, Collection<SurfaceType> allowedSurfaces) {
        int x = middlePoint.getX() - itemFreeWidth / 2;
        int y = middlePoint.getY() - itemFreeHeight / 2;

        if (x < 0 || y < 0) {
            return false;
        }
        if (x + itemFreeWidth > terrainSettings.getPlayFieldXSize()) {
            return false;
        }
        if (y + itemFreeHeight > terrainSettings.getPlayFieldYSize()) {
            return false;
        }
        Rectangle rectangle = new Rectangle(x, y, itemFreeWidth, itemFreeHeight);
        Collection<SurfaceType> surfaceTypes = getSurfaceTypeTilesInRegion(rectangle);
        return !surfaceTypes.isEmpty() && (allowedSurfaces == null || allowedSurfaces.containsAll(surfaceTypes));
    }

    @Override
    public boolean isFree(Index middlePoint, ItemType itemType) {
        return isFree(middlePoint,
                itemType.getBoundingBox().getRadius(),
                itemType.getBoundingBox().getRadius(),
                itemType.getTerrainType().getSurfaceTypes());
    }

    @Override
    public boolean isFreeZeroSize(Index point, ItemType itemType) {
        return isFree(point, 1, 1, itemType.getTerrainType().getSurfaceTypes());
    }

    @Override
    public SurfaceType getSurfaceTypeAbsolute(Index absoluteIndex) {
        Index tileIndex = getTerrainTileIndexForAbsPosition(absoluteIndex);
        return getSurfaceType(tileIndex);
    }

    @Override
    public SurfaceType getSurfaceType(Index tileIndex) {
        if (tileIndex == null || tileIndex.getX() >= terrainSettings.getPlayFieldXSize() || tileIndex.getY() >= terrainSettings.getPlayFieldYSize()) {
            return null;
        }
        TerrainTile terrainTile = terrainTileField[tileIndex.getX()][tileIndex.getY()];
        if (terrainTile == null) {
            return SurfaceType.NONE;
        } else {
            return terrainTile.getSurfaceType();
        }
    }

    public Rectangle getRectangle4Widget(Widget widget) {
        return new Rectangle(widget.getAbsoluteLeft(), widget.getAbsoluteTop(), widget.getOffsetWidth(), widget.getOffsetHeight());
    }

    @Override
    public Index correctPosition(SyncItem syncItem, Index position) {
        int x;
        int radius = syncItem.getSyncItemArea().getBoundingBox().getRadius();
        if (position.getX() - radius < 0) {
            log.warning("Corrected min x position for: " + syncItem);
            x = radius;
        } else if (position.getX() + radius > terrainSettings.getPlayFieldXSize()) {
            log.warning("Corrected max x position for: " + syncItem);
            x = terrainSettings.getPlayFieldXSize() - radius;
        } else {
            x = position.getX();
        }
        int y;
        if (position.getY() - radius < 0) {
            log.warning("Corrected min y position for: " + syncItem);
            y = radius;
        } else if (position.getY() + radius > terrainSettings.getPlayFieldYSize()) {
            log.warning("Corrected max y position for: " + syncItem);
            y = terrainSettings.getPlayFieldYSize() - radius;
        } else {
            y = position.getY();
        }
        return new Index(x, y);
    }

    @Override
    public void createTerrainTileField(Collection<TerrainImagePosition> terrainImagePositions, Collection<SurfaceRect> surfaceRects) {
        if (terrainSettings == null) {
            throw new IllegalStateException("terrainSettings == null");
        }
        log.info("Starting setup collision service");
        long time = System.currentTimeMillis();

        terrainTileField = new TerrainTile[terrainSettings.getTileXCount()][terrainSettings.getTileYCount()];

        for (SurfaceRect surfaceRect : surfaceRects) {
            if (surfaceRect.getTileWidth() == 0 || surfaceRect.getTileHeight() == 0) {
                continue;
            }
            SurfaceImage surfaceImage = getCommonTerrainImageService().getSurfaceImage(surfaceRect.getSurfaceImageId());
            SurfaceType surfaceType = surfaceImage.getSurfaceType();
            int endX = surfaceRect.getTileWidth() + surfaceRect.getTileIndex().getX();
            for (int x = surfaceRect.getTileIndex().getX(); x < endX; x++) {
                if (x > terrainSettings.getTileXCount() - 1) {
                    continue;
                }
                int endY = surfaceRect.getTileHeight() + surfaceRect.getTileIndex().getY();
                for (int y = surfaceRect.getTileIndex().getY(); y < endY; y++) {
                    if (y > terrainSettings.getTileYCount() - 1) {
                        continue;
                    }
                    try {
                        terrainTileField[x][y] = new TerrainTile(surfaceType, true, surfaceRect.getSurfaceImageId(), x, y);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        log.log(Level.SEVERE, "AbstractTerrainServiceImpl.createTerrainTileField()", e);
                    }
                }
            }
        }
        Collection<TerrainImagePosition> layer2 = new ArrayList<TerrainImagePosition>();
        for (TerrainImagePosition terrainImagePosition : terrainImagePositions) {
            switch (terrainImagePosition.getzIndex()) {
                case LAYER_1:
                    fillTerrainTypeMap(terrainTileField, terrainImagePosition);
                    break;
                case LAYER_2:
                    layer2.add(terrainImagePosition);
                    break;
                default:
                    log.warning("AbstractTerrainServiceImpl.createTerrainTileField() z Index not supported: " + terrainImagePosition.getzIndex());
            }
        }

        for (TerrainImagePosition terrainImagePosition : layer2) {
            fillTerrainTypeMap(terrainTileField, terrainImagePosition);
        }
        log.info("Time needed to setup terrain field: " + (System.currentTimeMillis() - time) + "ms");
    }

    private void fillTerrainTypeMap(TerrainTile[][] terrainTileField, TerrainImagePosition terrainImagePosition) {
        TerrainImage terrainImage = getCommonTerrainImageService().getTerrainImage(terrainImagePosition.getImageId());
        Index imageIndex = terrainImagePosition.getTileIndex();
        SurfaceType[][] surfaceTypes = terrainImage.getSurfaceTypes();
        for (int x = 0; x < surfaceTypes.length; x++) {
            int absX = x + imageIndex.getX();
            if (absX > terrainSettings.getTileXCount() - 1) {
                continue;
            }
            for (int y = 0; y < surfaceTypes[x].length; y++) {
                int absY = y + imageIndex.getY();
                if (absY > terrainSettings.getTileYCount() - 1) {
                    continue;
                }
                TerrainTile terrainTile = terrainTileField[absX][absY];
                if (terrainTile != null) {
                    terrainTile.setSurfaceType(surfaceTypes[x][y], false, terrainImage.getId(), x, y);
                } else {
                    terrainTileField[absX][absY] = new TerrainTile(surfaceTypes[x][y], false, terrainImage.getId(), x, y);
                }
            }
        }
    }



}
