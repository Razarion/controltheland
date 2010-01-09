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

package com.btxtech.game.services.terrain;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.services.terrain.impl.DbKey;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EmbeddedId;
import javax.persistence.OneToOne;
import javax.persistence.CascadeType;

/**
 * User: beat
 * Date: 07.01.2010
 * Time: 22:25:25
 */
@Entity(name = "TERRAIN_IMAGE_POSITION")
public class DbTerrainImagePosition implements Serializable {
    @EmbeddedId
    private DbKey dbKey;
    @OneToOne(cascade = CascadeType.ALL)
    private TerrainImage terrainImage;

    /**
     * Used by hibernate
     */
    protected DbTerrainImagePosition() {
    }

    public DbTerrainImagePosition(Index tileIndex) {
        dbKey = new DbKey(tileIndex.getX(), tileIndex.getY());
    }

    public Index getTileIndex() {
        return dbKey.getIndex();
    }

    public void setTileIndex(Index tileIndex) {
        dbKey.setIndexX(tileIndex.getX());
        dbKey.setIndexY(tileIndex.getY());
    }

    public TerrainImage getTerrainImage() {
        return terrainImage;
    }

    public void setTerrainImage(TerrainImage terrainImage) {
        this.terrainImage = terrainImage;
    }

    public TerrainImagePosition createTerrainImagePosition() {
        return new TerrainImagePosition(dbKey.getIndex().getCopy(), terrainImage.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbTerrainImagePosition that = (DbTerrainImagePosition) o;

        if (dbKey != null ? !dbKey.equals(that.dbKey) : that.dbKey != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return dbKey != null ? dbKey.hashCode() : 0;
    }
}