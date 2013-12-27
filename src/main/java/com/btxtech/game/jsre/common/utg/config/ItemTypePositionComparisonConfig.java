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

import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.utg.condition.AbstractComparison;
import com.btxtech.game.jsre.common.utg.condition.ItemTypePositionComparison;

import java.util.Map;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 21:06:41
 */
public class ItemTypePositionComparisonConfig implements AbstractComparisonConfig {
    private Map<ItemType, Integer> itemTypes;
    private Region region;
    private Integer time;
    private boolean addExistingItems;

    /**
     * Used by GWT
     */
    public ItemTypePositionComparisonConfig() {
    }

    public ItemTypePositionComparisonConfig(Map<ItemType, Integer> itemTypes, Region region, Integer time, boolean addExistingItems) {
        this.itemTypes = itemTypes;
        this.region = region;
        this.time = time;
        this.addExistingItems = addExistingItems;
    }

    @Override
    public AbstractComparison createAbstractComparison(PlanetServices planetServices, SimpleBase simpleBase) {
        if (planetServices == null) {
            throw new IllegalArgumentException("PlanetServices is not allowed to be null on a ItemTypePositionComparisonConfig");
        }
        return new ItemTypePositionComparison(itemTypes, region, time, addExistingItems, planetServices, simpleBase);
    }
}