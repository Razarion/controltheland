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

package com.btxtech.game.services.utg.condition.backup;

import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.utg.condition.AbstractComparison;
import com.btxtech.game.jsre.common.utg.condition.SyncItemTypeComparison;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.mgmt.impl.BackupEntry;
import com.btxtech.game.services.mgmt.impl.DbUserState;
import com.btxtech.game.services.user.UserState;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import org.hibernate.annotations.CollectionOfElements;

/**
 * User: beat
 * Date: 29.01.2011
 * Time: 23:10:55
 */
@Entity
@DiscriminatorValue("SYNC_ITEM_TYPE")
public class DbSyncItemTypeComparisonBackup extends DbAbstractComparisonBackup {
    @CollectionOfElements(fetch = FetchType.EAGER)
    @JoinTable(name = "BACKUP_LEVEL_COMPARISON_SYNC_ITEM_TYPE")
    // Does not work
    @org.hibernate.annotations.MapKey(columns = @Column(name = "itemType"))
    @Column(name = "theCount")
    private Map<DbItemType, Integer> itemTypeCount;

    /**
     * Used by hibernate
     */
    public DbSyncItemTypeComparisonBackup() {
    }

    public DbSyncItemTypeComparisonBackup(DbUserState userState, SyncItemTypeComparison syncItemTypeComparison, ItemService itemService) {
        super(userState);
        itemTypeCount = new HashMap<DbItemType, Integer>();
        for (Map.Entry<ItemType, Integer> entry : syncItemTypeComparison.getRemaining().entrySet()) {
            DbItemType dbItemType = itemService.getDbItemType(entry.getKey().getId());
            itemTypeCount.put(dbItemType, entry.getValue());
        }
    }

    @Override
    public void restore(AbstractComparison abstractComparison, ItemService itemService) {
        Map<ItemType, Integer> remaining = new HashMap<ItemType, Integer>();
        for (Map.Entry<DbItemType, Integer> entry : itemTypeCount.entrySet()) {
            ItemType itemType = itemService.getItemType(entry.getKey());
            remaining.put(itemType, entry.getValue());
        }
        ((SyncItemTypeComparison) abstractComparison).setRemaining(remaining);
    }
}
