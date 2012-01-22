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

import com.btxtech.game.jsre.client.ClientServices;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.utg.condition.AbstractConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.impl.ConditionServiceImpl;

/**
 * User: beat
 * Date: 28.12.2010
 * Time: 13:09:19
 */
public class SimulationConditionServiceImpl extends ConditionServiceImpl<Object> {
    private static final SimulationConditionServiceImpl INSTANCE = new SimulationConditionServiceImpl();
    private AbstractConditionTrigger<Object> abstractConditionTrigger;

    public static SimulationConditionServiceImpl getInstance() {
        return INSTANCE;
    }

    @Override
    protected void saveAbstractConditionTrigger(AbstractConditionTrigger<Object> abstractConditionTrigger) {
        this.abstractConditionTrigger = abstractConditionTrigger;
    }

    @Override
    protected AbstractConditionTrigger<Object> getAbstractConditionPrivate(SimpleBase actor, ConditionTrigger conditionTrigger) {
        if (abstractConditionTrigger != null && abstractConditionTrigger.getConditionTrigger() == conditionTrigger) {
            return abstractConditionTrigger;
        } else {
            return null;
        }
    }

    @Override
    protected void cleanup() {
        abstractConditionTrigger = null;
    }

    @Override
    protected Services getServices() {
        return ClientServices.getInstance();
    }
}
