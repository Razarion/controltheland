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

import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryArtifactInfo;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.packets.LevelTaskPacket;
import com.btxtech.game.jsre.common.utg.condition.AbstractComparison;
import com.btxtech.game.jsre.common.utg.condition.AbstractConditionTrigger;
import com.btxtech.game.jsre.common.utg.condition.ArtifactItemIdConditionTrigger;
import com.btxtech.game.jsre.common.utg.condition.GenericComparisonValueContainer;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.impl.ConditionServiceImpl;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.ServerGlobalServices;
import com.btxtech.game.services.inventory.GlobalInventoryService;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.mgmt.impl.DbUserState;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.condition.DbGenericComparisonValue;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
    private PlanetSystemService planetSystemService;
    @Autowired
    private UserService userService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private ServerGlobalServices serverGlobalServices;
    @Autowired
    private GlobalInventoryService globalInventoryService;
    private long rate = 10000;
    private long rateDeferredUpdate = 2000;
    private final Map<UserState, Collection<AbstractConditionTrigger<UserState, Integer>>> triggerMap = new HashMap<>();
    private ScheduledThreadPoolExecutor timer = new ScheduledThreadPoolExecutor(1, new CustomizableThreadFactory("ServerConditionServiceImpl timer "));
    private ScheduledThreadPoolExecutor deferredUpdateTimer = new ScheduledThreadPoolExecutor(1, new CustomizableThreadFactory("ServerConditionServiceImpl deferred update timer "));
    private static Log log = LogFactory.getLog(ServerConditionServiceImpl.class);
    private ScheduledFuture scheduledFuture;
    private ScheduledFuture deferredUpdateScheduledFuture;

    @PostConstruct
    public void postConstruct() {
        try {
            deferredUpdateScheduledFuture = timer.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    sendDeferredUpdate();
                }
            }, rateDeferredUpdate, rateDeferredUpdate, TimeUnit.MILLISECONDS);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            timer.shutdownNow();
            deferredUpdateTimer.shutdownNow();
            if (deferredUpdateScheduledFuture != null) {
                deferredUpdateScheduledFuture.cancel(true);
                deferredUpdateScheduledFuture = null;
            }
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    protected void saveAbstractConditionTrigger(AbstractConditionTrigger<UserState, Integer> abstractConditionTrigger) {
        synchronized (triggerMap) {
            Collection<AbstractConditionTrigger<UserState, Integer>> conditions = triggerMap.get(abstractConditionTrigger.getActor());
            if (conditions == null) {
                conditions = new ArrayList<>();
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
        Collection<AbstractConditionTrigger<UserState, Integer>> removed = new ArrayList<>();
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
        Collection<AbstractConditionTrigger<UserState, Integer>> result = new ArrayList<>();
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
            return planetSystemService.getServerPlanetServices(actorBase).getBaseService().getUserState(actorBase);
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
                            activeTrigger.getAbstractComparison().restoreFromGenericComparisonValue(dbGenericComparisonValue.createGenericComparisonValueContainer(serverItemTypeService));
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
                        dbUserState.addDbGenericComparisonValue(new DbGenericComparisonValue(conditionTrigger.getIdentifier(), container, serverItemTypeService));
                    }
                }
            } catch (Exception e) {
                log.error("Can not backup user: " + userState, e);
            }

        }
    }

    @Override
    protected GlobalServices getGlobalServices() {
        return serverGlobalServices;
    }

    @Override
    protected PlanetServices getPlanetServices(UserState userState) {
        LevelScope levelScope = userGuidanceService.getLevelScope(userState);
        if (levelScope.hasPlanet()) {
            return planetSystemService.getServerPlanetServices(userState);
        } else {
            return null;
        }
    }

    @Override
    public void onIncreaseXp(UserState userState, int xp) {
        // No suppress check due to level 0 has no planet
        triggerValue(userState, ConditionTrigger.XP_INCREASED, xp);
    }

    @Override
    public void onTutorialFinished(UserState userState, int taskId) {
        // No suppress check due to level 0 has no planet
        Collection<AbstractConditionTrigger<UserState, Integer>> abstractConditionTriggers = getAbstractConditions(userState, taskId, ConditionTrigger.TUTORIAL);
        triggerSimple(abstractConditionTriggers);
    }

    @Override
    public void onRazarionIncreased(UserState actor, boolean planetInteraction, int razarion) {
        if (isActorIgnored(actor, planetInteraction)) {
            return;
        }
        triggerValue(actor, ConditionTrigger.RAZARION_INCREASED, razarion);
    }

    @Override
    public void onArtifactItemAdded(UserState actor, boolean planetInteraction, int artifactItemId) {
        if (isActorIgnored(actor, planetInteraction)) {
            return;
        }
        triggerArtifactItem(actor, ConditionTrigger.ARTIFACT_ITEM_ADDED, artifactItemId);
    }

    private void triggerArtifactItem(UserState actor, ConditionTrigger conditionTrigger, int artifactItemId) {
        Collection<AbstractConditionTrigger<UserState, Integer>> abstractConditionTriggers = getAbstractConditions(actor, conditionTrigger);
        if (abstractConditionTriggers == null) {
            return;
        }
        for (AbstractConditionTrigger<UserState, Integer> abstractConditionTrigger : abstractConditionTriggers) {
            ArtifactItemIdConditionTrigger artifactItemIdConditionTrigger = (ArtifactItemIdConditionTrigger) abstractConditionTrigger;
            artifactItemIdConditionTrigger.onArtifactItemId(artifactItemId);
            if (artifactItemIdConditionTrigger.isFulfilled()) {
                conditionPassed(abstractConditionTrigger);
            }
        }
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
        if (abstractComparison != null
                && actor.getBase() != null
                && abstractComparison.getAbstractConditionTrigger().getConditionTrigger() != ConditionTrigger.XP_INCREASED
                && abstractComparison.getAbstractConditionTrigger().getConditionTrigger() != ConditionTrigger.TUTORIAL) {
            LevelTaskPacket levelTaskPacket = new LevelTaskPacket();
            levelTaskPacket.setQuestProgressInfo(getQuestProgressInfo(actor, identifier));
            planetSystemService.getServerPlanetServices(actor.getBase().getSimpleBase()).getConnectionService().sendPacket(actor.getBase().getSimpleBase(), levelTaskPacket);
        }
    }

    private void sendDeferredUpdate() {
        synchronized (triggerMap) {
            for (Collection<AbstractConditionTrigger<UserState, Integer>> abstractConditionTriggers : triggerMap.values()) {
                for (AbstractConditionTrigger<UserState, Integer> abstractConditionTrigger : abstractConditionTriggers) {
                    AbstractComparison abstractComparison = abstractConditionTrigger.getAbstractComparison();
                    if (abstractComparison == null) {
                        continue;
                    }
                    abstractComparison.handleDeferredUpdate();
                }
            }
        }
    }

    @Override
    protected boolean isTriggerSuppressed(UserState actor, boolean planetInteraction) {
        return planetInteraction && !planetSystemService.isUserOnCorrectPlanet(actor);
    }

    @Override
    public InventoryArtifactInfo createInventoryArtifactInfo(int id) {
        return globalInventoryService.getArtifactCrud().readDbChild(id).generateInventoryArtifactInfo();
    }
}
