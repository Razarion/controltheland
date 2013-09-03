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

import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * User: beat
 * Date: Sep 20, 2009
 * Time: 6:06:38 PM
 */
@Entity(name = "BACKUP_ENTRY")
public class DbBackupEntry {
    @Id
    @GeneratedValue
    private Integer id;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "backupEntry", fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Set<GenericItem> items;
    private Date timeStamp;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "backupEntry", fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Set<DbUserState> userStates;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbBackupEntry that = (DbBackupEntry) o;

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

    public int getUserStateCount() {
        if (userStates != null) {
            return userStates.size();
        } else {
            return 0;
        }
    }

    public int getBaseCount() {
        if (items != null) {
            HashSet<DbBase> bases = new HashSet<DbBase>();
            for (GenericItem item : items) {
                if (item instanceof DbGenericBaseItem) {
                    bases.add(((DbGenericBaseItem) item).getBase());
                }
            }
            return bases.size();
        } else {
            return 0;
        }
    }

    public Set<DbUserState> getUserStates() {
        return userStates;
    }

    public void setUserStates(Set<DbUserState> userStates) {
        this.userStates = userStates;
    }
}
