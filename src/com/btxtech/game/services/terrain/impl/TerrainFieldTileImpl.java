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

package com.btxtech.game.services.terrain.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.services.terrain.TerrainFieldTile;
import com.btxtech.game.services.terrain.Tile;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 11:25:46 AM
 */
@Entity(name = "TERRAIN_FIELD")
@Deprecated
public class TerrainFieldTileImpl implements TerrainFieldTile {
    @EmbeddedId
    private DbKey dbKey;
    @ManyToOne
    @JoinColumn(name = "tile", nullable = false)
    private TileImpl tile;

    public TerrainFieldTileImpl() {
    }

    public TerrainFieldTileImpl(Index index, Tile tile) {
        dbKey = new DbKey(index.getX(), index.getY());
        this.tile = (TileImpl) tile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TerrainFieldTileImpl that = (TerrainFieldTileImpl) o;

        return !(dbKey != null ? !dbKey.equals(that.dbKey) : that.dbKey != null);

    }

    @Override
    public int hashCode() {
        return dbKey != null ? dbKey.hashCode() : 0;
    }

    @Override
    public Tile getTile() {
        return tile;
    }

    @Override
    public void setTile(Tile tile) {
        this.tile = (TileImpl) tile;
    }

    @Override
    public Index getIndex() {
        return dbKey.getIndex();
    }


}
