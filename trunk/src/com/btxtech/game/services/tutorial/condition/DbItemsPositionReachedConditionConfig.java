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

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.tutorial.condition.AbstractConditionConfig;
import com.btxtech.game.jsre.common.tutorial.condition.ItemsPositionReachedConditionConfig;
import com.btxtech.game.services.common.Utils;
import com.btxtech.game.services.common.db.RectangleUserType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

/**
 * User: beat
 * Date: 26.07.2010
 * Time: 22:41:27
 */
@Entity
@DiscriminatorValue("ITEMS_POSITION_REACHED")
@TypeDef(name = "rectangle", typeClass = RectangleUserType.class)
public class DbItemsPositionReachedConditionConfig extends DbAbstractConditionConfig {
    private String idsString;
    @Type(type = "rectangle")
    @Columns(columns = {@Column(name = "regionX"), @Column(name = "regionY"), @Column(name = "regionWidth"), @Column(name = "regionHeight")})
    private Rectangle region = new Rectangle(0, 0, 0, 0);

    public String getIdsString() {
        return idsString;
    }

    public void setIdsString(String idsString) {
        this.idsString = idsString;
    }

    public Rectangle getRegion() {
        return region;
    }

    public void setRegion(Rectangle region) {
        this.region = region;
    }

    @Override
    public AbstractConditionConfig createConditionConfig() {
        return new ItemsPositionReachedConditionConfig(Utils.stringToIntegers(idsString), region);
    }
}
