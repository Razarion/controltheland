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

package com.btxtech.game.services.market;

import com.btxtech.game.services.common.CrudChild;
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
public class MarketEntry implements CrudChild, Serializable{
    @Id
    @GeneratedValue
    private Integer id;
    private boolean alwaysAllowed;
    @ManyToOne
    private DbItemType itemType;
    private int price;
    @ManyToOne
    private MarketFunction marketFunction;
    @ManyToOne
    private MarketCategory marketCategory;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MarketEntry that = (MarketEntry) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void init() {
    }

    @Override
    public void setParent(Object o) {
        // Np parent
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

    public MarketFunction getMarketFunction() {
        return marketFunction;
    }

    public void setMarketFunction(MarketFunction marketFunction) {
        this.marketFunction = marketFunction;
    }

    public MarketCategory getMarketCategory() {
        return marketCategory;
    }

    public void setMarketCategory(MarketCategory marketCategory) {
        this.marketCategory = marketCategory;
    }
}
