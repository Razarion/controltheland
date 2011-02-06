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
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.mgmt.impl.BackupEntry;
import com.btxtech.game.services.user.UserState;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * User: beat
 * Date: 29.01.2011
 * Time: 23:11:21
 */
@Entity(name = "BACKUP_LEVEL_COMPARISON")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class DbAbstractComparisonBackup {
    @Id
    @GeneratedValue
    private Integer id;
    @OneToOne
    // (optional = false) does not work
    private UserState userState;
    @ManyToOne(optional = false)
    private BackupEntry backupEntry;

    /**
     * Used by hibernate
     */
    protected DbAbstractComparisonBackup() {
    }

    protected DbAbstractComparisonBackup(BackupEntry backupEntry, UserState userState) {
        this.backupEntry = backupEntry;
        this.userState = userState;
    }

    public UserState getUserState() {
        return userState;
    }

    public abstract void restore(AbstractComparison abstractComparison, ItemService itemService);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbAbstractComparisonBackup)) return false;

        DbAbstractComparisonBackup that = (DbAbstractComparisonBackup) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
