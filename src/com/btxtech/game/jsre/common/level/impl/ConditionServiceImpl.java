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

package com.btxtech.game.jsre.common.level.impl;

import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncTickItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.level.ConditionService;
import com.btxtech.game.jsre.common.level.condition.AbstractComparison;
import com.btxtech.game.jsre.common.level.condition.AbstractConditionTrigger;
import com.btxtech.game.jsre.common.level.condition.KillConditionTrigger;
import com.btxtech.game.jsre.common.level.config.ConditionConfig;
import com.google.gwt.event.dom.client.ClickEvent;

/**
 * User: beat
 * Date: 27.12.2010
 * Time: 17:21:24
 */
public abstract class ConditionServiceImpl<T> implements ConditionService<T> {

    protected abstract void saveAbstractConditionTrigger(AbstractConditionTrigger<T> abstractConditionTrigger);

    protected abstract AbstractConditionTrigger<T> getAbstractConditionPrivate(SimpleBase simpleBase);

    protected abstract void conditionPassed(T t);

    @Override
    public void activateCondition(ConditionConfig conditionConfig, T t) {
        AbstractComparison abstractComparison = null;
        if(conditionConfig.getConditionTrigger().isComparisonNeeded()) {
            abstractComparison = conditionConfig.getAbstractComparisonConfig().createAbstractComparison();
        }
        AbstractConditionTrigger<T> abstractConditionTrigger = conditionConfig.getConditionTrigger().createAbstractConditionTrigger(abstractComparison, t);
        saveAbstractConditionTrigger(abstractConditionTrigger);
    }

    private <U extends AbstractConditionTrigger<T>> U getAbstractCondition(SimpleBase actor, Class<U> theClass) {
        AbstractConditionTrigger<T> abstractConditionTrigger = getAbstractConditionPrivate(actor);
        if (abstractConditionTrigger == null) {
            return null;
        }
        if (theClass.equals(abstractConditionTrigger.getClass())) {
            return (U) abstractConditionTrigger;
        } else {
            return null;
        }
    }

    //------ Client ------

    @Override
    public void onOwnSelectionChanged(Group selectedGroup) {
    }

    @Override
    public void onSendCommand(SyncBaseItem syncItem, BaseCommand baseCommand) {
    }

    @Override
    public void onSyncItemDeactivated(SyncTickItem syncTickItem) {
    }

    @Override
    public void onScroll(int left, int top, int width, int height, int deltaLeft, int deltaTop) {
    }

    @Override
    public void onClick(ClickEvent event) {
    }

    //------ Server------

    @Override
    public void onIncreaseXp(SimpleBase base, int xp) {
    }

    //------ Both Client and Server ------

    @Override
    public void onSyncItemKilled(SimpleBase actor, SyncItem killedItem) {
        KillConditionTrigger<T> killConditionTrigger = getAbstractCondition(actor, KillConditionTrigger.class);
        if (killConditionTrigger == null) {
            return;
        }
        killConditionTrigger.onItemKilled(actor, killedItem);
        if (killConditionTrigger.isFulfilled()) {
            conditionPassed(killConditionTrigger.getUserObject());
        }
    }

    @Override
    public void onSyncItemBuilt(SyncBaseItem syncBaseItem) {
    }

    @Override
    public void onMoneyIncrease(SimpleBase base, double accountBalance) {
    }

    //------ Only used for fail ------

    @Override
    public void onWithdrawalMoney() {
    }

    @Override
    public void onBaseDeleted(SimpleBase base) {
    }
}
