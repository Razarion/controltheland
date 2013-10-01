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

import com.btxtech.game.jsre.common.utg.config.AbstractComparisonConfig;
import com.btxtech.game.jsre.common.utg.config.CountComparisonConfig;
import com.btxtech.game.services.item.ServerItemTypeService;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * User: beat
 * Date: 27.12.2010
 * Time: 16:31:29
 */
@Entity
@DiscriminatorValue("COUNT")
public class DbCountComparisonConfig extends DbAbstractComparisonConfig {
    @Column(name = "theCount")
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public AbstractComparisonConfig createComparisonConfig(ServerItemTypeService serverItemTypeService) {
        return new CountComparisonConfig(count);
    }

    @Override
    protected DbAbstractComparisonConfig createCopy() {
        DbCountComparisonConfig copy = new DbCountComparisonConfig();
        copy.setCount(count);
        return copy;
    }
}
