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

package com.btxtech.game.services.itemTypeAccess.impl;

import com.btxtech.game.services.itemTypeAccess.ItemTypeAccessEntry;
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
import javax.persistence.Column;

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
    private Set<ItemTypeAccessEntry> allowedItemTypes;
    private int xp = 0;

    /**
     * Used by Hibernate
     */
    public UserItemTypeAccess() {
    }

    public UserItemTypeAccess(Collection<ItemTypeAccessEntry> allowedItemTypes) {
        this.allowedItemTypes = new HashSet<ItemTypeAccessEntry>(allowedItemTypes);
    }

    public Collection<Integer> getItemTypeIds() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (ItemTypeAccessEntry allowedItemType : allowedItemTypes) {
            if (allowedItemType.getItemType() != null) {
                list.add(allowedItemType.getItemType().getId());
            }
        }
        return list;
    }

    public boolean contains(int itemTypeId) {
        for (ItemTypeAccessEntry allowedItemType : allowedItemTypes) {
            if (allowedItemType.getItemType() != null) {
                if (allowedItemType.getItemType().getId() == itemTypeId) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean contains(ItemTypeAccessEntry itemTypeAccessEntry) {
        return allowedItemTypes.contains(itemTypeAccessEntry);
    }

    public Set<ItemTypeAccessEntry> getAllowedItemTypes() {
        return allowedItemTypes;
    }

    public int getXp() {
        return xp;
    }

    protected void increaseXp(int value) {
        xp += value;
    }

    public void buy(ItemTypeAccessEntry itemTypeAccessEntry) {
        if (itemTypeAccessEntry.getPrice() > xp) {
            throw new IllegalArgumentException("Not enough XP to buy: " + itemTypeAccessEntry);
        }
        xp -= itemTypeAccessEntry.getPrice();
        allowedItemTypes.add(itemTypeAccessEntry);
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

    public boolean isPersistent() {
        return id != null;
    }

    public Integer getId() {
        return id;
    }
}
