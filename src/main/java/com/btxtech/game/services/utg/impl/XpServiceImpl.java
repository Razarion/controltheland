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
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.XpService;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    @Autowired
    private UserService userService;
    @Value(value = "${xpService.killQueuePeriod}")
    private long killQueuePeriodMs;
    @Value(value = "${xpService.killQueueSize}")
    private int killQueueSize;

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
                sumUpXpPerBase(baseXpHashMap, xpPerKill.getActorBase(), xpPerKill.getKilledItem());
            }

            increaseXpPerBase(baseXpHashMap);
        }
    }

    private void sumUpXpPerBase(HashMap<SimpleBase, Integer> xpIncreasePreBase, SimpleBase base, SyncBaseItem syncBaseItem) {
        Integer xp = xpIncreasePreBase.get(base);
        if (xp == null) {
            xp = 0;
        }
        xp += syncBaseItem.getBaseItemType().getXpOnKilling();
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
        if (!planetServices.getBaseService().isBot(actorBase) && !planetServices.getBaseService().isAbandoned(actorBase) && planetSystemService.isUserOnCorrectPlanet(userService.getUserState(actorBase))) {
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
        XpPacket xpPacket = new XpPacket();
        xpPacket.setXp(userState.getXp());
        xpPacket.setXp2LevelUp(userGuidanceService.getXp2LevelUp(userState));
        planetSystemService.sendPacket(userState, xpPacket);
        serverConditionService.onIncreaseXp(userState, deltaXp);
    }

    private void activateXpSettings() {
        stop();
        xpPerKillQueueWorker = new XpPerKillQueueWorker(killQueuePeriodMs, killQueueSize);
    }
}
