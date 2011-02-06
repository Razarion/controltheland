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

package com.btxtech.game.services.territory;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.services.common.db.RectangleUserType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

/**
 * User: beat
 * Date: 23.05.2010
 * Time: 14:45:28
 */
@Entity(name = "TERRITORY_TERRITORY_REGION")
@TypeDef(name = "rectangle", typeClass = RectangleUserType.class)
public class DbTerritoryRegion implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne(optional = false)
    private DbTerritory dbTerritory;
    @Type(type = "rectangle")
    @Columns(columns = {@Column(name = "x"), @Column(name = "y"), @Column(name = "width"), @Column(name = "height")})
    private Rectangle tileRectangle;

    public DbTerritory getDbTerritory() {
        return dbTerritory;
    }

    public void setDbTerritory(DbTerritory dbTerritory) {
        this.dbTerritory = dbTerritory;
    }

    public Rectangle getTileRectangle() {
        return tileRectangle;
    }

    public void setTileRectangle(Rectangle tileRectangle) {
        this.tileRectangle = tileRectangle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbTerritoryRegion)) return false;

        DbTerritoryRegion that = (DbTerritoryRegion) o;

        if (id != null && that.id != null) {
            return id.equals(that.id);
        }

        if (tileRectangle == null) {
            return that.tileRectangle == null;
        }

        return tileRectangle.equals(that.tileRectangle);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        } else {
            if (tileRectangle != null) {
                return tileRectangle.hashCode();
            } else {
                return 0;
            }
        }
    }
}
