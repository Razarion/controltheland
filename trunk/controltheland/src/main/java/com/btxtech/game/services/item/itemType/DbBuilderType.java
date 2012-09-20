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

import com.btxtech.game.services.common.ContentProvider;
import com.btxtech.game.services.common.ReadonlyCollectionContentProvider;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * User: beat
 * Date: 09.12.2009
 * Time: 17:45:47
 */
@Entity(name = "ITEM_BUILDER_TYPE")
public class DbBuilderType implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name= "theRange")
    private int range;
    private double progress;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "ITEM_BUILDER_TYPE_ABLE_TO_BUILD",
            joinColumns = @JoinColumn(name = "builderId"),
            inverseJoinColumns = @JoinColumn(name = "itemTypeId")
    )
    private Set<DbBaseItemType> ableToBuild;

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public Collection<DbBaseItemType> getAbleToBuild() {
        return ableToBuild;
    }

    public void setAbleToBuild(Set<DbBaseItemType> ableToBuild) {
        this.ableToBuild = ableToBuild;
    }

    public ContentProvider<DbBaseItemType> getAbleToBuildCrud() {
        return new ReadonlyCollectionContentProvider<DbBaseItemType>(ableToBuild);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbBuilderType that = (DbBuilderType) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
