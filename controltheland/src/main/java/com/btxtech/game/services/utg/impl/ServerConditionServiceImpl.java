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

import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.utg.condition.AbstractComparison;
import com.btxtech.game.jsre.common.utg.condition.AbstractConditionTrigger;
import com.btxtech.game.jsre.common.utg.condition.CountComparison;
import com.btxtech.game.jsre.common.utg.condition.SyncItemIdComparison;
import com.btxtech.game.jsre.common.utg.condition.SyncItemTypeComparison;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.impl.ConditionServiceImpl;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.common.ServerServices;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.mgmt.impl.DbUserState;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.ServerConditionService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.services.utg.condition.backup.DbAbstractComparisonBackup;
import com.btxtech.game.services.utg.condition.backup.DbCountComparisonBackup;
import com.btxtech.game.services.utg.condition.backup.DbSyncItemIdComparisonBackup;
import com.btxtech.game.services.utg.condition.backup.DbSyncItemTypeComparisonBackup;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
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
    @Autowired
    private ServerServices serverServices;
    @Autowired
    private SessionFactory sessionFactory;
    private final Map<UserState, AbstractConditionTrigger<UserState>> triggerMap = new HashMap<UserState, AbstractConditionTrigger<UserState>>();
    private Log log = LogFactory.getLog(ServerConditionServiceImpl.class);

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
    public void restoreBackup(Map<DbUserState, UserState> userStates, ItemService itemService) {
        // TODO
//        synchronized (triggerMap) {
//            triggerMap.clear();
//            for (Map.Entry<DbUserState, UserState> entry : userStates.entrySet()) {
//                try {
//                    DbLevel dbLevel = userGuidanceService.getDbLevel(entry.getValue().getDbLevel().getId());
//                    AbstractConditionTrigger<UserState> abstractConditionTrigger = activateCondition(dbLevel.getConditionConfig(), entry.getValue());
//                    if (entry.getKey().getDbAbstractComparisonBackup() != null) {
//                        entry.getKey().getDbAbstractComparisonBackup().restore(abstractConditionTrigger.getAbstractComparison(), itemService);
//                    }
//                } catch (Exception e) {
//                    log.error("Can not restore user: " + entry.getKey(), e);
//                }
//            }
//        }
    }

    @Override
    public void onTutorialFinished(UserState userState, Integer taskId) {
        // TODO
    }

    @Override
    public DbAbstractComparisonBackup createBackup(DbUserState dbUserState, UserState userState) {
        AbstractConditionTrigger abstractConditionTrigger;
        synchronized (triggerMap) {
            abstractConditionTrigger = triggerMap.get(userState);
        }
        AbstractComparison abstractComparison = abstractConditionTrigger.getAbstractComparison();
        if (abstractComparison == null) {
            return null;
        }
        if (abstractComparison instanceof CountComparison) {
            return new DbCountComparisonBackup(dbUserState, (CountComparison) abstractComparison);
        } else if (abstractComparison instanceof SyncItemIdComparison) {
            return new DbSyncItemIdComparisonBackup(dbUserState, (SyncItemIdComparison) abstractComparison);
        } else if (abstractComparison instanceof SyncItemTypeComparison) {
            return new DbSyncItemTypeComparisonBackup(dbUserState, (SyncItemTypeComparison) abstractComparison, itemService);
        } else {
            throw new IllegalArgumentException("Unknown AbstractComparison: " + abstractComparison);
        }
    }

    @Override
    protected Services getServices() {
        return serverServices;
    }
}
