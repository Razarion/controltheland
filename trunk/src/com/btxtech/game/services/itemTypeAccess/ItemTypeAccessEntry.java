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

package com.btxtech.game.services.itemTypeAccess;

import com.btxtech.game.services.item.itemType.DbItemType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Entity;
import java.io.Serializable;

/**
 * User: beat
 * Date: 18.12.2009
 * Time: 22:19:08
 */
@Entity(name = "ITEM_TYPE_ACCESS_ENTRY")
public class ItemTypeAccessEntry implements Serializable{
    @Id
    @GeneratedValue
    private Integer id;
    private boolean alwaysAllowed;
    @ManyToOne
    private DbItemType itemType;
    private int price;
    @ManyToOne
    private ItemTypeAccessEntry precondition;

    

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemTypeAccessEntry that = (ItemTypeAccessEntry) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public Integer getId() {
        return id;
    }

    public boolean isAlwaysAllowed() {
        return alwaysAllowed;
    }

    public void setAlwaysAllowed(boolean alwaysAllowed) {
        this.alwaysAllowed = alwaysAllowed;
    }

    public DbItemType getItemType() {
        return itemType;
    }

    public void setItemType(DbItemType itemType) {
        this.itemType = itemType;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public ItemTypeAccessEntry getPrecondition() {
        return precondition;
    }

    public void setPrecondition(ItemTypeAccessEntry precondition) {
        this.precondition = precondition;
    }
}
