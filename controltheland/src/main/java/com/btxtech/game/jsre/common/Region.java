package com.btxtech.game.jsre.common;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainUtil;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: beat
 * Date: 11.09.12
 * Time: 14:36
 */
public class Region implements Serializable {
    private int id;
    private Set<Index> tiles = new HashSet<Index>();

    /**
     * Used by GWT
     */
    Region() {
    }

    public Region(int id, Set<Index> tiles) {
        this.id = id;
        this.tiles = tiles;
    }

    public boolean isInsideTile(Index tile) {
        return tiles.contains(tile);
    }

    public boolean isInsideAbsolute(Index tile) {
        return isInsideTile(TerrainUtil.getTerrainTileIndexForAbsPosition(tile));
    }

    /**
     * Shall only be called by RegionBuilder and save to DB
     *
     * @return all tiles
     */
    public Set<Index> getTiles() {
        return tiles;
    }

    public int getId() {
        return id;
    }

    public List<Index> getTileCopy() {
        return new ArrayList<Index>(tiles);
    }

    public boolean isInside(SyncItem syncItem) {
        return isInsideAbsolute(syncItem.getSyncItemArea().getPosition());
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
