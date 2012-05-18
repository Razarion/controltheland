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

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainListener;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 05.02.2010
 * Time: 22:28:01
 */
public abstract class AbstractTerrainServiceImpl implements AbstractTerrainService {
    private Collection<TerrainImagePosition> terrainImagePositions = new ArrayList<TerrainImagePosition>();
    private Collection<SurfaceRect> surfaceRects = new ArrayList<SurfaceRect>();
    private Map<Integer, TerrainImage> terrainImages = new HashMap<Integer, TerrainImage>();
    private Map<Integer, SurfaceImage> surfaceImages = new HashMap<Integer, SurfaceImage>();
    private ArrayList<TerrainListener> terrainListeners = new ArrayList<TerrainListener>();
    private TerrainImageBackground terrainImageBackground;
    private TerrainSettings terrainSettings;
    private Logger log = Logger.getLogger(AbstractTerrainServiceImpl.class.getName());

    @Override
    public Collection<TerrainImagePosition> getTerrainImagePositions() {
        return terrainImagePositions;
    }

    public void setTerrainImagePositions(Collection<TerrainImagePosition> terrainImagePositions) {
        this.terrainImagePositions = terrainImagePositions;
    }

    protected void addTerrainImagePosition(TerrainImagePosition terrainImagePosition) {
        terrainImagePositions.add(terrainImagePosition);
    }

    @Override
    public Collection<SurfaceRect> getSurfaceRects() {
        return surfaceRects;
    }

    public void setSurfaceRects(Collection<SurfaceRect> surfaceRects) {
        this.surfaceRects = surfaceRects;
    }

    public void addSurfaceRect(SurfaceRect surfaceRect) {
        surfaceRects.add(surfaceRect);
    }

    public TerrainImageBackground getTerrainImageBackground() {
        return terrainImageBackground;
    }

    public void setTerrainImageBackground(TerrainImageBackground terrainImageBackground) {
        this.terrainImageBackground = terrainImageBackground;
    }

    protected void removeTerrainImagePosition(TerrainImagePosition terrainImagePosition) {
        terrainImagePositions.remove(terrainImagePosition);
    }

    protected void removeSurfaceRect(SurfaceRect surfaceRect) {
        surfaceRects.remove(surfaceRect);
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

    protected void fireTerrainChanged() {
        for (TerrainListener terrainListener : terrainListeners) {
            terrainListener.onTerrainChanged();
        }
    }

    protected void setupImages(Collection<SurfaceImage> surfaceImages, Collection<TerrainImage> terrainImages) {
        clearSurfaceImages();
        for (SurfaceImage surfaceImage : surfaceImages) {
            putSurfaceImage(surfaceImage);
        }
        clearTerrainImages();
        for (TerrainImage terrainImage : terrainImages) {
            putTerrainImage(terrainImage);
        }
    }

    protected void clearTerrainImages() {
        terrainImages.clear();
    }

    protected void putTerrainImage(TerrainImage terrainImage) {
        terrainImages.put(terrainImage.getId(), terrainImage);
    }

    protected void clearSurfaceImages() {
        surfaceImages.clear();
    }

    protected void putSurfaceImage(SurfaceImage surfaceImage) {
        surfaceImages.put(surfaceImage.getImageId(), surfaceImage);
    }

    @Override
    public Collection<TerrainImage> getTerrainImages() {
        return new ArrayList<TerrainImage>(terrainImages.values());
    }

    @Override
    public SurfaceImage getSurfaceImage(SurfaceRect surfaceRect) {
        SurfaceImage getSurfaceImage = surfaceImages.get(surfaceRect.getSurfaceImageId());
        if (getSurfaceImage == null) {
            throw new IllegalArgumentException(this + " getSurfaceImage(): image id does not exit: " + surfaceRect.getSurfaceImageId());
        }
        return getSurfaceImage;
    }

    @Override
    public Collection<SurfaceImage> getSurfaceImages() {
        return new ArrayList<SurfaceImage>(surfaceImages.values());
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
    public List<TerrainImagePosition> getTerrainImagesInRegion(Rectangle absRectangle) {
        ArrayList<TerrainImagePosition> result = new ArrayList<TerrainImagePosition>();
        if (terrainSettings == null || terrainImagePositions == null) {
            return result;
        }
        Rectangle tileRect = convertToTilePositionRoundUp(absRectangle);
        for (TerrainImagePosition terrainImagePosition : terrainImagePositions) {
            if (tileRect.adjoinsEclusive(getTerrainImagePositionRectangle(terrainImagePosition))) {
                result.add(terrainImagePosition);
            }
        }
        return result;
    }

    @Override
    public TerrainImagePosition getTerrainImagePosition(TerrainImagePosition.ZIndex zIndex, int absoluteX, int absoluteY) {
        if (terrainSettings == null || terrainImagePositions == null) {
            return null;
        }
        Index tileIndex = getTerrainTileIndexForAbsPosition(absoluteX, absoluteY);
        return getTerrainImagePosition(zIndex, tileIndex);
    }

    @Override
    public TerrainImagePosition getTerrainImagePosition(TerrainImagePosition.ZIndex zIndex, Index tileIndex) {
        // TODO slow!!!
        for (TerrainImagePosition terrainImagePosition : terrainImagePositions) {
            if (getTerrainImagePositionRectangle(terrainImagePosition).containsExclusive(tileIndex)) {
                if (terrainImagePosition.getzIndex() == zIndex) {
                    return terrainImagePosition;
                }
            }
        }
        return null;
    }

    @Override
    public TerrainImagePosition getTerrainImagePosition(Index tileIndex) {
        // TODO slow!!!
        TerrainImagePosition layer2Image = null;
        for (TerrainImagePosition terrainImagePosition : terrainImagePositions) {
            if (getTerrainImagePositionRectangle(terrainImagePosition).containsExclusive(tileIndex)) {
                if (terrainImagePosition.getzIndex() == TerrainImagePosition.ZIndex.LAYER_2) {
                    return terrainImagePosition;
                }
                layer2Image = terrainImagePosition;
            }
        }
        return layer2Image;
    }

    @Override
    public Rectangle getTerrainImagePositionRectangle(TerrainImagePosition terrainImagePosition) {
        TerrainImage terrainImage = getTerrainImage(terrainImagePosition);
        return new Rectangle(terrainImagePosition.getTileIndex().getX(),
                terrainImagePosition.getTileIndex().getY(),
                terrainImage.getTileWidth(),
                terrainImage.getTileHeight());
    }

    @Override
    public TerrainImage getTerrainImage(TerrainImagePosition terrainImagePosition) {
        TerrainImage terrainImage = terrainImages.get(terrainImagePosition.getImageId());
        if (terrainImage == null) {
            throw new IllegalArgumentException(this + " getTerrainImagePosRect(): image id does not exit: " + terrainImagePosition.getImageId());
        }
        return terrainImage;
    }

    @Override
    public SurfaceRect getSurfaceRect(int absoluteX, int absoluteY) {
        if (terrainSettings == null || surfaceRects == null) {
            return null;
        }
        Index tileIndex = getTerrainTileIndexForAbsPosition(absoluteX, absoluteY);
        return getSurfaceRect(tileIndex);
    }

    @Override
    public SurfaceRect getSurfaceRect(Index tileIndex) {
        // TODO slow!!!
        for (SurfaceRect surfaceRect : surfaceRects) {
            if (surfaceRect.getTileRectangle().containsExclusive(tileIndex)) {
                return surfaceRect;
            }
        }
        return null;
    }

    public List<SurfaceRect> getSurfaceRectsInRegion(Rectangle absRectangle) {
        ArrayList<SurfaceRect> result = new ArrayList<SurfaceRect>();
        if (terrainSettings == null || surfaceRects == null) {
            return result;
        }
        Rectangle tileRect = convertToTilePositionRoundUp(absRectangle);
        for (SurfaceRect surfaceRect : surfaceRects) {
            if (tileRect.adjoinsEclusive(surfaceRect.getTileRectangle())) {
                result.add(surfaceRect);
            }
        }
        return result;
    }


    @Override
    public Index getTerrainTileIndexForAbsPosition(int x, int y) {
        return new Index(x / terrainSettings.getTileWidth(), y / terrainSettings.getTileHeight());
    }

    @Override
    public int getTerrainTileIndexForAbsXPosition(int x) {
        return x / terrainSettings.getTileWidth();
    }

    @Override
    public int getTerrainTileIndexForAbsYPosition(int y) {
        return y / terrainSettings.getTileHeight();
    }

    @Override
    public Index getTerrainTileIndexForAbsPosition(Index absolutePos) {
        return new Index(absolutePos.getX() / terrainSettings.getTileWidth(), absolutePos.getY() / terrainSettings.getTileHeight());
    }

    @Override
    public Index getAbsolutIndexForTerrainTileIndex(Index tileIndex) {
        return new Index(tileIndex.getX() * terrainSettings.getTileWidth(), tileIndex.getY() * terrainSettings.getTileHeight());
    }

    @Override
    public Index getTerrainTileIndexForAbsPositionRoundUp(Index absolutePos) {
        return new Index((int) Math.ceil((double) absolutePos.getX() / (double) terrainSettings.getTileWidth()),
                (int) Math.ceil((double) absolutePos.getY() / (double) terrainSettings.getTileHeight()));
    }

    @Override
    public Index getAbsolutIndexForTerrainTileIndex(int xTile, int yTile) {
        return new Index(xTile * terrainSettings.getTileWidth(), yTile * terrainSettings.getTileHeight());
    }

    @Override
    public int getAbsolutXForTerrainTile(int xTile) {
        return xTile * terrainSettings.getTileWidth();
    }

    @Override
    public int getAbsolutYForTerrainTile(int yTile) {
        return yTile * terrainSettings.getTileHeight();
    }

    @Override
    public Rectangle convertToTilePosition(Rectangle rectangle) {
        Index start = getTerrainTileIndexForAbsPosition(rectangle.getStart());
        Index end = getTerrainTileIndexForAbsPosition(rectangle.getEnd());
        return new Rectangle(start, end);
    }

    @Override
    public Rectangle convertToTilePositionRoundUp(Rectangle rectangle) {
        Index start = getTerrainTileIndexForAbsPosition(rectangle.getStart());
        Index end = getTerrainTileIndexForAbsPositionRoundUp(rectangle.getEnd());
        return new Rectangle(start, end);
    }

    @Override
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
                itemType.getBoundingBox().getWidth(),
                itemType.getBoundingBox().getHeight(),
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
        TerrainImagePosition terrainImagePosition = getTerrainImagePosition(tileIndex);
        if (terrainImagePosition != null) {
            TerrainImage terrainImage = getTerrainImage(terrainImagePosition);
            Index imgPosIndex = tileIndex.sub(terrainImagePosition.getTileIndex());
            return terrainImage.getSurfaceType(imgPosIndex.getX(), imgPosIndex.getY());
        } else {
            SurfaceRect surfaceRect = getSurfaceRect(tileIndex);
            if (surfaceRect != null) {
                return getSurfaceImage(surfaceRect).getSurfaceType();
            } else {
                return SurfaceType.NONE;
            }
        }
    }

    public Rectangle getRectangle4Widget(Widget widget) {
        return new Rectangle(widget.getAbsoluteLeft(), widget.getAbsoluteTop(), widget.getOffsetWidth(), widget.getOffsetHeight());
    }

    @Override
    public Index correctPosition(SyncItem syncItem, Index position) {
        int x;
        int halfWidth = syncItem.getSyncItemArea().getBoundingBox().getWidth() / 2;
        if (position.getX() - halfWidth < 0) {
            log.warning("Corrected min x position for: " + syncItem);
            x = halfWidth;
        } else if (position.getX() + halfWidth > terrainSettings.getPlayFieldXSize()) {
            log.warning("Corrected max x position for: " + syncItem);
            x = terrainSettings.getPlayFieldXSize() - halfWidth;
        } else {
            x = position.getX();
        }
        int y;
        int halfHeight = syncItem.getSyncItemArea().getBoundingBox().getHeight() / 2;
        if (position.getY() - halfHeight < 0) {
            log.warning("Corrected min y position for: " + syncItem);
            y = halfHeight;
        } else if (position.getY() + halfHeight > terrainSettings.getPlayFieldYSize()) {
            log.warning("Corrected max y position for: " + syncItem);
            y = terrainSettings.getPlayFieldYSize() - halfHeight;
        } else {
            y = position.getY();
        }
        return new Index(x, y);
    }

    @Override
    public Map<TerrainType, boolean[][]> createSurfaceTypeField() {
        if (terrainSettings == null) {
            throw new IllegalStateException("terrainSettings == null");
        }

        Map<TerrainType, boolean[][]> terrainTypeMap = new HashMap<TerrainType, boolean[][]>();
        for (TerrainType terrainType : TerrainType.values()) {
            terrainTypeMap.put(terrainType, new boolean[terrainSettings.getTileXCount()][terrainSettings.getTileYCount()]);
        }

        for (SurfaceRect surfaceRect : surfaceRects) {
            SurfaceType surfaceType = surfaceImages.get(surfaceRect.getSurfaceImageId()).getSurfaceType();
            Collection<TerrainType> allowedTerrainTypes = TerrainType.getAllowedTerrainType(surfaceType);
            for (TerrainType terrainType : allowedTerrainTypes) {
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
                        terrainTypeMap.get(terrainType)[x][y] = true;
                    }
                }
            }
        }
        Collection<TerrainImagePosition> layer2 = new ArrayList<TerrainImagePosition>();
        for (TerrainImagePosition terrainImagePosition : terrainImagePositions) {
            switch (terrainImagePosition.getzIndex()) {
                case LAYER_1:
                    fillTerrainTypeMap(terrainTypeMap, terrainImagePosition);
                    break;
                case LAYER_2:
                    layer2.add(terrainImagePosition);
                    break;
                default:
                    log.warning("AbstractTerrainServiceImpl.createSurfaceTypeField() z Index not supported: " + terrainImagePosition.getzIndex());
            }
        }

        for (TerrainImagePosition terrainImagePosition : layer2) {
            fillTerrainTypeMap(terrainTypeMap, terrainImagePosition);
        }

        return terrainTypeMap;
    }

    private void fillTerrainTypeMap(Map<TerrainType, boolean[][]> terrainTypeMap, TerrainImagePosition terrainImagePosition) {
        TerrainImage terrainImage = terrainImages.get(terrainImagePosition.getImageId());
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

                for (TerrainType terrainType : TerrainType.values()) {
                    terrainTypeMap.get(terrainType)[absX][absY] = terrainType.allowSurfaceType(surfaceTypes[x][y]);

                }
            }
        }
    }
}
