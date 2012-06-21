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

package com.btxtech.game.services.statistics.impl;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.common.ReadonlyListContentProvider;
import com.btxtech.game.services.mgmt.impl.DbUserState;
import com.btxtech.game.services.statistics.CurrentStatisticEntry;
import com.btxtech.game.services.statistics.StatisticsEntry;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: Sep 13, 2009
 * Time: 1:25:52 PM
 */
@Component("statisticsService")
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private BaseService baseService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    private final Map<UserState, StatisticsEntry> userStateStatisticsEntryMap = new HashMap<>();
    private Log log = LogFactory.getLog(StatisticsServiceImpl.class);

    @Override
    public void onItemKilled(SyncBaseItem targetItem, SimpleBase actorBase) {
        try {
            if (!baseService.isAbandoned(actorBase)) {
                boolean targetHuman = !baseService.isBot(targetItem.getBase());
                boolean actorHuman = !baseService.isBot(actorBase);
                if (targetHuman && actorHuman) {
                    StatisticsEntry actorEntry = getStatisticsEntry(actorBase);
                    if (targetItem.hasSyncMovable()) {
                        actorEntry.increaseKilledUnitsPlayer();
                        if (!baseService.isAbandoned(targetItem.getBase())) {
                            StatisticsEntry targetEntry = getStatisticsEntry(targetItem.getBase());
                            targetEntry.increaseLostUnitsPlayer();
                        }
                    } else {
                        actorEntry.increaseKilledStructurePlayer();
                        if (!baseService.isAbandoned(targetItem.getBase())) {
                            StatisticsEntry targetEntry = getStatisticsEntry(targetItem.getBase());
                            targetEntry.increaseLostStructurePlayer();
                        }
                    }
                } else if (!targetHuman && actorHuman) {
                    StatisticsEntry actorEntry = getStatisticsEntry(actorBase);
                    if (targetItem.hasSyncMovable()) {
                        actorEntry.increaseKilledUnitsBot();
                    } else {
                        actorEntry.increaseKilledStructureBot();
                    }
                } else if (targetHuman && !actorHuman) {
                    if (!baseService.isAbandoned(targetItem.getBase())) {
                        StatisticsEntry targetEntry = getStatisticsEntry(targetItem.getBase());
                        if (targetItem.hasSyncMovable()) {
                            targetEntry.increaseLostUnitsBot();
                        } else {
                            targetEntry.increaseLostStructureBot();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void onItemCreated(SyncBaseItem syncBaseItem) {
        try {
            if (!baseService.isBot(syncBaseItem.getBase()) && !baseService.isAbandoned(syncBaseItem.getBase())) {
                StatisticsEntry entry = getStatisticsEntry(syncBaseItem.getBase());
                if (syncBaseItem.hasSyncMovable()) {
                    entry.increaseBuiltUnits();
                } else {
                    entry.increaseBuiltStructures();
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void onBaseKilled(SimpleBase target, SimpleBase actor) {
        try {
            if (!baseService.isAbandoned(actor)) {
                boolean targetHuman = !baseService.isBot(target);
                boolean actorHuman = !baseService.isBot(actor);
                if (targetHuman && actorHuman) {
                    StatisticsEntry actorEntry = getStatisticsEntry(actor);
                    actorEntry.increaseBasesDestroyedPlayer();
                    if (!baseService.isAbandoned(target)) {
                        StatisticsEntry targetEntry = getStatisticsEntry(target);
                        targetEntry.increaseBasesLostPlayer();
                    }
                } else if (!targetHuman && actorHuman) {
                    StatisticsEntry actorEntry = getStatisticsEntry(actor);
                    actorEntry.increaseBasesDestroyedBot();
                } else if (targetHuman && !actorHuman) {
                    if (!baseService.isAbandoned(target)) {
                        StatisticsEntry targetEntry = getStatisticsEntry(target);
                        targetEntry.increaseBasesLostBot();
                    }
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void restoreBackup(Map<DbUserState, UserState> userStates) {
        try {
            synchronized (userStateStatisticsEntryMap) {
                userStateStatisticsEntryMap.clear();
                for (Map.Entry<DbUserState, UserState> entry : userStates.entrySet()) {
                    userStateStatisticsEntryMap.put(entry.getValue(), entry.getKey().getStatisticsEntry());
                }
            }
        } catch (Throwable t) {
            log.error("StatisticServiceImpl.restore()", t);
        }
    }

    @Override
    public void createAndAddBackup(DbUserState dbUserState, UserState userState) {
        try {
            dbUserState.setStatisticsEntry(getStatisticsEntry(userState));
        } catch (Throwable t) {
            log.error("StatisticServiceImpl.restore() dbUserState: " + dbUserState + " userState: " + userState, t);
        }
    }

    @Override
    public void onRemoveUserState(UserState userState) {
        synchronized (userStateStatisticsEntryMap) {
            userStateStatisticsEntryMap.remove(userState);
        }
    }

    private StatisticsEntry getStatisticsEntry(UserState userState) {
        if (userState == null) {
            throw new NullPointerException("userState == null");
        }
        synchronized (userStateStatisticsEntryMap) {
            StatisticsEntry statisticsEntry = userStateStatisticsEntryMap.get(userState);
            if (statisticsEntry == null) {
                statisticsEntry = new StatisticsEntry();
                userStateStatisticsEntryMap.put(userState, statisticsEntry);
            }
            return statisticsEntry;
        }
    }

    private StatisticsEntry getStatisticsEntry(SimpleBase simpleBase) {
        return getStatisticsEntry(baseService.getUserState(simpleBase));
    }

    @Override
    public StatisticsEntry getStatisticsEntryAccess(UserState userState) {
       return getStatisticsEntry(userState);
    }

    @Override
    public ReadonlyListContentProvider<CurrentStatisticEntry> getCmsCurrentStatistics() {
        List<CurrentStatisticEntry> entries = new ArrayList<CurrentStatisticEntry>();
        for (UserState userState : userService.getAllUserStates()) {
            String userName = null;
            Integer money = null;
            Integer itemCount = null;
            Long upTime = null;
            if (userState.isRegistered()) {
                userName = userState.getUser();
            }
            if (userState.getBase() != null) {
                if (!userState.isRegistered()) {
                    userName = baseService.getBaseName(userState.getBase().getSimpleBase());
                }
                upTime = userState.getBase().getUptime();
                itemCount = userState.getBase().getItemCount();
                money = (int) Math.round(userState.getBase().getAccountBalance());
            }
            entries.add(new CurrentStatisticEntry(userGuidanceService.getDbLevel(userState),
                    userState.getXp(),
                    userName,
                    upTime,
                    itemCount,
                    money,
                    getStatisticsEntry(userState)));
        }
        return new CurrentStatisticServiceContentProvider(entries);
    }


}
