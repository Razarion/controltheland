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

package com.btxtech.game.services.utg.condition;

import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.utg.config.AbstractComparisonConfig;
import com.btxtech.game.jsre.common.utg.config.SyncItemTypeComparisonConfig;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.item.ServerItemTypeService;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: beat
 * Date: 27.12.2010
 * Time: 16:31:29
 */
@Entity
@DiscriminatorValue("ITEM_TYPE")
public class DbSyncItemTypeComparisonConfig extends DbAbstractComparisonConfig implements CrudParent {
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "dbSyncItemTypeComparisonConfig", orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Set<DbComparisonItemCount> dbComparisonItemCounts;
    @Transient
    private CrudChildServiceHelper<DbComparisonItemCount> dbComparisonItemCountCrudServiceHelper;

    public Set<DbComparisonItemCount> getDbComparisonItemCounts() {
        if (dbComparisonItemCounts == null) {
            dbComparisonItemCounts = new HashSet<>();
        }
        return dbComparisonItemCounts;
    }

    public void setDbComparisonItemCounts(Set<DbComparisonItemCount> dbComparisonItemCounts) {
        this.dbComparisonItemCounts = dbComparisonItemCounts;
    }

    public CrudChildServiceHelper<DbComparisonItemCount> getCrudDbComparisonItemCount() {
        if (dbComparisonItemCountCrudServiceHelper == null) {
            if (dbComparisonItemCounts == null) {
                dbComparisonItemCounts = new HashSet<>();
            }
            dbComparisonItemCountCrudServiceHelper = new CrudChildServiceHelper<>(dbComparisonItemCounts, DbComparisonItemCount.class, this);
        }
        return dbComparisonItemCountCrudServiceHelper;
    }

    @Override
    public AbstractComparisonConfig createComparisonConfig(ServerItemTypeService serverItemTypeService) {
        Map<ItemType, Integer> itemTypeCount = new HashMap<>();
        for (DbComparisonItemCount dbComparisonItemCount : dbComparisonItemCounts) {
            itemTypeCount.put(serverItemTypeService.getItemType(dbComparisonItemCount.getItemType()), dbComparisonItemCount.getCount());
        }

        return new SyncItemTypeComparisonConfig(itemTypeCount);
    }

    @Override
    protected DbAbstractComparisonConfig createCopy() {
        DbSyncItemTypeComparisonConfig copy = new DbSyncItemTypeComparisonConfig();
        copy.dbComparisonItemCounts = new HashSet<>();
        getCrudDbComparisonItemCount().copyTo(copy.getCrudDbComparisonItemCount());
        return copy;
    }
}
