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
import com.btxtech.game.jsre.common.utg.condition.CountComparison;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.mgmt.impl.BackupEntry;
import com.btxtech.game.services.user.UserState;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * User: beat
 * Date: 29.01.2011
 * Time: 23:10:12
 */
@Entity
@DiscriminatorValue("COUNT")
public class DbCountComparisonBackup extends DbAbstractComparisonBackup {
    @Column(name = "theCount")
    private double count;

    /**
     * Used by hibernate
     */
    public DbCountComparisonBackup() {
    }

    public DbCountComparisonBackup(UserState userState, CountComparison countComparison) {
        super(userState);
        count = countComparison.getCount();
    }

    @Override
    public void restore(AbstractComparison abstractComparison, ItemService itemService) {
        ((CountComparison) abstractComparison).setCount(count);
    }
}
