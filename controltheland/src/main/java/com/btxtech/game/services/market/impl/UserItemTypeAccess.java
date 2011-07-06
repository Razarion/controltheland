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

package com.btxtech.game.services.market.impl;

import com.btxtech.game.services.market.DbMarketEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User: beat
 * Date: 19.12.2009
 * Time: 13:00:19
 */
public class UserItemTypeAccess implements Serializable {
    private Set<DbMarketEntry> allowedItemTypes;
    private int xp = 0;

    public UserItemTypeAccess(Collection<DbMarketEntry> allowedItemTypes) {
        this.allowedItemTypes = new HashSet<DbMarketEntry>(allowedItemTypes);
    }

    public Collection<Integer> getItemTypeIds() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (DbMarketEntry allowedItemType : allowedItemTypes) {
            if (allowedItemType.getItemType() != null) {
                list.add(allowedItemType.getItemType().getId());
            }
        }
        return list;
    }

    public boolean contains(int itemTypeId) {
        for (DbMarketEntry allowedItemType : allowedItemTypes) {
            if (allowedItemType.getItemType() != null) {
                if (allowedItemType.getItemType().getId() == itemTypeId) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean contains(DbMarketEntry dbMarketEntry) {
        return allowedItemTypes.contains(dbMarketEntry);
    }

    public Set<DbMarketEntry> getAllowedItemTypes() {
        return allowedItemTypes;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    protected void increaseXp(int value) {
        xp += value;
    }

    public void buy(DbMarketEntry dbMarketEntry) {
        if (dbMarketEntry.getPrice() > xp) {
            throw new IllegalArgumentException("Not enough XP to buy: " + dbMarketEntry);
        }
        xp -= dbMarketEntry.getPrice();
        allowedItemTypes.add(dbMarketEntry);
    }
}
