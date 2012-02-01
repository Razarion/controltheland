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
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.utg.condition.AbstractComparison;
import com.btxtech.game.jsre.common.utg.condition.ItemTypePositionComparison;

import java.util.Map;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 21:06:41
 */
public class ItemTypePositionComparisonConfig implements AbstractComparisonConfig {
    private Integer excludedTerritoryId;
    private Map<ItemType, Integer> itemTypes;
    private Rectangle region;
    private Integer time;

    /**
     * Used by GWT
     */
    public ItemTypePositionComparisonConfig() {
    }

    public ItemTypePositionComparisonConfig(Integer excludedTerritoryId, Map<ItemType, Integer> itemTypes, Rectangle region, Integer time) {
        this.excludedTerritoryId = excludedTerritoryId;
        this.itemTypes = itemTypes;
        this.region = region;
        this.time = time;
    }

    @Override
    public AbstractComparison createAbstractComparison() {
        return new ItemTypePositionComparison(excludedTerritoryId, itemTypes, region, time);
    }
}