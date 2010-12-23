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

import com.btxtech.game.jsre.client.cockpit.Cockpit;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.Message;
import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.client.common.UserMessage;
import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.client.control.task.DeferredStartup;
import com.btxtech.game.jsre.client.dialogs.MessageDialog;
import com.btxtech.game.jsre.client.item.ClientItemTypeAccess;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.MissionTarget;
import com.btxtech.game.jsre.common.AccountBalancePacket;
import com.btxtech.game.jsre.common.BaseChangedPacket;
import com.btxtech.game.jsre.common.EnergyPacket;
import com.btxtech.game.jsre.common.EventTrackingItem;
import com.btxtech.game.jsre.common.EventTrackingStart;
import com.btxtech.game.jsre.common.LevelPacket;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.SelectionTrackingItem;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.jsre.common.UserStage;
import com.btxtech.game.jsre.common.XpBalancePacket;
import com.btxtech.game.jsre.common.gameengine.services.itemTypeAccess.ItemTypeAccessSyncInfo;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.jsre.common.tutorial.HouseSpacePacket;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * User: beat
 * Date: Jul 4, 2009
 * Time: 11:23:52 AM
 */
public class Connection implements AsyncCallback<Void> {
    public static final int MIN_DELAY_BETWEEN_POLL = 200;
    public static final int STATISTIC_DELAY = 10000;
    public static final Connection INSTANCE = new Connection();
    private boolean isRegistered;
    private GameInfo gameInfo;
    private Collection<SyncItemInfo> syncInfos;
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

    public void downloadGameInfo(final DeferredStartup deferredStartup) {
        if (movableServiceAsync != null) {
            movableServiceAsync.getGameInfo(new AsyncCallback<GameInfo>() {

                @Override
                public void onFailure(Throwable caught) {
                    handleDisconnection(caught);
                    deferredStartup.failed(caught);
                }

                @Override
                public void onSuccess(GameInfo gameInfo) {
                    Connection.this.gameInfo = gameInfo;
                    deferredStartup.finished();
                }
            });
        } else {
            deferredStartup.failed(DeferredStartup.NO_CONNECTION);
        }
    }

    /**
     * Shall only be used by the play back functionality. Other classes must use
     * downloadGameInfo.
     *
     * @param gameInfo the gameinfo
     */
    public void setGameInfo(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    public void downloadAllSyncInfo(final DeferredStartup deferredStartup) {
        if (movableServiceAsync != null) {
            movableServiceAsync.getAllSyncInfo(new AsyncCallback<Collection<SyncItemInfo>>() {
                @Override
                public void onFailure(Throwable throwable) {
                    handleDisconnection(throwable);
                    deferredStartup.failed(throwable);
                }

                @Override
                public void onSuccess(Collection<SyncItemInfo> syncInfos) {
                    if (syncInfos != null) {
                        Connection.this.syncInfos = syncInfos;
                        deferredStartup.finished();
                    } else {
                        deferredStartup.failed(DeferredStartup.NO_SYNC_INFO);
                    }
                }
            });
        } else {
            deferredStartup.failed(DeferredStartup.NO_CONNECTION);
        }
    }

    public void startSyncInfoPoll() {
        timer = new Timer() {
            @Override
            public void run() {
                pollSyncInfo();
            }
        };
        timer.schedule(MIN_DELAY_BETWEEN_POLL);
    }

    public void stopSyncInfoPoll() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void pollSyncInfo() {
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
                    timer.schedule(MIN_DELAY_BETWEEN_POLL);
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
                    Message message = (Message) packet;
                    MessageDialog.show("<h1>" + message.getMessage() + "</h1>");
                } else if (packet instanceof AccountBalancePacket) {
                    AccountBalancePacket balancePacket = (AccountBalancePacket) packet;
                    ClientBase.getInstance().setAccountBalance(balancePacket.getAccountBalance());
                } else if (packet instanceof XpBalancePacket) {
                    XpBalancePacket xpBalancePacket = (XpBalancePacket) packet;
                    Cockpit.getInstance().updateXp(xpBalancePacket.getXp());
                } else if (packet instanceof ItemTypeAccessSyncInfo) {
                    ItemTypeAccessSyncInfo itemTypeAccessSyncInfo = (ItemTypeAccessSyncInfo) packet;
                    ClientItemTypeAccess.getInstance().setAllowedItemTypes(itemTypeAccessSyncInfo.getAllowedItemTypes());
                    SelectionHandler.getInstance().refresh();
                } else if (packet instanceof EnergyPacket) {
                    EnergyPacket energyPacket = (EnergyPacket) packet;
                    Cockpit.getInstance().updateEnergy(energyPacket.getGenerating(), energyPacket.getConsuming());
                    RadarPanel.getInstance().updateEnergy(energyPacket.getGenerating(), energyPacket.getConsuming());
                } else if (packet instanceof UserMessage) {
                    Cockpit.getInstance().onMessageReceived((UserMessage) packet);
                } else if (packet instanceof LevelPacket) {
                    MissionTarget.getInstance().onLevelChanged((LevelPacket) packet);
                } else if (packet instanceof BaseChangedPacket) {
                    ClientBase.getInstance().onBaseChangedPacket((BaseChangedPacket) packet);
                } else if (packet instanceof HouseSpacePacket) {
                    HouseSpacePacket houseSpacePacket = (HouseSpacePacket) packet;
                    ClientBase.getInstance().setHouseSpace(houseSpacePacket.getHouseSpace());
                    Cockpit.getInstance().updateItemLimit();
                } else {
                    throw new IllegalArgumentException(this + " unknown packet: " + packet);
                }

            } catch (Throwable t) {
                GwtCommon.handleException(t);
            }
        }
    }


    public void addCommandToQueue(BaseCommand baseCommand) {
        if (movableServiceAsync != null && gameInfo.hasServerCommunication()) {
            commandQueue.add(baseCommand);
        }
    }

    public void sendCommandQueue() {
        if (movableServiceAsync != null && !commandQueue.isEmpty() && gameInfo.hasServerCommunication()) {
            movableServiceAsync.sendCommands(commandQueue, this);
        }
        commandQueue.clear();
    }

    public void sendTutorialProgress(TutorialConfig.TYPE type, String name, String parent, long duration, long clientTimeStamp, final ParametrisedRunnable<UserStage> runnable) {
        if (movableServiceAsync != null) {
            movableServiceAsync.sendTutorialProgress(type, name, parent, duration, clientTimeStamp, new AsyncCallback<UserStage>() {
                @Override
                public void onFailure(Throwable caught) {
                    GwtCommon.handleException(caught);
                }

                @Override
                public void onSuccess(UserStage userStage) {
                    if (runnable != null) {
                        runnable.run(userStage);
                    }
                }
            });
        }
    }

    public void sendUserMessage(String text) {
        if (movableServiceAsync != null) {
            UserMessage userMessage = new UserMessage();
            userMessage.setBaseName(ClientBase.getInstance().getOwnBaseName());
            userMessage.setMessage(text);
            movableServiceAsync.sendUserMessage(userMessage, this);
        }
    }

    public void sendEventTrackingStart(EventTrackingStart eventTrackingStart) {
        if (movableServiceAsync != null) {
            movableServiceAsync.sendEventTrackingStart(eventTrackingStart, this);
        }
    }

    public void sendEventTrackerItems(List<EventTrackingItem> eventTrackingItems, List<BaseCommand> baseCommands, List<SelectionTrackingItem> selectionTrackingItems) {
        if (movableServiceAsync != null) {
            movableServiceAsync.sendEventTrackerItems(eventTrackingItems, baseCommands, selectionTrackingItems, this);
        }
    }

    public void sendStartupFinished(Collection<StartupTaskInfo> infos, long totalTime) {
        if (movableServiceAsync != null) {
            movableServiceAsync.sendStartupInfo(infos, totalTime, this);
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


    public void sendSellItem(SyncItem syncItem) {
        if (!syncItem.getId().isSynchronized()) {
            return;
        }
        if (movableServiceAsync != null) {
            movableServiceAsync.sellItem(syncItem.getId(), this);
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

    public void sendCloseWindow(long totalRunningTime, long clientTimeStamp) {
        if (movableServiceAsync != null) {
            movableServiceAsync.sendCloseWindow(totalRunningTime, clientTimeStamp, this);
        }
    }

    public void log(String logMessage, Date date) {
        if (movableServiceAsync != null) {
            movableServiceAsync.log(logMessage, date, this);
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
            MessageDialog.show("Not your Base: Most likely you start another<br />base in another browser window");
        } else if (throwable instanceof NoConnectionException) {
            MessageDialog.show("No Connection: Most likely you start another<br />base in another browser window: " + throwable.getMessage());
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

    public Collection<SyncItemInfo> getAndClearSyncInfos() {
        if (syncInfos == null) {
            throw new IllegalArgumentException("No syncInfos loaded");
        }
        Collection<SyncItemInfo> tmp = syncInfos;
        syncInfos = null;
        return tmp;
    }
}
