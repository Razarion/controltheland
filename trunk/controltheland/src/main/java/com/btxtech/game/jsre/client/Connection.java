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
import com.btxtech.game.jsre.client.cockpit.chat.ChatMessageFilter;
import com.btxtech.game.jsre.client.cockpit.item.InvitingUnregisteredBaseException;
import com.btxtech.game.jsre.client.cockpit.menu.MenuBarCockpit;
import com.btxtech.game.jsre.client.cockpit.quest.QuestVisualisationCockpit;
import com.btxtech.game.jsre.client.cockpit.quest.QuestVisualisationModel;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.client.common.info.InvalidLevelStateException;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.common.info.SimpleUser;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.client.control.GameStartupSeq;
import com.btxtech.game.jsre.client.control.StartupProgressListener;
import com.btxtech.game.jsre.client.control.StartupScreen;
import com.btxtech.game.jsre.client.control.StartupSeq;
import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.btxtech.game.jsre.client.control.task.AbstractStartupTask;
import com.btxtech.game.jsre.client.control.task.DeferredStartup;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.MessageDialog;
import com.btxtech.game.jsre.client.dialogs.guild.FullGuildInfo;
import com.btxtech.game.jsre.client.dialogs.guild.GuildMemberInfo;
import com.btxtech.game.jsre.client.dialogs.guild.MyGuildDialog;
import com.btxtech.game.jsre.client.dialogs.highscore.CurrentStatisticEntryInfo;
import com.btxtech.game.jsre.client.dialogs.highscore.HighscoreDialog;
import com.btxtech.game.jsre.client.dialogs.incentive.InviteFriendsDialog;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryDialog;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryInfo;
import com.btxtech.game.jsre.client.dialogs.quest.QuestDialog;
import com.btxtech.game.jsre.client.dialogs.quest.QuestOverview;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.simulation.Simulation;
import com.btxtech.game.jsre.client.unlock.ClientUnlockServiceImpl;
import com.btxtech.game.jsre.client.utg.ClientUserGuidanceService;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.FacebookUtils;
import com.btxtech.game.jsre.common.Html5NotSupportedException;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.jsre.common.UserIsAlreadyGuildMemberException;
import com.btxtech.game.jsre.common.gameengine.services.PlanetLiteInfo;
import com.btxtech.game.jsre.common.gameengine.services.connection.CommonConnectionService;
import com.btxtech.game.jsre.common.gameengine.services.unlock.impl.UnlockContainer;
import com.btxtech.game.jsre.common.gameengine.services.user.NoSuchUserException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.packets.AccountBalancePacket;
import com.btxtech.game.jsre.common.packets.BaseChangedPacket;
import com.btxtech.game.jsre.common.packets.BaseLostPacket;
import com.btxtech.game.jsre.common.packets.BoxPickedPacket;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.packets.EnergyPacket;
import com.btxtech.game.jsre.common.packets.HouseSpacePacket;
import com.btxtech.game.jsre.common.packets.LevelPacket;
import com.btxtech.game.jsre.common.packets.LevelTaskPacket;
import com.btxtech.game.jsre.common.packets.Message;
import com.btxtech.game.jsre.common.packets.MessageIdPacket;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.jsre.common.packets.ServerRebootMessagePacket;
import com.btxtech.game.jsre.common.packets.StorablePacket;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;
import com.btxtech.game.jsre.common.packets.UnlockContainerPacket;
import com.btxtech.game.jsre.common.packets.UserAttentionPacket;
import com.btxtech.game.jsre.common.packets.UserPacket;
import com.btxtech.game.jsre.common.packets.XpPacket;
import com.btxtech.game.jsre.common.perfmon.Perfmon;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.jsre.common.perfmon.TimerPerfmon;
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
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.ValueBoxBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: Jul 4, 2009
 * Time: 11:23:52 AM
 */
public class Connection implements StartupProgressListener, GlobalCommonConnectionService, CommonConnectionService {
    public static final int MAX_DISCONNECTION_COUNT = 20;
    public static final int MIN_DELAY_BETWEEN_POLL = 200;
    public static final int STATISTIC_DELAY = 10000;
    public static final Connection INSTANCE = new Connection();
    private GameInfo gameInfo;
    private Collection<SyncItemInfo> syncInfos;
    private ArrayList<BaseCommand> commandQueue = new ArrayList<BaseCommand>();
    private static Logger log = Logger.getLogger(Connection.class.getName());
    private GameEngineMode gameEngineMode;
    private MovableServiceAsync movableServiceAsync = GWT.create(MovableService.class);
    private Timer timer;
    private boolean resendLast;
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
            movableServiceAsync.getRealGameInfo(ClientGlobalServices.getInstance().getClientRunner().getStartUuid(),
                    ClientUserGuidanceService.getInstance().getAndClearNextPlanetId(),
                    new AsyncCallback<RealGameInfo>() {

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
        if (caught instanceof InvalidLevelStateException) {
            Window.open(CmsUtil.getUrl4Game(((InvalidLevelStateException) caught).getLevelTaskId()), CmsUtil.TARGET_SELF, "");
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
            movableServiceAsync.getAllSyncInfo(ClientGlobalServices.getInstance().getClientRunner().getStartUuid(), new AsyncCallback<Collection<SyncItemInfo>>() {
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
        timer = new TimerPerfmon(PerfmonEnum.SYNC_POLL) {
            @Override
            public void runPerfmon() {
                pollSyncInfo();
            }
        };
        timer.schedule(MIN_DELAY_BETWEEN_POLL);
    }

    private void pollSyncInfo() {
        if (movableServiceAsync == null) {
            return;
        }
        movableServiceAsync.getSyncInfo(ClientGlobalServices.getInstance().getClientRunner().getStartUuid(), resendLast, new AsyncCallback<List<Packet>>() {
            @Override
            public void onFailure(Throwable throwable) {
                if (!handleDisconnection("pollSyncInfo", throwable)) {
                    scheduleTimer(true);
                }
            }

            @Override
            public void onSuccess(List<Packet> packets) {
                if (gameEngineMode != GameEngineMode.SLAVE) {
                    // Probably an answer is very late and a tutorial has already been started
                    return;
                }
                Perfmon.getInstance().onEntered(PerfmonEnum.SYNC_HANDLE_PACKETS);
                try {
                    disconnectionCount = 0;
                    handlePackets(packets);
                } finally {
                    Perfmon.getInstance().onLeft(PerfmonEnum.SYNC_HANDLE_PACKETS);
                    scheduleTimer(false);
                }
            }
        });
    }

    private void scheduleTimer(boolean resendLast) {
        if (timer != null) {
            this.resendLast = resendLast;
            timer.schedule(MIN_DELAY_BETWEEN_POLL);
        }
    }

    private void handlePackets(List<Packet> packets) {
        if (packets == null) {
            return;
        }
        Collection<SyncItemInfo> syncItemInfos = new ArrayList<SyncItemInfo>();
        for (Packet packet : packets) {
            try {
                if (packet instanceof BaseChangedPacket) {
                    ClientBase.getInstance().onBaseChangedPacket((BaseChangedPacket) packet);
                } else if (packet instanceof SyncItemInfo) {
                    syncItemInfos.add((SyncItemInfo) packet);
                } else if (packet instanceof Message) {
                    Message message = (Message) packet;
                    DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.messageDialog(), message.getMessage(), message.isShowRegisterDialog()), DialogManager.Type.QUEUE_ABLE);
                } else if (packet instanceof AccountBalancePacket) {
                    AccountBalancePacket balancePacket = (AccountBalancePacket) packet;
                    ClientBase.getInstance().setAccountBalance(balancePacket.getAccountBalance());
                } else if (packet instanceof EnergyPacket) {
                    ClientEnergyService.getInstance().onEnergyPacket((EnergyPacket) packet);
                } else if (packet instanceof ChatMessage) {
                    ClientMessageIdPacketHandler.getInstance().onMessageReceived((ChatMessage) packet);
                } else if (packet instanceof LevelPacket) {
                    ClientUserGuidanceService.getInstance().setLevel(((LevelPacket) packet).getLevel());
                    SplashManager.getInstance().onLevelUp();
                    QuestDialog.updateQuestDialog();
                    FacebookUtils.postToFeedLevelUp(((LevelPacket) packet).getLevel());
                    FacebookUtils.callConversationTrackingOnLevelPromotionDone();
                } else if (packet instanceof LevelTaskPacket) {
                    QuestVisualisationModel.getInstance().setLevelTask((LevelTaskPacket) packet);
                    QuestDialog.updateQuestDialog();
                } else if (packet instanceof HouseSpacePacket) {
                    HouseSpacePacket houseSpacePacket = (HouseSpacePacket) packet;
                    ClientBase.getInstance().setHouseSpace(houseSpacePacket.getHouseSpace());
                    SideCockpit.getInstance().updateItemLimit();
                } else if (packet instanceof BoxPickedPacket) {
                    SideCockpit.getInstance().onBoxPicked((BoxPickedPacket) packet);
                    InventoryDialog.onBoxPicket();
                } else if (packet instanceof XpPacket) {
                    XpPacket xpPacket = (XpPacket) packet;
                    SideCockpit.getInstance().setXp(xpPacket.getXp(), xpPacket.getXp2LevelUp());
                } else if (packet instanceof UnlockContainerPacket) {
                    ClientUnlockServiceImpl.getInstance().setUnlockContainer(((UnlockContainerPacket) packet).getUnlockContainer());
                } else if (packet instanceof ServerRebootMessagePacket) {
                    ClientMessageIdPacketHandler.getInstance().onMessageReceived((ServerRebootMessagePacket) packet);
                } else if (packet instanceof BaseLostPacket) {
                    StartPointMode.getInstance().onBaseLost((BaseLostPacket) packet);
                } else if (packet instanceof UserPacket) {
                    ClientUserService.getInstance().onUserPacket((UserPacket)packet);
                } else if (packet instanceof UserAttentionPacket) {
                    MenuBarCockpit.getInstance().onUserAttentionPacket((UserAttentionPacket) packet);
                } else if (packet instanceof StorablePacket) {
                    handleStorablePacket((StorablePacket) packet);
                } else {
                    throw new IllegalArgumentException(this + " unknown packet: " + packet);
                }
            } catch (Throwable t) {
                ClientExceptionHandler.handleException(t);
            }
        }
        ItemContainer.getInstance().doSynchronize(syncItemInfos);
    }

    public void handleStorablePackets(Collection<StorablePacket> storablePackets) {
        if (storablePackets == null) {
            return;
        }
        for (StorablePacket storablePacket : storablePackets) {
            handleStorablePacket(storablePacket);
        }
    }

    private void handleStorablePacket(StorablePacket storablePacket) {
        switch (storablePacket.getType()) {
            case GUILD_LOST:
                ClientBase.getInstance().onGuildLost();
                break;
            default:
                throw new IllegalArgumentException("Connection.handleStorablePacket() unknown type: " + storablePacket.getType());
        }
    }

    public void addCommandToQueue(BaseCommand baseCommand) {
        if (movableServiceAsync != null && gameEngineMode == GameEngineMode.SLAVE) {
            commandQueue.add(baseCommand);
        }
    }

    public void sendCommandQueue() {
        if (movableServiceAsync != null && !commandQueue.isEmpty() && gameEngineMode == GameEngineMode.SLAVE) {
            movableServiceAsync.sendCommands(ClientGlobalServices.getInstance().getClientRunner().getStartUuid(), commandQueue, new VoidAsyncCallback("sendCommandQueue"));
        }
        commandQueue.clear();
    }

    public void sendTutorialProgress(final TutorialConfig.TYPE type, final int levelTaskId, final String name, final long duration, final long clientTimeStamp, final ParametrisedRunnable<GameFlow> runnable) {
        if (movableServiceAsync != null) {
            movableServiceAsync.sendTutorialProgress(type,
                    ClientGlobalServices.getInstance().getClientRunner().getStartUuid(),
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
    public void sendSyncInfo(SyncItem syncItem) {
        ClientUserTracker.getInstance().trackSyncInfo(syncItem);
    }

    @Override
    public void sendChatMessage(ChatMessage chatMessage, final ChatMessageFilter chatMessageFilter) {
        if (movableServiceAsync != null) {
            movableServiceAsync.sendChatMessage(chatMessage, chatMessageFilter, new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable caught) {
                    log.log(Level.SEVERE, "Chat message sending failed", caught);
                }

                @Override
                public void onSuccess(Void result) {
                    ClientMessageIdPacketHandler.getInstance().pollMessagesIfInPollMode(chatMessageFilter);
                }
            });
        }
    }

    @Override
    public void pollChatMessages(Integer lastMessageId, ChatMessageFilter chatMessageFilter) {
        if (movableServiceAsync != null) {
            movableServiceAsync.pollMessageIdPackets(lastMessageId, chatMessageFilter, gameEngineMode, new AsyncCallback<List<MessageIdPacket>>() {
                @Override
                public void onFailure(Throwable caught) {
                    ClientExceptionHandler.handleExceptionOnlyOnce("Chat message polling failed", caught);
                }

                @Override
                public void onSuccess(List<MessageIdPacket> messageIdPackets) {
                    ClientMessageIdPacketHandler.getInstance().onMessageReceived(messageIdPackets);
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

    public void sellItem(SyncBaseItem syncBaseItem) {
        if (gameEngineMode == GameEngineMode.SLAVE) {
            if (movableServiceAsync != null) {
                movableServiceAsync.sellItem(ClientGlobalServices.getInstance().getClientRunner().getStartUuid(), syncBaseItem.getId(), new VoidAsyncCallback("sellItem"));
            }
        } else if (gameEngineMode == GameEngineMode.MASTER) {
            try {
                ItemContainer.getInstance().sellItem(syncBaseItem.getId());
            } catch (Exception e) {
                log.log(java.util.logging.Level.SEVERE, "sendSellItem()", e);
            }
        }
    }

    public void sendDebug(Date date, String category, String message) {
        if (movableServiceAsync != null) {
            movableServiceAsync.sendDebug(date, category, message, new VoidAsyncCallback("sendDebug"));
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
            if (movableServiceAsync != null) {
                movableServiceAsync = null;
                GwtCommon.sendLogViaLoadScriptCommunication("Client disconnected due to HTTP status code 0: " + message);
                MessageDialog messageDialog = new MessageDialog(ClientI18nHelper.CONSTANTS.connectionFailed(), ClientI18nHelper.CONSTANTS.connectionLost());
                messageDialog.setShowCloseButton(false);
                messageDialog.setGlassEnabled(true);
                DialogManager.showDialog(messageDialog, DialogManager.Type.PROMPTLY);
            }
            return true;
        }

        if (throwable instanceof NotYourBaseException) {
            movableServiceAsync = null;
            GwtCommon.sendLogViaLoadScriptCommunication("Client disconnected due to NotYourBaseException: " + message);
            DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.wrongBase(), ClientI18nHelper.CONSTANTS.notYourBase()), DialogManager.Type.PROMPTLY);
            return true;
        } else if (throwable instanceof NoConnectionException) {
            switch (((NoConnectionException) throwable).getType()) {
                case NON_EXISTENT: {
                    log.warning("Client disconnected due to non existing connection");
                    MessageDialog messageDialog = new MessageDialog(ClientI18nHelper.CONSTANTS.connectionFailed(), ClientI18nHelper.CONSTANTS.connectionNone());
                    messageDialog.setShowCloseButton(false);
                    messageDialog.setGlassEnabled(true);
                    DialogManager.showDialog(messageDialog, DialogManager.Type.PROMPTLY);
                    movableServiceAsync = null;
                    return true;
                }
                case LOGGED_OUT: {
                    log.warning("Client disconnected due to logged out");
                    MessageDialog messageDialog = new MessageDialog(ClientI18nHelper.CONSTANTS.connectionFailed(), ClientI18nHelper.CONSTANTS.connectionNoneLoggedOut());
                    messageDialog.setShowCloseButton(false);
                    messageDialog.setGlassEnabled(true);
                    DialogManager.showDialog(messageDialog, DialogManager.Type.PROMPTLY);
                    movableServiceAsync = null;
                    return true;
                }
                case ANOTHER_CONNECTION_EXISTS: {
                    log.warning("Client disconnected due to another connection");
                    MessageDialog messageDialog = new MessageDialog(ClientI18nHelper.CONSTANTS.connectionFailed(), ClientI18nHelper.CONSTANTS.connectionAnotherExits());
                    messageDialog.setGlassEnabled(true);
                    messageDialog.setShowCloseButton(false);
                    DialogManager.showDialog(messageDialog, DialogManager.Type.PROMPTLY);
                    movableServiceAsync = null;
                    return true;
                }
                case TIMED_OUT: {
                    log.warning("Client disconnected due to time out");
                    StartupScreen.getInstance().fadeOutAndStart(GameStartupSeq.WARM_REAL);
                    return true;
                }
                default: {
                    log.warning("Client disconnected type unknown: " + ((NoConnectionException) throwable).getType());
                    StartupScreen.getInstance().fadeOutAndStart(GameStartupSeq.WARM_REAL);
                    return true;
                }
            }
        } else {
            GwtCommon.sendLogViaLoadScriptCommunication("Unknown Error (See GWT log for stack trace): " + message + " " + throwable.getMessage());
            ClientExceptionHandler.handleException(message, throwable);
            return false;
        }
    }

    public static boolean isConnected() {
        return INSTANCE.movableServiceAsync != null;
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
                    ClientGlobalServices.getInstance().getClientRunner().getStartUuid(),
                    Simulation.getInstance().getLevelTaskId(),
                    new VoidAsyncCallback("onTaskFinished"));
        }
    }

    @Override
    public void onTaskFailed(AbstractStartupTask task, String error, Throwable t) {
        if (movableServiceAsync != null) {
            movableServiceAsync.sendStartupTask(task.createStartupTaskInfo(error),
                    ClientGlobalServices.getInstance().getClientRunner().getStartUuid(),
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
        } else if (t instanceof IncompatibleRemoteServiceException) {
            // Reload whole browser
            new Timer() {
                @Override
                public void run() {
                    Window.Location.reload();
                }
            }.schedule(1000);
        } else {
            log.log(java.util.logging.Level.SEVERE, "Startup task failed: '" + task.getTaskEnum().getStartupTaskEnumHtmlHelper().getNiceText() + "' error: " + error, t);
        }
    }

    @Override
    public void onStartupFinished(List<StartupTaskInfo> taskInfo, long totalTime) {
        if (movableServiceAsync != null) {
            movableServiceAsync.sendStartupTerminated(true,
                    totalTime,
                    ClientGlobalServices.getInstance().getClientRunner().getStartUuid(),
                    Simulation.getInstance().getLevelTaskId(),
                    new VoidAsyncCallback("onStartupFinished"));
        }
    }

    @Override
    public void onStartupFailed(List<StartupTaskInfo> taskInfo, long totalTime) {
        if (movableServiceAsync != null) {
            movableServiceAsync.sendStartupTerminated(false,
                    totalTime,
                    ClientGlobalServices.getInstance().getClientRunner().getStartUuid(),
                    Simulation.getInstance().getLevelTaskId(),
                    new VoidAsyncCallback("onStartupFailed"));
        }
    }

    @Override
    public GameEngineMode getGameEngineMode() {
        return gameEngineMode;
    }

    public void disconnect() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        commandQueue.clear();
    }

    public void clear() {
        gameEngineMode = null;
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

    public void loadInventory(Integer filterPlanetId, boolean filterLevel, final InventoryDialog inventoryDialog) {
        if (movableServiceAsync != null) {
            movableServiceAsync.getInventory(filterPlanetId, filterLevel, new AsyncCallback<InventoryInfo>() {
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

    public void assembleInventoryItem(int inventoryItemId, Integer filterPlanetId, boolean filterLevel, final InventoryDialog inventoryDialog) {
        if (movableServiceAsync != null) {
            movableServiceAsync.assembleInventoryItem(inventoryItemId, filterPlanetId, filterLevel, new AsyncCallback<InventoryInfo>() {
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

    public void buyInventoryItem(int inventoryItemId, Integer filterPlanetId, boolean filterLevel, final InventoryDialog inventoryDialog) {
        if (movableServiceAsync != null) {
            movableServiceAsync.buyInventoryItem(inventoryItemId, filterPlanetId, filterLevel, new AsyncCallback<InventoryInfo>() {
                @Override
                public void onFailure(Throwable caught) {
                    handleDisconnection("buyInventoryItem", caught);
                }

                @Override
                public void onSuccess(InventoryInfo inventoryInfo) {
                    inventoryDialog.onItemsReceived(inventoryInfo);
                }
            });
        }
    }

    public void buyInventoryArtifact(int inventoryArtifactId, Integer filterPlanetId, boolean filterLevel, final InventoryDialog inventoryDialog) {
        if (movableServiceAsync != null) {
            movableServiceAsync.buyInventoryArtifact(inventoryArtifactId, filterPlanetId, filterLevel, new AsyncCallback<InventoryInfo>() {
                @Override
                public void onFailure(Throwable caught) {
                    handleDisconnection("buyInventoryArtifact", caught);
                }

                @Override
                public void onSuccess(InventoryInfo inventoryInfo) {
                    inventoryDialog.onItemsReceived(inventoryInfo);
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

    public void loadCurrentStatisticEntryInfos(final HighscoreDialog highscoreDialog) {
        if (movableServiceAsync != null) {
            movableServiceAsync.loadCurrentStatisticEntryInfos(new AsyncCallback<Collection<CurrentStatisticEntryInfo>>() {
                @Override
                public void onFailure(Throwable caught) {
                    handleDisconnection("loadQuestOverview", caught);
                }

                @Override
                public void onSuccess(Collection<CurrentStatisticEntryInfo> entryInfos) {
                    highscoreDialog.onHighscoreRecived(entryInfos);
                }
            });
        }
    }

    public void sendPerfmonData(Map<PerfmonEnum, Integer> workTimes, int totalTime) {
        if (movableServiceAsync != null) {
            movableServiceAsync.sendPerfmonData(workTimes, totalTime, new VoidAsyncCallback("sendPerfmonData"));
        }
    }

    public void surrenderBase(final Runnable runnable) {
        if (movableServiceAsync != null) {
            movableServiceAsync.surrenderBase(new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable caught) {
                    ClientExceptionHandler.handleException("MovableServiceAsync.surrenderBase()", caught);
                }

                @Override
                public void onSuccess(Void result) {
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            });
        }
    }

    public void getRazarion(final ParametrisedRunnable<Integer> runnable) {
        if (movableServiceAsync != null) {
            movableServiceAsync.getRazarion(new AsyncCallback<Integer>() {

                @Override
                public void onFailure(Throwable caught) {
                    handleDisconnection("getRazarion", caught);
                }

                @Override
                public void onSuccess(Integer razarion) {
                    runnable.run(razarion);
                }
            });
        }
    }

    public void unlockItemType(int itemTypeId, final Runnable successRunnable) {
        if (movableServiceAsync != null) {
            movableServiceAsync.unlockItemType(itemTypeId, new AsyncCallback<UnlockContainer>() {

                @Override
                public void onFailure(Throwable caught) {
                    handleDisconnection("unlockItemType", caught);
                }

                @Override
                public void onSuccess(UnlockContainer unlockContainer) {
                    if (unlockContainer != null) {
                        ClientUnlockServiceImpl.getInstance().setUnlockContainer(unlockContainer);
                    }
                    successRunnable.run();
                }
            });
        }
    }

    public void unlockQuest(int questId, final Runnable successRunnable) {
        if (movableServiceAsync != null) {
            movableServiceAsync.unlockQuest(questId, new AsyncCallback<UnlockContainer>() {

                @Override
                public void onFailure(Throwable caught) {
                    handleDisconnection("unlockQuest", caught);
                }

                @Override
                public void onSuccess(UnlockContainer unlockContainer) {
                    if (unlockContainer != null) {
                        ClientUnlockServiceImpl.getInstance().setUnlockContainer(unlockContainer);
                    }
                    if (successRunnable != null) {
                        successRunnable.run();
                    }
                }
            });
        }
    }


    public void unlockPlanet(final PlanetLiteInfo planetLiteInfo, final Runnable successRunnable) {
        if (movableServiceAsync != null) {
            movableServiceAsync.unlockPlanet(planetLiteInfo.getPlanetId(), new AsyncCallback<UnlockContainer>() {

                @Override
                public void onFailure(Throwable caught) {
                    handleDisconnection("unlockPlanet", caught);
                }

                @Override
                public void onSuccess(UnlockContainer unlockContainer) {
                    if (unlockContainer != null) {
                        ClientUnlockServiceImpl.getInstance().setUnlockContainer(unlockContainer);
                        QuestVisualisationCockpit.getInstance().displayNextPlanetPanel();
                    }
                    if (successRunnable != null) {
                        successRunnable.run();
                    }
                }
            });
        }
    }

    public void saveGuildText(String guildTextRwHTML) {
        if (movableServiceAsync != null) {
            movableServiceAsync.saveGuildText(guildTextRwHTML, new AsyncCallback<FullGuildInfo>() {
                @Override
                public void onFailure(Throwable caught) {
                    ClientExceptionHandler.handleException("MovableServiceAsync.saveGuildText()", caught);
                }

                @Override
                public void onSuccess(FullGuildInfo fullGuildInfo) {
                    MyGuildDialog.updateIfShowing(fullGuildInfo);
                }
            });
        }
    }

    public void saveGuildMemberRank(int userId, GuildMemberInfo.Rank rank) {
        if (movableServiceAsync != null) {
            movableServiceAsync.changeGuildMemberRank(userId, rank, new AsyncCallback<FullGuildInfo>() {
                @Override
                public void onFailure(Throwable caught) {
                    ClientExceptionHandler.handleException("MovableServiceAsync.changeGuildMemberRank()", caught);
                }

                @Override
                public void onSuccess(FullGuildInfo fullGuildInfo) {
                    MyGuildDialog.updateIfShowing(fullGuildInfo);
                }
            });
        }
    }

    public void getSuggestedUserName(final SuggestOracle.Request request, final SuggestOracle.Callback callback) {
        if (movableServiceAsync != null) {
            movableServiceAsync.getSuggestedUserName(request.getQuery(), request.getLimit(), new AsyncCallback<SuggestOracle.Response>() {

                @Override
                public void onFailure(Throwable caught) {
                    handleDisconnection("getSuggestedUserName", caught);
                }

                @Override
                public void onSuccess(SuggestOracle.Response response) {
                    callback.onSuggestionsReady(request, response);
                }
            });
        }
    }


    public void inviteGuildMember(final String userName) {
        if (movableServiceAsync != null) {
            movableServiceAsync.inviteUserToGuild(userName, new AsyncCallback<FullGuildInfo>() {

                @Override
                public void onFailure(Throwable caught) {
                    if (caught instanceof NoSuchUserException) {
                        DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.gildMemberInvited(),
                                ClientI18nHelper.CONSTANTS.noSuchUser(((NoSuchUserException) caught).getUserName())),
                                DialogManager.Type.STACK_ABLE);
                    } else if (caught instanceof UserIsAlreadyGuildMemberException) {
                        DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.gildMemberInvited(),
                                ClientI18nHelper.CONSTANTS.userIsAlreadyAGuildMember(userName)),
                                DialogManager.Type.STACK_ABLE);
                    } else {
                        handleDisconnection("inviteUserToGuild", caught);
                    }
                }

                @Override
                public void onSuccess(FullGuildInfo fullGuildInfo) {
                    MyGuildDialog.updateIfShowing(fullGuildInfo);
                    DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.gildMemberInvited(), ClientI18nHelper.CONSTANTS.gildMemberInvitedMessage(userName)), DialogManager.Type.STACK_ABLE);
                }
            });
        }
    }

    public void inviteGuildMember(final SimpleBase simpleBase, final String baseName) {
        if (movableServiceAsync != null) {
            movableServiceAsync.inviteUserToGuild(simpleBase, new AsyncCallback<Void>() {

                @Override
                public void onFailure(Throwable caught) {
                    if (caught instanceof InvitingUnregisteredBaseException) {
                        DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.gildMemberInvited(),
                                ClientI18nHelper.CONSTANTS.guildInvitationNotRegistered(baseName),
                                false),
                                DialogManager.Type.STACK_ABLE);
                    } else {
                        handleDisconnection("inviteUserToGuild", caught);
                    }
                }

                @Override
                public void onSuccess(Void aVoid) {
                    DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.gildMemberInvited(), ClientI18nHelper.CONSTANTS.gildMemberInvitedMessage(baseName)), DialogManager.Type.STACK_ABLE);
                }
            });
        }
    }

    public void dismissGuildMemberRequest(SimpleUser simpleUser) {
        if (movableServiceAsync != null) {
            movableServiceAsync.dismissGuildMemberRequest(simpleUser.getId(), new AsyncCallback<FullGuildInfo>() {
                @Override
                public void onFailure(Throwable caught) {
                    ClientExceptionHandler.handleException("MovableServiceAsync.dismissGuildMemberRequest()", caught);
                }

                @Override
                public void onSuccess(FullGuildInfo fullGuildInfo) {
                    MyGuildDialog.updateIfShowing(fullGuildInfo);
                }
            });
        }
    }

    public void sendFacebookInvite(String fbRequestId, Collection<String> fbUserIds) {
        if (movableServiceAsync != null) {
            InviteFriendsDialog.enableFacebookButton(false);
            movableServiceAsync.onFacebookInvite(fbRequestId, fbUserIds, new AsyncCallback<Void>() {

                @Override
                public void onFailure(Throwable caught) {
                    ClientExceptionHandler.handleException("Connection sendFacebookInvite()", caught);
                    InviteFriendsDialog.enableFacebookButton(true);
                }

                @Override
                public void onSuccess(Void aVoid) {
                    InviteFriendsDialog.enableFacebookButton(true);
                    DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.inviteFriends(), ClientI18nHelper.CONSTANTS.invitationFacebookSent()), DialogManager.Type.STACK_ABLE);
                }
            });
        }
    }

    public void sendMailInvite(final String emailAddress, final FocusWidget button, final ValueBoxBase textBox) {
        if (movableServiceAsync != null) {
            button.setEnabled(false);
            movableServiceAsync.sendMailInvite(emailAddress, new AsyncCallback<Void>() {

                @Override
                public void onFailure(Throwable caught) {
                    ClientExceptionHandler.handleException("Connection sendMailInvite()", caught);
                    button.setEnabled(true);
                }

                @Override
                public void onSuccess(Void aVoid) {
                    textBox.setText("");
                    button.setEnabled(true);
                    DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.inviteFriends(), ClientI18nHelper.CONSTANTS.invitationEmailSent(emailAddress)), DialogManager.Type.STACK_ABLE);
                }
            });
        }
    }


}
