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

package com.btxtech.game.services.item.itemType;

import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.services.common.ContentProvider;
import com.btxtech.game.services.common.ReadonlyCollectionContentProvider;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.util.Set;

/**
 * User: beat
 * Date: 02.05.2010
 * Time: 15:12:27
 */
@Entity(name = "ITEM_ITEM_CONTAINER_TYPE")
public class DbItemContainerType implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    private int maxCount;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "ITEM_ITEM_CONTAINER_TYPE_ABLE_TO_CONTAIN",
            joinColumns = @JoinColumn(name = "containId"),
            inverseJoinColumns = @JoinColumn(name = "itemTypeId")
    )
    private Set<DbBaseItemType> ableToContain;
    @Column(name = "theRange")
    private int range;
    @Enumerated(value = EnumType.STRING)
    private SurfaceType operationSurfaceType;

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public Set<DbBaseItemType> getAbleToContain() {
        return ableToContain;
    }

    public void setAbleToContain(Set<DbBaseItemType> ableToContain) {
        this.ableToContain = ableToContain;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public ContentProvider<DbBaseItemType> getAbleToContainCrud() {
        return new ReadonlyCollectionContentProvider<DbBaseItemType>(ableToContain);
    }

    public SurfaceType getOperationSurfaceType() {
        return operationSurfaceType;
    }

    public void setOperationSurfaceType(SurfaceType operationSurfaceType) {
        this.operationSurfaceType = operationSurfaceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbItemContainerType that = (DbItemContainerType) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}