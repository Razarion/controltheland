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
import com.btxtech.game.jsre.common.utg.condition.ContainedInComparison;

/**
 * User: beat
 * Date: 29.03.2011
 * Time: 14:04:29
 */
public class ContainedInComparisonConfig implements AbstractComparisonConfig {
    private boolean containedId;

    /**
     * Used by GWT
     */
    public ContainedInComparisonConfig() {
    }

    public ContainedInComparisonConfig(boolean containedId) {
        this.containedId = containedId;
    }

    @Override
    public AbstractComparison createAbstractComparison() {
        return new ContainedInComparison(containedId);
    }
}
