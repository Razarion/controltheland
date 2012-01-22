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

package com.btxtech.game.jsre.common.utg;

import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncTickItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.utg.condition.AbstractConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.google.gwt.event.dom.client.ClickEvent;

import java.util.Collection;

/**
 * User: beat
 * Date: 28.12.2010
 * Time: 18:14:41
 */
public interface ConditionService<T> {
    AbstractConditionTrigger<T> activateCondition(ConditionConfig conditionConfig, T t);

    void activateConditions(Collection<ConditionConfig> allLevelTaskConditions, Object userObject);
   
    void onIncreaseXp(SimpleBase base, int xp);

    void onSyncItemKilled(SimpleBase actor, SyncBaseItem killedItem);

    void onSyncItemBuilt(SyncBaseItem syncBaseItem);

    void onMoneyIncrease(SimpleBase base, double accountBalance);

    void onBaseDeleted(SimpleBase actorBase);

    void setConditionServiceListener(ConditionServiceListener<T> conditionServiceListener);
}
