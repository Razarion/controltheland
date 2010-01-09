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

import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.services.terrain.Tile;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User: beat
 * Date: Aug 31, 2009
 * Time: 6:45:44 PM
 */
@Entity(name = "TERRAIN_TILE")
@Deprecated
public class TileImpl implements Tile, Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false, length = 500000)
    private byte[] imageData;
    @Column(nullable = false)
    private TerrainType terrainType;

    public TileImpl() {
    }

    public TileImpl(byte[] imageData, TerrainType terrainType) {
        this.imageData = imageData;
        this.terrainType = terrainType;
    }

    @Override
    public byte[] getImageData() {
        return imageData;
    }

    @Override
    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    @Override
    public int getImageSize() {
        if(imageData == null) {
            return 0;
        }
        return imageData.length;
    }

    @Override
    public TerrainType getTerrainType() {
        return terrainType;
    }

    @Override
    public void setTerrainType(TerrainType terrainType) {
        this.terrainType = terrainType;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public boolean checkTerrainType(TerrainType terrainType) {
        return terrainType != null && terrainType == this.terrainType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TileImpl tileImpl = (TileImpl) o;

        return !(id != null ? !id.equals(tileImpl.id) : tileImpl.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
