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
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.io.Serializable;


/**
 * User: beat
 * Date: 07.01.2010
 * Time: 22:25:25
 */
@Entity(name = "TERRAIN_IMAGE_POSITION")
public class DbTerrainImagePosition implements Serializable, CrudChild<DbTerrainSetting> {
    @Id
    @GeneratedValue
    private Integer id;
    private int tileX;
    private int tileY;
    @OneToOne
    private DbTerrainImage dbTerrainImage;
    @ManyToOne(optional = false)
    private DbTerrainSetting dbTerrainSetting;
    private TerrainImagePosition.ZIndex zIndex;

    /**
     * Used by hibernate
     */
    protected DbTerrainImagePosition() {
    }

    public DbTerrainImagePosition(Index position, DbTerrainImage dbTerrainImage, TerrainImagePosition.ZIndex zIndex) {
        this.zIndex = zIndex;
        tileX = position.getX();
        tileY = position.getY();
        this.dbTerrainImage = dbTerrainImage;
    }

    public Integer getId() {
        return id;
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

    public DbTerrainImage getTerrainImage() {
        return dbTerrainImage;
    }

    public void setTerrainImage(DbTerrainImage dbTerrainImage) {
        this.dbTerrainImage = dbTerrainImage;
    }

    public TerrainImagePosition.ZIndex getzIndex() {
        return zIndex;
    }

    public void setzIndex(TerrainImagePosition.ZIndex zIndex) {
        this.zIndex = zIndex;
    }

    public TerrainImagePosition createTerrainImagePosition() {
        return new TerrainImagePosition(new Index(tileX, tileY), dbTerrainImage.getId(), zIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbTerrainImagePosition that = (DbTerrainImagePosition) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id;
        } else {
            return System.identityHashCode(this);
        }
    }

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
        // DO not use if parameters are passed via constructor
    }

    @Override
    public void setParent(DbTerrainSetting dbTerrainSetting) {
        this.dbTerrainSetting = dbTerrainSetting;
    }

    @Override
    public DbTerrainSetting getParent() {
        return dbTerrainSetting;
    }
}