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

package com.btxtech.game.jsre.common.utg.condition;

import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;

/**
 * User: beat
 * Date: 12.01.2011
 * Time: 11:25:19
 */
public class ContainedInTrigger<T> extends AbstractConditionTrigger<T> {

    public ContainedInTrigger(AbstractComparison abstractComparison, T t) {
        super(ConditionTrigger.CONTAINED_IN, abstractComparison, t);
    }

    public void onContainedInChanged(boolean containedIn) {
        if (isFulfilled()) {
            return;
        }
        ContainedInComparison containedInComparison = (ContainedInComparison) getAbstractComparison();
        containedInComparison.onContainedInChanged(containedIn);

        if (containedInComparison.isFulfilled()) {
            setFulfilled();
        }
    }
}
