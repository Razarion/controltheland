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

import com.btxtech.game.jsre.common.utg.condition.AbstractComparison;
import com.btxtech.game.jsre.common.utg.condition.SyncItemIdComparison;
import com.btxtech.game.services.common.Utils;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.mgmt.impl.BackupEntry;
import com.btxtech.game.services.user.UserState;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * User: beat
 * Date: 29.01.2011
 * Time: 23:10:34
 */
@Entity
@DiscriminatorValue("SYNC_ITEM_ID")
public class DbSyncItemIdComparisonBackup extends DbAbstractComparisonBackup {
    private String idString;

    /**
     * Used by hibernate
     */
    public DbSyncItemIdComparisonBackup() {
    }

    public DbSyncItemIdComparisonBackup(BackupEntry backupEntry, UserState userState, SyncItemIdComparison syncItemIdComparison) {
        super(backupEntry, userState);
        idString = Utils.integerToSting(syncItemIdComparison.getSyncItemIds());
    }

    @Override
    public void restore(AbstractComparison abstractComparison, ItemService itemService) {
        ((SyncItemIdComparison) abstractComparison).setSyncItemIds(Utils.stringToIntegers(idString));
    }
}
