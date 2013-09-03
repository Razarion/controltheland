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

import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.cockpit.chat.ChatMessageFilter;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.packets.MessageIdPacket;
import com.btxtech.game.jsre.common.packets.ServerRebootMessagePacket;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.connection.Connection;
import com.btxtech.game.services.connection.DbClientDebugEntry;
import com.btxtech.game.services.connection.DbConnectionStatistics;
import com.btxtech.game.services.connection.MessageIdPacketQueue;
import com.btxtech.game.services.connection.ServerGlobalConnectionService;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserTrackingService;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * User: beat
 * Date: Jul 15, 2009
 * Time: 1:20:27 PM
 */
@Component("ServerGlobalConnectionService")
public class ServerGlobalConnectionServiceImpl implements ServerGlobalConnectionService {
    private static int ONLINE_MISSION_TIMER_DELAY = 10000;
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
    private MessageIdPacketQueue messageIdPacketQueue;
    private ScheduledThreadPoolExecutor onlineMissionThreadPool;
    private final Set<UserState> onlineMissionUserStates = new HashSet<>();
    private Set<UserState> onlineMissionUserStatesTmp = new HashSet<>();

    @PostConstruct
    public void init() {
        onlineMissionThreadPool = new ScheduledThreadPoolExecutor(1, new CustomizableThreadFactory("ServerGlobalConnectionServiceImpl botThread "));
        onlineMissionThreadPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (onlineMissionUserStates) {
                        onlineMissionUserStates.clear();
                        onlineMissionUserStates.addAll(onlineMissionUserStatesTmp);
                        onlineMissionUserStatesTmp.clear();
                    }
                } catch (Exception e) {
                    ExceptionHandler.handleException(e);
                }
            }
        }, ONLINE_MISSION_TIMER_DELAY, ONLINE_MISSION_TIMER_DELAY, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void destroy() {
        if (onlineMissionThreadPool != null) {
            onlineMissionThreadPool.shutdownNow();
            onlineMissionThreadPool = null;
        }
    }


    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public void createConnectionStatisticsNoSession(String sessionId, double ticksPerSecond, int planetId) {
        DbConnectionStatistics connectionStatistics = new DbConnectionStatistics(sessionId, ticksPerSecond, planetId);
        HibernateUtil.openSession4InternalCall(sessionFactory);
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(connectionStatistics);
        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
        }
    }

    @Override
    public void sendChatMessage(ChatMessage chatMessage, ChatMessageFilter chatMessageFilter) {
        messageIdPacketQueue.setFilterAndPutMessage(chatMessage, chatMessageFilter);
        planetSystemService.sendPacket(chatMessage);
        userTrackingService.trackChatMessage(chatMessage);
    }

    @Override
    public void sendServerRebootMessage(int rebootInSeconds, int downTimeInMinutes) {
        ServerRebootMessagePacket packet = new ServerRebootMessagePacket();
        packet.setRebootInSeconds(rebootInSeconds);
        packet.setDownTimeInMinutes(downTimeInMinutes);
        messageIdPacketQueue.initAndPutMessage(packet);
        planetSystemService.sendPacket(packet);
    }

    @Override
    public List<MessageIdPacket> pollMessageIdPackets(Integer lastMessageId, ChatMessageFilter chatMessageFilter, GameEngineMode gameEngineMode) {
        if (gameEngineMode == GameEngineMode.MASTER) {
            synchronized (onlineMissionUserStates) {
                onlineMissionUserStatesTmp.add(userService.getUserState());
            }
        }
        return messageIdPacketQueue.peekMessages(lastMessageId, chatMessageFilter);
    }

    @Override
    public MessageIdPacketQueue getMessageIdPacketQueue() {
        return messageIdPacketQueue;
    }

    @Override
    @Transactional
    public void saveClientDebug(Date date, String category, String message) {
        DbClientDebugEntry dbClientDebugEntry = new DbClientDebugEntry(date, session, userService.getUserState().getUser(), category, message);
        sessionFactory.getCurrentSession().saveOrUpdate(dbClientDebugEntry);
    }

    @Override
    public List<UserState> getAllOnlineMissionUserState() {
        synchronized (onlineMissionUserStates) {
            return new ArrayList<>(onlineMissionUserStates);
        }
    }

    @Override
    public void onLogout() {
        try {
            Connection connection = session.getConnection();
            if(connection == null) {
                return;
            }
            connection.getServerPlanetServices().getConnectionService().onLogout();
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }


    @Override
    public Connection getConnection(String startUuid) throws NoConnectionException {
        Connection connection = session.getConnection();
        if(connection == null) {
            throw new NoConnectionException(NoConnectionException.Type.NON_EXISTENT);
        }
        if(connection.isClosed())  {
            throw new NoConnectionException(connection.getClosedReason());
        }
        if (!connection.getStartUuid().equals(startUuid)) {
            throw new NoConnectionException(NoConnectionException.Type.ANOTHER_CONNECTION_EXISTS);
        }
        return connection;
    }

}
