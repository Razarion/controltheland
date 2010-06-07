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

package com.btxtech.game.services.utg;

import com.btxtech.game.services.item.itemType.DbBaseItemType;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 15.05.2010
 * Time: 11:40:42
 */
@Entity(name = "GUIDANCE_LEVEL_ITEM_COUNT")
public class DbItemCount implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne(optional = false)
    private DbLevel dbLevel;
    @ManyToOne
    private DbBaseItemType baseItemType;
    private int count;

    public DbLevel getDbLevel() {
        return dbLevel;
    }

    public void setDbLevel(DbLevel dbLevel) {
        this.dbLevel = dbLevel;
    }

    public DbBaseItemType getBaseItemType() {
        return baseItemType;
    }

    public void setBaseItemType(DbBaseItemType baseItemType) {
        this.baseItemType = baseItemType;
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
        if (!(o instanceof DbItemCount)) return false;

        DbItemCount that = (DbItemCount) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
