/*
 * Copyright (c) 2011.
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

package com.btxtech.game.services.utg;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.item.itemType.DbBaseItemType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


/**
 * User: beat
 * Date: 01.02.2011
 * Time: 23:24:19
 */
@Entity(name = "GUIDANCE_LEVEL_ITEM_TYPE_LIMITATION")
public class DbItemTypeLimitation implements CrudChild<DbRealGameLevel> {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    private DbBaseItemType dbBaseItemType;
    @Column(name = "theCount")
    private int count;
    @ManyToOne
    private DbRealGameLevel dbRealGameLevel;

    public DbItemTypeLimitation() {
    }

    public DbItemTypeLimitation(DbItemTypeLimitation original) {
        dbBaseItemType = original.dbBaseItemType;
        count = original.count;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public DbBaseItemType getDbBaseItemType() {
        return dbBaseItemType;
    }

    public void setDbBaseItemType(DbBaseItemType dbBaseItemType) {
        this.dbBaseItemType = dbBaseItemType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbItemTypeLimitation)) return false;

        DbItemTypeLimitation that = (DbItemTypeLimitation) o;

        return id != null && id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
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
    public void init() {
        // Ignore
    }

    @Override
    public void setParent(DbRealGameLevel parent) {
        dbRealGameLevel = parent;
    }
}
