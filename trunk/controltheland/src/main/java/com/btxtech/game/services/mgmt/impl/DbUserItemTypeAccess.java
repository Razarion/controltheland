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

import com.btxtech.game.services.market.MarketEntry;
import com.btxtech.game.services.market.impl.UserItemTypeAccess;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * User: beat
 * Date: 31.03.2011
 * Time: 13:00:19
 */
@Entity(name = "BACKUP_USER_ITEM_TYPE_ACCESS")
public class DbUserItemTypeAccess {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "BACKUP_USER_ITEM_TYPE_ACCESS_BOUGHT",
            joinColumns = @JoinColumn(name = "itemTypeAccessId"),
            inverseJoinColumns = @JoinColumn(name = "userItemTypeId")
    )
    private Set<MarketEntry> allowedItemTypes;
    private int xp = 0;

    /**
     * Used by Hibernate
     */
    public DbUserItemTypeAccess() {
    }

    public DbUserItemTypeAccess(UserItemTypeAccess userItemTypeAccess) {
        this.allowedItemTypes = new HashSet<MarketEntry>(userItemTypeAccess.getAllowedItemTypes());
        xp = userItemTypeAccess.getXp();
    }

    public UserItemTypeAccess createUserItemTypeAccess() {
        UserItemTypeAccess userItemTypeAccess = new UserItemTypeAccess(allowedItemTypes);
        userItemTypeAccess.setXp(xp);
        return userItemTypeAccess;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbUserItemTypeAccess that = (DbUserItemTypeAccess) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
