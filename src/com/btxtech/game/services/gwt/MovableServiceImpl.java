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
import com.btxtech.game.jsre.client.StartupTask;
import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.UserMessage;
import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.client.common.info.RealityInfo;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.common.EventTrackingItem;
import com.btxtech.game.jsre.common.EventTrackingStart;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.jsre.common.gameengine.services.utg.MissionAction;
import com.btxtech.game.jsre.common.gameengine.services.utg.UserAction;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.energy.ServerEnergyService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.market.ServerMarketService;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.mgmt.StartupData;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.territory.TerritoryService;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("movableService")
public class MovableServiceImpl implements MovableService {
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
    private ServerMarketService serverMarketService;
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
    public Collection<Packet> getSyncInfo(SimpleBase simpleBase) throws NotYourBaseException, NoConnectionException {
        try {
            if (connectionService.getConnection().checkBase(simpleBase)) {
                return connectionService.getConnection().getAndRemovePendingPackets();
            }
        } catch (NoConnectionException e) {
            throw e;
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
        throw new NotYourBaseException();
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
    public void startUpTaskFinished(StartupTask state, Date clientTimeStamp, long duration) {
        try {
            userTrackingService.startUpTaskFinished(state, clientTimeStamp, duration);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void startUpTaskFailed(StartupTask state, Date clientTimeStamp, long duration, String failureText) {
        try {
            userTrackingService.startUpTaskFailed(state, clientTimeStamp, duration, failureText);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    @Deprecated
    public void sendUserActions(ArrayList<UserAction> userActions, ArrayList<MissionAction> missionActions) {
        try {
            userTrackingService.saveUserActions(userActions, missionActions);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void sendTotalStartupTime(long totalStartupTime) {
        try {
            userTrackingService.onTotalStartupTime(totalStartupTime);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void sendCloseWindow(long totalRunningTime) {
        try {
            userTrackingService.onCloseWindow(totalRunningTime);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public GameInfo getGameInfo() {
        if (userGuidanceService.isTutorialRequired()) {
            return createSimulationInfo();
        } else {
            return createRealInfo();
        }
    }

    private GameInfo createRealInfo() {
        try {
            RealityInfo realityInfo = new RealityInfo();
            setCommonInfo(realityInfo);
            realityInfo.setBase(baseService.continueOrCreateBase().getSimpleBase());
            realityInfo.setAccountBalance(baseService.getBase().getAccountBalance());
            realityInfo.setAllowedItemTypes(serverMarketService.getAllowedItemTypes());
            realityInfo.setXp(serverMarketService.getXp());
            realityInfo.setEnergyConsuming(serverEnergyService.getConsuming());
            realityInfo.setEnergyGenerating(serverEnergyService.getGenerating());
            realityInfo.setTerrainSettings(terrainService.getTerrainSettings());
            realityInfo.setTerrainImagePositions(terrainService.getTerrainImagePositions());
            realityInfo.setTerrainImages(terrainService.getTerrainImages());
            realityInfo.setSurfaceRects(terrainService.getSurfaceRects());
            realityInfo.setSurfaceImages(terrainService.getSurfaceImages());
            realityInfo.setOnlineBaseUpdate(connectionService.getOnlineBaseUpdate());
            StartupData startupData = mgmtService.getStartupData();
            realityInfo.setTutorialTimeout(startupData.getTutorialTimeout());
            realityInfo.setUserActionCollectionTime(startupData.getUserActionCollectionTime());
            realityInfo.setLevel(userGuidanceService.getLevel4Base());
            realityInfo.setTerritories(territoryService.getTerritories());
            realityInfo.setLevelToRunMissionTarget(userGuidanceService.getLevelToRunMissionTarget());
            return realityInfo;
        } catch (com.btxtech.game.services.connection.NoConnectionException t) {
            log.error(t.getMessage() + " SessionId: " + t.getSessionId());
        } catch (Throwable t) {
            log.error("", t);
        }
        return null;
    }

    private SimulationInfo createSimulationInfo() {
        try {
            SimulationInfo simulationInfo = new SimulationInfo();
            // Common
            setCommonInfo(simulationInfo);
            simulationInfo.setTutorialConfig(tutorialService.getTutorialConfig());
            // Terrain
            simulationInfo.setTerrainSettings(new TerrainSettings(20, 10, 100, 100));
            Collection<TerrainImagePosition> terrainImagePositions = new ArrayList<TerrainImagePosition>();
            simulationInfo.setTerrainImagePositions(terrainImagePositions);
            simulationInfo.setTerrainImages(terrainService.getTerrainImages());
            Collection<SurfaceRect> surfaceRects = new ArrayList<SurfaceRect>();
            surfaceRects.add(new SurfaceRect(new Rectangle(0, 0, 2000, 1000), 1));
            simulationInfo.setSurfaceRects(surfaceRects);
            simulationInfo.setSurfaceImages(terrainService.getSurfaceImages());
            return simulationInfo;
        } catch (Throwable t) {
            log.error("", t);
        }
        return null;
    }

    private void setCommonInfo(GameInfo gameInfo) {
        gameInfo.setRegistered(userService.isLoggedin());
        gameInfo.setItemTypes(itemService.getItemTypes());
        StartupData startupData = mgmtService.getStartupData();
        gameInfo.setRegisterDialogDelay(startupData.getRegisterDialogDelay());
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
    public void createMissionTraget(Id attacker) {
        try {
            userGuidanceService.createMissionTarget(attacker);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void createMissionMoney(Id harvester) {
        try {
            userGuidanceService.createMissionMoney(harvester);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void register(String userName, String password, String confirmPassword) throws UserAlreadyExistsException, PasswordNotMatchException {
        try {
            userService.createUserAndLoggin(userName, password, confirmPassword);
        } catch (UserAlreadyExistsException e) {
            throw e;
        } catch (PasswordNotMatchException e) {
            throw e;
        } catch (Throwable t) {
            log.error("", t);
        }

    }

    @Override
    public void sendUserMessage(UserMessage userMessage) {
        try {
            connectionService.sendUserMessage(userMessage);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void surrenderBase() {
        try {
            baseService.surrenderBase(baseService.getBaseForLoggedInUser());
            connectionService.closeConnection();
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void closeConnection() {
        try {
            connectionService.closeConnection();
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public String getMissionTarget() {
        try {
            return userGuidanceService.getMissionTarget4NextLevel(baseService.getBase());
        } catch (Throwable t) {
            log.error("", t);
            return t.toString();
        }
    }

    @Override
    public void tutorialTerminated() {
        try {
            userGuidanceService.onTutorialTerminated();
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void sendTutorialProgress(TutorialConfig.TYPE type, String name, String parent, long duration) {
        try {
            userTrackingService.onTutorialProgressChanged(type, name, parent, duration);
        } catch (Throwable t) {
            log.error("", t);
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
    public void sendEventTrackerItems(List<EventTrackingItem> eventTrackingItems) {
        try {
            userTrackingService.onEventTrackerItems(eventTrackingItems);
        } catch (Throwable t) {
            log.error("", t);
        }
    }
}
