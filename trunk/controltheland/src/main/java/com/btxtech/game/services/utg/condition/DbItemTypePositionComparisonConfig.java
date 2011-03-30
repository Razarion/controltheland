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
import com.btxtech.game.jsre.common.utg.config.AbstractComparisonConfig;
import com.btxtech.game.jsre.common.utg.config.ItemTypePositionComparisonConfig;
import com.btxtech.game.jsre.common.utg.config.SyncItemIdPositionComparisonConfig;
import com.btxtech.game.services.common.db.RectangleUserType;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbItemType;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 30.03.2011
 * Time: 16:31:29
 */
@Entity
@DiscriminatorValue("ITEM_TYPE_POSITION")
@TypeDef(name = "rectangle", typeClass = RectangleUserType.class)
public class DbItemTypePositionComparisonConfig extends DbAbstractComparisonConfig {
    @ManyToOne
    private DbItemType dbItemType;
    @Type(type = "rectangle")
    @Columns(columns = {@Column(name = "regionX"), @Column(name = "regionY"), @Column(name = "regionWidth"), @Column(name = "regionHeight")})
    private Rectangle region;

    public DbItemType getDbItemType() {
        return dbItemType;
    }

    public void setDbItemType(DbItemType dbItemType) {
        this.dbItemType = dbItemType;
    }

    public Rectangle getRegion() {
        return region;
    }

    public void setRegion(Rectangle region) {
        this.region = region;
    }

    @Override
    public AbstractComparisonConfig createComparisonConfig(ItemService itemService) {
        return new ItemTypePositionComparisonConfig(itemService.getItemType(dbItemType), region);
    }
}
