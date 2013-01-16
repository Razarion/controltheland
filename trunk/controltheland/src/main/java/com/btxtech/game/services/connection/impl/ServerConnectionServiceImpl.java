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
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.packets.Message;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.services.common.ServerGlobalServices;
import com.btxtech.game.services.connection.Connection;
import com.btxtech.game.services.connection.ServerConnectionService;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.impl.ServerPlanetServicesImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * User: beat
 * Date: Jul 15, 2009
 * Time: 1:20:27 PM
 */
public class ServerConnectionServiceImpl implements ServerConnectionService {
    private static final long USER_TRACKING_PERIODE = 10 * 1000;
    private static final int MAX_NO_TICK_COUNT = 20;
    private ServerPlanetServicesImpl planetSystemService;
    private ServerGlobalServices serverGlobalServices;
    private Timer timer;
    private Log log = LogFactory.getLog(ServerConnectionServiceImpl.class);
    private final ArrayList<Connection> onlineConnection = new ArrayList<>();

    public void init(ServerPlanetServicesImpl planetServices, ServerGlobalServices serverGlobalServices) {
        this.planetSystemService = planetServices;
        this.serverGlobalServices = serverGlobalServices;
    }

    @Override
    public void activate() {
        timer = new Timer(getClass().getName(), true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    synchronized (onlineConnection) {
                        for (Iterator<Connection> it = onlineConnection.iterator(); it.hasNext(); ) {
                            Connection connection = it.next();
                            try {
                                int tickCount = connection.resetAndGetTickCount();
                                if (connection.getNoTickCount() > MAX_NO_TICK_COUNT) {
                                    log.info("User kicked due timeout: " + planetSystemService.getBaseService().getBaseName(connection.getBase().getSimpleBase()));
                                    if (connection.getBase() != null && connection.getBase().getUserState() != null && connection.getBase().getUserState().getUser() != null) {
                                        serverGlobalServices.getUserTrackingService().onUserLeftGameNoSession(serverGlobalServices.getUserService().getUser(connection.getBase().getUserState().getUser()));
                                    }
                                    connection.setClosed(NoConnectionException.Type.TIMED_OUT);
                                    it.remove();
                                } else {
                                    double ticksPerSecond = (double) tickCount / (double) (USER_TRACKING_PERIODE / 1000);
                                    if (!Double.isInfinite(ticksPerSecond) && !Double.isNaN(ticksPerSecond)) {
                                        String baseName = planetSystemService.getBaseService().getBaseName(connection.getBase().getSimpleBase());
                                        serverGlobalServices.getServerGlobalConnectionService().createConnectionStatisticsNoSession(baseName, connection.getSessionId(), ticksPerSecond);
                                    }
                                }
                            } catch (Throwable t) {
                                log.error("", t);
                            }
                        }
                    }
                } catch (Throwable t) {
                    log.error("", t);
                }
            }
        }, 0, USER_TRACKING_PERIODE);
    }

    @Override
    public void deactivate() {
        try {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        } catch (Throwable t) {
            log.error("", t);
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
    public void sendMessage(SimpleBase simpleBase, String key, Object[] args, boolean showRegisterDialog) {
        Message message = new Message();
        message.setMessage(serverGlobalServices.getServerI18nHelper().getStringNoRequest(simpleBase, key, args));
        message.setShowRegisterDialog(showRegisterDialog);
        sendPacket(simpleBase, message);
    }

    @Override
    public void createConnection(Base base, String startUuid) {
        // Connection to same base from same browser
        Connection connection = serverGlobalServices.getServerGlobalConnectionService().getSession().getConnection();
        if (connection != null) {
            log.warn("Existing connection will be terminated I");
            closeConnection(connection, NoConnectionException.Type.ANOTHER_CONNECTION_EXISTS);
        }
        // Connection to same base from different browser
        Connection preventConcurrentException = null;
        synchronized (onlineConnection) {
            for (Connection existingConnection : onlineConnection) {
                if (existingConnection.getBase().equals(base)) {
                    preventConcurrentException = existingConnection;
                    break;
                }
            }
        }
        if (preventConcurrentException != null) {
            log.warn("Existing connection will be terminated II");
            closeConnection(preventConcurrentException, NoConnectionException.Type.ANOTHER_CONNECTION_EXISTS);
        }

        connection = new Connection(serverGlobalServices.getServerGlobalConnectionService().getSession().getSessionId(), startUuid);
        connection.setBase(base);
        serverGlobalServices.getServerGlobalConnectionService().getSession().setConnection(connection);
        log.debug("Connection established");
        synchronized (onlineConnection) {
            onlineConnection.add(connection);
        }
        if (base.getUserState() != null && base.getUserState().getUser() != null) {
            serverGlobalServices.getUserTrackingService().onUserEnterGame(serverGlobalServices.getUserService().getUser(base.getUserState().getUser()));
        }
    }

    private void closeConnection(Connection connection, NoConnectionException.Type closedReason) {
        if (connection.getBase() != null && connection.getBase().getUserState() != null && connection.getBase().getUserState().getUser() != null) {
            serverGlobalServices.getUserTrackingService().onUserLeftGame(serverGlobalServices.getUserService().getUser(connection.getBase().getUserState().getUser()));
        }
        connection.setClosed(closedReason);
        log.debug("Connection closed 1");
        synchronized (onlineConnection) {
            onlineConnection.remove(connection);
        }
    }

    @Override
    public void closeConnection(SimpleBase simpleBase, NoConnectionException.Type closedReason) {
        Connection connection = null;
        synchronized (onlineConnection) {
            for (Iterator<Connection> iterator = onlineConnection.iterator(); iterator.hasNext(); ) {
                Connection online = iterator.next();
                if (online.getBase().getSimpleBase().equals(simpleBase)) {
                    connection = online;
                    iterator.remove();
                }
            }
        }
        if (connection == null) {
            throw new IllegalStateException("Online connection does not exist for base: " + simpleBase);
        }

        connection.setClosed(closedReason);
        log.debug("Connection closed 2");
    }


    @Override
    public Connection getConnection(String startUuid) throws NoConnectionException {
        Connection connection = serverGlobalServices.getServerGlobalConnectionService().getSession().getConnection();
        if (connection == null) {
            throw new NoConnectionException(NoConnectionException.Type.NON_EXISTENT);
        }
        if (connection.isClosed()) {
            throw new NoConnectionException(connection.getClosedReason());
        }
        if (!connection.getStartUuid().equals(startUuid)) {
            throw new NoConnectionException(NoConnectionException.Type.ANOTHER_CONNECTION_EXISTS);
        }
        return connection;
    }

    @Override
    public boolean hasConnection(SimpleBase simpleBase) {
        synchronized (onlineConnection) {
            for (Connection connection : onlineConnection) {
                if (connection.getBase().getSimpleBase().equals(simpleBase)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Collection<SimpleBase> getOnlineBases() {
        HashSet<SimpleBase> simpleBases = new HashSet<>();
        synchronized (onlineConnection) {
            for (Connection connection : onlineConnection) {
                if (connection.getBase() != null) {
                    simpleBases.add(connection.getBase().getSimpleBase());
                }
            }
        }
        return simpleBases;
    }

    @Override
    public GameEngineMode getGameEngineMode() {
        return GameEngineMode.MASTER;
    }
}
