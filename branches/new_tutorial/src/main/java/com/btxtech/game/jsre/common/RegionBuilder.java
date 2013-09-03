package com.btxtech.game.jsre.common;

import com.btxtech.game.jsre.client.common.Index;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User: beat
 * Date: 09.09.12
 * Time: 18:20
 */
public class RegionBuilder {
    private Set<Index> tiles;
    private int regionId;

    public RegionBuilder(Region region) {
        regionId = region.getId();
        tiles = new HashSet<Index>(region.getTiles());
    }

    public void insertTile(Collection<Index> tile) {
        tiles.addAll(tile);
    }

    public void removeTile(Collection<Index> tile) {
        tiles.removeAll(tile);
    }

    public Collection<Index> queryRegions() {
        return tiles;
    }

    public void clear() {
        tiles.clear();
    }

    public Region toRegion() {
        return new Region(regionId, GeometricalUtil.groupIndexToRectangles(tiles));
    }
}
