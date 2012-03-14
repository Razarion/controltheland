package com.btxtech.game.mockservlet;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.common.AbstractGwtTest;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.RadarMode;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.UserMessage;
import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.MovableType;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.jsre.common.tutorial.GameFlow;
import com.btxtech.game.jsre.common.tutorial.ItemTypeAndPosition;
import com.btxtech.game.jsre.common.tutorial.TaskConfig;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.ItemTypePositionComparisonConfig;
import com.btxtech.game.jsre.common.utg.tracking.BrowserWindowTracking;
import com.btxtech.game.jsre.common.utg.tracking.DialogTracking;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingStart;
import com.btxtech.game.jsre.common.utg.tracking.SelectionTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.TerrainScrollTracking;
import com.btxtech.game.services.AbstractServiceTest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 04.11.2011
 * Time: 21:38:41
 */
public class MockMovableService extends RemoteServiceServlet implements MovableService {
    private BaseItemType movableItemType;

    @Override
    public RealGameInfo getRealGameInfo() {
        return null;
    }

    @Override
    public SimulationInfo getSimulationGameInfo(int levelTaskId) {
        SimulationInfo simulationInfo = new SimulationInfo();
        simulationInfo.setRegisterDialogDelay(1000);
        simulationInfo.setLevelTaskId(1);
        Map<CmsUtil.CmsPredefinedPage, String> predefinedUrls = new HashMap<CmsUtil.CmsPredefinedPage, String>();
        predefinedUrls.put(CmsUtil.CmsPredefinedPage.LEVEL_TASK_DONE, "TestLevelTaskDone");
        simulationInfo.setPredefinedUrls(predefinedUrls);
        setupSimpleTerrain(simulationInfo);
        setupItemTypes(simulationInfo);
        setupTutorialConfig(simulationInfo);
        return simulationInfo;
    }

    private void setupTutorialConfig(SimulationInfo simulationInfo) {
        // Condition
        Map<ItemType, Integer> conditionItemType = new HashMap<ItemType, Integer>();
        conditionItemType.put(movableItemType, 1);
        ConditionConfig conditionConfig1 = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, conditionItemType, new Rectangle(500, 500, 400, 400), null, true));
        ConditionConfig conditionConfig2 = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, conditionItemType, new Rectangle(500, 500, 400, 400), null, true));

        // Setup SimulationInfo
        List<ItemTypeAndPosition> ownItems = new ArrayList<ItemTypeAndPosition>();
        ownItems.add(new ItemTypeAndPosition(movableItemType.getId(), new Index(100, 100), 0));
        Map<Integer, Integer> itemTypeLimitation = new HashMap<Integer, Integer>();
        List<TaskConfig> taskConfigs = new ArrayList<TaskConfig>();
        taskConfigs.add(new TaskConfig(ownItems, null, conditionConfig1, 10, 100, 1000, 0.5, "TestTask1", null, itemTypeLimitation, RadarMode.MAP));
        taskConfigs.add(new TaskConfig(ownItems, null, conditionConfig2, 10, 100, 1000, 0.5, "TestTask2", null, itemTypeLimitation, RadarMode.MAP));
        TutorialConfig tutorialConfig = new TutorialConfig(taskConfigs, "MyTestBase", 500, 500, false, "", false);
        simulationInfo.setTutorialConfig(tutorialConfig);
        simulationInfo.setLevelName("TestLevel");
    }

    protected void setupSimpleTerrain(GameInfo gameInfo) {
        gameInfo.setTerrainSettings(new TerrainSettings(50, 50, 100, 100));
        gameInfo.setTerrainImagePositions(new ArrayList<TerrainImagePosition>());
        Collection<SurfaceRect> surfaceRects = new ArrayList<SurfaceRect>();
        surfaceRects.add(new SurfaceRect(new Rectangle(0, 0, 50, 50), 0));
        gameInfo.setSurfaceRects(surfaceRects);
        Collection<SurfaceImage> surfaceImages = new ArrayList<SurfaceImage>();
        surfaceImages.add(new SurfaceImage(SurfaceType.LAND, 0, ""));
        gameInfo.setSurfaceImages(surfaceImages);
        gameInfo.setTerrainImages(new ArrayList<TerrainImage>());
    }

    protected void setupItemTypes(GameInfo gameInfo) {
        Collection<ItemType> itemTypes = new ArrayList<ItemType>();
        itemTypes.add(createMovableItem());
        itemTypes.add(createAttackItem());
        itemTypes.add(createDefenseTower());
        gameInfo.setItemTypes(itemTypes);
    }

    private BaseItemType createMovableItem() {
        movableItemType = new BaseItemType();
        movableItemType.setId(1);
        movableItemType.setId(AbstractGwtTest.ITEM_MOVABLE);
        movableItemType.setHealth(10);
        movableItemType.setTerrainType(TerrainType.LAND);
        movableItemType.setBoundingBox(new BoundingBox(80, 80, 50, 50, AbstractServiceTest.ANGELS_24));
        movableItemType.setMovableType(new MovableType(100, SurfaceType.LAND));
        return movableItemType;
    }

    private BaseItemType createAttackItem() {
        BaseItemType baseItemType = new BaseItemType();
        baseItemType.setId(2);
        baseItemType.setId(AbstractGwtTest.ITEM_ATTACKER);
        baseItemType.setHealth(10);
        baseItemType.setBoundingBox(new BoundingBox(80, 80, 60, 60, AbstractServiceTest.ANGELS_24));
        baseItemType.setMovableType(new MovableType(100, SurfaceType.LAND));
        Collection<Integer> allowedItemTypes = new ArrayList<Integer>();
        allowedItemTypes.add(AbstractGwtTest.ITEM_ATTACKER);
        allowedItemTypes.add(AbstractGwtTest.ITEM_DEFENSE_TOWER);
        baseItemType.setWeaponType(new WeaponType(100, 1, 0.1, 0, 0, false, allowedItemTypes, null));
        return baseItemType;
    }

    private BaseItemType createDefenseTower() {
        BaseItemType baseItemType = new BaseItemType();
        baseItemType.setId(3);
        baseItemType.setId(AbstractGwtTest.ITEM_DEFENSE_TOWER);
        baseItemType.setHealth(20);
        baseItemType.setBoundingBox(new BoundingBox(100, 100, 80, 80, AbstractServiceTest.ANGELS_24));
        Collection<Integer> allowedItemTypes = new ArrayList<Integer>();
        allowedItemTypes.add(AbstractGwtTest.ITEM_ATTACKER);
        allowedItemTypes.add(AbstractGwtTest.ITEM_DEFENSE_TOWER);
        allowedItemTypes.add(AbstractGwtTest.ITEM_MOVABLE);
        baseItemType.setWeaponType(new WeaponType(100, 1, 0.1, 0, 0, false, allowedItemTypes, null));
        return baseItemType;
    }

    @Override
    public void log(String message, Date date) {
        System.out.println("MockMovableService.log: " + date + " message: " + message);
    }

    @Override
    public void sendCommands(List<BaseCommand> baseCommands) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<Packet> getSyncInfo() throws NoConnectionException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<SyncItemInfo> getAllSyncInfo() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void register(String userName, String password, String confirmPassword, String email) throws UserAlreadyExistsException, PasswordNotMatchException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendUserMessage(UserMessage userMessage) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void surrenderBase() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void closeConnection() {
    }

    @Override
    public void sendStartupTask(StartupTaskInfo startupTaskInfo, String uuid, Integer levelTaskId) {
        System.out.println("MockMovableService.sendStartupTask: startupTaskInfo:" + startupTaskInfo + " uuid: " + uuid + " levelTaskId: " + levelTaskId);
    }

    @Override
    public void sendStartupTerminated(boolean successful, long totalTime, String startUuid, Integer levelTaskId) {
        System.out.println("---sendStartupTerminated Success:" + successful);
    }

    @Override
    public GameFlow sendTutorialProgress(TutorialConfig.TYPE type, String startUuid, int levelTaskId, String name, long duration, long clientTimeStamp) {
        System.out.println("---sendTutorialProgress " + type);
        if (type == TutorialConfig.TYPE.TUTORIAL) {
            return new GameFlow(GameFlow.Type.SHOW_LEVEL_TASK_DONE_PAGE, null);
        } else {
            return null;
        }
    }

    @Override
    public void sendEventTrackingStart(EventTrackingStart eventTrackingStart) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendEventTrackerItems(Collection<EventTrackingItem> eventTrackingItems, Collection<SyncItemInfo> syncItemInfos, Collection<SelectionTrackingItem> selectionTrackingItems, Collection<TerrainScrollTracking> terrainScrollTrackings, Collection<BrowserWindowTracking> browserWindowTrackings, Collection<DialogTracking> dialogTrackings) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sellItem(Id id) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
