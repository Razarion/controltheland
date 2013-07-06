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
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.packets.Message;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.ServerGlobalServices;
import com.btxtech.game.services.connection.Connection;
import com.btxtech.game.services.connection.OnlineUserDTO;
import com.btxtech.game.services.connection.ServerConnectionService;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.impl.ServerPlanetServicesImpl;
import com.btxtech.game.services.user.UserState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * User: beat
 * Date: Jul 15, 2009
 * Time: 1:20:27 PM
 */
public class ServerConnectionServiceImpl implements ServerConnectionService {
    private static long USER_TRACKING_PERIODE = 10 * 1000;
    private static final int MAX_NO_TICK_COUNT = 20;
    private ServerPlanetServicesImpl planetServices;
    private ServerGlobalServices serverGlobalServices;
    private Timer timer;
    private Log log = LogFactory.getLog(ServerConnectionServiceImpl.class);
    private final Map<UserState, Connection> onlineConnection = new HashMap<>();

    public void init(ServerPlanetServicesImpl planetServices, ServerGlobalServices serverGlobalServices) {
        this.planetServices = planetServices;
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
                        Collection<Connection> timedOutConnections = new ArrayList<>();
                        for (Connection connection : onlineConnection.values()) {
                            try {
                                int tickCount = connection.resetAndGetTickCount();
                                if (connection.getNoTickCount() > MAX_NO_TICK_COUNT) {
                                    timedOutConnections.add(connection);
                                } else {
                                    double ticksPerSecond = (double) tickCount / (double) (USER_TRACKING_PERIODE / 1000);
                                    if (!Double.isInfinite(ticksPerSecond) && !Double.isNaN(ticksPerSecond)) {
                                        serverGlobalServices.getServerGlobalConnectionService().createConnectionStatisticsNoSession(connection.getSessionId(), ticksPerSecond, planetServices.getPlanetInfo().getPlanetId());
                                    }
                                }
                            } catch (Throwable t) {
                                ExceptionHandler.handleException(t);
                            }
                        }
                        for (Connection timedOutConnection : timedOutConnections) {
                            try {
                                closeConnection(timedOutConnection, NoConnectionException.Type.TIMED_OUT);
                            } catch (Throwable t) {
                                ExceptionHandler.handleException(t);
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
            for (Connection connection : onlineConnection.values()) {
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
            for (Connection connection : onlineConnection.values()) {
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
        try {
            UserState userState = planetServices.getBaseService().getUserState(base);
            if (userState == null) {
                return;
            }
            synchronized (onlineConnection) {
                Connection connection = onlineConnection.get(userState);
                if (connection != null) {
                    connection.sendPacket(packet);
                }
            }
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public boolean sendPacket(UserState userState, Packet packet) {
        try {
            synchronized (onlineConnection) {
                Connection connection = onlineConnection.get(userState);
                if (connection != null) {
                    connection.sendPacket(packet);
                    return true;
                }
            }
        } catch (Throwable t) {
            log.error("", t);
        }
        return false;
    }


    @Override
    public void setChatMessageFilter(UserState userState, ChatMessageFilter chatMessageFilter) {
        try {
            synchronized (onlineConnection) {
                Connection connection = onlineConnection.get(userState);
                if (connection != null) {
                    connection.setChatMessageFilter(chatMessageFilter);
                }
            }
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void sendMessage(UserState userState, String key, Object[] args, boolean showRegisterDialog) {
        Message message = new Message();
        message.setMessage(serverGlobalServices.getServerI18nHelper().getStringNoRequest(userState, key, args));
        message.setShowRegisterDialog(showRegisterDialog);
        sendPacket(userState, message);
    }

    @Override
    public void sendMessage(SimpleBase simpleBase, String key, Object[] args, boolean showRegisterDialog) {
        Message message = new Message();
        message.setMessage(serverGlobalServices.getServerI18nHelper().getStringNoRequest(simpleBase, key, args));
        message.setShowRegisterDialog(showRegisterDialog);
        sendPacket(simpleBase, message);
    }

    @Override
    public void createConnection(UserState userState, String startUuid) {
        // Connection to same base from same browser
        Connection connection = serverGlobalServices.getServerGlobalConnectionService().getSession().getConnection();
        if (connection != null) {
            closeConnection(connection, NoConnectionException.Type.ANOTHER_CONNECTION_EXISTS);
        }
        // Connection to same base from different browser
        synchronized (onlineConnection) {
            Connection existingConnection = onlineConnection.get(userState);
            if (existingConnection != null) {
                closeConnection(existingConnection, NoConnectionException.Type.ANOTHER_CONNECTION_EXISTS);
            }
        }

        connection = new Connection(userState, planetServices, serverGlobalServices, startUuid);
        serverGlobalServices.getServerGlobalConnectionService().getSession().setConnection(connection);
        synchronized (onlineConnection) {
            onlineConnection.put(userState, connection);
        }
        if (userState.isRegistered()) {
            serverGlobalServices.getUserTrackingService().onUserEnterGame(serverGlobalServices.getUserService().getUser(userState));
        }
    }

    private void closeConnection(Connection connection, NoConnectionException.Type closedReason) {
        UserState userState = connection.getUserState();
        if (userState.isRegistered()) {
            serverGlobalServices.getUserTrackingService().onUserLeftGame(serverGlobalServices.getUserService().getUser(userState));
        }
        connection.setClosed(closedReason);
        synchronized (onlineConnection) {
            onlineConnection.remove(userState);
        }
    }

    @Override
    public boolean hasConnection(UserState userState) {
        synchronized (onlineConnection) {
            return onlineConnection.containsKey(userState);
        }
    }

    @Override
    public Collection<OnlineUserDTO> getOnlineConnections() {
        ArrayList<UserState> onlineUserStates = new ArrayList<>();
        synchronized (onlineConnection) {
            for (Connection connection : onlineConnection.values()) {
                onlineUserStates.add(connection.getUserState());
            }
        }
        Collection<OnlineUserDTO> onlineUsers = new ArrayList<>();
        for (UserState userState : onlineUserStates) {
            OnlineUserDTO onlineUserDTO = new OnlineUserDTO(userState, planetServices.getPlanetInfo().getName());
            Base base = planetServices.getBaseService().getBase(userState);
            if (base != null) {
                onlineUserDTO.setBaseName(planetServices.getBaseService().getBaseName(base.getSimpleBase()));
            }
            if (userState.isRegistered()) {
                onlineUserDTO.setUser(serverGlobalServices.getUserService().getUser(userState));
            }
            onlineUsers.add(onlineUserDTO);
        }
        return onlineUsers;
    }

    @Override
    public GameEngineMode getGameEngineMode() {
        return GameEngineMode.MASTER;
    }

    @Override
    public void onLogout() {
        UserState userState = serverGlobalServices.getUserService().getUserState();
        Connection connection;
        synchronized (onlineConnection) {
            connection = onlineConnection.get(userState);
        }
        if (connection != null) {
            closeConnection(connection, NoConnectionException.Type.LOGGED_OUT);
        }
    }
}
