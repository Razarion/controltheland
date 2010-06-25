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
import com.btxtech.game.jsre.client.common.GameInfo;
import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.client.common.UserMessage;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.jsre.common.gameengine.services.utg.MissionAction;
import com.btxtech.game.jsre.common.gameengine.services.utg.UserAction;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
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
    public Collection<ItemType> getItemTypes() {
        try {
            return itemService.getItemTypes();
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
    }

    @Override
    public void startUpTaskFinished(StartupTask state, long duration) {
        try {
            userTrackingService.startUpTaskFinished(state, duration);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void startUpTaskFailed(StartupTask state, long duration, String failureText) {
        try {
            userTrackingService.startUpTaskFailed(state, duration, failureText);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void sendUserActions(ArrayList<UserAction> userActions, ArrayList<MissionAction> missionActions) {
        try {
            userTrackingService.saveUserActions(userActions, missionActions);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public GameInfo getGameInfo() {
        try {
            GameInfo gameInfo = new GameInfo();
            gameInfo.setBase(baseService.getBase().getSimpleBase());
            gameInfo.setRegistered(baseService.getBase().getUser() != null);
            gameInfo.setAccountBalance(baseService.getBase().getAccountBalance());
            gameInfo.setAllowedItemTypes(serverMarketService.getAllowedItemTypes());
            gameInfo.setXp(serverMarketService.getXp());
            gameInfo.setEnergyConsuming(serverEnergyService.getConsuming());
            gameInfo.setEnergyGenerating(serverEnergyService.getGenerating());
            gameInfo.setTerrainSettings(terrainService.getTerrainSettings());
            gameInfo.setTerrainImagePositions(terrainService.getTerrainImagePositions());
            gameInfo.setTerrainImages(terrainService.getTerrainImages());
            gameInfo.setSurfaceRects(terrainService.getSurfaceRects());
            gameInfo.setSurfaceImages(terrainService.getSurfaceImages());
            gameInfo.setOnlineBaseUpdate(connectionService.getOnlineBaseUpdate());
            StartupData startupData = mgmtService.getStartupData();
            gameInfo.setTutorialTimeout(startupData.getTutorialTimeout());
            gameInfo.setRegisterDialogDelay(startupData.getRegisterDialogDelay());
            gameInfo.setUserActionCollectionTime(startupData.getUserActionCollectionTime());
            gameInfo.setLevel(userGuidanceService.getLevel4Base());
            gameInfo.setTerritories(territoryService.getTerritories());
            return gameInfo;
        } catch (com.btxtech.game.services.connection.NoConnectionException t) {
            log.error(t.getMessage() + " SessionId: " + t.getSessionId());
        } catch (Throwable t) {
            log.error("", t);
        }
        return null;
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
            userGuidanceService.tutorialTerminated();
        } catch (Throwable t) {
            log.error("", t);
        }
    }
}
