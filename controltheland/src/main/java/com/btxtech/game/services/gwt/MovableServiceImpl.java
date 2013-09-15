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

package com.btxtech.game.services.gwt;


import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.NotAGuildMemberException;
import com.btxtech.game.jsre.client.PositionInBotException;
import com.btxtech.game.jsre.client.VerificationRequestCallback;
import com.btxtech.game.jsre.client.cockpit.chat.ChatMessageFilter;
import com.btxtech.game.jsre.client.cockpit.item.InvitingUnregisteredBaseException;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.CrystalCostInfo;
import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.client.common.info.InvalidLevelStateException;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.client.common.info.SimpleUser;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.client.dialogs.guild.FullGuildInfo;
import com.btxtech.game.jsre.client.dialogs.guild.GuildDetailedInfo;
import com.btxtech.game.jsre.client.dialogs.guild.GuildMemberInfo;
import com.btxtech.game.jsre.client.dialogs.guild.SearchGuildsResult;
import com.btxtech.game.jsre.client.dialogs.highscore.CurrentStatisticEntryInfo;
import com.btxtech.game.jsre.client.dialogs.history.HistoryElementInfo;
import com.btxtech.game.jsre.client.dialogs.history.HistoryFilter;
import com.btxtech.game.jsre.client.dialogs.incentive.FriendInvitationBonus;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryInfo;
import com.btxtech.game.jsre.client.dialogs.news.NewsEntryInfo;
import com.btxtech.game.jsre.client.dialogs.quest.QuestOverview;
import com.btxtech.game.jsre.client.dialogs.starmap.StarMapInfo;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.jsre.common.UserIsAlreadyGuildMemberException;
import com.btxtech.game.jsre.common.gameengine.services.unlock.impl.UnlockContainer;
import com.btxtech.game.jsre.common.gameengine.services.user.EmailAlreadyExitsException;
import com.btxtech.game.jsre.common.gameengine.services.user.LoginFailedException;
import com.btxtech.game.jsre.common.gameengine.services.user.LoginFailedNotVerifiedException;
import com.btxtech.game.jsre.common.gameengine.services.user.NoSuchUserException;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.packets.MessageIdPacket;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.jsre.common.tutorial.GameFlow;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.jsre.common.utg.tracking.BrowserWindowTracking;
import com.btxtech.game.jsre.common.utg.tracking.DialogTracking;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingStart;
import com.btxtech.game.jsre.common.utg.tracking.SelectionTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.TerrainScrollTracking;
import com.btxtech.game.services.cms.ContentService;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.PropertyService;
import com.btxtech.game.services.common.PropertyServiceEnum;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.connection.ServerGlobalConnectionService;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.inventory.GlobalInventoryService;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.media.ClipService;
import com.btxtech.game.services.media.SoundService;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import com.btxtech.game.services.socialnet.facebook.FacebookUtil;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.services.terrain.TerrainDbUtil;
import com.btxtech.game.services.terrain.TerrainImageService;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.unlock.ServerUnlockService;
import com.btxtech.game.services.user.GuildService;
import com.btxtech.game.services.user.InvitationService;
import com.btxtech.game.services.user.RegisterService;
import com.btxtech.game.services.user.UserNameSuggestionFilter;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import com.google.gwt.user.client.ui.SuggestOracle;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MovableServiceImpl extends AutowiredRemoteServiceServlet implements MovableService {
    @Autowired
    private TerrainImageService terrainImageService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private ServerGlobalConnectionService serverGlobalConnectionService;
    @Autowired
    private UserTrackingService userTrackingService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private UserService userService;
    @Autowired
    private MgmtService mgmtService;
    @Autowired
    private TutorialService tutorialService;
    @Autowired
    private Session session;
    @Autowired
    private CmsUiService cmsUiService;
    @Autowired
    private GuildService guildService;
    @Autowired
    private GlobalInventoryService globalInventoryService;
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private SoundService soundService;
    @Autowired
    private ClipService clipService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private RegisterService registerService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private ServerUnlockService serverUnlockService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ContentService contentService;
    @Autowired
    private InvitationService invitationService;
    @Autowired
    private PropertyService propertyService;

    @Override
    public void sendCommands(String startUuid, List<BaseCommand> baseCommands) throws NoConnectionException {
        try {
            serverGlobalConnectionService.getConnection(startUuid).getServerPlanetServices().getActionService().executeCommands(baseCommands);
        } catch (NoConnectionException e) {
            throw e;
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public List<Packet> getSyncInfo(String startUuid, boolean resendLast) throws NoConnectionException {
        try {
            return serverGlobalConnectionService.getConnection(startUuid).getAndRemovePendingPackets(resendLast);
        } catch (NoConnectionException e) {
            throw e;
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public Collection<SyncItemInfo> getAllSyncInfo(String startUuid) throws NoConnectionException {
        try {
            return serverGlobalConnectionService.getConnection(startUuid).getServerPlanetServices().getItemService().getSyncInfo();
        } catch (NoConnectionException e) {
            throw e;
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public void sendStartupTask(StartupTaskInfo startupTaskInfo, String startUuid, Integer levelTaskId) {
        try {
            userTrackingService.saveStartupTask(startupTaskInfo, startUuid, levelTaskId);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public void sendStartupTerminated(boolean successful, long totalTime, String startUuid, Integer levelTaskId) {
        try {
            userTrackingService.saveStartupTerminated(successful, totalTime, startUuid, levelTaskId);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public RealGameInfo getRealGameInfo(String startUuid, Integer planetId) throws InvalidLevelStateException {
        try {
            UserState userState = userService.getUserState();
            ServerPlanetServices serverPlanetServices = planetSystemService.getPlanetSystemService(userState, planetId);
            RealGameInfo realGameInfo = new RealGameInfo();
            setCommonInfo(realGameInfo, userService, serverItemTypeService, propertyService, cmsUiService, soundService, clipService);
            if (userState == null) {
                throw new IllegalStateException("No UserState available: " + userState);
            }
            if (userState.getBase() != null) {
                if (planetId != null && planetId != userState.getBase().getPlanet().getPlanetServices().getPlanetInfo().getPlanetId()) {
                    throw new IllegalStateException("User has a base but given planet id is different. Base planet: " + userState.getBase().getPlanet().getPlanetServices().getPlanetInfo().getPlanetLiteInfo() + " given planet id: " + planetId);
                }
                continueBase(serverPlanetServices, realGameInfo);
            } else {
                askForStartPosition(serverPlanetServices, userState, realGameInfo, planetSystemService, false);
            }
            realGameInfo.setStorablePackets(userState.getAndClearStorablePackets());
            realGameInfo.setMySimpleGuild(guildService.getSimpleGuild());
            terrainImageService.setupTerrainImages(realGameInfo);
            serverPlanetServices.getTerrainService().setupTerrainRealGame(realGameInfo);
            realGameInfo.setPlanetInfo(serverPlanetServices.getPlanetInfo());
            realGameInfo.setAllPlanets(planetSystemService.getAllPlanetLiteInfos());
            userGuidanceService.fillRealGameInfo(realGameInfo, request.getLocale());
            serverPlanetServices.getConnectionService().createConnection(userState, startUuid);
            realGameInfo.setUnlockContainer(serverUnlockService.getUnlockContainer(userState));
            return realGameInfo;
        } catch (InvalidLevelStateException invalidLevelStateException) {
            throw invalidLevelStateException;
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
        return null;
    }

    public static void askForStartPosition(ServerPlanetServices serverPlanetServices, UserState userState, RealGameInfo realGameInfo, PlanetSystemService planetSystemService, boolean existingGame) {
        SimpleBase fakeBase = SimpleBase.createFakeUser(serverPlanetServices.getPlanetInfo().getPlanetId());
        realGameInfo.setBase(fakeBase);
        realGameInfo.setAllBases(serverPlanetServices.getBaseService().createAllBaseAttributes4FakeBase(fakeBase, userState, serverPlanetServices.getPlanetInfo().getPlanetId()));
        realGameInfo.setStartPointInfo(planetSystemService.createStartPoint(serverPlanetServices.getPlanetInfo(), !existingGame));
    }

    private void continueBase(ServerPlanetServices serverPlanetServices, RealGameInfo realGameInfo) throws InvalidLevelStateException {
        fillRealGameInfo(realGameInfo);
        Base base = serverPlanetServices.getBaseService().getBase();
        realGameInfo.setUnlockContainer(serverUnlockService.getUnlockContainer(base.getSimpleBase()));
    }

    private void fillRealGameInfo(RealGameInfo realGameInfo) {
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices();
        Base base = serverPlanetServices.getBaseService().getBase();
        realGameInfo.setBase(base.getSimpleBase());
        realGameInfo.setAccountBalance(base.getAccountBalance());
        realGameInfo.setEnergyConsuming(serverPlanetServices.getEnergyService().getConsuming());
        realGameInfo.setEnergyGenerating(serverPlanetServices.getEnergyService().getGenerating());
        realGameInfo.setAllBases(planetSystemService.getServerPlanetServices(base.getSimpleBase()).getBaseService().getAllBaseAttributes());
        realGameInfo.setHouseSpace(base.getHouseSpace());
    }

    @Override
    public SimulationInfo getSimulationGameInfo(int levelTaskId) throws InvalidLevelStateException {
        try {
            SimulationInfo simulationInfo = new SimulationInfo();
            DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialConfig(levelTaskId);
            // Common
            setCommonInfo(simulationInfo, userService, serverItemTypeService, propertyService, cmsUiService, soundService, clipService);
            simulationInfo.setTutorialConfig(dbTutorialConfig.getTutorialConfig(serverItemTypeService, request.getLocale()));
            simulationInfo.setLevelTaskId(levelTaskId);
            simulationInfo.setLevelNumber(userGuidanceService.getDbLevel().getNumber());
            simulationInfo.setAbortable(userGuidanceService.getDbLevel().hasDbPlanet());
            simulationInfo.setSellAllowed(dbTutorialConfig.isSellAllowed());
            // Terrain
            terrainImageService.setupTerrainImages(simulationInfo);
            TerrainDbUtil.loadTerrainFromDb(dbTutorialConfig.getDbTerrainSetting(), simulationInfo);
            return simulationInfo;
        } catch (InvalidLevelStateException invalidLevelStateException) {
            throw invalidLevelStateException;
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
        return null;
    }

    @Override
    public void surrenderBase() {
        try {
            planetSystemService.getServerPlanetServices().getBaseService().surrenderBase(planetSystemService.getServerPlanetServices().getBaseService().getBase());
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    public static void setCommonInfo(GameInfo gameInfo, UserService userService, ServerItemTypeService serverItemTypeService, PropertyService propertyService, CmsUiService cmsUiService, SoundService soundService, ClipService clipService) {
        gameInfo.setSimpleUser(userService.getSimpleUser());
        gameInfo.setItemTypes(serverItemTypeService.getItemTypes());
        gameInfo.setRegisterDialogDelay(propertyService.getIntPropertyFallback(PropertyServiceEnum.REGISTER_DIALOG_DELAY));
        gameInfo.setPredefinedUrls(cmsUiService.getPredefinedUrls());
        gameInfo.setCommonSoundInfo(soundService.getCommonSoundInfo());
        gameInfo.setImageSpriteMapLibrary(clipService.getImageSpriteMapLibrary());
        gameInfo.setClipLibrary(clipService.getClipLibrary());
        gameInfo.setPreloadedImageSpriteMapInfo(clipService.getPreloadedImageSpriteMapInfo());
        gameInfo.setUserAttentionPacket(userService.createUserAttentionPacket());
    }

    @Override
    public void sendDebug(Date date, String category, String message) {
        try {
            serverGlobalConnectionService.saveClientDebug(date, category, message);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public SimpleUser register(String userName, String password, String confirmPassword, String email) throws UserAlreadyExistsException, PasswordNotMatchException, EmailAlreadyExitsException {
        try {
            return registerService.register(userName, password, confirmPassword, email);
        } catch (UserAlreadyExistsException | PasswordNotMatchException | EmailAlreadyExitsException e) {
            throw e;
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public SimpleUser login(String name, String password) throws LoginFailedException, LoginFailedNotVerifiedException {
        try {
            return userService.inGameLogin(name, password);
        } catch (LoginFailedException | LoginFailedNotVerifiedException e) {
            throw e;
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            throw new LoginFailedException();
        }
    }

    @Override
    public void logout() {
        try {
            userService.inGameLogout();
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public SimpleUser createAndLoginFacebookUser(String signedRequestParameter, String nickname, String email) throws UserAlreadyExistsException {
        try {
            FacebookSignedRequest facebookSignedRequest = FacebookUtil.createAndCheckFacebookSignedRequest(cmsUiService.getFacebookAppSecret(), signedRequestParameter);
            facebookSignedRequest.setEmail(email);
            userService.createAndLoginFacebookUser(facebookSignedRequest, nickname);
            return userService.getSimpleUser();
        } catch (UserAlreadyExistsException e) {
            throw e;
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public void loginFacebookUser(String signedRequestParameter) throws UserAlreadyExistsException {
        try {
            FacebookSignedRequest facebookSignedRequest = FacebookUtil.createAndCheckFacebookSignedRequest(cmsUiService.getFacebookAppSecret(), signedRequestParameter);
            userService.loginFacebookUser(facebookSignedRequest);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public boolean isFacebookUserRegistered(String signedRequestParameter) {
        try {
            FacebookSignedRequest facebookSignedRequest = FacebookUtil.createAndCheckFacebookSignedRequest(cmsUiService.getFacebookAppSecret(), signedRequestParameter);
            return userService.isFacebookUserRegistered(facebookSignedRequest);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return false;
        }

    }

    @Override
    public VerificationRequestCallback.ErrorResult isNickNameValid(String nickname) {
        try {
            return userService.isNickNameValid(nickname);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return VerificationRequestCallback.ErrorResult.UNKNOWN_ERROR;
        }
    }

    @Override
    public void sendChatMessage(ChatMessage chatMessage, ChatMessageFilter chatMessageFilter) {
        try {
            serverGlobalConnectionService.sendChatMessage(chatMessage, chatMessageFilter);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public List<MessageIdPacket> setChatMessageFilter(ChatMessageFilter chatMessageFilter) throws NotAGuildMemberException {
        try {
            planetSystemService.setChatMessageFilter(userService.getUserState(), chatMessageFilter);
            return pollMessageIdPackets(null, chatMessageFilter, null);
        } catch (NotAGuildMemberException t) {
            throw t;
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public List<MessageIdPacket> pollMessageIdPackets(Integer lastMessageId, ChatMessageFilter chatMessageFilter, GameEngineMode gameEngineMode) {
        try {
            return serverGlobalConnectionService.pollMessageIdPackets(lastMessageId, chatMessageFilter, gameEngineMode);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public GameFlow sendTutorialProgress(TutorialConfig.TYPE type, String startUuid, int levelTaskId, String name, long duration, long clientTimeStamp) {
        try {
            userTrackingService.onTutorialProgressChanged(type, startUuid, levelTaskId, name, duration, clientTimeStamp);
            if (type == TutorialConfig.TYPE.TUTORIAL) {
                return userGuidanceService.onTutorialFinished(levelTaskId);
            } else {
                return null;
            }
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public void sendEventTrackingStart(EventTrackingStart eventTrackingStart) {
        try {
            userTrackingService.onEventTrackingStart(eventTrackingStart);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public void sendEventTrackerItems(Collection<EventTrackingItem> eventTrackingItems,
                                      Collection<SyncItemInfo> syncItemInfos,
                                      Collection<SelectionTrackingItem> selectionTrackingItems,
                                      Collection<TerrainScrollTracking> terrainScrollTrackings,
                                      Collection<BrowserWindowTracking> browserWindowTrackings,
                                      Collection<DialogTracking> dialogTrackings) {
        try {
            userTrackingService.onEventTrackerItems(eventTrackingItems, syncItemInfos, selectionTrackingItems, terrainScrollTrackings, browserWindowTrackings, dialogTrackings);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public void sellItem(String startUuid, Id id) {
        try {
            serverGlobalConnectionService.getConnection(startUuid).getServerPlanetServices().getItemService().sellItem(id);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public InventoryInfo getInventory(Integer filterPlanetId, boolean filterLevel) {
        try {
            return globalInventoryService.getInventory(filterPlanetId, filterLevel);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public InventoryInfo assembleInventoryItem(int inventoryItemId, Integer filterPlanetId, boolean filterLevel) {
        try {
            globalInventoryService.assembleInventoryItem(inventoryItemId);
            return globalInventoryService.getInventory(filterPlanetId, filterLevel);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public void useInventoryItem(int inventoryItemId, Collection<Index> positionToBePlaced) {
        try {
            globalInventoryService.useInventoryItem(inventoryItemId, positionToBePlaced);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public InventoryInfo buyInventoryItem(int inventoryItemId, Integer filterPlanetId, boolean filterLevel) {
        try {
            globalInventoryService.buyInventoryItem(inventoryItemId);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
        try {
            return globalInventoryService.getInventory(filterPlanetId, filterLevel);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public InventoryInfo buyInventoryArtifact(int inventoryArtifactId, Integer filterPlanetId, boolean filterLevel) {
        try {
            globalInventoryService.buyInventoryArtifact(inventoryArtifactId);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
        try {
            return globalInventoryService.getInventory(filterPlanetId, filterLevel);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public QuestOverview loadQuestOverview() {
        try {
            return userGuidanceService.getQuestOverview(request.getLocale());
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public void activateQuest(int questId) {
        try {
            userGuidanceService.activateQuest(questId, request.getLocale());
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public Collection<CurrentStatisticEntryInfo> loadCurrentStatisticEntryInfos() {
        try {
            return statisticsService.getInGameCurrentStatistics();
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public void sendPerfmonData(Map<PerfmonEnum, Integer> workTimes, int totalTime) {
        try {
            mgmtService.saveClientPerfmonData(session.getSessionId(), workTimes, totalTime);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public int getCrystals() {
        try {
            return userService.getUserState().getCrystals();
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return 0;
        }
    }

    @Override
    public UnlockContainer unlockItemType(int itemTypeId) {
        try {
            return serverUnlockService.unlockItemType(itemTypeId);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public UnlockContainer unlockQuest(int questId) {
        try {
            return serverUnlockService.unlockQuest(questId);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public UnlockContainer unlockPlanet(int planetId) {
        try {
            return serverUnlockService.unlockPlanet(planetId);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public RealGameInfo createBase(String startUuid, Index position) throws PositionInBotException, NoConnectionException{
        try {
            UserState userState = userService.getUserState();
            if (userState.getBase() != null) {
                throw new IllegalStateException("User does already have a base: " + userState);
            }
            ServerPlanetServices serverPlanetServices = serverGlobalConnectionService.getConnection(startUuid).getServerPlanetServices();
            planetSystemService.createBase(serverPlanetServices, userState, position);
            RealGameInfo realGameInfo = new RealGameInfo();
            fillRealGameInfo(realGameInfo);
            return realGameInfo;
        } catch (NoConnectionException | PositionInBotException e) {
            throw e;
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public HistoryElementInfo getHistoryElements(HistoryFilter historyFilter) {
        try {
            return historyService.getHistoryElements(historyFilter);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public NewsEntryInfo getNewsEntry(int index) {
        try {
            return contentService.getNewsEntry(index);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public SimpleGuild createGuild(String guildName) {
        try {
            return guildService.createGuild(guildName);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public FullGuildInfo getFullGuildInfo(int guildId) {
        try {
            return guildService.getFullGuildInfo(guildId);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public FullGuildInfo inviteUserToGuild(String userName) throws NoSuchUserException, UserIsAlreadyGuildMemberException {
        try {
            guildService.inviteUserToGuild(userName);
            //FullGuildInfo is created before the DbGuildInvitations has been removed from the DB (transaction not committed)
            return guildService.getFullGuildInfo(guildService.getSimpleGuild().getId());
        } catch (NoSuchUserException | UserIsAlreadyGuildMemberException e) {
            throw e;
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public void inviteUserToGuild(SimpleBase simpleBase) throws InvitingUnregisteredBaseException {
        try {
            guildService.inviteUserToGuild(simpleBase);
        } catch (InvitingUnregisteredBaseException e) {
            throw e;
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }


    @Override
    public SimpleGuild joinGuild(int guildId) {
        try {
            return guildService.joinGuild(guildId);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public List<GuildDetailedInfo> dismissGuildInvitation(int guildId) {
        try {
            return guildService.dismissGuildInvitation(guildId);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }


    @Override
    public List<GuildDetailedInfo> getGuildInvitations() {
        try {
            return guildService.getGuildInvitations();
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public CrystalCostInfo getCreateGuildCrystalCost() {
        try {
            return guildService.getCreateGuildCrystalCost();
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public void guildMembershipRequest(int guildId, String text) {
        try {
            guildService.guildMembershipRequest(guildId, text);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public FullGuildInfo dismissGuildMemberRequest(int userId) {
        try {
            return guildService.dismissGuildMemberRequest(userId);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public FullGuildInfo kickGuildMember(int userId) {
        try {
            return guildService.kickGuildMember(userId);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public FullGuildInfo changeGuildMemberRank(int userId, GuildMemberInfo.Rank rank) {
        try {
            return guildService.changeGuildMemberRank(userId, rank);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public FullGuildInfo saveGuildText(String text) {
        try {
            return guildService.saveGuildText(text);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public VerificationRequestCallback.ErrorResult isGuildNameValid(String guildName) {
        try {
            return guildService.isGuildNameValid(guildName);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return VerificationRequestCallback.ErrorResult.UNKNOWN_ERROR;
        }
    }

    @Override
    public SuggestOracle.Response getSuggestedUserName(String query, int limit) {
        try {
            return userService.getSuggestedUserName(query, UserNameSuggestionFilter.USER_GILD_SEARCH, limit);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public SearchGuildsResult searchGuilds(int start, int length, String guildNameQuery) {
        try {
            return guildService.searchGuilds(start, length, guildNameQuery);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }


    @Override
    public void leaveGuild() {
        try {
            guildService.leaveGuild();
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public void closeGuild() {
        try {
            guildService.closeGuild();
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public void sendMailInvite(String emailAddress) {
        try {
            invitationService.sendMailInvite(emailAddress);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public void onFacebookInvite(String fbRequestId, Collection<String> fbUserIds) {
        try {
            invitationService.onFacebookInvite(fbRequestId, fbUserIds);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public List<FriendInvitationBonus> getFriendInvitationBonuses() {
        try {
            return invitationService.getFriendInvitationBonus();
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public StarMapInfo getStarMapInfo() {
        try {
            return planetSystemService.getStarMapInfo();
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

}
