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

package com.btxtech.game.services.utg.condition;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.item.itemType.DbItemType;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * User: beat
 * Date: 15.05.2010
 * Time: 11:40:42
 */
@Entity(name = "GUIDANCE_COMPARISON_ITEM_COUNT")
public class DbComparisonItemCount implements CrudChild<DbSyncItemTypeComparisonConfig>, Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne(optional = false)
    private DbSyncItemTypeComparisonConfig dbSyncItemTypeComparisonConfig;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbItemType itemType;
    private int count;

    public Integer getId() {
        return id;
    }

    public DbItemType getItemType() {
        return itemType;
    }

    public void setItemType(DbItemType itemType) {
        this.itemType = itemType;
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
        if (!(o instanceof DbComparisonItemCount)) return false;

        DbComparisonItemCount that = (DbComparisonItemCount) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String getName() {
        throw new NotImplementedException();
    }

    @Override
    public void setName(String name) {
        throw new NotImplementedException();
    }

    @Override
    public void init() {
        // Ignore
    }

    @Override
    public void setParent(DbSyncItemTypeComparisonConfig dbSyncItemTypeComparisonConfig) {
        this.dbSyncItemTypeComparisonConfig = dbSyncItemTypeComparisonConfig;
    }
}
