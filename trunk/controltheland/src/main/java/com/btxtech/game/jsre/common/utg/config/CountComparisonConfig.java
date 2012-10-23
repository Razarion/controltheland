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

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.utg.condition.AbstractComparison;
import com.btxtech.game.jsre.common.utg.condition.CountComparison;

/**
 * User: beat
 * Date: 01.01.2011
 * Time: 14:04:29
 */
public class CountComparisonConfig implements AbstractComparisonConfig {
    private int count;

    /**
     * Used by GWT
     */
    protected CountComparisonConfig() {
    }

    public CountComparisonConfig(int count) {
        this.count = count;
    }

    @Override
    public AbstractComparison createAbstractComparison(PlanetServices planetServices, SimpleBase simpleBase) {
        return new CountComparison(count);
    }
}
