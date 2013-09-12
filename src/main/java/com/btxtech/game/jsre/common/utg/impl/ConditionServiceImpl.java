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

import com.btxtech.game.jsre.client.cockpit.quest.QuestProgressInfo;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.utg.ConditionService;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.jsre.common.utg.condition.AbstractComparison;
import com.btxtech.game.jsre.common.utg.condition.AbstractConditionTrigger;
import com.btxtech.game.jsre.common.utg.condition.AbstractUpdatingComparison;
import com.btxtech.game.jsre.common.utg.condition.SimpleConditionTrigger;
import com.btxtech.game.jsre.common.utg.condition.SyncItemConditionTrigger;
import com.btxtech.game.jsre.common.utg.condition.TimeAware;
import com.btxtech.game.jsre.common.utg.condition.ValueConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat Date: 27.12.2010 Time: 17:21:24
 * <p/>
 * A: Actor I: Identifier
 */
public abstract class ConditionServiceImpl<A, I> implements ConditionService<A, I> {
    private ConditionServiceListener<A, I> conditionServiceListener;
    private Collection<TimeAware> timeAwareList = new ArrayList<TimeAware>();
    private Logger log = Logger.getLogger(ConditionServiceImpl.class.getName());

    protected abstract void saveAbstractConditionTrigger(AbstractConditionTrigger<A, I> abstractConditionTrigger);

    protected abstract AbstractConditionTrigger<A, I> removeActorConditionsPrivate(A actor, I identifier);

    protected abstract AbstractConditionTrigger<A, I> getActorConditionsPrivate(A actor, I identifier);

    protected abstract Collection<AbstractConditionTrigger<A, I>> removeAllActorConditionsPrivate(A a);

    protected abstract void removeAllConditionsPrivate();

    protected abstract Collection<AbstractConditionTrigger<A, I>> getAbstractConditionPrivate(A actor, ConditionTrigger conditionTrigger);

    protected abstract GlobalServices getGlobalServices();

    protected abstract PlanetServices getPlanetServices(A a);

    protected abstract A getActor(SimpleBase actorBase);

    protected abstract SimpleBase getSimpleBase(A actorBase);

    protected abstract void startTimer();

    protected abstract void stopTimer();

    @Override
    public void setConditionServiceListener(ConditionServiceListener<A, I> conditionServiceListener) {
        this.conditionServiceListener = conditionServiceListener;
    }

    @Override
    public void activateCondition(ConditionConfig conditionConfig, A a, I i) {
        if (conditionConfig == null) {
            return;
        }
        AbstractComparison abstractComparison = null;
        if (conditionConfig.getConditionTrigger().isComparisonNeeded()) {
            abstractComparison = conditionConfig.getAbstractComparisonConfig().createAbstractComparison(getPlanetServices(a), getSimpleBase(a));
            if (abstractComparison instanceof AbstractUpdatingComparison) {
                ((AbstractUpdatingComparison) abstractComparison).setGlobalServices(getGlobalServices());
            }
            if (abstractComparison instanceof TimeAware && ((TimeAware) abstractComparison).isTimerNeeded()) {
                timeAwareList.add((TimeAware) abstractComparison);
                if (timeAwareList.size() == 1) {
                    startTimer();
                }
            }
        }
        AbstractConditionTrigger<A, I> abstractConditionTrigger = conditionConfig.getConditionTrigger().createAbstractConditionTrigger(abstractComparison);
        abstractConditionTrigger.setActorAndIdentifier(a, i);
        saveAbstractConditionTrigger(abstractConditionTrigger);

        if (abstractConditionTrigger.isFulfilled()) {
            conditionPassed(abstractConditionTrigger);
        }
    }

    @Override
    public void deactivateActorCondition(A a, I i) {
        AbstractConditionTrigger<A, I> abstractConditionTrigger = removeActorConditionsPrivate(a, i);
        if (abstractConditionTrigger != null) {
            handleTimerRemoval(abstractConditionTrigger);
        }
    }

    @Override
    public void deactivateAllActorConditions(A a) {
        Collection<AbstractConditionTrigger<A, I>> abstractConditionTriggers = removeAllActorConditionsPrivate(a);
        for (AbstractConditionTrigger<A, I> abstractConditionTrigger : abstractConditionTriggers) {
            handleTimerRemoval(abstractConditionTrigger);
        }
    }

    @Override
    public void deactivateAll() {
        stopTimer();
        timeAwareList.clear();
        removeAllConditionsPrivate();
    }

    @Override
    public QuestProgressInfo getQuestProgressInfo(A a, I i) {
        try {
            AbstractConditionTrigger<A, I> abstractConditionTrigger = getActorConditionsPrivate(a, i);
            QuestProgressInfo questProgressInfo = new QuestProgressInfo(abstractConditionTrigger.getConditionTrigger());
            abstractConditionTrigger.getAbstractComparison().fillQuestProgressInfo(questProgressInfo, this);
            return questProgressInfo;
        } catch (Exception e) {
            log.log(Level.SEVERE, "ConditionServiceImpl.getProgressHtml()", e);
            return null;
        }
    }

    private void handleTimerRemoval(AbstractConditionTrigger<A, I> abstractConditionTrigger) {
        AbstractComparison abstractComparison = abstractConditionTrigger.getAbstractComparison();
        if (abstractComparison instanceof TimeAware) {
            TimeAware timeAware = (TimeAware) abstractComparison;
            boolean wasRemoved = timeAwareList.remove(timeAware);
            if (wasRemoved && timeAwareList.size() == 0) {
                stopTimer();
            }
        }
    }

    @Override
    public void onSyncItemKilled(SimpleBase actorBase, SyncBaseItem killedItem) {
        A actor = getActor(actorBase);
        if (isActorIgnored(actor, true)) {
            return;
        }
        triggerSyncItem(actor, ConditionTrigger.SYNC_ITEM_KILLED, killedItem);
        triggerSyncItem(actor, ConditionTrigger.SYNC_ITEM_POSITION, killedItem);
    }

    @Override
    public void onSyncItemBuilt(SyncBaseItem syncBaseItem) {
        A actor = getActor(syncBaseItem.getBase());
        if (isActorIgnored(actor, true)) {
            return;
        }
        triggerSyncItem(actor, ConditionTrigger.SYNC_ITEM_BUILT, syncBaseItem);
        triggerSyncItem(actor, ConditionTrigger.SYNC_ITEM_POSITION, syncBaseItem);
    }

    @Override
    public void onSyncItemDeactivated(SyncBaseItem syncBaseItem) {
        A actor = getActor(syncBaseItem.getBase());
        if (isActorIgnored(actor, true)) {
            return;
        }
        if (syncBaseItem.isReady()) {
            triggerSyncItem(actor, ConditionTrigger.SYNC_ITEM_POSITION, syncBaseItem);
        }
    }

    @Override
    public void onSyncItemUnloaded(SyncBaseItem syncBaseItem) {
        A actor = getActor(syncBaseItem.getBase());
        if (isActorIgnored(actor, true)) {
            return;
        }
        triggerSyncItem(actor, ConditionTrigger.SYNC_ITEM_POSITION, syncBaseItem);
    }

    @Override
    public void onMoneyIncrease(SimpleBase actorBase, double accountBalance) {
        A actor = getActor(actorBase);
        if (isActorIgnored(actor, true)) {
            return;
        }
        triggerValue(actor, ConditionTrigger.MONEY_INCREASED, accountBalance);
    }

    @Override
    public void onBaseDeleted(SimpleBase actorBase) {
        A actor = getActor(actorBase);
        if (isActorIgnored(actor, true)) {
            return;
        }
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

    protected void conditionPassed(AbstractConditionTrigger<A, I> abstractConditionTrigger) {
        deactivateActorCondition(abstractConditionTrigger.getActor(), abstractConditionTrigger.getIdentifier());
        if (conditionServiceListener != null) {
            conditionServiceListener.conditionPassed(abstractConditionTrigger.getActor(), abstractConditionTrigger.getIdentifier());
        }
    }

    protected Collection<AbstractConditionTrigger<A, I>> getAbstractConditions(A actor, ConditionTrigger conditionTrigger) {
        Collection<AbstractConditionTrigger<A, I>> abstractConditionTriggers = getAbstractConditionPrivate(actor, conditionTrigger);
        if (abstractConditionTriggers == null || abstractConditionTriggers.isEmpty()) {
            return null;
        }
        return abstractConditionTriggers;
    }

    protected void onTimer() {
        Collection<AbstractConditionTrigger<A, I>> triggers = new ArrayList<AbstractConditionTrigger<A, I>>();
        for (TimeAware timeAware : timeAwareList) {
            try {
                timeAware.onTimer();
                AbstractConditionTrigger<A, I> abstractConditionTrigger = timeAware.getAbstractConditionTrigger();
                if (abstractConditionTrigger.isFulfilled()) {
                    triggers.add(abstractConditionTrigger);
                }
            } catch (Exception e) {
                log.log(Level.SEVERE, "Exception in ConditionServiceImpl.onTimer()", e);
            }
        }
        for (AbstractConditionTrigger<A, I> trigger : triggers) {
            conditionPassed(trigger);
        }
    }

    protected boolean isActorIgnored(A actor, boolean planetInteraction) {
        return actor == null || isTriggerSuppressed(actor, planetInteraction);
    }

    protected boolean isTriggerSuppressed(A actor, boolean planetInteraction) {
        return false;
    }
}
