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

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;


/**
 * User: beat
 * Date: 21.04.2010
 * Time: 14:02:37
 */
@Entity(name = "TERRAIN_SURFACE_RECT")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"tileX", "tileY", "tileWidth", "tileHeight", "dbSurfaceImage_id", "dbTerrainSetting_id"}))
public class DbSurfaceRect implements CrudChild<DbTerrainSetting>, Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @OneToOne
    private DbSurfaceImage dbSurfaceImage;
    private int tileX;
    private int tileY;
    private int tileWidth;
    private int tileHeight;
    @ManyToOne(optional = false)
    private DbTerrainSetting dbTerrainSetting;

    /**
     * Used by Hibernate
     */
    protected DbSurfaceRect() {
    }

    public DbSurfaceRect(Rectangle tileRectangle, DbSurfaceImage dbSurfaceImage) {
        tileX = tileRectangle.getX();
        tileY = tileRectangle.getY();
        tileWidth = tileRectangle.getWidth();
        tileHeight = tileRectangle.getHeight();
        this.dbSurfaceImage = dbSurfaceImage;

    }

    public Integer getId() {
        return id;
    }

    public Rectangle getRectangle() {
        return new Rectangle(tileX, tileY, tileWidth, tileHeight);
    }

    public DbSurfaceImage getDbSurfaceImage() {
        return dbSurfaceImage;
    }

    public SurfaceRect createSurfaceRect() {
        return new SurfaceRect(getRectangle(), dbSurfaceImage.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbSurfaceRect that = (DbSurfaceRect) o;

        return id != null && id.equals(that.id);

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
