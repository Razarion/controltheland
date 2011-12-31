package com.btxtech.game.mockservlet;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.common.AbstractGwtTest;
import com.btxtech.game.jsre.client.common.Level;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.UserMessage;
import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.MovableType;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
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
import java.util.List;

/**
 * User: beat
 * Date: 04.11.2011
 * Time: 21:38:41
 */
public class MockMovableService extends RemoteServiceServlet implements MovableService {
    @Override
    public GameInfo getGameInfo() {
        SimulationInfo simulationInfo = new SimulationInfo();
        setupCommon(simulationInfo);
        setupSimpleTerrain(simulationInfo);
        setupItemTypes(simulationInfo);
        setupLevel(simulationInfo);
        setupTutorialConfig(simulationInfo);
        return simulationInfo;
    }

    private void setupCommon(SimulationInfo simulationInfo) {
        simulationInfo.setRegisterDialogDelay(1000);
    }

    private void setupTutorialConfig(SimulationInfo simulationInfo) {
        ArrayList<BaseAttributes> baseAttributes = new ArrayList<BaseAttributes>();
        baseAttributes.add(new BaseAttributes(AbstractGwtTest.MY_BASE, "MyTestBase", false));
        baseAttributes.add(new BaseAttributes(AbstractGwtTest.BOT_BASE, "MyBotBase", false));

        simulationInfo.setTutorialConfig(new TutorialConfig(null, AbstractGwtTest.MY_BASE, 0, 0, baseAttributes, false, null, false, false));
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
        BaseItemType baseItemType = new BaseItemType();
        baseItemType.setId(AbstractGwtTest.ITEM_MOVABLE);
        baseItemType.setHealth(10);
        baseItemType.setBoundingBox(new BoundingBox(80, 80, 50, 50, AbstractServiceTest.ANGELS_24));
        baseItemType.setMovableType(new MovableType(100, SurfaceType.LAND));
        return baseItemType;
    }

    private BaseItemType createAttackItem() {
        BaseItemType baseItemType = new BaseItemType();
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

    private void setupLevel(GameInfo gameInfo) {
        gameInfo.setLevel(new Level(0, "TestLevel", "", false, 1000, null, 100));
    }

    @Override
    public void log(String message, Date date) {
        //To change body of implemented methods use File | Settings | File Templates.
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
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendStartupInfo(List<StartupTaskInfo> infos, long totalTime) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Level sendTutorialProgress(TutorialConfig.TYPE type, String name, String parent, long duration, long clientTimeStamp) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
