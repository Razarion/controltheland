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

package com.btxtech.game.services.utg.impl;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.utg.condition.AbstractComparison;
import com.btxtech.game.jsre.common.utg.condition.AbstractConditionTrigger;
import com.btxtech.game.jsre.common.utg.condition.CountComparison;
import com.btxtech.game.jsre.common.utg.condition.SyncItemIdComparison;
import com.btxtech.game.jsre.common.utg.condition.SyncItemTypeComparison;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.impl.ConditionServiceImpl;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbAbstractLevel;
import com.btxtech.game.services.utg.ServerConditionService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.condition.backup.DbAbstractComparisonBackup;
import com.btxtech.game.services.utg.condition.backup.DbCountComparisonBackup;
import com.btxtech.game.services.utg.condition.backup.DbSyncItemIdComparisonBackup;
import com.btxtech.game.services.utg.condition.backup.DbSyncItemTypeComparisonBackup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 28.12.2010
 * Time: 18:16:33
 */
@Component("serverConditionService")
public class ServerConditionServiceImpl extends ConditionServiceImpl<UserState> implements ServerConditionService {
    @Autowired
    private BaseService baseService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    private final Map<UserState, AbstractConditionTrigger<UserState>> triggerMap = new HashMap<UserState, AbstractConditionTrigger<UserState>>();

    @Override
    protected void saveAbstractConditionTrigger(AbstractConditionTrigger<UserState> abstractConditionTrigger) {
        synchronized (triggerMap) {
            triggerMap.put(abstractConditionTrigger.getUserObject(), abstractConditionTrigger);
        }
    }

    @Override
    protected AbstractConditionTrigger<UserState> getAbstractConditionPrivate(SimpleBase simpleBase, ConditionTrigger conditionTrigger) {
        UserState userState;
        if (simpleBase != null) {
            userState = baseService.getUserState(simpleBase);
        } else {
            userState = userService.getUserState();
        }
        AbstractConditionTrigger<UserState> abstractConditionTrigger;
        synchronized (triggerMap) {
            abstractConditionTrigger = triggerMap.get(userState);
        }
        if (abstractConditionTrigger == null) {
            return null;
        }
        if (abstractConditionTrigger.getConditionTrigger() == conditionTrigger) {
            return abstractConditionTrigger;
        } else {
            return null;
        }
    }

    @Override
    public void onTutorialFinished(UserState userState) {
        triggerSimple(ConditionTrigger.TUTORIAL);
    }

    public void restore(Collection<UserState> userStates) {
        synchronized (triggerMap) {
            triggerMap.clear();
            for (UserState userState : userStates) {
                if (userState.isBot()) {
                    continue;
                }
                DbAbstractLevel dbAbstractLevel = userState.getCurrentAbstractLevel();
                dbAbstractLevel = userGuidanceService.getDbLevel(dbAbstractLevel.getId());
                userState.setCurrentAbstractLevel(dbAbstractLevel);
                activateCondition(dbAbstractLevel.getConditionConfig(), userState);

                DbAbstractComparisonBackup dbAbstractComparisonBackup = userState.getDbAbstractComparisonBackup();
                if (dbAbstractComparisonBackup != null) {
                    AbstractConditionTrigger abstractConditionTrigger = triggerMap.get(userState);
                    AbstractComparison abstractComparison = abstractConditionTrigger.getAbstractComparison();
                    dbAbstractComparisonBackup.restore(abstractComparison, itemService);
                    userState.setDbAbstractComparisonBackup(null);
                }

            }
        }
    }

    public void backup() {
        synchronized (triggerMap) {
            for (Map.Entry<UserState, AbstractConditionTrigger<UserState>> entry : triggerMap.entrySet()) {
                AbstractComparison abstractComparison = entry.getValue().getAbstractComparison();
                DbAbstractComparisonBackup dbAbstractComparisonBackup = null;
                if (abstractComparison instanceof CountComparison) {
                    dbAbstractComparisonBackup = new DbCountComparisonBackup(entry.getKey(), (CountComparison) abstractComparison);
                } else if (abstractComparison instanceof SyncItemIdComparison) {
                    dbAbstractComparisonBackup = new DbSyncItemIdComparisonBackup(entry.getKey(), (SyncItemIdComparison) abstractComparison);
                } else if (abstractComparison instanceof SyncItemTypeComparison) {
                    dbAbstractComparisonBackup = new DbSyncItemTypeComparisonBackup(entry.getKey(), (SyncItemTypeComparison) abstractComparison, itemService);
                }

                if (dbAbstractComparisonBackup != null) {
                    entry.getKey().setDbAbstractComparisonBackup(dbAbstractComparisonBackup);
                }
            }
        }
    }
}
