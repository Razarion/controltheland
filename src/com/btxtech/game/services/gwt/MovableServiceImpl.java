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
import com.btxtech.game.jsre.client.common.GameInfo;
import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.utg.GameStartupState;
import com.btxtech.game.jsre.common.gameengine.services.utg.UserAction;
import com.btxtech.game.jsre.common.gameengine.services.utg.MissionAction;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.energy.ServerEnergyService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.itemTypeAccess.ServerItemTypeAccessService;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
    private ServerItemTypeAccessService serverItemTypeAccessService;
    @Autowired
    private ServerEnergyService serverEnergyService;
    @Autowired
    private UserTrackingService userTrackingService;
    @Autowired
    private UserGuidanceService userGuidanceService;

    private Log log = LogFactory.getLog(MovableServiceImpl.class);

    @Override
    public void sendCommand(BaseCommand baseCommand) {
        try {
            actionService.executeCommand(baseCommand, false);
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
    public int[][] getTerrainField() {
        try {
            return terrainService.getTerrainField();
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
    }

    @Override
    public void gameStartupState(GameStartupState state, Date timeStamp) {
        try {
            userTrackingService.gameStartup(state, timeStamp);
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
            gameInfo.setTerrainField(terrainService.getTerrainField());
            gameInfo.setPassableTerrainTileIds(terrainService.getPassableTerrainTileIds());
            gameInfo.setAccountBalance(baseService.getBase().getAccountBalance());
            gameInfo.setAllowedItemTypes(serverItemTypeAccessService.getAllowedItemTypes());
            gameInfo.setXp(serverItemTypeAccessService.getXp());
            gameInfo.setEnergyConsuming(serverEnergyService.getConsuming());
            gameInfo.setEnergyGenerating(serverEnergyService.getGenerating());
            return gameInfo;
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
    }


    @Override
    public void log(String message) {
        try {
            connectionService.clientLog(message);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void createMissionTraget(Id attacker) {
        try {
            userGuidanceService.createMissionTraget(attacker);
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
}
