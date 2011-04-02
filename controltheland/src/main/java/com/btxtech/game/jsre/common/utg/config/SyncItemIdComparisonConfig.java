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

package com.btxtech.game.jsre.common.utg.config;

import com.btxtech.game.jsre.common.utg.condition.AbstractComparison;
import com.btxtech.game.jsre.common.utg.condition.SyncItemIdComparison;
import java.util.Collection;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 21:06:41
 */
public class SyncItemIdComparisonConfig implements AbstractComparisonConfig {
    private Integer excludedTerritoryId;
    private Collection<Integer> syncItemId;

    /**
     * Used by GWT
     */
    public SyncItemIdComparisonConfig() {
    }

    public SyncItemIdComparisonConfig(Integer excludedTerritoryId, Collection<Integer> syncItemId) {
        this.excludedTerritoryId = excludedTerritoryId;
        this.syncItemId = syncItemId;
    }

    @Override
    public AbstractComparison createAbstractComparison() {
        return new SyncItemIdComparison(excludedTerritoryId, syncItemId);
    }
}