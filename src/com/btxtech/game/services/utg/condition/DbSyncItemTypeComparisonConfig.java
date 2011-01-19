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

import com.btxtech.game.jsre.common.utg.config.AbstractComparisonConfig;
import com.btxtech.game.jsre.common.utg.config.SyncItemTypeComparisonConfig;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbItemType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 27.12.2010
 * Time: 16:31:29
 */
@Entity
@DiscriminatorValue("ITEM_TYPE")
public class DbSyncItemTypeComparisonConfig extends DbAbstractComparisonConfig {
    @ManyToOne(fetch = FetchType.LAZY)
    private DbItemType dbItemType;

    public DbItemType getDbItemType() {
        return dbItemType;
    }

    public void setDbItemType(DbItemType dbItemType) {
        this.dbItemType = dbItemType;
    }

    @Override
    public AbstractComparisonConfig createComparisonConfig(ItemService itemService) {
        return new SyncItemTypeComparisonConfig(itemService.getItemType(dbItemType));
    }
}
