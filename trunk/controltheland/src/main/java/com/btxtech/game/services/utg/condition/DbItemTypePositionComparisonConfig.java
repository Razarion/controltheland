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

package com.btxtech.game.services.utg.condition;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.utg.config.AbstractComparisonConfig;
import com.btxtech.game.jsre.common.utg.config.ItemTypePositionComparisonConfig;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.common.DateUtil;
import com.btxtech.game.services.common.db.RectangleUserType;
import com.btxtech.game.services.item.ItemService;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: beat
 * Date: 30.03.2011
 * Time: 16:31:29
 */
@Entity
@DiscriminatorValue("ITEM_TYPE_POSITION")
@TypeDef(name = "rectangle", typeClass = RectangleUserType.class)
public class DbItemTypePositionComparisonConfig extends DbAbstractComparisonConfig implements CrudParent {
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "dbItemTypePositionComparisonConfig", orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Set<DbComparisonItemCount> dbComparisonItemCounts;
    @Type(type = "rectangle")
    @Columns(columns = {@Column(name = "regionX"), @Column(name = "regionY"), @Column(name = "regionWidth"), @Column(name = "regionHeight")})
    private Rectangle region;
    private Integer timeInMinutes;
    private boolean addExistingItems;

    @Transient
    private CrudChildServiceHelper<DbComparisonItemCount> dbComparisonItemCountCrudServiceHelper;

    public Rectangle getRegion() {
        return region;
    }

    public void setRegion(Rectangle region) {
        this.region = region;
    }

    public Integer getTimeInMinutes() {
        return timeInMinutes;
    }

    public void setTimeInMinutes(Integer timeInMinutes) {
        this.timeInMinutes = timeInMinutes;
    }

    public boolean isAddExistingItems() {
        return addExistingItems;
    }

    public void setAddExistingItems(boolean addExistingItems) {
        this.addExistingItems = addExistingItems;
    }

    public CrudChildServiceHelper<DbComparisonItemCount> getCrudDbComparisonItemCount() {
        if (dbComparisonItemCountCrudServiceHelper == null) {
            if (dbComparisonItemCounts == null) {
                dbComparisonItemCounts = new HashSet<DbComparisonItemCount>();
            }
            dbComparisonItemCountCrudServiceHelper = new CrudChildServiceHelper<DbComparisonItemCount>(dbComparisonItemCounts, DbComparisonItemCount.class, this);
        }
        return dbComparisonItemCountCrudServiceHelper;
    }

    @Override
    public AbstractComparisonConfig createComparisonConfig(ItemService itemService) {
        Map<ItemType, Integer> itemTypeCount = new HashMap<ItemType, Integer>();
        for (DbComparisonItemCount dbComparisonItemCount : getCrudDbComparisonItemCount().readDbChildren()) {
            itemTypeCount.put(itemService.getItemType(dbComparisonItemCount.getItemType()), dbComparisonItemCount.getCount());
        }
        Integer timeInMs = timeInMinutes == null ? null : (int) (timeInMinutes * DateUtil.MILLIS_IN_MINUTE);
        return new ItemTypePositionComparisonConfig(getExcludedTerritoryId(), itemTypeCount, region, timeInMs, addExistingItems);
    }

    @Override
    protected DbAbstractComparisonConfig createCopy() {
        throw new UnsupportedOperationException();
    }
}
