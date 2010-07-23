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

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 21:06:41
 */
public class ItemBuiltConditionConfig extends AbstractConditionConfig {
    private int itemTypeId;

    /**
     * Used by GWT
     */
    public ItemBuiltConditionConfig() {
    }

    public ItemBuiltConditionConfig(int itemTypeId) {
        this.itemTypeId = itemTypeId;
    }

    public int getItemTypeId() {
        return itemTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemBuiltConditionConfig)) return false;

        ItemBuiltConditionConfig that = (ItemBuiltConditionConfig) o;

        if (itemTypeId != that.itemTypeId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return itemTypeId;
    }
}