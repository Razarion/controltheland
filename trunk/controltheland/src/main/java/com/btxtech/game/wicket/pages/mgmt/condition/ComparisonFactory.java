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
import com.btxtech.game.services.utg.condition.DbCockpitButtonClickedComparisonConfig;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.services.utg.condition.DbContainedInComparisonConfig;
import com.btxtech.game.services.utg.condition.DbCountComparisonConfig;
import com.btxtech.game.services.utg.condition.DbItemTypePositionComparisonConfig;
import com.btxtech.game.services.utg.condition.DbPositionComparisonConfig;
import com.btxtech.game.services.utg.condition.DbSyncItemIdComparisonConfig;
import com.btxtech.game.services.utg.condition.DbSyncItemIdPositionComparisonConfig;
import com.btxtech.game.services.utg.condition.DbSyncItemTypeComparisonConfig;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: beat
 * Date: 12.01.2011
 * Time: 20:04:51
 */
public class ComparisonFactory {
    public enum ComparisonClass {
        SYNC_ITEM_KILLED(ConditionTrigger.SYNC_ITEM_KILLED, DbSyncItemTypeComparisonConfig.class, DbSyncItemIdComparisonConfig.class, DbCountComparisonConfig.class),
        TUTORIAL(ConditionTrigger.TUTORIAL),
        SYNC_ITEM_SELECT(ConditionTrigger.SYNC_ITEM_SELECT, DbSyncItemTypeComparisonConfig.class, DbSyncItemIdComparisonConfig.class, DbCountComparisonConfig.class),
        SYNC_ITEM_DEACTIVATE(ConditionTrigger.SYNC_ITEM_DEACTIVATE, DbSyncItemIdPositionComparisonConfig.class, DbItemTypePositionComparisonConfig.class),
        SYNC_ITEM_BUILT(ConditionTrigger.SYNC_ITEM_BUILT, DbSyncItemTypeComparisonConfig.class, DbSyncItemIdComparisonConfig.class, DbCountComparisonConfig.class),
        SCROLL(ConditionTrigger.SCROLL),
        COCKPIT_BUTTON_EVENT(ConditionTrigger.COCKPIT_BUTTON_EVENT, DbCockpitButtonClickedComparisonConfig.class),
        MONEY_INCREASED(ConditionTrigger.MONEY_INCREASED, DbCountComparisonConfig.class),
        XP_INCREASED(ConditionTrigger.XP_INCREASED, DbCountComparisonConfig.class),
        CONTAINED_IN(ConditionTrigger.CONTAINED_IN, DbContainedInComparisonConfig.class),
        SCROLL_TO_POSITION(ConditionTrigger.SCROLL_TO_POSITION, DbPositionComparisonConfig.class),
        BASE_DELETED(ConditionTrigger.BASE_DELETED, DbCountComparisonConfig.class);

        private ConditionTrigger conditionTrigger;
        private List<Class<? extends DbAbstractComparisonConfig>> comparisons;

        ComparisonClass(ConditionTrigger conditionTrigger, Class<? extends DbAbstractComparisonConfig>... comparisonArray) {
            this.conditionTrigger = conditionTrigger;
            if (comparisonArray != null && comparisonArray.length > 0) {
                comparisons = new ArrayList<Class<? extends DbAbstractComparisonConfig>>();
                comparisons.addAll(Arrays.asList(comparisonArray));
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

    public static Component createComparisonPanel(DbConditionConfig dbConditionConfig, String id) {
        if (dbConditionConfig == null || dbConditionConfig.getDbAbstractComparisonConfig() == null) {
            return new Label(id, "").setVisible(false);
        } else {
            DbAbstractComparisonConfig config = dbConditionConfig.getDbAbstractComparisonConfig();
            if (config instanceof DbSyncItemTypeComparisonConfig) {
                return new SyncItemTypeComparisonConfigPanel(id);
            } else if (config instanceof DbCockpitButtonClickedComparisonConfig) {
                return new CockpitButtonClickedComparisonConfigPanel(id);
            } else if (config instanceof DbSyncItemIdComparisonConfig) {
                return new SyncItemIdComparisonConfigPanel(id);
            } else if (config instanceof DbSyncItemIdPositionComparisonConfig) {
                return new SyncItemIdPositionComparisonConfigPanel(id);
            } else if (config instanceof DbCountComparisonConfig) {
                return new CountComparisonConfigPanel(id);
            } else if (config instanceof DbContainedInComparisonConfig) {
                return new ContainedInComparisonConfigPanel(id);
            } else if (config instanceof DbItemTypePositionComparisonConfig) {
                return new ItemTypePositionComparisonConfigPanel(id);
            } else if (config instanceof DbPositionComparisonConfig) {
                return new PositionComparisonConfigPanel(id);
            } else {
                throw new IllegalArgumentException("No panel for " + config);
            }
        }
    }

}
