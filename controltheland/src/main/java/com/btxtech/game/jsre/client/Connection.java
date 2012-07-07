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

import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.cockpit.SplashManager;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.client.common.info.InvalidLevelState;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.client.control.GameStartupSeq;
import com.btxtech.game.jsre.client.control.StartupProgressListener;
import com.btxtech.game.jsre.client.control.StartupScreen;
import com.btxtech.game.jsre.client.control.StartupSeq;
import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.btxtech.game.jsre.client.control.task.AbstractStartupTask;
import com.btxtech.game.jsre.client.control.task.DeferredStartup;
import com.btxtech.game.jsre.client.dialogs.AllianceDialog;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.MessageDialog;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryDialog;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryInfo;
import com.btxtech.game.jsre.client.dialogs.inventory.MarketDialog;
import com.btxtech.game.jsre.client.dialogs.quest.QuestDialog;
import com.btxtech.game.jsre.client.dialogs.quest.QuestOverview;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.simulation.Simulation;
import com.btxtech.game.jsre.client.utg.ClientLevelHandler;
import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.Html5NotSupportedException;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.packets.AccountBalancePacket;
import com.btxtech.game.jsre.common.packets.AllianceOfferPacket;
import com.btxtech.game.jsre.common.packets.BaseChangedPacket;
import com.btxtech.game.jsre.common.packets.BoxPickedPacket;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.packets.EnergyPacket;
import com.btxtech.game.jsre.common.packets.HouseSpacePacket;
import com.btxtech.game.jsre.common.packets.LevelPacket;
import com.btxtech.game.jsre.common.packets.LevelTaskPacket;
import com.btxtech.game.jsre.common.packets.Message;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;
import com.btxtech.game.jsre.common.packets.XpPacket;
import com.btxtech.game.jsre.common.tutorial.GameFlow;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.jsre.common.utg.tracking.BrowserWindowTracking;
import com.btxtech.game.jsre.common.utg.tracking.DialogTracking;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: Jul 4, 2009
 * Time: 11:23:52 AM
 */
public class Connection implements StartupProgressListener, ConnectionI {
    public static final int MAX_DISCONNECTION_COUNT = 20;
    public static final int MIN_DELAY_BETWEEN_POLL = 200;
    public static final int STATISTIC_DELAY = 10000;
    private static final String CONNECTION_DIALOG = "Lost connection to game server. Try to reload the page. You may have to login again.";
    public static final Connection INSTANCE = new Connection();
    private boolean isRegistered;
    private GameInfo gameInfo;
    private Collection<SyncItemInfo> syncInfos;
    private ArrayList<BaseCommand> commandQueue = new ArrayList<BaseCommand>();
    private static Logger log = Logger.getLogger(Connection.class.getName());
    private GameEngineMode gameEngineMode;

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

    public void downloadRealGameInfo(final DeferredStartup deferredStartup) {
        if (movableServiceAsync != null) {
            movableServiceAsync.getRealGameInfo(ClientServices.getInstance().getClientRunner().getStartUuid(), new AsyncCallback<RealGameInfo>() {

                @Override
                public void onFailure(Throwable caught) {
                    handleGameInfoThrowable(caught, deferredStartup);
                }

                @Override
                public void onSuccess(RealGameInfo realGameInfo) {
                    disconnectionCount = 0;
                    Connection.this.gameInfo = realGameInfo;
                    gameEngineMode = GameEngineMode.SLAVE;
                    deferredStartup.finished();
                }
            });
        } else {
            deferredStartup.failed(DeferredStartup.NO_CONNECTION);
        }
    }

    public void downloadSimulationGameInfo(int levelTaskId, final DeferredStartup deferredStartup) {
        if (movableServiceAsync != null) {
            movableServiceAsync.getSimulationGameInfo(levelTaskId, new AsyncCallback<SimulationInfo>() {

                @Override
                public void onFailure(Throwable caught) {
                    handleGameInfoThrowable(caught, deferredStartup);
                }

                @Override
                public void onSuccess(SimulationInfo simulationInfo) {
                    disconnectionCount = 0;
                    Connection.this.gameInfo = simulationInfo;
                    gameEngineMode = GameEngineMode.MASTER;
                    deferredStartup.finished();
                }
            });
        } else {
            deferredStartup.failed(DeferredStartup.NO_CONNECTION);
        }
    }

    private void handleGameInfoThrowable(Throwable caught, DeferredStartup deferredStartup) {
        if (caught instanceof InvalidLevelState) {
            Window.open(CmsUtil.getUrl4Game(((InvalidLevelState) caught).getLevelTaskId()), CmsUtil.TARGET_SELF, "");
        } else {
            deferredStartup.failed(caught);
            gameEngineMode = null;
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
        gameEngineMode = GameEngineMode.PLAYBACK;
    }

    public void init4ItemTypeEditor() {
        gameEngineMode = GameEngineMode.MASTER;
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
        movableServiceAsync.getSyncInfo(ClientServices.getInstance().getClientRunner().getStartUuid(), new AsyncCallback<List<Packet>>() {
            @Override
            public void onFailure(Throwable throwable) {
                if (!handleDisconnection("pollSyncInfo", throwable)) {
                    scheduleTimer();
                }
            }

            @Override
            public void onSuccess(List<Packet> packets) {
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

    private void handlePackets(List<Packet> packets) {
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
                    DialogManager.showDialog(new MessageDialog("Message", message.getMessage(), message.isShowRegisterDialog()), DialogManager.Type.QUEUE_ABLE);
                } else if (packet instanceof AccountBalancePacket) {
                    AccountBalancePacket balancePacket = (AccountBalancePacket) packet;
                    ClientBase.getInstance().setAccountBalance(balancePacket.getAccountBalance());
                } else if (packet instanceof EnergyPacket) {
                    ClientEnergyService.getInstance().onEnergyPacket((EnergyPacket) packet);
                } else if (packet instanceof ChatMessage) {
                    ClientChatHandler.getInstance().onMessageReceived((ChatMessage) packet);
                } else if (packet instanceof LevelPacket) {
                    ClientLevelHandler.getInstance().setLevel(((LevelPacket) packet).getLevel());
                    SplashManager.getInstance().onLevelUp();
                    QuestDialog.updateQuestDialog();
                } else if (packet instanceof LevelTaskPacket) {
                    ClientLevelHandler.getInstance().setLevelTask((LevelTaskPacket) packet);
                    QuestDialog.updateQuestDialog();
                } else if (packet instanceof HouseSpacePacket) {
                    HouseSpacePacket houseSpacePacket = (HouseSpacePacket) packet;
                    ClientBase.getInstance().setHouseSpace(houseSpacePacket.getHouseSpace());
                    SideCockpit.getInstance().updateItemLimit();
                } else if (packet instanceof AllianceOfferPacket) {
                    ClientAllianceHandler.getInstance().handleAllianceOfferPacket((AllianceOfferPacket) packet);
                } else if (packet instanceof BoxPickedPacket) {
                    SideCockpit.getInstance().onBoxPicked((BoxPickedPacket) packet);
                    InventoryDialog.onBoxPicket();
                    MarketDialog.onBoxPicket();
                } else if (packet instanceof XpPacket) {
                    XpPacket xpPacket = (XpPacket) packet;
                    SideCockpit.getInstance().setXp(xpPacket.getXp(), xpPacket.getXp2LevelUp());
                } else {
                    throw new IllegalArgumentException(this + " unknown packet: " + packet);
                }
            } catch (Throwable t) {
                GwtCommon.handleException(t);
            }
        }
    }


    public void addCommandToQueue(BaseCommand baseCommand) {
        if (movableServiceAsync != null && gameEngineMode == GameEngineMode.SLAVE) {
            commandQueue.add(baseCommand);
        }
    }

    public void sendCommandQueue() {
        if (movableServiceAsync != null && !commandQueue.isEmpty() && gameEngineMode == GameEngineMode.SLAVE) {
            movableServiceAsync.sendCommands(commandQueue, new VoidAsyncCallback("sendCommandQueue"));
        }
        commandQueue.clear();
    }

    public void sendTutorialProgress(final TutorialConfig.TYPE type, final int levelTaskId, final String name, final long duration, final long clientTimeStamp, final ParametrisedRunnable<GameFlow> runnable) {
        if (movableServiceAsync != null) {
            movableServiceAsync.sendTutorialProgress(type,
                    ClientServices.getInstance().getClientRunner().getStartUuid(),
                    levelTaskId,
                    name,
                    duration,
                    clientTimeStamp,
                    new AsyncCallback<GameFlow>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            if (!handleDisconnection("sendTutorialProgress", caught)) {
                                sendTutorialProgress(type, levelTaskId, name, duration, clientTimeStamp, runnable);
                            }
                        }

                        @Override
                        public void onSuccess(GameFlow gameFlow) {
                            if (runnable != null) {
                                runnable.run(gameFlow);
                            }
                        }
                    });
        }
    }

    @Override
    public void sendChatMessage(ChatMessage chatMessage) {
        if (movableServiceAsync != null) {
            movableServiceAsync.sendChatMessage(chatMessage, new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable caught) {
                    log.log(Level.SEVERE, "Chat message sending failed", caught);
                }

                @Override
                public void onSuccess(Void result) {
                    ClientChatHandler.getInstance().pollMessagesIfInPollMode();
                }
            });
        }
    }

    @Override
    public void pollChatMessages(Integer lastMessageId) {
        if (movableServiceAsync != null) {
            movableServiceAsync.pollChatMessages(lastMessageId, new AsyncCallback<List<ChatMessage>>() {
                @Override
                public void onFailure(Throwable caught) {
                    log.log(Level.SEVERE, "Chat message polling failed", caught);
                }

                @Override
                public void onSuccess(List<ChatMessage> chatMessages) {
                    ClientChatHandler.getInstance().onMessageReceived(chatMessages);
                }
            });
        }
    }

    public void sendEventTrackingStart(EventTrackingStart eventTrackingStart) {
        if (movableServiceAsync != null) {
            movableServiceAsync.sendEventTrackingStart(eventTrackingStart, new VoidAsyncCallback("sendEventTrackingStart"));
        }
    }

    public void sendEventTrackerItems(List<EventTrackingItem> eventTrackingItems, List<SyncItemInfo> syncItemInfos, List<SelectionTrackingItem> selectionTrackingItems, List<TerrainScrollTracking> terrainScrollTrackings, List<BrowserWindowTracking> browserWindowTrackings, List<DialogTracking> dialogTrackings) {
        if (movableServiceAsync != null) {
            movableServiceAsync.sendEventTrackerItems(eventTrackingItems, syncItemInfos, selectionTrackingItems, terrainScrollTrackings, browserWindowTrackings, dialogTrackings, new VoidAsyncCallback("sendEventTrackerItems"));
        }
    }

    public void sellItem(SyncItem syncItem) {
        if (gameEngineMode == GameEngineMode.SLAVE) {
            if (!syncItem.getId().isSynchronized()) {
                return;
            }
            if (movableServiceAsync != null) {
                movableServiceAsync.sellItem(syncItem.getId(), new VoidAsyncCallback("sellItem"));
            }
        } else if (gameEngineMode == GameEngineMode.MASTER) {
            try {
                ItemContainer.getInstance().sellItem(syncItem.getId());
            } catch (Exception e) {
                log.log(java.util.logging.Level.SEVERE, "sendSellItem()", e);
            }
        }
    }

    public void log(String logMessage, Date date) {
        if (movableServiceAsync != null) {
            movableServiceAsync.log(logMessage, date, new VoidAsyncCallback("log"));
        }
    }

    public static MovableServiceAsync getMovableServiceAsync() {
        return INSTANCE.movableServiceAsync;
    }

    private boolean handleDisconnection(String message, Throwable throwable) {
        if (GwtCommon.checkAndReportHttpStatusCode0(message, throwable)) {
            disconnectionCount++;
            if (disconnectionCount < MAX_DISCONNECTION_COUNT) {
                return false;
            }
            movableServiceAsync = null;
            GwtCommon.sendLogViaLoadScriptCommunication("Client disconnected due to HTTP status code 0: " + message);
            DialogManager.showDialog(new MessageDialog("Connection failed", CONNECTION_DIALOG), DialogManager.Type.PROMPTLY);
            return true;
        }

        if (throwable instanceof NotYourBaseException) {
            movableServiceAsync = null;
            GwtCommon.sendLogViaLoadScriptCommunication("Client disconnected due to NotYourBaseException: " + message);
            DialogManager.showDialog(new MessageDialog("Wrong base", "Not your Base: Most likely you start another base in another browser window."), DialogManager.Type.PROMPTLY);
            return true;
        } else if (throwable instanceof NoConnectionException) {
            log.warning("Client disconnected due to NoConnectionException: " + message + " NoConnectionException: " + throwable.getMessage());
            DialogManager.showDialog(new MessageDialog("Connection failed", "Connection to server lost. Most likely you start another browser or tab window."), DialogManager.Type.PROMPTLY);
            movableServiceAsync = null;
            return true;
        } else {
            GwtCommon.sendLogViaLoadScriptCommunication("Unknown Error (See GWT log for stack trace): " + message + " " + throwable.getMessage());
            GwtCommon.handleException(throwable);
            return false;
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
        if (movableServiceAsync != null) {
            movableServiceAsync.sendStartupTask(task.createStartupTaskInfo(),
                    ClientServices.getInstance().getClientRunner().getStartUuid(),
                    Simulation.getInstance().getLevelTaskId(),
                    new VoidAsyncCallback("onTaskFinished"));
        }
    }

    @Override
    public void onTaskFailed(AbstractStartupTask task, String error, Throwable t) {
        if (movableServiceAsync != null) {
            movableServiceAsync.sendStartupTask(task.createStartupTaskInfo(error),
                    ClientServices.getInstance().getClientRunner().getStartUuid(),
                    Simulation.getInstance().getLevelTaskId(),
                    new VoidAsyncCallback("onTaskFailed"));
        }

        @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
        Throwable cause = CommonJava.getMostInnerThrowable(t);
        if (cause instanceof Html5NotSupportedException) {
            Window.Location.assign(CmsUtil.PREDEFINED_PAGE_URL_NO_HTML_5);
        } else if (GwtCommon.checkAndReportHttpStatusCode0("onTaskFailed", cause)) {
            // Reload whole browser
            Window.Location.reload();
        } else {
            log.log(java.util.logging.Level.SEVERE, "Startup task failed: '" + task.getTaskEnum().getStartupTaskEnumHtmlHelper().getNiceText() + "' error: " + error, t);
        }
    }

    @Override
    public void onStartupFinished(List<StartupTaskInfo> taskInfo, long totalTime) {
        if (movableServiceAsync != null) {
            movableServiceAsync.sendStartupTerminated(true,
                    totalTime,
                    ClientServices.getInstance().getClientRunner().getStartUuid(),
                    Simulation.getInstance().getLevelTaskId(),
                    new VoidAsyncCallback("onStartupFinished"));
        }
    }

    @Override
    public void onStartupFailed(List<StartupTaskInfo> taskInfo, long totalTime) {
        if (movableServiceAsync != null) {
            movableServiceAsync.sendStartupTerminated(false,
                    totalTime,
                    ClientServices.getInstance().getClientRunner().getStartUuid(),
                    Simulation.getInstance().getLevelTaskId(),
                    new VoidAsyncCallback("onStartupFailed"));
        }
    }

    @Override
    public GameEngineMode getGameEngineMode() {
        if (gameEngineMode == null) {
            throw new NullPointerException("GameEngineMode is null");
        }
        return gameEngineMode;
    }


    class VoidAsyncCallback implements AsyncCallback<Void> {
        private String message;

        VoidAsyncCallback(String message) {
            this.message = message;
        }

        @Override
        public void onFailure(Throwable caught) {
            handleDisconnection(message, caught);
        }

        @Override
        public void onSuccess(Void result) {
            disconnectionCount = 0;
        }
    }

    public void acceptAllianceOffer(String partnerUserName) {
        if (movableServiceAsync != null) {
            movableServiceAsync.acceptAllianceOffer(partnerUserName, new VoidAsyncCallback("acceptAllianceOffer"));
        }
    }

    public void breakAlliance(String partnerUserName) {
        if (movableServiceAsync != null) {
            movableServiceAsync.breakAlliance(partnerUserName, new VoidAsyncCallback("breakAlliance"));
        }
    }

    public void proposeAlliance(SimpleBase partner) {
        if (movableServiceAsync != null) {
            movableServiceAsync.proposeAlliance(partner, new VoidAsyncCallback("proposeAlliance"));
        }
    }

    public void rejectAllianceOffer(String partnerUserName) {
        if (movableServiceAsync != null) {
            movableServiceAsync.rejectAllianceOffer(partnerUserName, new VoidAsyncCallback("rejectAllianceOffer"));
        }
    }

    public void getAllAlliances(final AllianceDialog allianceDialog) {
        if (movableServiceAsync != null && isRegistered) {
            movableServiceAsync.getAllAlliances(new AsyncCallback<Collection<String>>() {
                @Override
                public void onFailure(Throwable caught) {
                    handleDisconnection("getAllAlliances", caught);
                }

                @Override
                public void onSuccess(Collection<String> alliances) {
                    allianceDialog.onAlliancesReceived(alliances);
                }
            });
        }
    }

    public void loadInventory(final InventoryDialog inventoryDialog) {
        if (movableServiceAsync != null) {
            movableServiceAsync.getInventory(new AsyncCallback<InventoryInfo>() {
                @Override
                public void onFailure(Throwable caught) {
                    handleDisconnection("loadInventory", caught);
                }

                @Override
                public void onSuccess(InventoryInfo inventoryInfo) {
                    inventoryDialog.onItemsReceived(inventoryInfo);
                }
            });
        }
    }

    public void assembleInventoryItem(int inventoryItemId, final InventoryDialog inventoryDialog) {
        if (movableServiceAsync != null) {
            movableServiceAsync.assembleInventoryItem(inventoryItemId, new AsyncCallback<InventoryInfo>() {
                @Override
                public void onFailure(Throwable caught) {
                    handleDisconnection("assembleInventoryItem", caught);
                }

                @Override
                public void onSuccess(InventoryInfo inventoryInfo) {
                    inventoryDialog.onItemsReceived(inventoryInfo);
                }
            });
        }
    }

    public void useInventoryItem(int inventoryItemId, Collection<Index> positionToBePlaced) {
        if (movableServiceAsync != null) {
            movableServiceAsync.useInventoryItem(inventoryItemId, positionToBePlaced, new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable caught) {
                    handleDisconnection("useInventoryItem", caught);
                }

                @Override
                public void onSuccess(Void aVoid) {
                    // Do nothing
                }
            });
        }
    }

    public void buyInventoryItem(int inventoryItemId, final MarketDialog marketDialog) {
        if (movableServiceAsync != null) {
            movableServiceAsync.buyInventoryItem(inventoryItemId, new AsyncCallback<Integer>() {
                @Override
                public void onFailure(Throwable caught) {
                    handleDisconnection("buyInventoryItem", caught);
                }

                @Override
                public void onSuccess(Integer razarion) {
                    marketDialog.updateRazarion(razarion);
                }
            });
        }
    }

    public void buyInventoryArtifact(int inventoryArtifactId, final MarketDialog marketDialog) {
        if (movableServiceAsync != null) {
            movableServiceAsync.buyInventoryArtifact(inventoryArtifactId, new AsyncCallback<Integer>() {
                @Override
                public void onFailure(Throwable caught) {
                    handleDisconnection("buyInventoryArtifact", caught);
                }

                @Override
                public void onSuccess(Integer razarion) {
                    marketDialog.updateRazarion(razarion);
                }
            });
        }
    }


    public void loadRazarion(final MarketDialog marketDialog) {
        if (movableServiceAsync != null) {
            movableServiceAsync.loadRazarion(new AsyncCallback<Integer>() {
                @Override
                public void onFailure(Throwable caught) {
                    handleDisconnection("loadRazarion", caught);
                }

                @Override
                public void onSuccess(Integer razarion) {
                    marketDialog.updateRazarion(razarion);
                }
            });
        }
    }

    public void loadQuestOverview(final QuestDialog questDialog) {
        if (movableServiceAsync != null) {
            movableServiceAsync.loadQuestOverview(new AsyncCallback<QuestOverview>() {
                @Override
                public void onFailure(Throwable caught) {
                    handleDisconnection("loadQuestOverview", caught);
                }

                @Override
                public void onSuccess(QuestOverview questOverview) {
                    questDialog.displayQuestOverview(questOverview);
                }
            });
        }
    }

    public void activateQuest(int questId) {
        if (movableServiceAsync != null) {
            movableServiceAsync.activateQuest(questId, new VoidAsyncCallback("activateQuest"));
        }
    }

}
