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

package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 28.12.2010
 * Time: 23:32:02
 */
@Entity(name = "GUIDANCE_SCOPE")
public class DbScope {
    @Id
    @GeneratedValue
    private Integer id;
    private int deltaMoney;
    private double itemSellFactor;
//    private int itemLimit;
    private int houseSpace;
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "dbLevel")
//    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
//    @Deprecated
//    private Set<DbItemCount> dbItemCounts;
    // new base
    private boolean createRealBase;
    @ManyToOne
    private DbBaseItemType startItemType;
    private Rectangle startRectangle;
    private int startItemFreeRange;


    public int getDeltaMoney() {
        return deltaMoney;
    }

    public void setDeltaMoney(int deltaMoney) {
        this.deltaMoney = deltaMoney;
    }

    public double getItemSellFactor() {
        return itemSellFactor;
    }

    public void setItemSellFactor(double itemSellFactor) {
        this.itemSellFactor = itemSellFactor;
    }

    public int getHouseSpace() {
        return houseSpace;
    }

    public void setHouseSpace(int houseSpace) {
        this.houseSpace = houseSpace;
    }

    public boolean isCreateRealBase() {
        return createRealBase;
    }

    public void setCreateRealBase(boolean createRealBase) {
        this.createRealBase = createRealBase;
    }

    public DbBaseItemType getStartItemType() {
        return startItemType;
    }

    public void setStartItemType(DbBaseItemType startItemType) {
        this.startItemType = startItemType;
    }

    public Rectangle getStartRectangle() {
        return startRectangle;
    }

    public void setStartRectangle(Rectangle startRectangle) {
        this.startRectangle = startRectangle;
    }

    public int getStartItemFreeRange() {
        return startItemFreeRange;
    }

    public void setStartItemFreeRange(int startItemFreeRange) {
        this.startItemFreeRange = startItemFreeRange;
    }

    public int getItemLimit() {
        return 100;            // TODO
    }
}
