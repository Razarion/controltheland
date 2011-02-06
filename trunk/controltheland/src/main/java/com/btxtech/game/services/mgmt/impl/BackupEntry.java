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

package com.btxtech.game.services.mgmt.impl;

import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.utg.condition.backup.DbAbstractComparisonBackup;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * User: beat
 * Date: Sep 20, 2009
 * Time: 6:06:38 PM
 */
@Entity(name = "BACKUP_ENTRY")
public class BackupEntry {
    @Id
    @GeneratedValue
    private Integer id;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "backupEntry", fetch = FetchType.EAGER)
    private Set<GenericItem> items;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "backupEntry", fetch = FetchType.EAGER)
    private Set<DbAbstractComparisonBackup> abstractComparison;
    private Date timeStamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BackupEntry that = (BackupEntry) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public Collection<GenericItem> getItems() {
        return items;
    }

    public void setItems(Set<GenericItem> items) {
        this.items = items;
    }

    public Collection<DbAbstractComparisonBackup> getAbstractComparison() {
        return abstractComparison;
    }

    public void setAbstractComparison(Set<DbAbstractComparisonBackup> abstractComparison) {
        this.abstractComparison = abstractComparison;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getItemCount() {
        if (items != null) {
            return items.size();
        } else {
            return 0;
        }
    }

    public int getBaseCount() {
        if (items != null) {
            HashSet<Base> bases = new HashSet<Base>();
            for (GenericItem item : items) {
                if (item instanceof GenericBaseItem) {
                    bases.add(((GenericBaseItem) item).getBase());
                }
            }
            return bases.size();
        } else {
            return 0;
        }
    }
}
