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

package com.btxtech.game.wicket.pages.mgmt.condition;

import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.services.utg.condition.DbAbstractComparisonConfig;
import com.btxtech.game.services.utg.condition.DbSyncItemTypeComparisonConfig;
import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 12.01.2011
 * Time: 20:04:51
 */
public class ComparisonFactory {
    public enum ComparisonClass {
        SYNC_ITEM_KILLED(ConditionTrigger.SYNC_ITEM_KILLED, DbSyncItemTypeComparisonConfig.class),
        TUTORIAL(ConditionTrigger.TUTORIAL),
        SYNC_ITEM_SELECT(ConditionTrigger.SYNC_ITEM_SELECT),
        SYNC_ITEM_DEACTIVATE(ConditionTrigger.SYNC_ITEM_DEACTIVATE),
        SYNC_ITEM_BUILT(ConditionTrigger.SYNC_ITEM_BUILT),
        SCROLL(ConditionTrigger.SCROLL),
        COCKPIT_BUTTON_EVENT(ConditionTrigger.COCKPIT_BUTTON_EVENT);

        private ConditionTrigger conditionTrigger;
        private List<Class<? extends DbAbstractComparisonConfig>> comparisons;

        ComparisonClass(ConditionTrigger conditionTrigger, Class<? extends DbAbstractComparisonConfig>... comparisonArray) {
            this.conditionTrigger = conditionTrigger;
            if (comparisonArray != null && comparisonArray.length > 0) {
                comparisons = new ArrayList<Class<? extends DbAbstractComparisonConfig>>();
                for (Class<? extends DbAbstractComparisonConfig> comparison : comparisonArray) {
                    comparisons.add(comparison);
                }
            }
        }

        public static List<Class<? extends DbAbstractComparisonConfig>> getClasses4ConditionTrigger(ConditionTrigger conditionTrigger) {
            if (conditionTrigger == null) {
                return null;
            }
            for (ComparisonClass comparisonClass : values()) {
                if (comparisonClass.conditionTrigger == conditionTrigger) {
                    return comparisonClass.comparisons;
                }
            }
            throw new IllegalStateException(conditionTrigger + " not found");
        }
    }
}
