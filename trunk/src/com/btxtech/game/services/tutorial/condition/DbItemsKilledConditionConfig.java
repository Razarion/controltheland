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
import com.btxtech.game.jsre.common.tutorial.condition.ItemsKilledConditionConfig;
import com.btxtech.game.services.common.Utils;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * User: beat
 * Date: 26.07.2010
 * Time: 22:41:09
 */
@Entity
@DiscriminatorValue("ITEMS_KILLED")
public class DbItemsKilledConditionConfig extends DbAbstractConditionConfig {
    private String idsString;

    public String getIdsString() {
        return idsString;
    }

    public void setIdsString(String idsString) {
        this.idsString = idsString;
    }

    @Override
    public AbstractConditionConfig createConditionConfig() {
        return new ItemsKilledConditionConfig(Utils.stringToIntegers(idsString));
    }
}
