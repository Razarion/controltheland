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

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainListener;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

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
        if (endX > terrainSettings.getTileXCount()) {
            endX = terrainSettings.getTileXCount();
        }
        int endY = tileRect.getEndY();
        if (endY > terrainSettings.getTileYCount()) {
            endY = terrainSettings.getTileYCount();
        }

        for (int x = startX; x < endX; x += xIncrease) {
            for (int y = startY; y < endY; y += yIncrease) {
                try {
                    TerrainTile terrainTile = terrainTileField[x][y];
                    terrainTileEvaluator.evaluate(x, y, terrainTile);
                } catch (Exception e) {
                    ClientExceptionHandler.handleExceptionOnlyOnce("AbstractTerrainServiceImpl.iteratorOverAllTerrainTiles()", e);
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
        Rectangle tileRect = TerrainUtil.convertToTilePositionRoundUp(absRectangle);

        for (int x = tileRect.getX(); x < tileRect.getEndX(); x++) {
            for (int y = tileRect.getY(); y < tileRect.getEndY(); y++) {
                surfaceTypes.add(getSurfaceType(new Index(x, y)));
            }

        }
        return surfaceTypes;
    }

    @Override
    public boolean isFree(Index middlePoint, int radius, Collection<SurfaceType> allowedSurfaces) {
        int x = middlePoint.getX() - radius;
        int y = middlePoint.getY() - radius;

        if (x < 0 || y < 0) {
            return false;
        }
        if (x + radius * 2 > terrainSettings.getPlayFieldXSize()) {
            return false;
        }
        if (y + radius * 2 > terrainSettings.getPlayFieldYSize()) {
            return false;
        }
        Rectangle rectangle = new Rectangle(x, y, radius * 2, radius * 2);
        Collection<SurfaceType> surfaceTypes = getSurfaceTypeTilesInRegion(rectangle);
        return !surfaceTypes.isEmpty() && (allowedSurfaces == null || allowedSurfaces.containsAll(surfaceTypes));
    }

    @Override
    public boolean isFree(Index middlePoint, ItemType itemType) {
        return isFree(middlePoint,
                itemType.getBoundingBox().getRadius(),
                itemType.getTerrainType().getSurfaceTypes());
    }

    @Override
    public SurfaceType getSurfaceTypeAbsolute(Index absoluteIndex) {
        Index tileIndex = TerrainUtil.getTerrainTileIndexForAbsPosition(absoluteIndex);
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

    @Override
    public Index correctPosition(SyncItem syncItem, Index position) {
        int radius = syncItem.getSyncItemArea().getBoundingBox().getRadius();
        Index correctedPosition = correctPosition(radius, position);
        if (!correctedPosition.equals(position)) {
            log.warning("Position for SyncItem has been corrected. Before: " + position + ". Corrected: " + correctedPosition + ". SyncItem: " + syncItem);
        }
        return correctedPosition;
    }

    @Override
    public Index correctPosition(int radius, Index position) {
        int x;
        if (position.getX() - radius < 0) {
            x = radius;
        } else if (position.getX() + radius > terrainSettings.getPlayFieldXSize()) {
            x = terrainSettings.getPlayFieldXSize() - radius;
        } else {
            x = position.getX();
        }
        int y;
        if (position.getY() - radius < 0) {
            y = radius;
        } else if (position.getY() + radius > terrainSettings.getPlayFieldYSize()) {
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
                        terrainTileField[x][y] = new TerrainTile(surfaceType, true, surfaceRect.getSurfaceImageId(), surfaceImage.getImageSpriteMapInfo(), x, y);
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
                    terrainTile.setSurfaceType(surfaceTypes[x][y], false, terrainImage.getId(), terrainImage.getImageSpriteMapInfo(), x, y);
                } else {
                    terrainTileField[absX][absY] = new TerrainTile(surfaceTypes[x][y], false, terrainImage.getId(), terrainImage.getImageSpriteMapInfo(), x, y);
                }
            }
        }
    }


}
