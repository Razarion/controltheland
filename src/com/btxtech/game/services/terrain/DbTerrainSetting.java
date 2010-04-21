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

import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User: beat
 * Date: 08.01.2010
 * Time: 21:01:14
 */
@Entity(name = "TERRAIN_SETTINGS")
public class DbTerrainSetting {
    @Id
    @GeneratedValue
    private Integer id;
    private int tileXCount;
    private int tileYCount;
    private int tileHeight;
    private int tileWidth;
    //@Column(length = 500000)
    //private byte[] bgImageData;
    //private String bgContentType;

    public Integer getId() {
        return id;
    }

    public int getTileXCount() {
        return tileXCount;
    }

    public void setTileXCount(int tileXCount) {
        this.tileXCount = tileXCount;
    }

    public int getTileYCount() {
        return tileYCount;
    }

    public void setTileYCount(int tileYCount) {
        this.tileYCount = tileYCount;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public void setTileHeight(int tileHeight) {
        this.tileHeight = tileHeight;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public void setTileWidth(int tileWidth) {
        this.tileWidth = tileWidth;
    }

    public int getPlayFieldXSize() {
        return tileXCount * tileWidth;
    }

    public int getPlayFieldYSize() {
        return tileYCount * tileHeight;
    }

    public TerrainSettings createTerrainSettings() {
        return new TerrainSettings(tileXCount, tileYCount, tileHeight, tileWidth);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbTerrainSetting that = (DbTerrainSetting) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
