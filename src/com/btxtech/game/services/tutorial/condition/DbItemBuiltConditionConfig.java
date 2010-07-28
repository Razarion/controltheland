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

package com.btxtech.game.services.tutorial.condition;

import com.btxtech.game.jsre.common.tutorial.condition.AbstractConditionConfig;
import com.btxtech.game.jsre.common.tutorial.condition.ItemBuiltConditionConfig;
import com.btxtech.game.services.item.itemType.DbItemType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 26.07.2010
 * Time: 22:35:32
 */
@Entity
@DiscriminatorValue("ITEM_BUILD")
public class DbItemBuiltConditionConfig extends DbAbstractConditionConfig {
    @ManyToOne
    private DbItemType itemType;

    public DbItemType getItemType() {
        return itemType;
    }

    public void setItemType(DbItemType itemType) {
        this.itemType = itemType;
    }

    @Override
    public AbstractConditionConfig createConditionConfig() {
        return new ItemBuiltConditionConfig(itemType.getId());
    }
}
