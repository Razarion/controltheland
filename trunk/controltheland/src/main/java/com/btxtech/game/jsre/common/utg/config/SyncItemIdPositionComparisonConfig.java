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

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.utg.condition.AbstractComparison;
import com.btxtech.game.jsre.common.utg.condition.SyncItemIdPositionComparison;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 21:06:41
 */
public class SyncItemIdPositionComparisonConfig implements AbstractComparisonConfig {
    private Integer excludedTerritoryId;
    private int syncItemId;
    private Rectangle region;

    /**
     * Used by GWT
     */
    public SyncItemIdPositionComparisonConfig() {
    }

    public SyncItemIdPositionComparisonConfig(Integer excludedTerritoryId, int syncItemId, Rectangle region) {
        this.excludedTerritoryId = excludedTerritoryId;
        this.syncItemId = syncItemId;
        this.region = region;
    }

    @Override
    public AbstractComparison createAbstractComparison() {
        return new SyncItemIdPositionComparison(excludedTerritoryId, syncItemId, region);
    }
}