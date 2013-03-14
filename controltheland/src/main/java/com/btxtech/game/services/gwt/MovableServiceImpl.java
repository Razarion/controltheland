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
import com.btxtech.game.jsre.client.InvalidNickName;
import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.SimpleUser;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.client.common.info.InvalidLevelStateException;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.client.dialogs.highscore.CurrentStatisticEntryInfo;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryInfo;
import com.btxtech.game.jsre.client.dialogs.quest.QuestOverview;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.jsre.common.gameengine.services.unlock.impl.UnlockContainer;
import com.btxtech.game.jsre.common.gameengine.services.user.EmailAlreadyExitsException;
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
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.connection.ServerGlobalConnectionService;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.inventory.GlobalInventoryService;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.media.ClipService;
import com.btxtech.game.services.media.SoundService;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.mgmt.StartupData;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.NoSuchPlanetException;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import com.btxtech.game.services.socialnet.facebook.FacebookUtil;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.services.terrain.TerrainDbUtil;
import com.btxtech.game.services.terrain.TerrainImageService;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.unlock.ServerUnlockService;
import com.btxtech.game.services.user.AllianceService;
import com.btxtech.game.services.user.RegisterService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
    private AllianceService allianceService;
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

    @Override
    public void sendCommands(List<BaseCommand> baseCommands) {
        try {
            planetSystemService.getServerPlanetServices().getActionService().executeCommands(baseCommands);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public List<Packet> getSyncInfo(String startUuid, boolean resendLast) throws NoConnectionException {
        try {
            return planetSystemService.getServerPlanetServices().getConnectionService().getConnection(startUuid).getAndRemovePendingPackets(resendLast);
        } catch (NoConnectionException e) {
            throw e;
        } catch (NoSuchPlanetException e) {
            // Happens during server restart while client (user on a planet) still polls server
            ExceptionHandler.handleException(e);
            throw new NoConnectionException(NoConnectionException.Type.NON_EXISTENT);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
    }

    @Override
    public Collection<SyncItemInfo> getAllSyncInfo() {
        try {
            return planetSystemService.getServerPlanetServices().getItemService().getSyncInfo();
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
    public RealGameInfo getRealGameInfo(String startUuid) throws InvalidLevelStateException {
        try {
            planetSystemService.continuePlanet(startUuid);
            RealGameInfo realGameInfo = new RealGameInfo();
            setCommonInfo(realGameInfo, userService, serverItemTypeService, mgmtService, cmsUiService, soundService, clipService);
            ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices();
            Base base = serverPlanetServices.getBaseService().getBase();
            realGameInfo.setBase(base.getSimpleBase());
            realGameInfo.setAccountBalance(base.getAccountBalance());
            realGameInfo.setEnergyConsuming(serverPlanetServices.getEnergyService().getConsuming());
            realGameInfo.setEnergyGenerating(serverPlanetServices.getEnergyService().getGenerating());
            terrainImageService.setupTerrainImages(realGameInfo);
            serverPlanetServices.getTerrainService().setupTerrainRealGame(realGameInfo);
            realGameInfo.setAllBases(planetSystemService.getServerPlanetServices(base.getSimpleBase()).getBaseService().getAllBaseAttributes());
            realGameInfo.setHouseSpace(base.getHouseSpace());
            realGameInfo.setPlanetInfo(planetSystemService.getServerPlanetServices(base.getSimpleBase()).getPlanetInfo());
            realGameInfo.setAllPlanets(planetSystemService.getAllPlanetLiteInfos());
            userGuidanceService.fillRealGameInfo(realGameInfo, request.getLocale());
            realGameInfo.setAllianceOffers(allianceService.getPendingAllianceOffers());
            realGameInfo.setUnlockContainer(serverUnlockService.getUnlockContainer(base.getSimpleBase()));
            return realGameInfo;
        } catch (InvalidLevelStateException invalidLevelStateException) {
            throw invalidLevelStateException;
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
        return null;
    }

    @Override
    public SimulationInfo getSimulationGameInfo(int levelTaskId) throws InvalidLevelStateException {
        try {
            SimulationInfo simulationInfo = new SimulationInfo();
            DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialConfig(levelTaskId);
            // Common
            setCommonInfo(simulationInfo, userService, serverItemTypeService, mgmtService, cmsUiService, soundService, clipService);
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

    public static void setCommonInfo(GameInfo gameInfo, UserService userService, ServerItemTypeService serverItemTypeService, MgmtService mgmtService, CmsUiService cmsUiService, SoundService soundService, ClipService clipService) {
        gameInfo.setSimpleUser(userService.getSimpleUser());
        gameInfo.setItemTypes(serverItemTypeService.getItemTypes());
        StartupData startupData = mgmtService.getStartupData();
        gameInfo.setRegisterDialogDelay(startupData.getRegisterDialogDelay());
        gameInfo.setPredefinedUrls(cmsUiService.getPredefinedUrls());
        gameInfo.setCommonSoundInfo(soundService.getCommonSoundInfo());
        gameInfo.setImageSpriteMapLibrary(clipService.getImageSpriteMapLibrary());
        gameInfo.setClipLibrary(clipService.getClipLibrary());
        gameInfo.setPreloadedImageSpriteMapInfo(clipService.getPreloadedImageSpriteMapInfo());
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
    public InvalidNickName isNickNameValid(String nickname) {
        try {
            return userService.isNickNameValid(nickname);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return InvalidNickName.UNKNOWN_ERROR;
        }
    }

    @Override
    public void sendChatMessage(ChatMessage chatMessage) {
        try {
            serverGlobalConnectionService.sendChatMessage(chatMessage);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public List<MessageIdPacket> pollMessageIdPackets(Integer lastMessageId, GameEngineMode gameEngineMode) {
        try {
            return serverGlobalConnectionService.pollMessageIdPackets(lastMessageId, gameEngineMode);
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
    public void sellItem(Id id) {
        try {
            planetSystemService.getServerPlanetServices().getItemService().sellItem(id);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public void proposeAlliance(SimpleBase partner) {
        try {
            allianceService.proposeAlliance(partner);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public void acceptAllianceOffer(String partnerUserName) {
        try {
            allianceService.acceptAllianceOffer(partnerUserName);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public void rejectAllianceOffer(String partnerUserName) {
        try {
            allianceService.rejectAllianceOffer(partnerUserName);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public void breakAlliance(String partnerUserName) {
        try {
            allianceService.breakAlliance(partnerUserName);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public Collection<String> getAllAlliances() {
        try {
            return allianceService.getAllAlliances();
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return new ArrayList<>();
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
    public int getRazarion() {
        try {
            return userService.getUserState().getRazarion();
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
}
