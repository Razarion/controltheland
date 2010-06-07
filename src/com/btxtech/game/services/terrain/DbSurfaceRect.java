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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * User: beat
 * Date: 21.04.2010
 * Time: 14:02:37
 */
@Entity(name = "TERRAIN_SURFACE_RECT")
public class DbSurfaceRect {
    @Id
    @GeneratedValue
    private Integer id;
    @OneToOne
    private DbSurfaceImage dbSurfaceImage;
    private int tileX;
    private int tileY;
    private int tileWidth;
    private int tileHeight;

    public Rectangle getRectangle() {
        return new Rectangle(tileX, tileY, tileWidth, tileHeight);
    }

    public void setRectangle(Rectangle rectangle) {
        tileX = rectangle.getX();
        tileY = rectangle.getY();
        tileWidth = rectangle.getWidth();
        tileHeight = rectangle.getHeight();
    }

    public DbSurfaceImage getDbSurfaceImage() {
        return dbSurfaceImage;
    }

    public void setDbSurfaceImage(DbSurfaceImage dbSurfaceImage) {
        this.dbSurfaceImage = dbSurfaceImage;
    }

    public SurfaceRect createSurfaceRect() {
        return new SurfaceRect(getRectangle(), dbSurfaceImage.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbSurfaceRect that = (DbSurfaceRect) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }
}
