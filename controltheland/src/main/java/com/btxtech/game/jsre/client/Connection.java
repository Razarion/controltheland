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
import com.btxtech.game.jsre.client.common.Level;
import com.btxtech.game.jsre.client.common.Message;
import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.client.common.UserMessage;
import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.client.control.GameStartupSeq;
import com.btxtech.game.jsre.client.control.StartupProgressListener;
import com.btxtech.game.jsre.client.control.StartupSeq;
import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.btxtech.game.jsre.client.control.task.AbstractStartupTask;
import com.btxtech.game.jsre.client.control.task.DeferredStartup;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.MessageDialog;
import com.btxtech.game.jsre.client.item.ClientItemTypeAccess;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.ClientLevelHandler;
import com.btxtech.game.jsre.common.AccountBalancePacket;
import com.btxtech.game.jsre.common.BaseChangedPacket;
import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.EnergyPacket;
import com.btxtech.game.jsre.common.Html5NotSupportedException;
import com.btxtech.game.jsre.common.LevelPacket;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.jsre.common.XpBalancePacket;
import com.btxtech.game.jsre.common.gameengine.services.itemTypeAccess.ItemTypeAccessSyncInfo;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.jsre.common.tutorial.HouseSpacePacket;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.jsre.common.utg.tracking.BrowserWindowTracking;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingStart;
import com.btxtech.game.jsre.common.utg.tracking.SelectionTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.TerrainScrollTracking;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: Jul 4, 2009
 * Time: 11:23:52 AM
 */
public class Connection implements AsyncCallback<Void>, StartupProgressListener {
    public static final int MAX_DISCONNECTION_COUNT = 20;
    public static final int MIN_DELAY_BETWEEN_POLL = 200;
    public static final int STATISTIC_DELAY = 10000;
    private static final String CONNECTION_DIALOG = "Lost connection to game server.<br />Try to reload the page.<br />You may have to login again.";
    public static final Connection INSTANCE = new Connection();
    private boolean isRegistered;
    private GameInfo gameInfo;
    private Collection<SyncItemInfo> syncInfos;
    private ArrayList<BaseCommand> commandQueue = new ArrayList<BaseCommand>();
    private static Logger log = Logger.getLogger(Connection.class.getName());
    private ClientMode clientMode;

    private MovableServiceAsync movableServiceAsync = GWT.create(MovableService.class);
    private Timer timer;
    private int disconnectionCount = 0;

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
                    deferredStartup.failed(caught);
                    clientMode = null;
                }

                @Override
                public void onSuccess(GameInfo gameInfo) {
                    disconnectionCount = 0;
                    Connection.this.gameInfo = gameInfo;
                    deferredStartup.finished();
                    if (gameInfo.isRealGame()) {
                        clientMode = ClientMode.SLAVE;
                    } else {
                        clientMode = ClientMode.MASTER;
                    }
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
        clientMode = ClientMode.PLAYBACK;
    }

    public void downloadAllSyncInfo(final DeferredStartup deferredStartup) {
        if (movableServiceAsync != null) {
            movableServiceAsync.getAllSyncInfo(new AsyncCallback<Collection<SyncItemInfo>>() {
                @Override
                public void onFailure(Throwable throwable) {
                    deferredStartup.failed(throwable);
                }

                @Override
                public void onSuccess(Collection<SyncItemInfo> syncInfos) {
                    disconnectionCount = 0;
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
        movableServiceAsync.getSyncInfo(new AsyncCallback<Collection<Packet>>() {
            @Override
            public void onFailure(Throwable throwable) {
                if (!handleDisconnection(throwable)) {
                    scheduleTimer();
                }
            }

            @Override
            public void onSuccess(Collection<Packet> packets) {
                try {
                    disconnectionCount = 0;
                    handlePackets(packets);
                } finally {
                    scheduleTimer();
                }
            }
        });
    }

    private void scheduleTimer() {
        if (timer != null) {
            timer.schedule(MIN_DELAY_BETWEEN_POLL);
        }
    }

    private void handlePackets(Collection<Packet> packets) {
        if (packets == null) {
            return;
        }
        for (Packet packet : packets) {
            try {
                if (packet instanceof BaseChangedPacket) {
                    ClientBase.getInstance().onBaseChangedPacket((BaseChangedPacket) packet);
                } else if (packet instanceof SyncItemInfo) {
                    ItemContainer.getInstance().sychronize((SyncItemInfo) packet);
                } else if (packet instanceof Message) {
                    Message message = (Message) packet;
                    DialogManager.showDialog(new MessageDialog("<h1>" + message.getMessage() + "</h1>"), DialogManager.Type.QUEUE_ABLE);
                } else if (packet instanceof AccountBalancePacket) {
                    AccountBalancePacket balancePacket = (AccountBalancePacket) packet;
                    ClientBase.getInstance().setAccountBalance(balancePacket.getAccountBalance());
                } else if (packet instanceof XpBalancePacket) {
                    XpBalancePacket xpBalancePacket = (XpBalancePacket) packet;
                    Cockpit.getInstance().updateXp(xpBalancePacket.getXp());
                } else if (packet instanceof ItemTypeAccessSyncInfo) {
                    ItemTypeAccessSyncInfo itemTypeAccessSyncInfo = (ItemTypeAccessSyncInfo) packet;
                    ClientItemTypeAccess.getInstance().setAllowedItemTypes(itemTypeAccessSyncInfo.getAllowedItemTypes());
                    Cockpit.getInstance().onStateChanged();
                } else if (packet instanceof EnergyPacket) {
                    ClientEnergyService.getInstance().onEnergyPacket((EnergyPacket) packet);
                } else if (packet instanceof UserMessage) {
                    Cockpit.getInstance().onMessageReceived((UserMessage) packet);
                } else if (packet instanceof LevelPacket) {
                    ClientLevelHandler.getInstance().onLevelChanged(((LevelPacket) packet).getLevel());
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
        if (movableServiceAsync != null && clientMode == ClientMode.SLAVE) {
            commandQueue.add(baseCommand);
        }
    }

    public void sendCommandQueue() {
        if (movableServiceAsync != null && !commandQueue.isEmpty() && clientMode == ClientMode.SLAVE) {
            movableServiceAsync.sendCommands(commandQueue, this);
        }
        commandQueue.clear();
    }

    public void sendTutorialProgress(final TutorialConfig.TYPE type, final String name, final String parent, final long duration, final long clientTimeStamp, final ParametrisedRunnable<Level> runnable) {
        if (movableServiceAsync != null) {
            movableServiceAsync.sendTutorialProgress(type, name, parent, duration, clientTimeStamp, new AsyncCallback<Level>() {
                @Override
                public void onFailure(Throwable caught) {
                    if (!handleDisconnection(caught)) {
                        sendTutorialProgress(type, name, parent, duration, clientTimeStamp, runnable);
                    }
                }

                @Override
                public void onSuccess(Level level) {
                    if (runnable != null) {
                        runnable.run(level);
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

    public void sendEventTrackerItems(List<EventTrackingItem> eventTrackingItems, List<SyncItemInfo> syncItemInfos, List<SelectionTrackingItem> selectionTrackingItems, List<TerrainScrollTracking> terrainScrollTrackings, List<BrowserWindowTracking> browserWindowTrackings) {
        if (movableServiceAsync != null) {
            movableServiceAsync.sendEventTrackerItems(eventTrackingItems, syncItemInfos, selectionTrackingItems, terrainScrollTrackings, browserWindowTrackings, this);
        }
    }

    public void sendStartupFinished(List<StartupTaskInfo> infos, long totalTime) {
        if (movableServiceAsync != null) {
            movableServiceAsync.sendStartupInfo(infos, totalTime, this);
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
        disconnectionCount = 0;
    }

    private boolean handleDisconnection(Throwable throwable) {
        if (GwtCommon.checkAndReportHttpStatusCode0(throwable)) {
            disconnectionCount++;
            if (disconnectionCount < MAX_DISCONNECTION_COUNT) {
                return false;
            }
            movableServiceAsync = null;
            GwtCommon.sendLogViaLoadScriptCommunication("Client disconnected due to HTTP status code 0");
            DialogManager.showDialog(new MessageDialog(CONNECTION_DIALOG), DialogManager.Type.PROMPTLY);
        }

        if (throwable instanceof NotYourBaseException) {
            movableServiceAsync = null;
            DialogManager.showDialog(new MessageDialog("Not your Base: Most likely you start another<br />base in another browser window"), DialogManager.Type.PROMPTLY);
        } else if (throwable instanceof NoConnectionException) {
            ClientServices.getInstance().getClientRunner().start(GameStartupSeq.WARM_REAL);
        } else {
            movableServiceAsync = null;
            GwtCommon.handleException(throwable);
            DialogManager.showDialog(new MessageDialog(CONNECTION_DIALOG), DialogManager.Type.PROMPTLY);
        }
        return true;
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

    @Override
    public void onStart(StartupSeq startupSeq) {
        // Ignore
    }

    @Override
    public void onNextTask(StartupTaskEnum taskEnum) {
        // Ignore
    }

    @Override
    public void onTaskFinished(AbstractStartupTask task) {
        // Ignore
    }

    @Override
    public void onTaskFailed(AbstractStartupTask task, String error, Throwable t) {
        @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
        Throwable cause = CommonJava.getMostInnerThrowable(t);
        if (cause instanceof Html5NotSupportedException) {
            Window.Location.assign(CmsUtil.PREDEFINED_PAGE_URL_NO_HTML_5);
        } else if (GwtCommon.checkAndReportHttpStatusCode0(cause)) {
            // Reload whole browser
            Window.Location.reload();
        } else {
            log.log(java.util.logging.Level.SEVERE, "Startup task failed: '" + task.getTaskEnum().getStartupTaskEnumHtmlHelper().getNiceText() + "' error: " + error, t);
        }
    }

    @Override
    public void onStartupFinished(List<StartupTaskInfo> taskInfo, long totalTime) {
        sendStartupFinished(taskInfo, totalTime);
    }

    @Override
    public void onStartupFailed(List<StartupTaskInfo> taskInfo, long totalTime) {
        sendStartupFinished(taskInfo, totalTime);
    }

    public ClientMode getClientMode() {
        if (clientMode == null) {
            throw new NullPointerException("ClientMode is null");
        }
        return clientMode;
    }
}
