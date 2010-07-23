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
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.UserMessage;
import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
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
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoneyCollectCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoveCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.jsre.common.tutorial.GraphicHintConfig;
import com.btxtech.game.jsre.common.tutorial.ItemTypeAndPosition;
import com.btxtech.game.jsre.common.tutorial.Preparation;
import com.btxtech.game.jsre.common.tutorial.StepConfig;
import com.btxtech.game.jsre.common.tutorial.TaskConfig;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.jsre.common.tutorial.condition.HarvestConditionConfig;
import com.btxtech.game.jsre.common.tutorial.condition.ItemBuiltConditionConfig;
import com.btxtech.game.jsre.common.tutorial.condition.ItemsKilledConditionConfig;
import com.btxtech.game.jsre.common.tutorial.condition.ItemsPositionReachedConditionConfig;
import com.btxtech.game.jsre.common.tutorial.condition.SelectionConditionConfig;
import com.btxtech.game.jsre.common.tutorial.condition.SendCommandConditionConfig;
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
import java.util.Arrays;
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
            SimulationInfo simulationInfo = new SimulationInfo();
            // Common
            simulationInfo.setRegistered(baseService.getBase().getUser() != null);
            simulationInfo.setTerrainSettings(new TerrainSettings(20, 10, 100, 100)); // TODO
            Collection<TerrainImagePosition> terrainImagePositions = new ArrayList<TerrainImagePosition>(); // TODO
            simulationInfo.setTerrainImagePositions(terrainImagePositions);
            simulationInfo.setTerrainImages(terrainService.getTerrainImages());
            Collection<SurfaceRect> surfaceRects = new ArrayList<SurfaceRect>(); // TODO
            surfaceRects.add(new SurfaceRect(new Rectangle(0, 0, 2000, 1000), 1));
            simulationInfo.setSurfaceRects(surfaceRects);
            simulationInfo.setSurfaceImages(terrainService.getSurfaceImages());
            simulationInfo.setItemTypes(itemService.getItemTypes());
            StartupData startupData = mgmtService.getStartupData();
            simulationInfo.setRegisterDialogDelay(startupData.getRegisterDialogDelay());
            // Simulation
            SimpleBase simBase = new SimpleBase("Your Base", "#0000FF", false);
            List<TaskConfig> taskConfigs = new ArrayList<TaskConfig>();
            addSingleMoveTask(taskConfigs, simBase);
            addMultiMoveTask(taskConfigs, simBase);
            //addAttackRestartTask(taskConfigs, simBase);
            addAttackTask(taskConfigs, simBase);
            addBuildFactoryTask(taskConfigs, simBase);
            addBuildTankTask(taskConfigs, simBase);
            addEarnMoneyTask(taskConfigs, simBase);
            simulationInfo.setTutorialConfig(new TutorialConfig(taskConfigs, simBase, "Thanks Commander for taking time to do the tutorial"));
            return simulationInfo;
        } catch (com.btxtech.game.services.connection.NoConnectionException t) {
            log.error(t.getMessage() + " SessionId: " + t.getSessionId());
        } catch (Throwable t) {
            log.error("", t);
        }
        return null;
    }

    @Deprecated
    private void addSingleMoveTask(List<TaskConfig> taskConfigs, SimpleBase simBase) {
        Collection<ItemTypeAndPosition> itemTypeAndPositions = new ArrayList<ItemTypeAndPosition>();
        itemTypeAndPositions.add(new ItemTypeAndPosition(simBase, 1, 1, new Index(400, 600)));
        ArrayList<StepConfig> stepConfigs = new ArrayList<StepConfig>();
        stepConfigs.add(new StepConfig(new SelectionConditionConfig(Arrays.asList(1)), new GraphicHintConfig(new Index(400, 600), 1),"Select your CV by clicking on it"));
        stepConfigs.add(new StepConfig(new SendCommandConditionConfig(MoveCommand.class), null, "Move your cursor to the market box and press the left mouse button"));
        taskConfigs.add(new TaskConfig(new Preparation(true, itemTypeAndPositions, false, false, false, new Index(0, 0)),
                stepConfigs,
                new ItemsPositionReachedConditionConfig(Arrays.asList(1), new Rectangle(100, 100, 100, 100)),
                new GraphicHintConfig(new Index(100, 100), 1),
                null,
                0,
                null,
                "Here you will learn how to command your troops"));
    }

    @Deprecated
    private void addMultiMoveTask(List<TaskConfig> taskConfigs, SimpleBase simBase) {
        Collection<ItemTypeAndPosition> itemTypeAndPositions = new ArrayList<ItemTypeAndPosition>();
        itemTypeAndPositions.add(new ItemTypeAndPosition(simBase, 1, 1, new Index(400, 600)));
        itemTypeAndPositions.add(new ItemTypeAndPosition(simBase, 2, 1, new Index(450, 600)));
        itemTypeAndPositions.add(new ItemTypeAndPosition(simBase, 3, 1, new Index(500, 600)));
        ArrayList<StepConfig> stepConfigs = new ArrayList<StepConfig>();
        stepConfigs.add(new StepConfig(new SelectionConditionConfig(Arrays.asList(1, 2, 3)), new GraphicHintConfig(new Index(450, 600), 1),"Stop 1"));
        stepConfigs.add(new StepConfig(new SendCommandConditionConfig(MoveCommand.class), null, "Move your cursor"));
        taskConfigs.add(new TaskConfig(new Preparation(true, itemTypeAndPositions, false, false, false, new Index(0, 0)),
                stepConfigs,
                new ItemsPositionReachedConditionConfig(Arrays.asList(1, 2, 3), new Rectangle(100, 100, 100, 100)),
                new GraphicHintConfig(new Index(100, 100), 1),
                null,
                0,
                null,
                "Task"));
    }

    @Deprecated
    private void addAttackRestartTask(List<TaskConfig> taskConfigs, SimpleBase simBase) {
        Collection<ItemTypeAndPosition> itemTypeAndPositions = new ArrayList<ItemTypeAndPosition>();
        itemTypeAndPositions.add(new ItemTypeAndPosition(simBase, 1, 1, new Index(200, 300)));
        SimpleBase simpleBase = new SimpleBase("Enemy", "#FF0000", false);
        itemTypeAndPositions.add(new ItemTypeAndPosition(simpleBase, 2, 1, new Index(190, 100)));
        itemTypeAndPositions.add(new ItemTypeAndPosition(simpleBase, 3, 1, new Index(193, 100)));
        itemTypeAndPositions.add(new ItemTypeAndPosition(simpleBase, 4, 1, new Index(196, 100)));
        itemTypeAndPositions.add(new ItemTypeAndPosition(simpleBase, 5, 1, new Index(199, 100)));
        itemTypeAndPositions.add(new ItemTypeAndPosition(simpleBase, 6, 1, new Index(202, 100)));
        itemTypeAndPositions.add(new ItemTypeAndPosition(simpleBase, 7, 1, new Index(205, 100)));
        ArrayList<StepConfig> stepConfigs = new ArrayList<StepConfig>();
        stepConfigs.add(new StepConfig(new SelectionConditionConfig(Arrays.asList(1, 2, 3)), null, "Stop 1"));
        stepConfigs.add(new StepConfig(new SendCommandConditionConfig(AttackCommand.class), null, "Stop 2"));
        taskConfigs.add(new TaskConfig(new Preparation(true, itemTypeAndPositions, false, false, false, new Index(0, 0)),
                stepConfigs,
                new ItemsKilledConditionConfig(Arrays.asList(2,3,4,5,6,7)),
                new GraphicHintConfig(new Index(250, 100), 1),
                null,
                0,
                new ItemsKilledConditionConfig(Arrays.asList(1)),
                "Task"));
    }

    @Deprecated
    private void addAttackTask(List<TaskConfig> taskConfigs, SimpleBase simBase) {
        Collection<ItemTypeAndPosition> itemTypeAndPositions = new ArrayList<ItemTypeAndPosition>();
        itemTypeAndPositions.add(new ItemTypeAndPosition(simBase, 1, 1, new Index(200, 300)));
        itemTypeAndPositions.add(new ItemTypeAndPosition(simBase, 2, 1, new Index(250, 300)));
        itemTypeAndPositions.add(new ItemTypeAndPosition(simBase, 3, 1, new Index(300, 300)));
        itemTypeAndPositions.add(new ItemTypeAndPosition(new SimpleBase("Enemy", "#FF0000", false), 4, 1, new Index(250, 100)));
        ArrayList<StepConfig> stepConfigs = new ArrayList<StepConfig>();
        stepConfigs.add(new StepConfig(new SelectionConditionConfig(Arrays.asList(1, 2, 3)), null, "Stop 1"));
        stepConfigs.add(new StepConfig(new SendCommandConditionConfig(AttackCommand.class), null, "Stop 2"));
        taskConfigs.add(new TaskConfig(new Preparation(true, itemTypeAndPositions, false, false, false, new Index(0, 0)),
                stepConfigs,
                new ItemsKilledConditionConfig(Arrays.asList(4)),
                new GraphicHintConfig(new Index(250, 100), 1),
                null,
                0,
                null,
                "Task"));
    }

    @Deprecated
    private void addBuildFactoryTask(List<TaskConfig> taskConfigs, SimpleBase simBase) {
        Collection<ItemTypeAndPosition> itemTypeAndPositions = new ArrayList<ItemTypeAndPosition>();
        itemTypeAndPositions.add(new ItemTypeAndPosition(simBase, 1, 4, new Index(200, 300)));
        ArrayList<StepConfig> stepConfigs = new ArrayList<StepConfig>();
        stepConfigs.add(new StepConfig(new SelectionConditionConfig(Arrays.asList(1)), null, "Stop 1"));
        stepConfigs.add(new StepConfig(new SendCommandConditionConfig(BuilderCommand.class), null, "Stop 2"));
        taskConfigs.add(new TaskConfig(new Preparation(true, itemTypeAndPositions, false, false, true, new Index(0, 0)),
                stepConfigs,
                new ItemBuiltConditionConfig(3),
                new GraphicHintConfig(new Index(250, 100), 1),
                Arrays.asList(3),
                300,
                null,
                "Task"));
    }

    @Deprecated
    private void addBuildTankTask(List<TaskConfig> taskConfigs, SimpleBase simBase) {
        Collection<ItemTypeAndPosition> itemTypeAndPositions = new ArrayList<ItemTypeAndPosition>();
        itemTypeAndPositions.add(new ItemTypeAndPosition(simBase, 1, 3, new Index(200, 300)));
        ArrayList<StepConfig> stepConfigs = new ArrayList<StepConfig>();
        stepConfigs.add(new StepConfig(new SelectionConditionConfig(Arrays.asList(1)), null, "Stop 1"));
        stepConfigs.add(new StepConfig(new SendCommandConditionConfig(BuilderCommand.class), null, "Stop 2"));
        taskConfigs.add(new TaskConfig(new Preparation(true, itemTypeAndPositions, false, false, true, new Index(0, 0)),
                stepConfigs,
                new ItemBuiltConditionConfig(1),
                new GraphicHintConfig(new Index(250, 100), 1),
                Arrays.asList(1),
                300,
                null,
                "Task"));
    }

    @Deprecated
    private void addEarnMoneyTask(List<TaskConfig> taskConfigs, SimpleBase simBase) {
        Collection<ItemTypeAndPosition> itemTypeAndPositions = new ArrayList<ItemTypeAndPosition>();
        itemTypeAndPositions.add(new ItemTypeAndPosition(simBase, 1, 2, new Index(300, 600)));
        itemTypeAndPositions.add(new ItemTypeAndPosition(null, 2, 5, new Index(300, 200)));
        ArrayList<StepConfig> stepConfigs = new ArrayList<StepConfig>();
        stepConfigs.add(new StepConfig(new SelectionConditionConfig(Arrays.asList(1)), null, "Stop 1"));
        stepConfigs.add(new StepConfig(new SendCommandConditionConfig(MoneyCollectCommand.class), null, "Stop 2"));
        taskConfigs.add(new TaskConfig(new Preparation(true, itemTypeAndPositions, false, false, true, new Index(0, 0)),
                stepConfigs,
                new HarvestConditionConfig(10),
                new GraphicHintConfig(new Index(250, 100), 1),
                Arrays.asList(1),
                300,
                null,
                "Task"));
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
}
