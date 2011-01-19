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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

/**
 * User: beat
 * Date: 19.12.2009
 * Time: 13:00:19
 */
@Entity(name = "USER_ITEM_TYPE_ACCESS")
public class UserItemTypeAccess implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "USER_ITEM_TYPE_ACCESS_BOUGHT",
            joinColumns = @JoinColumn(name = "itemTypeAccessId"),
            inverseJoinColumns = @JoinColumn(name = "userItemTypeId")
    )
    private Set<MarketEntry> allowedItemTypes;
    private int xp = 0;

    /**
     * Used by Hibernate
     */
    public UserItemTypeAccess() {
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserItemTypeAccess that = (UserItemTypeAccess) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " id: " + id + " xp: " + xp;
    }

    public Integer getId() {
        return id;
    }
}
