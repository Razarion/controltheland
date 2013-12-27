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

import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * User: beat
 * Date: 26.04.2010
 * Time: 20:56:05
 */
@Entity(name = "TERRAIN_IMAGE_TILE_SURFACE_TYPE")
public class TerrainImageTileSurfaceType implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Enumerated(value = EnumType.STRING)
    private SurfaceType surfaceType;
    private int tileX;
    private int tileY;
    @ManyToOne(optional = false)
    private DbTerrainImage dbTerrainImage;

    /**
     * Used by hibernate
     */
    public TerrainImageTileSurfaceType() {
    }

    public TerrainImageTileSurfaceType(DbTerrainImage dbTerrainImage, int tileX, int tileY, SurfaceType surfaceType) {
        this.dbTerrainImage = dbTerrainImage;
        this.tileX = tileX;
        this.tileY = tileY;
        this.surfaceType = surfaceType;
    }

    public SurfaceType getSurfaceType() {
        return surfaceType;
    }

    public void setSurfaceType(SurfaceType surfaceType) {
        this.surfaceType = surfaceType;
    }

    public int getTileX() {
        return tileX;
    }

    public void setTileX(int tileX) {
        this.tileX = tileX;
    }

    public int getTileY() {
        return tileY;
    }

    public void setTileY(int tileY) {
        this.tileY = tileY;
    }

    public DbTerrainImage getDbTerrainImage() {
        return dbTerrainImage;
    }

    public void setDbTerrainImage(DbTerrainImage dbTerrainImage) {
        this.dbTerrainImage = dbTerrainImage;
    }

    public boolean equalsTile(int tileX, int tileY) {
        return tileX == this.tileX && tileY == this.tileY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TerrainImageTileSurfaceType)) return false;

        TerrainImageTileSurfaceType that = (TerrainImageTileSurfaceType) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
