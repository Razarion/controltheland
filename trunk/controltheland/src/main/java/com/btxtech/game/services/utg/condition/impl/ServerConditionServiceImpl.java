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

package com.btxtech.game.services.utg.condition.impl;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.utg.condition.AbstractConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.impl.ConditionServiceImpl;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.common.ServerServices;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.mgmt.impl.DbUserState;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import com.btxtech.game.services.utg.condition.backup.DbAbstractComparisonBackup;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User: beat
 * Date: 28.12.2010
 * Time: 18:16:33
 */
@Component("serverConditionService")
public class ServerConditionServiceImpl extends ConditionServiceImpl<UserState, Integer> implements ServerConditionService {
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
    private final Map<UserState, Collection<AbstractConditionTrigger<UserState, Integer>>> triggerMap = new HashMap<UserState, Collection<AbstractConditionTrigger<UserState, Integer>>>();

    @Override
    protected void saveAbstractConditionTrigger(AbstractConditionTrigger<UserState, Integer> abstractConditionTrigger) {
        synchronized (triggerMap) {
            Collection<AbstractConditionTrigger<UserState, Integer>> conditions = triggerMap.get(abstractConditionTrigger.getActor());
            if (conditions == null) {
                conditions = new ArrayList<AbstractConditionTrigger<UserState, Integer>>();
                triggerMap.put(abstractConditionTrigger.getActor(), conditions);
            }
            conditions.add(abstractConditionTrigger);
        }
    }

    @Override
    public void deactivateActorConditions(UserState userState, Integer identifier) {
        synchronized (triggerMap) {
            Collection<AbstractConditionTrigger<UserState, Integer>> conditions = triggerMap.get(userState);
            if (conditions == null) {
                return;
            }
            for (Iterator<AbstractConditionTrigger<UserState, Integer>> iterator = conditions.iterator(); iterator.hasNext();) {
                AbstractConditionTrigger<UserState, Integer> condition = iterator.next();
                Integer conditionIdentifier = condition.getIdentifier();
                if (conditionIdentifier == null && identifier == null) {
                    iterator.remove();
                    cleanupTriggerMap(userState, conditions);
                    return;
                } else if (identifier != null && identifier.equals(conditionIdentifier)) {
                    iterator.remove();
                    cleanupTriggerMap(userState, conditions);
                    return;
                }
            }
        }
    }

    private void cleanupTriggerMap(UserState userState, Collection<AbstractConditionTrigger<UserState, Integer>> conditions) {
        if (conditions.isEmpty()) {
            triggerMap.remove(userState);
        }
    }

    @Override
    protected Collection<AbstractConditionTrigger<UserState, Integer>> getAbstractConditionPrivate(UserState userState, ConditionTrigger conditionTrigger) {
        Collection<AbstractConditionTrigger<UserState, Integer>> abstractConditionTrigger;
        synchronized (triggerMap) {
            abstractConditionTrigger = triggerMap.get(userState);
        }
        if (abstractConditionTrigger == null) {
            return null;
        }
        Collection<AbstractConditionTrigger<UserState, Integer>> result = new ArrayList<AbstractConditionTrigger<UserState, Integer>>();
        for (AbstractConditionTrigger<UserState, Integer> condition : abstractConditionTrigger) {
            if (condition.getConditionTrigger() == conditionTrigger) {
                result.add(condition);
            }
        }
        return result;
    }

    private Collection<AbstractConditionTrigger<UserState, Integer>> getAbstractConditions(UserState userState, int taskId, ConditionTrigger conditionTrigger) {
        Collection<AbstractConditionTrigger<UserState, Integer>> result = getAbstractConditionPrivate(userState, conditionTrigger);
        if (result == null) {
            return null;
        }
        for (Iterator<AbstractConditionTrigger<UserState, Integer>> iterator = result.iterator(); iterator.hasNext();) {
            AbstractConditionTrigger<UserState, Integer> condition = iterator.next();
            if (condition.getIdentifier() == null || condition.getIdentifier() != taskId) {
                iterator.remove();
            }
        }
        return result;
    }

    @Override
    public boolean hasConditionTrigger(UserState actor, int identifier) {
        Collection<AbstractConditionTrigger<UserState, Integer>> conditions = triggerMap.get(actor);
        if (conditions == null) {
            return false;
        }
        for (AbstractConditionTrigger<UserState, Integer> condition : conditions) {
            if (condition.getIdentifier() != null && condition.getIdentifier() == identifier) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected UserState getActor(SimpleBase actorBase) {
        if (actorBase != null) {
            return baseService.getUserState(actorBase);
        } else {
            return userService.getUserState();
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
    public DbAbstractComparisonBackup createBackup(DbUserState dbUserState, UserState userState) {
        return null;
        // TODO
//        AbstractConditionTrigger abstractConditionTrigger;
//        synchronized (triggerMap) {
//            abstractConditionTrigger = triggerMap.get(userState);
//        }
//        AbstractComparison abstractComparison = abstractConditionTrigger.getAbstractComparison();
//        if (abstractComparison == null) {
//            return null;
//        }
//        if (abstractComparison instanceof CountComparison) {
//            return new DbCountComparisonBackup(dbUserState, (CountComparison) abstractComparison);
//        } else if (abstractComparison instanceof SyncItemIdComparison) {
//            return new DbSyncItemIdComparisonBackup(dbUserState, (SyncItemIdComparison) abstractComparison);
//        } else if (abstractComparison instanceof SyncItemTypeComparison) {
//            return new DbSyncItemTypeComparisonBackup(dbUserState, (SyncItemTypeComparison) abstractComparison, itemService);
//        } else {
//            throw new IllegalArgumentException("Unknown AbstractComparison: " + abstractComparison);
//        }
    }

    @Override
    protected Services getServices() {
        return serverServices;
    }

    @Override
    public void onIncreaseXp(UserState userState, int xp) {
        triggerValue(userState, ConditionTrigger.XP_INCREASED, xp);
    }

    @Override
    public void onTutorialFinished(UserState userState, int taskId) {
        Collection<AbstractConditionTrigger<UserState, Integer>> abstractConditionTriggers = getAbstractConditions(userState, taskId, ConditionTrigger.TUTORIAL);
        triggerSimple(abstractConditionTriggers);
    }
}
