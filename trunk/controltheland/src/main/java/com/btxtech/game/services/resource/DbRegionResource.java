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

package com.btxtech.game.services.resource;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.db.RectangleUserType;
import com.btxtech.game.services.item.itemType.DbResourceItemType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

/**
 * User: beat
 * Date: 08.05.2010
 * Time: 22:07:56
 */
@Entity(name = "RESOURCE_REGION_RESOURCE")
@TypeDef(name = "rectangle", typeClass = RectangleUserType.class)
public class DbRegionResource implements CrudChild {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = "theCount")
    private int count;
    private int minDistanceToItems;
    @Type(type = "rectangle")
    @Columns(columns = {@Column(name = "x"), @Column(name = "y"), @Column(name = "width"), @Column(name = "height")})    
    private Rectangle region;
    private String name;
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private DbResourceItemType resourceItemType;

    @Override
    public Serializable getId() {
        return id;
    }

    @Override
    public void init(UserService userService) {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Rectangle getRegion() {
        if (region == null) {
            region = new Rectangle(0, 0, 100, 100);
        }
        return region;
    }

    public void setRegion(Rectangle region) {
        this.region = region;
    }

    public int getMinDistanceToItems() {
        return minDistanceToItems;
    }

    public void setMinDistanceToItems(int minDistanceToItems) {
        this.minDistanceToItems = minDistanceToItems;
    }

    public DbResourceItemType getResourceItemType() {
        return resourceItemType;
    }

    public void setResourceItemType(DbResourceItemType resourceItemType) {
        this.resourceItemType = resourceItemType;
    }

    @Override
    public void setParent(Object o) {
        // Np parent
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbRegionResource)) return false;

        DbRegionResource that = (DbRegionResource) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
