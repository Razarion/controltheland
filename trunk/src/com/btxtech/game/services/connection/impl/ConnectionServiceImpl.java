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

import com.btxtech.game.jsre.client.common.OnlineBaseUpdate;
import com.btxtech.game.jsre.client.common.UserMessage;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.connection.ClientLogEntry;
import com.btxtech.game.services.connection.Connection;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.connection.ConnectionStatistics;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.utg.UserTrackingService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

/**
 * User: beat
 * Date: Jul 15, 2009
 * Time: 1:20:27 PM
 */
@Component("connectionService")
public class ConnectionServiceImpl extends TimerTask implements ConnectionService {
    public static final long USER_TRACKING_PERIODE = 10 * 1000;
    @Autowired
    private Session session;
    @Autowired
    private UserTrackingService userTrackingService;
    @Autowired
    private BotService botService;
    @Autowired
    private BaseService baseService;
    private Timer timer;
    private Log log = LogFactory.getLog(ConnectionServiceImpl.class);
    private HibernateTemplate hibernateTemplate;
    private final ArrayList<Connection> onlineConnection = new ArrayList<Connection>();
    private static final int MAX_NO_TICK_COUNT = 20;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @PostConstruct
    public void init() {
        timer = new Timer(getClass().getName(), true);
        timer.scheduleAtFixedRate(this, 0, USER_TRACKING_PERIODE);
    }

    @PreDestroy
    public void cleanup() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void sendSyncInfo(SyncItem syncItem) {
        synchronized (onlineConnection) {
            for (Connection connection : onlineConnection) {
                try {
                    connection.sendBaseSyncItem(syncItem);
                } catch (Throwable t) {
                    log.error("", t);
                }
            }
        }
    }

    @Override
    public void sendSyncInfos(Collection<SyncBaseItem> syncItem) {
        for (SyncItem item : syncItem) {
            sendSyncInfo(item);
        }
    }

    @Override
    public void sendPacket(Packet packet) {
        synchronized (onlineConnection) {
            for (Connection connection : onlineConnection) {
                try {
                    connection.sendPacket(packet);
                } catch (Throwable t) {
                    log.error("", t);
                }
            }
        }
    }

    @Override
    public void sendPacket(SimpleBase base, Packet packet) {
        synchronized (onlineConnection) {
            for (Connection connection : onlineConnection) {
                try {
                    if (connection.getBase().getSimpleBase().equals(base)) {
                        connection.sendPacket(packet);
                        return;
                    }
                } catch (Throwable t) {
                    log.error("", t);
                }
            }
        }
    }

    @Override
    public void sendUserMessage(UserMessage userMessage) {
        sendPacket(userMessage);
        userTrackingService.trackUserMessage(userMessage);
    }

    @Override
    public void run() {
        ArrayList<Base> closedConnection = new ArrayList<Base>();
        for (Iterator<Connection> it = onlineConnection.iterator(); it.hasNext();) {
            Connection connection = it.next();
            try {
                int tickCount = connection.resetAndGetTickCount();
                if (connection.getNoTickCount() > MAX_NO_TICK_COUNT) {
                    log.info("User kicked due timeout: " + baseService.getBaseName(connection.getBase().getSimpleBase()));
                    if (connection.getBase() != null && connection.getBase().getUser() != null) {
                        userTrackingService.onUserLeftGame(connection.getBase().getUser());
                    }
                    closedConnection.add(connection.getBase());
                    connection.setClosed();
                    it.remove();
                } else {
                    double ticksPerSecond = (double) tickCount / (double) (USER_TRACKING_PERIODE / 1000);
                    if (!Double.isInfinite(ticksPerSecond) && !Double.isNaN(ticksPerSecond)) {
                        String baseName = baseService.getBaseName(connection.getBase().getSimpleBase());
                        ConnectionStatistics connectionStatistics = new ConnectionStatistics(baseName, connection.getSessionId(), ticksPerSecond);
                        hibernateTemplate.saveOrUpdate(connectionStatistics);
                    }
                }
            } catch (Throwable t) {
                log.error("", t);
            }
        }
        if (!closedConnection.isEmpty()) {
            sendOnlineBasesUpdate();
        }
        for (Base base : closedConnection) {
            botService.onConnectionClosed(base);
        }
    }

    @Override
    public void clientLog(String message, Date date) {
        try {
            String baseName = null;
            try {
                baseName = baseService.getBaseName(baseService.getBase().getSimpleBase());
            } catch (com.btxtech.game.services.connection.NoConnectionException e) {
                // Ignore
            }
            ClientLogEntry clientLogEntry = new ClientLogEntry(message, date, session, baseName);
            // hibernateTemplate.saveOrUpdate(clientLogEntry);
            log.info(clientLogEntry.getFormatMessage());
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void createConnection(Base base) {
        Connection connection = session.getConnection();
        if (connection != null) {
            log.info("Existing connection will be terminated");
            closeConnection();
        }
        connection = new Connection(session.getSessionId());
        connection.setBase(base);
        session.setConnection(connection);
        synchronized (onlineConnection) {
            onlineConnection.add(connection);
        }
        if (base.getUser() != null) {
            userTrackingService.onUserEnterGame(base.getUser());
        }
        sendOnlineBasesUpdate();
        botService.onConnectionCreated(connection.getBase());
    }

    @Override
    public void closeConnection() {
        Connection connection = session.getConnection();
        if (connection == null) {
            throw new IllegalStateException("Connection does not exist");
        }
        if (connection.getBase() != null && connection.getBase().getUser() != null) {
            userTrackingService.onUserLeftGame(connection.getBase().getUser());
        }
        connection.setClosed();
        session.setConnection(null);
        synchronized (onlineConnection) {
            onlineConnection.remove(connection);
        }
        sendOnlineBasesUpdate();
        botService.onConnectionClosed(connection.getBase());
    }

    @Override
    public Connection getConnection() throws NoConnectionException {
        Connection connection = session.getConnection();
        if (connection == null) {
            throw new NoConnectionException("Connection does not exist");
        }
        if (connection.isClosed()) {
            throw new NoConnectionException("Connection already closed");
        }
        return connection;
    }

    @Override
    public boolean hasConnection() {
        Connection connection = session.getConnection();
        return connection != null && !connection.isClosed();
    }

    @Override
    public void sendOnlineBasesUpdate() {
        sendPacket(getOnlineBaseUpdate());
    }

    @Override
    public OnlineBaseUpdate getOnlineBaseUpdate() {
        HashSet<SimpleBase> simpleBases = new HashSet<SimpleBase>();
        synchronized (onlineConnection) {
            for (Connection connection : onlineConnection) {
                if (connection.getBase() != null) {
                    simpleBases.add(connection.getBase().getSimpleBase());
                }
            }
            simpleBases.addAll(botService.getRunningBotBases());
        }
        OnlineBaseUpdate onlineBaseUpdate = new OnlineBaseUpdate();
        onlineBaseUpdate.setOnlineBases(simpleBases);
        return onlineBaseUpdate;
    }

}
