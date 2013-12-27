package com.btxtech.game.jsre.common;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainUtil;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User: beat
 * Date: 11.09.12
 * Time: 14:36
 */
public class Region implements Serializable {
    private int id;
    private Collection<Rectangle> rectangles;
    private transient Set<Index> tiles;

    /**
     * Used by GWT
     */
    Region() {
    }

    public Region(int id, Collection<Rectangle> rectangles) {
        this.id = id;
        this.rectangles = rectangles;
    }

    public boolean isInsideTile(Index tile) {
        return getTiles().contains(tile);
    }

    public boolean isInsideAbsolute(Index tile) {
        return isInsideTile(TerrainUtil.getTerrainTileIndexForAbsPosition(tile));
    }


    public boolean isInside(SyncItem syncItem) {
        return isInsideAbsolute(syncItem.getSyncItemArea().getPosition());
    }

    /**
     * Shall only be called by RegionBuilder
     *
     * @return all tiles
     */
    Set<Index> getTiles() {
        if (tiles == null) {
            tiles = GeometricalUtil.splitRectanglesToIndexes(rectangles);
        }
        return tiles;
    }

    public Index getIndexForRandomPosition(int count) {
        if(count < 0) {
            throw new IllegalArgumentException("Region.getIndexForRandomPosition(): Count is not allowed to be negative: " + count);
        }
        if (count >= getTiles().size()) {
            throw new ArrayIndexOutOfBoundsException("Region.getIndexForRandomPosition(): Count: " + count + " size: " + getTiles().size());
        }
        for (Index tile : getTiles()) {
            if (count == 0) {
                return tile;
            }
            count--;
        }
        throw new IllegalStateException();
    }

    public int tileSize() {
        return getTiles().size();
    }

    public int getId() {
        return id;
    }

    public Collection<Rectangle> getRectangles() {
        return rectangles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Region region = (Region) o;

        return id == region.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
