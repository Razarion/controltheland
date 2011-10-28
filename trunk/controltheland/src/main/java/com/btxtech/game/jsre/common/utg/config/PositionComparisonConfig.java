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
import com.btxtech.game.jsre.common.utg.condition.PositionComparison;
import com.btxtech.game.jsre.common.utg.condition.SyncItemIdPositionComparison;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 21:06:41
 */
public class PositionComparisonConfig implements AbstractComparisonConfig {
    private Rectangle region;

    /**
     * Used by GWT
     */
    public PositionComparisonConfig() {
    }

    public PositionComparisonConfig(Rectangle region) {
        this.region = region;
    }

    @Override
    public AbstractComparison createAbstractComparison() {
        return new PositionComparison(region);
    }
}