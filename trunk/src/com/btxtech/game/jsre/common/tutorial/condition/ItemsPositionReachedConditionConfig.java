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

package com.btxtech.game.jsre.common.tutorial.condition;

import com.btxtech.game.jsre.client.common.Rectangle;
import java.util.Collection;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 21:06:41
 */
public class ItemsPositionReachedConditionConfig extends AbstractConditionConfig {
    private Collection<Integer> ids;
    private Rectangle region;

    /**
     * Used by GWT
     */
    public ItemsPositionReachedConditionConfig() {
    }

    public ItemsPositionReachedConditionConfig(Collection<Integer> ids, Rectangle region) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Ids are needed in position reached condition");
        }
        this.ids = ids;
        this.region = region;
    }

    public Collection<Integer> getIds() {
        return ids;
    }

    public Rectangle getRegion() {
        return region;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemsPositionReachedConditionConfig)) return false;

        ItemsPositionReachedConditionConfig that = (ItemsPositionReachedConditionConfig) o;

        return ids.equals(that.ids) && region.equals(that.region);

    }

    @Override
    public int hashCode() {
        int result = ids.hashCode();
        result = 31 * result + region.hashCode();
        return result;
    }
}