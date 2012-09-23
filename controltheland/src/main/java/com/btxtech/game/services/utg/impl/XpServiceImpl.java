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
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.packets.XpPacket;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.QueueWorker;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbXpSettings;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.XpService;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: beat Date: 18.12.2009 Time: 21:11:36
 */
@Component("xpService")
public class XpServiceImpl implements XpService {
    @Autowired
    private ServerConditionService serverConditionService;
    @Autowired
    SessionFactory sessionFactory;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private PlanetSystemService planetSystemService;
    private DbXpSettings dbXpSettings;
    private Log log = LogFactory.getLog(XpServiceImpl.class);
    private XpPerKillQueueWorker xpPerKillQueueWorker;

    class XpPerKillQueueWorker extends QueueWorker<XpPerKill> {
        public XpPerKillQueueWorker(long period, int queueSize) {
            super(period, queueSize);
        }

        @Override
        protected void processEntries(List<XpPerKill> xpPerKills) {
            HashMap<SimpleBase, Integer> baseXpHashMap = new HashMap<>();
            for (XpPerKill xpPerKill : xpPerKills) {
                if (xpPerKill.getPlanetServices().getBaseService().isAbandoned(xpPerKill.getActorBase())) {
                    continue;
                }
                sumUpXpPerBase(baseXpHashMap, xpPerKill.getActorBase(), xpPerKill.getKilledItem(), dbXpSettings.getKillPriceFactor());
            }

            increaseXpPerBase(baseXpHashMap);
        }
    }

    private void sumUpXpPerBase(HashMap<SimpleBase, Integer> xpIncreasePreBase, SimpleBase base, SyncBaseItem syncBaseItem, double factor) {
        Integer xp = xpIncreasePreBase.get(base);
        if (xp == null) {
            xp = 0;
        }
        xp += (int) (syncBaseItem.getBaseItemType().getPrice() * factor);
        xpIncreasePreBase.put(base, xp);
    }

    private void increaseXpPerBase(HashMap<SimpleBase, Integer> xpIncreasePreBase) {
        for (Map.Entry<SimpleBase, Integer> entry : xpIncreasePreBase.entrySet()) {
            increaseXpPerBase(entry.getKey(), entry.getValue());
        }
    }

    @PostConstruct
    public void start() {
        HibernateUtil.openSession4InternalCall(sessionFactory);
        try {
            activateXpSettings();
        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
        }
    }

    @PreDestroy
    public void stop() {
        try {
            if (xpPerKillQueueWorker != null) {
                xpPerKillQueueWorker.stop();
                xpPerKillQueueWorker = null;
            }
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void onItemKilled(SimpleBase actorBase, SyncBaseItem killedItem, PlanetServices planetServices) {
        if (!planetServices.getBaseService().isBot(actorBase)) {
            xpPerKillQueueWorker.put(new XpPerKill(actorBase, killedItem, planetServices));
        }
    }

    @Override
    public void onReward(UserState userState, int deltaXp) {
        increaseXpPerUserState(userState, deltaXp);
    }

    private void increaseXpPerBase(SimpleBase simpleBase, int deltaXp) {
        UserState userState = planetSystemService.getServerPlanetServices(simpleBase).getBaseService().getUserState(simpleBase);
        if (userState != null) {
            increaseXpPerUserState(userState, deltaXp);
        }
    }

    private void increaseXpPerUserState(UserState userState, int deltaXp) {
        userState.increaseXp(deltaXp);
        Base base = userState.getBase();
        if (base != null) {
            XpPacket xpPacket = new XpPacket();
            xpPacket.setXp(userState.getXp());
            xpPacket.setXp2LevelUp(userGuidanceService.getXp2LevelUp(userState));
            base.getPlanet().getPlanetServices().getConnectionService().sendPacket(base.getSimpleBase(), xpPacket);
        }
        serverConditionService.onIncreaseXp(userState, deltaXp);
    }

    @Override
    public DbXpSettings getXpPointSettings() {
        DbXpSettings dbXpSettings;
        List<DbXpSettings> settings = HibernateUtil.loadAll(sessionFactory, DbXpSettings.class);
        if (settings.isEmpty()) {
            log.warn("No DbXpSettings found in DB. Will be created.");
            dbXpSettings = new DbXpSettings();
            dbXpSettings.setKillPriceFactor(0.1);
            dbXpSettings.setKillQueuePeriod(2000);
            dbXpSettings.setKillQueueSize(10000);
            sessionFactory.getCurrentSession().saveOrUpdate(dbXpSettings);
        } else if (settings.size() != 1) {
            log.warn("More then one DbXpSettings found in DB.");
            dbXpSettings = settings.get(0);
        } else {
            dbXpSettings = settings.get(0);
        }
        return dbXpSettings;
    }

    @Transactional
    @Override
    public void saveXpPointSettings(DbXpSettings dbXpSettings) {
        DbXpSettings original = getXpPointSettings();
        original.fill(dbXpSettings);
        sessionFactory.getCurrentSession().save(original);
        activateXpSettings();
    }

    private void activateXpSettings() {
        dbXpSettings = getXpPointSettings();
        stop();
        xpPerKillQueueWorker = new XpPerKillQueueWorker(dbXpSettings.getKillQueuePeriod(), dbXpSettings.getKillQueueSize());
    }
}
