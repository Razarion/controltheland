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
 * Date: 21.07.2010
 * Time: 21:06:41
 */
public class HarvestConditionConfig extends AbstractConditionConfig {
    private int amount;

    /**
     * Used by GWT
     */
    public HarvestConditionConfig() {
    }

    public HarvestConditionConfig(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HarvestConditionConfig)) return false;

        HarvestConditionConfig that = (HarvestConditionConfig) o;

        return amount == that.amount;
    }

    @Override
    public int hashCode() {
        return amount;
    }
}