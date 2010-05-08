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

import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.InfoPanel;
import com.btxtech.game.jsre.client.OnlineBasePanel;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.TerrainListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 05.02.2010
 * Time: 22:28:01
 */
public class AbstractTerrainServiceImpl implements AbstractTerrainService {
    private Collection<TerrainImagePosition> terrainImagePositions = new ArrayList<TerrainImagePosition>();
    private Collection<SurfaceRect> surfaceRects = new ArrayList<SurfaceRect>();
    private Map<Integer, TerrainImage> terrainImages = new HashMap<Integer, TerrainImage>();
    private Map<Integer, SurfaceImage> surfaceImages = new HashMap<Integer, SurfaceImage>();
    private ArrayList<TerrainListener> terrainListeners = new ArrayList<TerrainListener>();
    private TerrainSettings terrainSettings;

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
    public TerrainImagePosition getTerrainImagePosition(int absoluteX, int absoluteY) {
        if (terrainSettings == null || terrainImagePositions == null) {
            return null;
        }
        Index tileIndex = getTerrainTileIndexForAbsPosition(absoluteX, absoluteY);
        return getTerrainImagePosition(tileIndex);
    }

    @Override
    public TerrainImagePosition getTerrainImagePosition(Index tileIndex) {
        for (TerrainImagePosition terrainImagePosition : terrainImagePositions) {
            if (getTerrainImagePositionRectangle(terrainImagePosition).containsExclusive(tileIndex)) {
                return terrainImagePosition;
            }
        }
        return null;
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
        for (SurfaceRect surfaceRect : surfaceRects) {
            if (surfaceRect.getTileRectangle().containsExclusive(tileIndex)) {
                return surfaceRect;
            }
        }
        return null;
    }

    public List<SurfaceRect> getSurfaceRectsInRegion(Rectangle absRectangle) {
        ArrayList<SurfaceRect> result = new ArrayList<SurfaceRect>();
        if (terrainSettings == null || terrainImagePositions == null) {
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
    public List<Index> setupPathToDestination(Index start, Index destionation, int range, TerrainType terrainType) {
        Index destination = start.getPointWithDistance(range, destionation);
        ArrayList<Index> path = new ArrayList<Index>();
        path.add(destination);
        return path;
    }

    @Override
    public List<Index> setupPathToDestination(Index start, Index destination, TerrainType terrainType) {
        ArrayList<Index> path = new ArrayList<Index>();
        path.add(destination);
        return path;
    }

    @Override
    public boolean isFree(Index point, int width, int height, Collection<SurfaceType> allowedSurfaces) {
        int x = point.getX() - width / 2;
        int y = point.getY() - height / 2;

        if (x < 0 || y < 0) {
            return false;
        }

        Rectangle rectangle = new Rectangle(x, y, width, height);
        Collection<SurfaceType> surfaceTypes = getSurfaceTypeTilesInRegion(rectangle);
        if (surfaceTypes.isEmpty()) {
            return false;
        }
        return allowedSurfaces.containsAll(surfaceTypes);
    }

    @Override
    public boolean isFree(Index posititon, ItemType itemType) {
        return isFree(posititon, itemType.getWidth(), itemType.getHeight(), itemType.getTerrainType().getSurfaceTypes());
    }

    @Override
    @Deprecated
    public boolean isTerrainPassable(Index posititon) {
        return posititon != null && !(posititon.getX() >= terrainSettings.getPlayFieldXSize() || posititon.getY() >= terrainSettings.getPlayFieldYSize())
                && getTerrainImagePosition(posititon.getX(), posititon.getY()) == null;
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

    public Index getAbsoluteFreeTerrainInRegion(Index absolutePos, int targetMinRange, int targetMaxRange, int edgeLength) {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            int x;
            int y;
            if (Random.nextBoolean()) {
                x = absolutePos.getX() + targetMinRange + Random.nextInt(targetMaxRange - targetMinRange);
            } else {
                x = absolutePos.getX() - targetMinRange - Random.nextInt(targetMaxRange - targetMinRange);
            }
            if (Random.nextBoolean()) {
                y = absolutePos.getY() + targetMinRange + Random.nextInt(targetMaxRange - targetMinRange);
            } else {
                y = absolutePos.getY() - targetMinRange - Random.nextInt(targetMaxRange - targetMinRange);
            }
            if (x - edgeLength / 2 < 0 || y - edgeLength / 2 < 0) {
                continue;
            }
            if (x + edgeLength / 2 > terrainSettings.getPlayFieldXSize() || y + edgeLength / 2 > terrainSettings.getPlayFieldYSize()) {
                continue;
            }

            Index point = new Index(x, y);
            if (!isTerrainPassable(point)) {
                continue;
            }
            Rectangle itemRectangle = new Rectangle(x - edgeLength / 2, y - edgeLength / 2, edgeLength, edgeLength);
            if (!ItemContainer.getInstance().getItemsInRect(itemRectangle, false).isEmpty()) {
                continue;
            }
            if (isInTopMapPanel(point)) {
                continue;
            }
            return point;
        }
        throw new IllegalStateException(this + " getAbsoluteFreeTerrainInRegion: Can not find free position absolutePos: " + absolutePos + " targetMinRange: " + targetMinRange);
    }

    public boolean isInTopMapPanel(Index absolutePoint) {
        Index point = TerrainView.getInstance().toRelativeIndex(absolutePoint);

        return Game.cockpitPanel.isExpanded() && getRectangle4Widget(Game.cockpitPanel).contains(point)
                || InfoPanel.getInstance().isExpanded() && getRectangle4Widget(InfoPanel.getInstance()).contains(point)
                || RadarPanel.getInstance().isExpanded() && getRectangle4Widget(RadarPanel.getInstance()).contains(point)
                || OnlineBasePanel.getInstance().isExpanded() && getRectangle4Widget(OnlineBasePanel.getInstance()).contains(point);
    }

    public Rectangle getRectangle4Widget(Widget widget) {
        return new Rectangle(widget.getAbsoluteLeft(), widget.getAbsoluteTop(), widget.getOffsetWidth(), widget.getOffsetHeight());
    }


}
