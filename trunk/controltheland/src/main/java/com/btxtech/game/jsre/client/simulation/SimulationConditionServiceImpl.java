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

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientServices;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.utg.condition.AbstractConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.impl.ConditionServiceImpl;

import java.util.Collection;
import java.util.Collections;

/**
 * User: beat
 * Date: 28.12.2010
 * Time: 13:09:19
 */
public class SimulationConditionServiceImpl extends ConditionServiceImpl<SimpleBase, Void> {
    private static final SimulationConditionServiceImpl INSTANCE = new SimulationConditionServiceImpl();
    private AbstractConditionTrigger<SimpleBase, Void> abstractConditionTrigger;

    public static SimulationConditionServiceImpl getInstance() {
        return INSTANCE;
    }

    @Override
    protected void saveAbstractConditionTrigger(AbstractConditionTrigger<SimpleBase, Void> abstractConditionTrigger) {
        if (!ClientBase.getInstance().isMyOwnBase(abstractConditionTrigger.getActor())) {
            throw new IllegalArgumentException("Only condition for own base cam be saved");
        }
        this.abstractConditionTrigger = abstractConditionTrigger;
    }

    @Override
    protected Collection<AbstractConditionTrigger<SimpleBase, Void>> getAbstractConditionPrivate(SimpleBase actor, ConditionTrigger conditionTrigger) {
        if (!ClientBase.getInstance().isMyOwnBase(actor)) {
            return null;
        }
        if (abstractConditionTrigger != null && abstractConditionTrigger.getConditionTrigger() == conditionTrigger) {
            return Collections.singletonList(abstractConditionTrigger);
        } else {
            return null;
        }
    }

    @Override
    protected SimpleBase getActor(SimpleBase actorBase) {
        return actorBase;
    }

    @Override
    public void deactivateActorConditions(SimpleBase actor, Void identifier) {
        if (!ClientBase.getInstance().isMyOwnBase(actor)) {
            return;
        }
        abstractConditionTrigger = null;
    }

    @Override
    protected Services getServices() {
        return ClientServices.getInstance();
    }
}
