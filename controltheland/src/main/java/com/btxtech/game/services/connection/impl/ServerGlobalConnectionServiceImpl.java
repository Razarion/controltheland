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

package com.btxtech.game.services.connection.impl;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.connection.ClientDebugEntry;
import com.btxtech.game.services.connection.ConnectionStatistics;
import com.btxtech.game.services.connection.ServerGlobalConnectionService;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.mgmt.ServerI18nHelper;
import com.btxtech.game.services.planet.Planet;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserTrackingService;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * User: beat
 * Date: Jul 15, 2009
 * Time: 1:20:27 PM
 */
@Component("ServerGlobalConnectionService")
public class ServerGlobalConnectionServiceImpl implements ServerGlobalConnectionService {
    @Autowired
    private Session session;
    @Autowired
    private UserTrackingService userTrackingService;
    @Autowired
    private UserService userService;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private ServerI18nHelper serverI18nHelper;
    private ChatMessageQueue chatMessageQueue = new ChatMessageQueue();

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public void createConnectionStatisticsNoSession(String baseName, String sessionId, double ticksPerSecond) {
        ConnectionStatistics connectionStatistics = new ConnectionStatistics(baseName, sessionId, ticksPerSecond);
        HibernateUtil.openSession4InternalCall(sessionFactory);
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(connectionStatistics);
        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
        }
    }

    @Override
    public void sendChatMessage(ChatMessage chatMessage) {
        User user = userService.getUser();
        String name;
        if (user != null) {
            name = user.getUsername();
        } else if (userService.getUserState().getBase() != null) {
            name = planetSystemService.getServerPlanetServices().getBaseService().getBaseName();
        } else {
            name = serverI18nHelper.getString("guest");
        }
        chatMessageQueue.initAndPutMessage(name, chatMessage);
        for (Planet planet : planetSystemService.getAllPlanets()) {
            planet.getPlanetServices().getConnectionService().sendPacket(chatMessage);
        }
        userTrackingService.trackChatMessage(chatMessage);
    }

    @Override
    public List<ChatMessage> pollChatMessages(Integer lastMessageId) {
        return chatMessageQueue.peekMessages(lastMessageId);
    }

    @Override
    public Collection<SimpleBase> getOnlineBases() {
        HashSet<SimpleBase> simpleBases = new HashSet<>();
        for (Planet planet : planetSystemService.getAllPlanets()) {
            simpleBases.addAll(planet.getPlanetServices().getConnectionService().getOnlineBases());
        }
        return simpleBases;
    }

    @Override
    @Transactional
    public void saveClientDebug(Date date, String category, String message) {
        ClientDebugEntry clientDebugEntry = new ClientDebugEntry(date, session, userService.getUserState().getUser(), category, message);
        sessionFactory.getCurrentSession().saveOrUpdate(clientDebugEntry);
    }

}
