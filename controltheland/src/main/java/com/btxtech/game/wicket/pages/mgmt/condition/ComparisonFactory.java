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
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.services.utg.condition.DbCountComparisonConfig;
import com.btxtech.game.services.utg.condition.DbItemTypePositionComparisonConfig;
import com.btxtech.game.services.utg.condition.DbSyncItemTypeComparisonConfig;
import com.btxtech.game.wicket.uiservices.TerrainLinkHelper;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * User: beat
 * Date: 12.01.2011
 * Time: 20:04:51
 */
public class ComparisonFactory {
    public enum ComparisonClass {
        SYNC_ITEM_KILLED(ConditionTrigger.SYNC_ITEM_KILLED, DbSyncItemTypeComparisonConfig.class, DbCountComparisonConfig.class),
        SYNC_ITEM_BUILT(ConditionTrigger.SYNC_ITEM_BUILT, DbSyncItemTypeComparisonConfig.class, DbCountComparisonConfig.class),
        MONEY_INCREASED(ConditionTrigger.MONEY_INCREASED, DbCountComparisonConfig.class),
        XP_INCREASED(ConditionTrigger.XP_INCREASED, DbCountComparisonConfig.class),
        BASE_DELETED(ConditionTrigger.BASE_KILLED, DbCountComparisonConfig.class),
        SYNC_ITEM_POSITION(ConditionTrigger.SYNC_ITEM_POSITION, DbItemTypePositionComparisonConfig.class);

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
                return Collections.emptyList();
            }
            for (ComparisonClass comparisonClass : values()) {
                if (comparisonClass.conditionTrigger == conditionTrigger) {
                    return comparisonClass.comparisons;
                }
            }
            throw new IllegalStateException(conditionTrigger + " not found");
        }
    }

    public static Component createComparisonPanel(String id, DbConditionConfig dbConditionConfig, TerrainLinkHelper terrainLinkHelper) {
        if (dbConditionConfig == null || dbConditionConfig.getDbAbstractComparisonConfig() == null) {
            return new Label(id, "").setVisible(false);
        } else {
            DbAbstractComparisonConfig config = dbConditionConfig.getDbAbstractComparisonConfig();
            if (config instanceof DbSyncItemTypeComparisonConfig) {
                return new SyncItemTypeComparisonConfigPanel(id);
            } else if (config instanceof DbCountComparisonConfig) {
                return new CountComparisonConfigPanel(id);
            } else if (config instanceof DbItemTypePositionComparisonConfig) {
                return new ItemTypePositionComparisonConfigPanel(id, terrainLinkHelper);
            } else {
                throw new IllegalArgumentException("No panel for " + config);
            }
        }
    }

    public static List<ConditionTrigger> getFilteredConditionTriggers() {
        List<ConditionTrigger> result = new ArrayList<ConditionTrigger>(Arrays.asList(ConditionTrigger.values()));
        result.remove(ConditionTrigger.TUTORIAL);
        return result;
    }

}
