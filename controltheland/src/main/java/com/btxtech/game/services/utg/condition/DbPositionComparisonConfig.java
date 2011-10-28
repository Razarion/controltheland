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
import com.btxtech.game.jsre.common.utg.config.PositionComparisonConfig;
import com.btxtech.game.services.item.ItemService;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * User: beat
 * Date: 28.10.2010
 * Time: 16:31:29
 */
@Entity
@DiscriminatorValue("POSITION")
public class DbPositionComparisonConfig extends DbAbstractComparisonConfig {
    @Type(type = "rectangle")
    @Columns(columns = {@Column(name = "regionX"), @Column(name = "regionY"), @Column(name = "regionWidth"), @Column(name = "regionHeight")})
    private Rectangle region;

    public Rectangle getRegion() {
        return region;
    }

    public void setRegion(Rectangle region) {
        this.region = region;
    }

    @Override
    public AbstractComparisonConfig createComparisonConfig(ItemService itemService) {
        return new PositionComparisonConfig(region);
    }

    @Override
    protected DbAbstractComparisonConfig createCopy() {
        DbPositionComparisonConfig copy = new DbPositionComparisonConfig();
        copy.setRegion(region);
        return copy;
    }
}
