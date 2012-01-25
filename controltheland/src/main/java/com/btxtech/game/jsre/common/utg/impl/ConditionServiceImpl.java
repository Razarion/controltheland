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

package com.btxtech.game.jsre.common.utg.impl;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncTickItem;
import com.btxtech.game.jsre.common.utg.ConditionService;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.jsre.common.utg.condition.AbstractComparison;
import com.btxtech.game.jsre.common.utg.condition.AbstractConditionTrigger;
import com.btxtech.game.jsre.common.utg.condition.AbstractSyncItemComparison;
import com.btxtech.game.jsre.common.utg.condition.SimpleConditionTrigger;
import com.btxtech.game.jsre.common.utg.condition.SyncItemConditionTrigger;
import com.btxtech.game.jsre.common.utg.condition.ValueConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;

import java.util.Collection;

/**
 * User: beat
 * Date: 27.12.2010
 * Time: 17:21:24
 * <p/>
 * A: Actor
 * I: Identifier
 */
public abstract class ConditionServiceImpl<A, I> implements ConditionService<A, I> {
    private ConditionServiceListener<A, I> conditionServiceListener;

    protected abstract void saveAbstractConditionTrigger(AbstractConditionTrigger<A, I> abstractConditionTrigger);

    protected abstract Collection<AbstractConditionTrigger<A, I>> getAbstractConditionPrivate(A actor, ConditionTrigger conditionTrigger);

    protected abstract Services getServices();

    protected abstract A getActor(SimpleBase actorBase);

    @Override
    public void setConditionServiceListener(ConditionServiceListener<A, I> conditionServiceListener) {
        this.conditionServiceListener = conditionServiceListener;
    }

    @Override
    public AbstractConditionTrigger<A, I> activateCondition(ConditionConfig conditionConfig, A a, I i) {
        AbstractComparison abstractComparison = null;
        if (conditionConfig.getConditionTrigger().isComparisonNeeded()) {
            abstractComparison = conditionConfig.getAbstractComparisonConfig().createAbstractComparison();
            if (abstractComparison instanceof AbstractSyncItemComparison) {
                ((AbstractSyncItemComparison) abstractComparison).setServices(getServices());
            }
        }
        AbstractConditionTrigger<A, I> abstractConditionTrigger = conditionConfig.getConditionTrigger().createAbstractConditionTrigger(abstractComparison);
        abstractConditionTrigger.setActorAndIdentifier(a, i);
        saveAbstractConditionTrigger(abstractConditionTrigger);
        return abstractConditionTrigger;
    }

    @Override
    public void onSyncItemKilled(SimpleBase actorBase, SyncBaseItem killedItem) {
        A actor = getActor(actorBase);
        triggerSyncItem(actor, ConditionTrigger.SYNC_ITEM_KILLED, killedItem);
    }

    @Override
    public void onSyncItemBuilt(SyncBaseItem syncBaseItem) {
        A actor = getActor(syncBaseItem.getBase());
        triggerSyncItem(actor, ConditionTrigger.SYNC_ITEM_BUILT, syncBaseItem);
    }

    @Override
    public void onMoneyIncrease(SimpleBase actorBase, double accountBalance) {
        A actor = getActor(actorBase);
        triggerValue(actor, ConditionTrigger.MONEY_INCREASED, accountBalance);
    }

    @Override
    public void onBaseDeleted(SimpleBase actorBase) {
        A actor = getActor(actorBase);
        triggerValue(actor, ConditionTrigger.BASE_KILLED, 1.0);
    }

    protected void triggerSyncItem(A actor, ConditionTrigger conditionTrigger, SyncBaseItem syncBaseItem) {
        Collection<AbstractConditionTrigger<A, I>> abstractConditionTriggers = getAbstractConditions(actor, conditionTrigger);
        if (abstractConditionTriggers == null) {
            return;
        }
        for (AbstractConditionTrigger<A, I> abstractConditionTrigger : abstractConditionTriggers) {
            SyncItemConditionTrigger syncItemConditionTrigger = (SyncItemConditionTrigger) abstractConditionTrigger;
            syncItemConditionTrigger.onItem(syncBaseItem);
            if (syncItemConditionTrigger.isFulfilled()) {
                conditionPassed(abstractConditionTrigger);
            }
        }
    }

    protected void triggerSimple(Collection<AbstractConditionTrigger<A, I>> abstractConditionTriggers) {
        if (abstractConditionTriggers == null) {
            return;
        }
        for (AbstractConditionTrigger<A, I> abstractConditionTrigger : abstractConditionTriggers) {
            SimpleConditionTrigger simpleConditionTrigger = (SimpleConditionTrigger) abstractConditionTrigger;
            simpleConditionTrigger.onTrigger();
            if (simpleConditionTrigger.isFulfilled()) {
                conditionPassed(abstractConditionTrigger);
            }
        }
    }

    protected void triggerValue(A actor, ConditionTrigger conditionTrigger, double value) {
        Collection<AbstractConditionTrigger<A, I>> abstractConditionTriggers = getAbstractConditions(actor, conditionTrigger);
        if (abstractConditionTriggers == null) {
            return;
        }

        for (AbstractConditionTrigger<A, I> abstractConditionTrigger : abstractConditionTriggers) {
            ValueConditionTrigger valueConditionTrigger = (ValueConditionTrigger) abstractConditionTrigger;
            valueConditionTrigger.onTriggerValue(value);
            if (valueConditionTrigger.isFulfilled()) {
                conditionPassed(abstractConditionTrigger);
            }
        }
    }

    private void conditionPassed(AbstractConditionTrigger<A, I> abstractConditionTrigger) {
        deactivateActorConditions(abstractConditionTrigger.getActor(), abstractConditionTrigger.getIdentifier());
        if (conditionServiceListener != null) {
            conditionServiceListener.conditionPassed(abstractConditionTrigger.getActor(), abstractConditionTrigger.getIdentifier());
        }
    }

    private Collection<AbstractConditionTrigger<A, I>> getAbstractConditions(A actor, ConditionTrigger conditionTrigger) {
        Collection<AbstractConditionTrigger<A, I>> abstractConditionTriggers = getAbstractConditionPrivate(actor, conditionTrigger);
        if (abstractConditionTriggers == null || abstractConditionTriggers.isEmpty()) {
            return null;
        }
        return abstractConditionTriggers;
    }
}
