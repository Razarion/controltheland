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

package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.GameInfo;
import com.btxtech.game.jsre.client.common.Message;
import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.client.common.OnlineBaseUpdate;
import com.btxtech.game.jsre.client.common.UserMessage;
import com.btxtech.game.jsre.client.dialogs.MessageDialog;
import com.btxtech.game.jsre.client.item.ClientItemTypeAccess;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.territory.ClientTerritoryService;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.client.utg.MissionTarget;
import com.btxtech.game.jsre.common.AccountBalancePacket;
import com.btxtech.game.jsre.common.EnergyPacket;
import com.btxtech.game.jsre.common.LevelPacket;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.XpBalancePacket;
import com.btxtech.game.jsre.common.bot.PlayerSimulation;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.itemTypeAccess.ItemTypeAccessSyncInfo;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: Jul 4, 2009
 * Time: 11:23:52 AM
 */
public class Connection implements AsyncCallback<Void> {
    public static final int MIN_DELAY_BETWEEN_TICKS = 200;
    public static final int STATISTIC_DELAY = 10000;
    public static final Connection INSTANCE = new Connection();
    private boolean isRegistered;
    private GameInfo gameInfo;
    private ArrayList<BaseCommand> commandQueue = new ArrayList<BaseCommand>();

    private MovableServiceAsync movableServiceAsync = GWT.create(MovableService.class);
    private Timer timer;

    static public Connection getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private Connection() {

    }

    public void start() {
        StartupProbe.getInstance().taskSwitch(StartupTask.INIT_GUI, StartupTask.DOWNLOAD_GAME_INFO);

        movableServiceAsync.getGameInfo(new AsyncCallback<GameInfo>() {

            @Override
            public void onFailure(Throwable caught) {
                handleDisconnection(caught);
                StartupProbe.getInstance().taskFailed(StartupTask.DOWNLOAD_GAME_INFO, caught);
            }

            @Override
            public void onSuccess(GameInfo gameInfo) {
                StartupProbe.getInstance().taskSwitch(StartupTask.DOWNLOAD_GAME_INFO, StartupTask.INIT_GAME);
                try {
                    if (gameInfo != null) {
                        setupGameStructure(gameInfo);
                    } else {
                        Window.Location.assign("/");
                    }
                } catch (Throwable throwable) {
                    GwtCommon.handleException(throwable);
                    StartupProbe.getInstance().taskFailed(StartupTask.INIT_GAME, throwable);
                }
            }
        });
    }

    private void setupGameStructure(final GameInfo gameInfo) {
        isRegistered = gameInfo.isRegistered();
        this.gameInfo = gameInfo;
        ClientBase.getInstance().setBase(gameInfo.getBase());
        ClientBase.getInstance().setAccountBalance(gameInfo.getAccountBalance());
        InfoPanel.getInstance().setGameInfo(gameInfo);
        ClientItemTypeAccess.getInstance().setAllowedItemTypes(gameInfo.getAllowedItemTypes());
        RadarPanel.getInstance().updateEnergy(gameInfo.getEnergyGenerating(), gameInfo.getEnergyConsuming());
        OnlineBasePanel.getInstance().setOnlineBases(gameInfo.getOnlineBaseUpdate());
        TerrainView.getInstance().setupTerrain(gameInfo.getTerrainSettings(),
                gameInfo.getTerrainImagePositions(),
                gameInfo.getSurfaceRects(),
                gameInfo.getSurfaceImages(),
                gameInfo.getTerrainImages());
        ClientUserTracker.getInstance().setCollectionTime(gameInfo.getUserActionCollectionTime());
        MissionTarget.getInstance().setLevel(gameInfo.getLevel());
        ClientTerritoryService.getInstance().setTerritories(gameInfo.getTerritories());

        StartupProbe.getInstance().taskSwitch(StartupTask.INIT_GAME, StartupTask.LOAD_UNIT_INFOS);
        movableServiceAsync.getItemTypes(new AsyncCallback<Collection<ItemType>>() {
            @Override
            public void onFailure(Throwable throwable) {
                StartupProbe.getInstance().taskFailed(StartupTask.LOAD_UNIT_INFOS, throwable);
                handleDisconnection(throwable);
            }

            @Override
            public void onSuccess(Collection<ItemType> itemTypes) {
                ItemContainer.getInstance().setItemTypes(itemTypes);
                StartupProbe.getInstance().taskSwitch(StartupTask.LOAD_UNIT_INFOS, StartupTask.LOAD_UNITS);
                movableServiceAsync.getAllSyncInfo(new AsyncCallback<Collection<SyncItemInfo>>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        handleDisconnection(throwable);
                        StartupProbe.getInstance().taskFailed(StartupTask.LOAD_UNITS, throwable);
                    }

                    @Override
                    public void onSuccess(Collection<SyncItemInfo> syncInfos) {
                        try {
                            StartupProbe.getInstance().taskSwitch(StartupTask.LOAD_UNITS, StartupTask.START_ACTION_HANDLER);
                            if (syncInfos == null) {
                                StartupProbe.getInstance().taskFailed(StartupTask.START_ACTION_HANDLER, "No synchronization information received");
                                return;
                            }
                            for (SyncItemInfo syncInfo : syncInfos) {
                                try {
                                    ItemContainer.getInstance().sychronize(syncInfo);
                                } catch (Throwable t) {
                                    GwtCommon.handleException(t);
                                }
                            }
                            TerrainView.getInstance().moveToHome();
                            PlayerSimulation.getInstance().start();
                            timer.schedule(MIN_DELAY_BETWEEN_TICKS);
                            StartupProbe.getInstance().taskFinished(StartupTask.START_ACTION_HANDLER);
                        } catch (Throwable t) {
                            GwtCommon.handleException(t);
                            StartupProbe.getInstance().taskFailed(StartupTask.START_ACTION_HANDLER, t);
                        }

                    }
                });
            }
        });

        timer = new Timer() {
            @Override
            public void run() {
                tick();
            }
        };
    }

    private void tick() {
        if (movableServiceAsync == null) {
            return;
        }
        movableServiceAsync.getSyncInfo(ClientBase.getInstance().getSimpleBase(), new AsyncCallback<Collection<Packet>>() {
            @Override
            public void onFailure(Throwable throwable) {
                handleDisconnection(throwable);
            }

            @Override
            public void onSuccess(Collection<Packet> packets) {
                try {
                    handlePackets(packets);
                } finally {
                    timer.schedule(MIN_DELAY_BETWEEN_TICKS);
                }
            }
        });
    }

    private void handlePackets(Collection<Packet> packets) {
        if (packets == null) {
            return;
        }
        for (Packet packet : packets) {
            try {
                if (packet instanceof SyncItemInfo) {
                    ItemContainer.getInstance().sychronize((SyncItemInfo) packet);
                } else if (packet instanceof Message) {
                    if (!PlayerSimulation.isActive()) {
                        Message message = (Message) packet;
                        MessageDialog.show(message.getTitle(), "<h1>" + message.getMessage() + "</h1>");
                    }
                } else if (packet instanceof AccountBalancePacket) {
                    AccountBalancePacket balancePacket = (AccountBalancePacket) packet;
                    ClientBase.getInstance().setAccountBalance(balancePacket.getAccountBalance());
                } else if (packet instanceof XpBalancePacket) {
                    XpBalancePacket xpBalancePacket = (XpBalancePacket) packet;
                    InfoPanel.getInstance().updateXp(xpBalancePacket.getXp());
                } else if (packet instanceof ItemTypeAccessSyncInfo) {
                    ItemTypeAccessSyncInfo itemTypeAccessSyncInfo = (ItemTypeAccessSyncInfo) packet;
                    ClientItemTypeAccess.getInstance().setAllowedItemTypes(itemTypeAccessSyncInfo.getAllowedItemTypes());
                    SelectionHandler.getInstance().refresh();
                } else if (packet instanceof EnergyPacket) {
                    EnergyPacket energyPacket = (EnergyPacket) packet;
                    InfoPanel.getInstance().updateEnergy(energyPacket.getGenerating(), energyPacket.getConsuming());
                    RadarPanel.getInstance().updateEnergy(energyPacket.getGenerating(), energyPacket.getConsuming());
                } else if (packet instanceof UserMessage) {
                    OnlineBasePanel.getInstance().onMessageReceived((UserMessage) packet);
                } else if (packet instanceof OnlineBaseUpdate) {
                    OnlineBasePanel.getInstance().setOnlineBases((OnlineBaseUpdate) packet);
                } else if (packet instanceof LevelPacket) {
                    MissionTarget.getInstance().onLevelChanged((LevelPacket) packet);
                } else {
                    throw new IllegalArgumentException(this + " unknown packet: " + packet);
                }
            } catch (Throwable t) {
                GwtCommon.handleException(t);
            }
        }
    }


    public void addCommandToQueue(BaseCommand baseCommand) {
        if (movableServiceAsync != null) {
            commandQueue.add(baseCommand);
        }
    }

    public void sendCommandQueue() {
        if (movableServiceAsync != null && !commandQueue.isEmpty()) {
            movableServiceAsync.sendCommands(commandQueue, this);
        }
        commandQueue.clear();
    }

    public void createMissionTraget(SyncBaseItem syncBaseItem) {
        if (!syncBaseItem.getId().isSynchronized()) {
            throw new IllegalStateException(this + " createMissionTarget: Item is not syncronized " + syncBaseItem);
        }
        if (movableServiceAsync != null) {
            movableServiceAsync.createMissionTraget(syncBaseItem.getId(), this);
        }
    }

    public void createMissionMoney(SyncBaseItem syncBaseItem) {
        if (!syncBaseItem.getId().isSynchronized()) {
            throw new IllegalStateException(this + " createMissionMoney: Item is not syncronized " + syncBaseItem);
        }
        if (movableServiceAsync != null) {
            movableServiceAsync.createMissionMoney(syncBaseItem.getId(), this);
        }
    }

    public void sendUserMessage(String text) {
        if (movableServiceAsync != null) {
            UserMessage userMessage = new UserMessage();
            userMessage.setBaseName(ClientBase.getInstance().getSimpleBase().getName());
            userMessage.setMessage(text);
            movableServiceAsync.sendUserMessage(userMessage, this);
        }
    }

    public void tutorialTerminated() {
        if (movableServiceAsync != null) {
            movableServiceAsync.tutorialTerminated(this);
        }
    }

    public void getMissionTarget(final MissionTarget missionTargetDialog) {
        if (movableServiceAsync != null) {
            movableServiceAsync.getMissionTarget(new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable caught) {
                    missionTargetDialog.setNoConnection(caught);
                }

                @Override
                public void onSuccess(String result) {
                    missionTargetDialog.setMissionTarget(result);
                }
            });
        } else {
            missionTargetDialog.setNoConnection(null);
        }
    }

    public void surrenderBase() {
        if (movableServiceAsync != null) {
            movableServiceAsync.surrenderBase(this);
            movableServiceAsync = null;
        }
    }

    public void closeConnection() {
        if (movableServiceAsync != null) {
            movableServiceAsync.closeConnection(this);
            movableServiceAsync = null;
        }
    }

    public static MovableServiceAsync getMovableServiceAsync() {
        return INSTANCE.movableServiceAsync;
    }

    @Override
    public void onFailure(Throwable caught) {
        handleDisconnection(caught);
    }

    @Override
    public void onSuccess(Void result) {
        // Ignore
    }

    private void handleDisconnection(Throwable throwable) {
        movableServiceAsync = null;
        if (throwable instanceof NotYourBaseException) {
            MessageDialog.show("Not your Base", "Most likely you start another<br />base in another browser window");
        } else if (throwable instanceof NoConnectionException) {
            MessageDialog.show("No Connection", "Most likely you start another<br />base in another browser window: " + throwable.getMessage());
        } else {
            GwtCommon.handleException(throwable);
        }
    }

    public static boolean isConnected() {
        return INSTANCE.movableServiceAsync != null;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }
}
