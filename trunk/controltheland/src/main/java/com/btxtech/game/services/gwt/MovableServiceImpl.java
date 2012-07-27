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


import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.client.common.info.InvalidLevelState;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.client.dialogs.highscore.CurrentStatisticEntryInfo;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryInfo;
import com.btxtech.game.jsre.client.dialogs.quest.QuestOverview;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.packets.ChatMessage;
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
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.connection.NoBaseException;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.energy.ServerEnergyService;
import com.btxtech.game.services.inventory.InventoryService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.mgmt.StartupData;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.territory.TerritoryService;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.user.AllianceService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MovableServiceImpl extends AutowiredRemoteServiceServlet implements MovableService {
    @Autowired
    private TerrainService terrainService;
    @Autowired
    private ActionService actionService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private ServerEnergyService serverEnergyService;
    @Autowired
    private UserTrackingService userTrackingService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private UserService userService;
    @Autowired
    private MgmtService mgmtService;
    @Autowired
    private TerritoryService territoryService;
    @Autowired
    private TutorialService tutorialService;
    @Autowired
    private Session session;
    @Autowired
    private CmsUiService cmsUiService;
    @Autowired
    private AllianceService allianceService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private StatisticsService statisticsService;

    private Log log = LogFactory.getLog(MovableServiceImpl.class);

    @Override
    public void sendCommands(List<BaseCommand> baseCommands) {
        try {
            actionService.executeCommands(baseCommands);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public List<Packet> getSyncInfo(String startUuid) throws NoConnectionException {
        try {
            return connectionService.getConnection(startUuid).getAndRemovePendingPackets();
        } catch (NoConnectionException e) {
            throw e;
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
    }

    @Override
    public Collection<SyncItemInfo> getAllSyncInfo() {
        try {
            return itemService.getSyncInfo();
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
    }

    @Override
    public void sendStartupTask(StartupTaskInfo startupTaskInfo, String startUuid, Integer levelTaskId) {
        try {
            userTrackingService.saveStartupTask(startupTaskInfo, startUuid, levelTaskId);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void sendStartupTerminated(boolean successful, long totalTime, String startUuid, Integer levelTaskId) {
        try {
            userTrackingService.saveStartupTerminated(successful, totalTime, startUuid, levelTaskId);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public RealGameInfo getRealGameInfo(String startUuid) throws InvalidLevelState {
        try {
            baseService.continueBase(startUuid);
            RealGameInfo realGameInfo = new RealGameInfo();
            setCommonInfo(realGameInfo, userService, itemService, mgmtService, cmsUiService);
            realGameInfo.setBase(baseService.getBase().getSimpleBase());
            realGameInfo.setAccountBalance(baseService.getBase().getAccountBalance());
            realGameInfo.setEnergyConsuming(serverEnergyService.getConsuming());
            realGameInfo.setEnergyGenerating(serverEnergyService.getGenerating());
            terrainService.setupTerrainRealGame(realGameInfo);
            realGameInfo.setTerritories(territoryService.getTerritories());
            realGameInfo.setAllBases(baseService.getAllBaseAttributes());
            realGameInfo.setHouseSpace(baseService.getBase().getHouseSpace());
            userGuidanceService.fillRealGameInfo(realGameInfo);
            realGameInfo.setAllianceOffers(allianceService.getPendingAllianceOffers());
            return realGameInfo;
        } catch (InvalidLevelState invalidLevelState) {
            throw invalidLevelState;
        } catch (NoBaseException t) {
            log.error(t.getMessage() + ", SessionId: " + t.getSessionId());
        } catch (Throwable t) {
            log.error("", t);
        }
        return null;
    }

    @Override
    public SimulationInfo getSimulationGameInfo(int levelTaskId) throws InvalidLevelState {
        try {
            SimulationInfo simulationInfo = new SimulationInfo();
            DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialConfig(levelTaskId);
            // Common
            setCommonInfo(simulationInfo, userService, itemService, mgmtService, cmsUiService);
            simulationInfo.setTutorialConfig(dbTutorialConfig.getTutorialConfig(itemService));
            simulationInfo.setLevelTaskId(levelTaskId);
            simulationInfo.setLevelTaskTitel(userGuidanceService.getDbLevel().getLevelTaskCrud().readDbChild(levelTaskId).getName());
            simulationInfo.setLevelNumber(userGuidanceService.getDbLevel().getNumber());
            simulationInfo.setSkipAble(userGuidanceService.getDbLevel().getParent().isRealBaseRequired());
            // Terrain
            terrainService.setupTerrainTutorial(simulationInfo, dbTutorialConfig);
            return simulationInfo;
        } catch (InvalidLevelState invalidLevelState) {
            throw invalidLevelState;
        } catch (Throwable t) {
            log.error("", t);
        }
        return null;
    }

    public static void setCommonInfo(GameInfo gameInfo, UserService userService, ItemService itemService, MgmtService mgmtService, CmsUiService cmsUiService) {
        gameInfo.setRegistered(userService.isRegistered());
        gameInfo.setItemTypes(itemService.getItemTypes());
        StartupData startupData = mgmtService.getStartupData();
        gameInfo.setRegisterDialogDelay(startupData.getRegisterDialogDelay());
        gameInfo.setPredefinedUrls(cmsUiService.getPredefinedUrls());
    }

    @Override
    public void log(String message, Date date) {
        try {
            connectionService.clientLog(message, date);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void register(String userName, String password, String confirmPassword, String email) throws UserAlreadyExistsException, PasswordNotMatchException {
        try {
            userService.createUser(userName, password, confirmPassword, email);
            Object o = session.getRequest().getSession().getAttribute("wicket:wicket:" + org.apache.wicket.Session.SESSION_ATTRIBUTE_NAME);
            if (o == null) {
                throw new Exception("Wicket session not found");
            }
            ((AuthenticatedWebSession) o).signIn(userName, password);
        } catch (UserAlreadyExistsException e) {
            throw e;
        } catch (PasswordNotMatchException e) {
            throw e;
        } catch (Throwable t) {
            log.error("", t);
        }

    }

    @Override
    public void sendChatMessage(ChatMessage chatMessage) {
        try {
            connectionService.sendChatMessage(chatMessage);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public List<ChatMessage> pollChatMessages(Integer lastMessageId) {
        try {
            return connectionService.pollChatMessages(lastMessageId);
        } catch (Throwable t) {
            log.error("", t);
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
            log.error("", t);
            return null;
        }
    }

    @Override
    public void sendEventTrackingStart(EventTrackingStart eventTrackingStart) {
        try {
            userTrackingService.onEventTrackingStart(eventTrackingStart);
        } catch (Throwable t) {
            log.error("", t);
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
            log.error("", t);
        }
    }

    @Override
    public void sellItem(Id id) {
        try {
            itemService.sellItem(id);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void proposeAlliance(SimpleBase partner) {
        try {
            allianceService.proposeAlliance(partner);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void acceptAllianceOffer(String partnerUserName) {
        try {
            allianceService.acceptAllianceOffer(partnerUserName);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void rejectAllianceOffer(String partnerUserName) {
        try {
            allianceService.rejectAllianceOffer(partnerUserName);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void breakAlliance(String partnerUserName) {
        try {
            allianceService.breakAlliance(partnerUserName);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public Collection<String> getAllAlliances() {
        try {
            return allianceService.getAllAlliances();
        } catch (Throwable t) {
            log.error("", t);
            return new ArrayList<>();
        }
    }

    @Override
    public InventoryInfo getInventory() {
        try {
            return inventoryService.getInventory();
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
    }

    @Override
    public InventoryInfo assembleInventoryItem(int inventoryItemId) {
        try {
            inventoryService.assembleInventoryItem(inventoryItemId);
            return inventoryService.getInventory();
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
    }

    @Override
    public void useInventoryItem(int inventoryItemId, Collection<Index> positionToBePlaced) {
        try {
            inventoryService.useInventoryItem(inventoryItemId, positionToBePlaced);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public int buyInventoryItem(int inventoryItemId) {
        try {
            return inventoryService.buyInventoryItem(inventoryItemId);
        } catch (Throwable t) {
            log.error("", t);
            return userService.getUserState().getRazarion();
        }
    }

    @Override
    public int buyInventoryArtifact(int inventoryArtifactId) {
        try {
            return inventoryService.buyInventoryArtifact(inventoryArtifactId);
        } catch (Throwable t) {
            log.error("", t);
            return 0;
        }
    }

    @Override
    public int loadRazarion() {
        try {
            return userService.getUserState().getRazarion();
        } catch (Throwable t) {
            log.error("", t);
            return 0;
        }
    }

    @Override
    public QuestOverview loadQuestOverview() {
        try {
            return userGuidanceService.getQuestOverview();
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
    }

    @Override
    public void activateQuest(int questId) {
        try {
            userGuidanceService.activateQuest(questId);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public Collection<CurrentStatisticEntryInfo> loadCurrentStatisticEntryInfos() {
        try {
            return statisticsService.getInGameCurrentStatistics();
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
    }

    @Override
    public void sendPerfmonData(Map<PerfmonEnum, Integer> workTimes, int totalTime) {
        try {
            mgmtService.saveClientPerfmonData(session.getSessionId(), workTimes, totalTime);
        } catch (Throwable t) {
            log.error("", t);
        }
    }
}
