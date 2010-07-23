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

import java.util.Collection;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 21:15:03
 */
public class SelectionConditionConfig extends AbstractConditionConfig {
    private Collection<Integer> ids;

    /**
     * Used by GWT
     */
    public SelectionConditionConfig() {
    }

    public SelectionConditionConfig(Collection<Integer> ids) {
        this.ids = ids;
    }

    public Collection<Integer> getIds() {
        return ids;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SelectionConditionConfig)) return false;

        SelectionConditionConfig that = (SelectionConditionConfig) o;

        if (!ids.equals(that.ids)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return ids.hashCode();
    }
}
