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
import com.btxtech.game.services.user.UserService;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;


/**
 * User: beat
 * Date: 15.05.2010
 * Time: 11:40:42
 */
@Entity(name = "GUIDANCE_COMPARISON_ITEM_COUNT")
public class DbComparisonItemCount implements CrudChild, Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    private DbSyncItemTypeComparisonConfig dbSyncItemTypeComparisonConfig;
    @ManyToOne
    private DbItemTypePositionComparisonConfig dbItemTypePositionComparisonConfig;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbItemType itemType;
    @Column(name = "theCount")
    private int count;

    /**
     * Used by hibernate
     */
    public DbComparisonItemCount() {
    }

    public DbComparisonItemCount(DbComparisonItemCount original) {
        itemType = original.itemType;
        count = original.count;
    }

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
    public void init(UserService userService) {
        // Ignore
    }

    @Override
    public void setParent(Object parent) {
        if (parent instanceof DbSyncItemTypeComparisonConfig) {
            dbSyncItemTypeComparisonConfig = (DbSyncItemTypeComparisonConfig) parent;
        } else {
            dbItemTypePositionComparisonConfig = (DbItemTypePositionComparisonConfig) parent;
        }
    }

    @Override
    public Object getParent() {
        return dbSyncItemTypeComparisonConfig != null ? dbSyncItemTypeComparisonConfig : dbItemTypePositionComparisonConfig;
    }
}
