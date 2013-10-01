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
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.user.UserService;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;


/**
 * User: beat
 * Date: 07.01.2010
 * Time: 22:09:11
 */
@Entity(name = "TERRAIN_IMAGE")
public class DbTerrainImage implements CrudChild<DbTerrainImageGroup> {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(length = 500000)
    private byte[] imageData;
    private String contentType;
    private int tileWidth;
    private int tileHeight;
    @OneToMany(mappedBy = "dbTerrainImage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Collection<TerrainImageTileSurfaceType> surfaceTypes;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbTerrainImageGroup parent;

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void init(UserService userService) {
        // Ignore
    }

    @Override
    public void setParent(DbTerrainImageGroup parent) {
        this.parent = parent;
    }

    @Override
    public DbTerrainImageGroup getParent() {
        return parent;
    }

    public Integer getId() {
        return id;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    public void setTiles(int tileWidth, int tileHeight) {
        if (tileWidth != this.tileWidth || tileHeight != this.tileHeight) {
            if (surfaceTypes != null) {
                surfaceTypes.clear();
            }
        }
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public TerrainImage createTerrainImage() {
        SurfaceType[][] surfaceType = new SurfaceType[tileWidth][];
        for (int x = 0; x < tileWidth; x++) {
            surfaceType[x] = new SurfaceType[tileHeight];
            for (int y = 0; y < tileHeight; y++) {
                surfaceType[x][y] = getSurfaceType(x, y);
            }
        }
        return new TerrainImage(id, parent.createImageSpriteMapInfo(), tileWidth, tileHeight, surfaceType);
    }

    public void setSurfaceType(int tileX, int tileY, SurfaceType surfaceType) {
        if (surfaceTypes == null) {
            surfaceTypes = new ArrayList<>();
        }

        for (TerrainImageTileSurfaceType terrainImageTileSurfaceType : surfaceTypes) {
            if (terrainImageTileSurfaceType.equalsTile(tileX, tileY)) {
                terrainImageTileSurfaceType.setSurfaceType(surfaceType);
                return;
            }
        }
        surfaceTypes.add(new TerrainImageTileSurfaceType(this, tileX, tileY, surfaceType));
    }

    public SurfaceType getSurfaceType(int tileX, int tileY) {
        if (surfaceTypes == null) {
            return SurfaceType.NONE;
        }

        for (TerrainImageTileSurfaceType terrainImageTileSurfaceType : surfaceTypes) {
            if (terrainImageTileSurfaceType.equalsTile(tileX, tileY)) {
                return terrainImageTileSurfaceType.getSurfaceType();
            }
        }
        return SurfaceType.NONE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbTerrainImage that = (DbTerrainImage) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
