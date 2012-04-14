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

import com.btxtech.game.jsre.common.LevelStatePacket;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.utg.condition.AbstractComparison;
import com.btxtech.game.jsre.common.utg.condition.AbstractConditionTrigger;
import com.btxtech.game.jsre.common.utg.condition.GenericComparisonValueContainer;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.impl.ConditionServiceImpl;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.common.ServerServices;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.mgmt.impl.DbUserState;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.condition.DbGenericComparisonValue;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * User: beat Date: 28.12.2010 Time: 18:16:33
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
    @Autowired
    private ConnectionService connectionService;
    private long rate = 10000;
    private final Map<UserState, Collection<AbstractConditionTrigger<UserState, Integer>>> triggerMap = new HashMap<UserState, Collection<AbstractConditionTrigger<UserState, Integer>>>();
    private ScheduledThreadPoolExecutor timer = new ScheduledThreadPoolExecutor(1, new CustomizableThreadFactory("ServerConditionServiceImpl timer "));
    private static Log log = LogFactory.getLog(ServerConditionServiceImpl.class);
    private ScheduledFuture scheduledFuture;

    @PreDestroy
    public void cleanup() {
        try {
            timer.shutdownNow();
        } catch (Throwable t) {
            log.error("", t);
        }
    }

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
    protected AbstractConditionTrigger<UserState, Integer> getActorConditionsPrivate(UserState userState, Integer identifier) {
        synchronized (triggerMap) {
            Collection<AbstractConditionTrigger<UserState, Integer>> conditions = triggerMap.get(userState);
            if (conditions == null) {
                throw new IllegalArgumentException("No such condition trigger. userState: " + userState + " identifier: " + identifier);
            }
            for (AbstractConditionTrigger<UserState, Integer> condition : conditions) {
                Integer conditionIdentifier = condition.getIdentifier();
                if (conditionIdentifier == null && identifier == null) {
                    return condition;
                } else if (identifier != null && identifier.equals(conditionIdentifier)) {
                    return condition;
                }
            }
        }
        throw new IllegalArgumentException("No such condition trigger. userState: " + userState + " identifier: " + identifier);
    }

    @Override
    protected AbstractConditionTrigger<UserState, Integer> removeActorConditionsPrivate(UserState userState, Integer identifier) {
        synchronized (triggerMap) {
            Collection<AbstractConditionTrigger<UserState, Integer>> conditions = triggerMap.get(userState);
            if (conditions == null) {
                return null;
            }
            for (Iterator<AbstractConditionTrigger<UserState, Integer>> iterator = conditions.iterator(); iterator.hasNext(); ) {
                AbstractConditionTrigger<UserState, Integer> condition = iterator.next();
                Integer conditionIdentifier = condition.getIdentifier();
                if (conditionIdentifier == null && identifier == null) {
                    iterator.remove();
                    cleanupTriggerMap(userState, conditions);
                    return condition;
                } else if (identifier != null && identifier.equals(conditionIdentifier)) {
                    iterator.remove();
                    cleanupTriggerMap(userState, conditions);
                    return condition;
                }
            }
        }
        return null;
    }

    @Override
    protected Collection<AbstractConditionTrigger<UserState, Integer>> removeAllActorConditionsPrivate(UserState userState) {
        Collection<AbstractConditionTrigger<UserState, Integer>> removed = new ArrayList<AbstractConditionTrigger<UserState, Integer>>();
        synchronized (triggerMap) {
            Collection<AbstractConditionTrigger<UserState, Integer>> conditions = triggerMap.remove(userState);
            if (conditions != null) {
                removed.addAll(conditions);
            }
        }
        return removed;
    }

    @Override
    protected void removeAllConditionsPrivate() {
        synchronized (triggerMap) {
            triggerMap.clear();
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
        for (Iterator<AbstractConditionTrigger<UserState, Integer>> iterator = result.iterator(); iterator.hasNext(); ) {
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
    protected SimpleBase getSimpleBase(UserState actor) {
        if (actor.getBase() != null) {
            return actor.getBase().getSimpleBase();
        } else {
            return null;
        }
    }

    @Override
    public void restoreBackup(DbUserState dbUserState, UserState userState) {
        synchronized (triggerMap) {
            try {
                Collection<AbstractConditionTrigger<UserState, Integer>> triggers = triggerMap.get(userState);
                if (triggers == null || triggers.isEmpty()) {
                    return;
                }
                Collection<DbGenericComparisonValue> comparisonValues = dbUserState.getDbGenericComparisonValues();
                if (comparisonValues == null || comparisonValues.isEmpty()) {
                    return;
                }
                for (DbGenericComparisonValue dbGenericComparisonValue : comparisonValues) {
                    Integer identifier = dbGenericComparisonValue.getIdentifier();
                    AbstractConditionTrigger<UserState, Integer> activeTrigger = getAbstractConditionTrigger(identifier, triggers);
                    if (activeTrigger != null) {
                        try {
                            activeTrigger.getAbstractComparison().restoreFromGenericComparisonValue(dbGenericComparisonValue.createGenericComparisonValueContainer(itemService));
                        } catch (Exception e) {
                            log.error("Can not backup user conditions: " + userState + " identifier: " + activeTrigger.getIdentifier(), e);
                        }
                    } else {
                        log.warn("Condition trigger was saved on DB but is not active: " + userState + " identifier: " + identifier);
                    }
                }
            } catch (Exception e) {
                log.error("Can not backup user conditions: " + userState, e);
            }
        }
    }

    private AbstractConditionTrigger<UserState, Integer> getAbstractConditionTrigger(Integer identifier, Collection<AbstractConditionTrigger<UserState, Integer>> triggers) {
        for (AbstractConditionTrigger<UserState, Integer> condition : triggers) {
            Integer conditionIdentifier = condition.getIdentifier();
            if (conditionIdentifier == null && identifier == null) {
                return condition;
            } else if (identifier != null && identifier.equals(conditionIdentifier)) {
                return condition;
            }
        }
        return null;
    }

    @Override
    public void createBackup(DbUserState dbUserState, UserState userState) {
        synchronized (triggerMap) {
            try {
                Collection<AbstractConditionTrigger<UserState, Integer>> triggers = triggerMap.get(userState);
                if (triggers == null || triggers.isEmpty()) {
                    return;
                }
                for (AbstractConditionTrigger<UserState, Integer> conditionTrigger : triggers) {
                    if (conditionTrigger.getAbstractComparison() == null) {
                        continue;
                    }
                    GenericComparisonValueContainer container = new GenericComparisonValueContainer();
                    conditionTrigger.getAbstractComparison().fillGenericComparisonValues(container);
                    if (!container.isEmpty()) {
                        dbUserState.addDbGenericComparisonValue(new DbGenericComparisonValue(conditionTrigger.getIdentifier(), container, itemService));
                    }
                }
            } catch (Exception e) {
                log.error("Can not restore user: " + userState, e);
            }

        }
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

    @Override
    protected void startTimer() {
        scheduledFuture = timer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                onTimer();
            }
        }, rate, rate, TimeUnit.MILLISECONDS);
    }

    public void setRate(long rate) {
        this.rate = rate;
    }

    @Override
    protected void stopTimer() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            scheduledFuture = null;
        }
    }

    @Override
    public void sendProgressUpdate(UserState actor, Integer identifier) {
        AbstractComparison abstractComparison = getActorConditionsPrivate(actor, identifier).getAbstractComparison();
        if (abstractComparison != null && actor.getBase() != null) {
            LevelStatePacket levelStatePacket = new LevelStatePacket();
            levelStatePacket.setActiveQuestProgress(abstractComparison.createProgressHtml());
            connectionService.sendPacket(actor.getBase().getSimpleBase(), levelStatePacket);
        }
    }
}
