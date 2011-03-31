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

import com.btxtech.game.services.market.MarketEntry;

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
    private Set<MarketEntry> allowedItemTypes;
    private int xp = 0;

    public UserItemTypeAccess(Collection<MarketEntry> allowedItemTypes) {
        this.allowedItemTypes = new HashSet<MarketEntry>(allowedItemTypes);
    }

    public Collection<Integer> getItemTypeIds() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (MarketEntry allowedItemType : allowedItemTypes) {
            if (allowedItemType.getItemType() != null) {
                list.add(allowedItemType.getItemType().getId());
            }
        }
        return list;
    }

    public boolean contains(int itemTypeId) {
        for (MarketEntry allowedItemType : allowedItemTypes) {
            if (allowedItemType.getItemType() != null) {
                if (allowedItemType.getItemType().getId() == itemTypeId) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean contains(MarketEntry marketEntry) {
        return allowedItemTypes.contains(marketEntry);
    }

    public Set<MarketEntry> getAllowedItemTypes() {
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

    public void buy(MarketEntry marketEntry) {
        if (marketEntry.getPrice() > xp) {
            throw new IllegalArgumentException("Not enough XP to buy: " + marketEntry);
        }
        xp -= marketEntry.getPrice();
        allowedItemTypes.add(marketEntry);
    }
}
