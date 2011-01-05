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

package com.btxtech.game.services.utg.condition;

import com.btxtech.game.jsre.common.utg.config.AbstractComparisonConfig;
import com.btxtech.game.services.item.ItemService;
import java.io.Serializable;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * User: beat
 * Date: 31.12.2010
 * Time: 22:51:15
 */
@Entity(name = "GUIDANCE_COMPARISON")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class DbAbstractComparisonConfig implements Serializable{
    @Id
    @GeneratedValue
    private Integer id;

    public abstract AbstractComparisonConfig createComparisonConfig(ItemService itemService);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbAbstractComparisonConfig)) return false;

        DbAbstractComparisonConfig that = (DbAbstractComparisonConfig) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
