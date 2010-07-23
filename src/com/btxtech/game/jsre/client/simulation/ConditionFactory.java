/*
 * Copyright (c) 2010.
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

package com.btxtech.game.jsre.client.simulation;

import com.btxtech.game.jsre.client.simulation.condition.AbstractCondition;
import com.btxtech.game.jsre.client.simulation.condition.HarvestCondition;
import com.btxtech.game.jsre.client.simulation.condition.ItemBuiltCondition;
import com.btxtech.game.jsre.client.simulation.condition.ItemsKilledCondition;
import com.btxtech.game.jsre.client.simulation.condition.ItemsPositionReachedCondition;
import com.btxtech.game.jsre.client.simulation.condition.SelectionCondition;
import com.btxtech.game.jsre.client.simulation.condition.SendCommandCondition;
import com.btxtech.game.jsre.common.tutorial.condition.AbstractConditionConfig;
import com.btxtech.game.jsre.common.tutorial.condition.HarvestConditionConfig;
import com.btxtech.game.jsre.common.tutorial.condition.ItemBuiltConditionConfig;
import com.btxtech.game.jsre.common.tutorial.condition.ItemsKilledConditionConfig;
import com.btxtech.game.jsre.common.tutorial.condition.ItemsPositionReachedConditionConfig;
import com.btxtech.game.jsre.common.tutorial.condition.SelectionConditionConfig;
import com.btxtech.game.jsre.common.tutorial.condition.SendCommandConditionConfig;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 19:00:21
 */
public class ConditionFactory {
    public static AbstractCondition createCondition(AbstractConditionConfig abstractConditionConfig) {
        if (abstractConditionConfig == null) {
            return null;
        }
        if (abstractConditionConfig instanceof SendCommandConditionConfig) {
            return new SendCommandCondition((SendCommandConditionConfig) abstractConditionConfig);
        } else if (abstractConditionConfig instanceof SelectionConditionConfig) {
            return new SelectionCondition((SelectionConditionConfig) abstractConditionConfig);
        } else if (abstractConditionConfig instanceof ItemsPositionReachedConditionConfig) {
            return new ItemsPositionReachedCondition((ItemsPositionReachedConditionConfig) abstractConditionConfig);
        } else if (abstractConditionConfig instanceof ItemsKilledConditionConfig) {
            return new ItemsKilledCondition((ItemsKilledConditionConfig) abstractConditionConfig);
        } else if (abstractConditionConfig instanceof ItemBuiltConditionConfig) {
            return new ItemBuiltCondition((ItemBuiltConditionConfig) abstractConditionConfig);
        } else if (abstractConditionConfig instanceof HarvestConditionConfig) {
            return new HarvestCondition((HarvestConditionConfig) abstractConditionConfig);
        } else {
            throw new IllegalArgumentException("Unknown condition config: " + abstractConditionConfig);
        }
    }
}
