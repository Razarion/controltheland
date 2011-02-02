/*
 * Copyright (c) 2011.
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

import com.btxtech.game.jsre.client.common.Level;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.common.CrudServiceHelper;
import com.btxtech.game.services.common.CrudServiceHelperCollectionImpl;
import com.btxtech.game.services.common.db.RectangleUserType;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.market.MarketEntry;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

/**
 * User: beat
 * Date: 16.01.2011
 * Time: 21:11:33
 */
@Entity
@DiscriminatorValue("REAL_GAME")
@TypeDef(name = "rectangle", typeClass = RectangleUserType.class)
public class DbRealGameLevel extends DbAbstractLevel implements CrudParent {
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private DbConditionConfig dbConditionConfig;
    // ----- New Base -----
    private boolean createRealBase;
    @ManyToOne
    private DbBaseItemType startItemType;
    @Type(type = "rectangle")
    @Columns(columns = {@Column(name = "startX"), @Column(name = "startY"), @Column(name = "startWidth"), @Column(name = "startHeight")})
    private Rectangle startRectangle;
    private int startItemFreeRange;
    // ----- Scope -----
    private double itemSellFactor;
    private int houseSpace;
    // ----- Rewards -----
    private int deltaMoney;
    private int deltaXp;
    // ----- Limitations -----
    private int maxMoney;
    private int maxXp;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "dbRealGameLevel")
    private Set<DbItemTypeLimitation> itemTypeLimitation;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "GUIDANCE_LEVEL_ITEM_LIMITATION")
    private Set<MarketEntry> allowedMarketEntries;
    @Transient
    private CrudServiceHelper<DbItemTypeLimitation> dbItemTypeLimitationCrudServiceHelper;

    public DbConditionConfig getDbConditionConfig() {
        return dbConditionConfig;
    }

    public void setDbConditionConfig(DbConditionConfig dbConditionConfig) {
        this.dbConditionConfig = dbConditionConfig;
    }

    public int getHouseSpace() {
        return houseSpace;
    }

    public void setHouseSpace(int houseSpace) {
        this.houseSpace = houseSpace;
    }

    public int getDeltaMoney() {
        return deltaMoney;
    }

    public void setDeltaMoney(int deltaMoney) {
        this.deltaMoney = deltaMoney;
    }

    public DbItemType getStartItemType() {
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

    public boolean isCreateRealBase() {
        return createRealBase;
    }

    public void setCreateRealBase(boolean createRealBase) {
        this.createRealBase = createRealBase;
    }

    public double getItemSellFactor() {
        return itemSellFactor;
    }

    public void setItemSellFactor(double itemSellFactor) {
        this.itemSellFactor = itemSellFactor;
    }

    public int getDeltaXp() {
        return deltaXp;
    }

    public void setDeltaXp(int deltaXp) {
        this.deltaXp = deltaXp;
    }

    public int getMaxMoney() {
        return maxMoney;
    }

    public void setMaxMoney(int maxMoney) {
        this.maxMoney = maxMoney;
    }

    public int getMaxXp() {
        return maxXp;
    }

    public void setMaxXp(int maxXp) {
        this.maxXp = maxXp;
    }

    @Override
    protected ConditionConfig createConditionConfig(ItemService itemService) {
        if (dbConditionConfig == null) {
            throw new IllegalStateException("No condition config for DbRealGameLevel: " + getName());
        }
        return dbConditionConfig.createConditionConfig(itemService);
    }

    public Level createLevel() {
        Map<Integer, Integer> itemTypeLimitation = new HashMap<Integer, Integer>();
        for (DbItemTypeLimitation dbItemTypeLimitation : this.itemTypeLimitation) {
            itemTypeLimitation.put(dbItemTypeLimitation.getDbBaseItemType().getId(), dbItemTypeLimitation.getCount());
        }
        return new Level(getName(), getHtml(), true, maxMoney, itemTypeLimitation, houseSpace);

    }

    @Override
    public String getDisplayType() {
        return "Real Game";
    }

    public CrudServiceHelper<DbItemTypeLimitation> getDbItemTypeLimitationCrudServiceHelper() {
        if (dbItemTypeLimitationCrudServiceHelper == null) {
            dbItemTypeLimitationCrudServiceHelper = new CrudServiceHelperCollectionImpl<DbItemTypeLimitation>(itemTypeLimitation, DbItemTypeLimitation.class, this);
        }
        return dbItemTypeLimitationCrudServiceHelper;
    }
}
