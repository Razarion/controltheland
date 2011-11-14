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

import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncTickItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.utg.ConditionService;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.jsre.common.utg.condition.AbstractComparison;
import com.btxtech.game.jsre.common.utg.condition.AbstractConditionTrigger;
import com.btxtech.game.jsre.common.utg.condition.AbstractSyncItemComparison;
import com.btxtech.game.jsre.common.utg.condition.CockpitButtonTrigger;
import com.btxtech.game.jsre.common.utg.condition.ContainedInTrigger;
import com.btxtech.game.jsre.common.utg.condition.PositionConditionTrigger;
import com.btxtech.game.jsre.common.utg.condition.SimpleConditionTrigger;
import com.btxtech.game.jsre.common.utg.condition.SyncItemConditionTrigger;
import com.btxtech.game.jsre.common.utg.condition.ValueConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.google.gwt.event.dom.client.ClickEvent;

/**
 * User: beat
 * Date: 27.12.2010
 * Time: 17:21:24
 */
public abstract class ConditionServiceImpl<T> implements ConditionService<T> {
    private ConditionServiceListener<T> conditionServiceListener;

    protected abstract void saveAbstractConditionTrigger(AbstractConditionTrigger<T> abstractConditionTrigger);

    protected abstract AbstractConditionTrigger<T> getAbstractConditionPrivate(SimpleBase simpleBase, ConditionTrigger conditionTrigger);

    protected abstract Services getServices();

    protected void cleanup() {

    }

    protected void conditionPassed(T t) {
        cleanup();
        if (conditionServiceListener != null) {
            conditionServiceListener.conditionPassed(t);
        }
    }

    public void setConditionServiceListener(ConditionServiceListener<T> conditionServiceListener) {
        this.conditionServiceListener = conditionServiceListener;
    }

    @Override
    public AbstractConditionTrigger<T> activateCondition(ConditionConfig conditionConfig, T t) {
        AbstractComparison abstractComparison = null;
        if (conditionConfig.getConditionTrigger().isComparisonNeeded()) {
            abstractComparison = conditionConfig.getAbstractComparisonConfig().createAbstractComparison();
            if (abstractComparison instanceof AbstractSyncItemComparison) {
                ((AbstractSyncItemComparison) abstractComparison).setServices(getServices());
            }
        }
        AbstractConditionTrigger<T> abstractConditionTrigger = conditionConfig.getConditionTrigger().createAbstractConditionTrigger(abstractComparison, t);
        saveAbstractConditionTrigger(abstractConditionTrigger);
        return abstractConditionTrigger;
    }

    private <U extends AbstractConditionTrigger<T>> U getAbstractCondition(SimpleBase actor, ConditionTrigger conditionTrigger) {
        AbstractConditionTrigger<T> abstractConditionTrigger = getAbstractConditionPrivate(actor, conditionTrigger);
        if (abstractConditionTrigger == null) {
            return null;
        }
        return (U) abstractConditionTrigger;
    }

    protected void triggerSyncItem(SimpleBase actor, ConditionTrigger conditionTrigger, SyncBaseItem syncBaseItem) {
        SyncItemConditionTrigger<T> syncItemConditionTrigger = getAbstractCondition(actor, conditionTrigger);
        if (syncItemConditionTrigger == null) {
            return;
        }
        syncItemConditionTrigger.onItem(actor, syncBaseItem);
        if (syncItemConditionTrigger.isFulfilled()) {
            conditionPassed(syncItemConditionTrigger.getUserObject());
        }
    }

    protected void triggerSimple(ConditionTrigger conditionTrigger) {
        SimpleConditionTrigger<T> simpleConditionTrigger = getAbstractCondition(null, conditionTrigger);
        if (simpleConditionTrigger == null) {
            return;
        }
        simpleConditionTrigger.onTrigger();
        if (simpleConditionTrigger.isFulfilled()) {
            conditionPassed(simpleConditionTrigger.getUserObject());
        }
    }

    private void triggerValue(SimpleBase actor, ConditionTrigger conditionTrigger, double value) {
        ValueConditionTrigger<T> valueConditionTrigger = getAbstractCondition(actor, conditionTrigger);
        if (valueConditionTrigger == null) {
            return;
        }
        valueConditionTrigger.onTriggerValue(value);
        if (valueConditionTrigger.isFulfilled()) {
            conditionPassed(valueConditionTrigger.getUserObject());
        }
    }

    private void triggerPosition(ConditionTrigger conditionTrigger, Index position) {
        PositionConditionTrigger<T> positionConditionTrigger = getAbstractCondition(null, conditionTrigger);
        if (positionConditionTrigger == null) {
            return;
        }
        positionConditionTrigger.onPosition(position);
        if (positionConditionTrigger.isFulfilled()) {
            conditionPassed(positionConditionTrigger.getUserObject());
        }
    }

    //------ Client ------

    @Override
    public void onOwnSelectionChanged(Group selectedGroup) {
        SyncBaseItem syncBaseItem = selectedGroup.getFirst().getSyncBaseItem();
        triggerSyncItem(syncBaseItem.getBase(), ConditionTrigger.SYNC_ITEM_SELECT, syncBaseItem);
    }

    @Override
    public void onSendCommand(SyncBaseItem syncItem, BaseCommand baseCommand) {
        // TODO
    }

    @Override
    public void onSyncItemDeactivated(SyncTickItem syncTickItem) {
        if (syncTickItem instanceof SyncBaseItem) {
            SyncBaseItem syncBaseItem = (SyncBaseItem) syncTickItem;
            triggerSyncItem(syncBaseItem.getBase(), ConditionTrigger.SYNC_ITEM_DEACTIVATE, syncBaseItem);
        }
    }

    @Override
    public void onScroll(int left, int top, int width, int height, int deltaLeft, int deltaTop) {
        triggerSimple(ConditionTrigger.SCROLL);
        Index position = new Index(left, top);
        position = position.add(width / 2, height / 2);
        triggerPosition(ConditionTrigger.SCROLL_TO_POSITION, position);
    }

    @Override
    public void onClick(ClickEvent event) {
        CockpitButtonTrigger<T> cockpitButtonTrigger = getAbstractCondition(null, ConditionTrigger.COCKPIT_BUTTON_EVENT);
        if (cockpitButtonTrigger == null) {
            return;
        }
        cockpitButtonTrigger.onClick(event);
        if (cockpitButtonTrigger.isFulfilled()) {
            conditionPassed(cockpitButtonTrigger.getUserObject());
        }
    }


    public void onContainedInChanged(boolean containedIn) {
        ContainedInTrigger<T> containedInTrigger = getAbstractCondition(null, ConditionTrigger.CONTAINED_IN);
        if (containedInTrigger == null) {
            return;
        }
        containedInTrigger.onContainedInChanged(containedIn);
        if (containedInTrigger.isFulfilled()) {
            conditionPassed(containedInTrigger.getUserObject());
        }
    }


    //------ Server------

    @Override
    public void onIncreaseXp(SimpleBase base, int xp) {
        triggerValue(base, ConditionTrigger.XP_INCREASED, xp);
    }

    //------ Both Client and Server ------

    @Override
    public void onSyncItemKilled(SimpleBase actor, SyncBaseItem killedItem) {
        triggerSyncItem(actor, ConditionTrigger.SYNC_ITEM_KILLED, killedItem);
    }

    @Override
    public void onSyncItemBuilt(SyncBaseItem syncBaseItem) {
        triggerSyncItem(syncBaseItem.getBase(), ConditionTrigger.SYNC_ITEM_BUILT, syncBaseItem);
    }

    @Override
    public void onMoneyIncrease(SimpleBase base, double accountBalance) {
        triggerValue(base, ConditionTrigger.MONEY_INCREASED, accountBalance);
    }

    @Override
    public void onBaseDeleted(SimpleBase actorBase) {
        triggerValue(actorBase, ConditionTrigger.BASE_DELETED, 1.0);
    }

    //------ Only used for fail ------

    @Override
    public void onWithdrawalMoney() {
        // TODO
    }
}
