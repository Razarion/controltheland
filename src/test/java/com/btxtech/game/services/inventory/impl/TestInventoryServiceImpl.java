package com.btxtech.game.services.inventory.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryArtifactInfo;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryInfo;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.BoxItemType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;
import com.btxtech.game.jsre.common.packets.BoxPickedPacket;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.ServerConnectionServiceTestHelper;
import com.btxtech.game.services.common.ServerGlobalServices;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.common.impl.ServerGlobalServicesImpl;
import com.btxtech.game.services.history.DbHistoryElement;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.inventory.DbInventoryArtifact;
import com.btxtech.game.services.inventory.DbInventoryArtifactCount;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.inventory.GlobalInventoryService;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemTypePossibility;
import com.btxtech.game.services.mgmt.BackupService;
import com.btxtech.game.services.mgmt.BackupSummary;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.BaseService;
import com.btxtech.game.services.planet.CollisionService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.ServerItemService;
import com.btxtech.game.services.planet.ServerTerrainService;
import com.btxtech.game.services.planet.db.DbBoxRegion;
import com.btxtech.game.services.planet.db.DbBoxRegionCount;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.planet.impl.InventoryServiceImpl;
import com.btxtech.game.services.planet.impl.ServerPlanetServicesImpl;
import com.btxtech.game.services.terrain.DbRegion;
import com.btxtech.game.services.terrain.RegionService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 16.05.12
 * Time: 13:45
 */
public class TestInventoryServiceImpl extends AbstractServiceTest {
    @Autowired
    private GlobalInventoryService globalInventoryService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private BackupService backupService;
    @Autowired
    private UserService userService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private ServerGlobalServices serverGlobalServices;
    @Autowired
    private RegionService regionService;
    private Log log = LogFactory.getLog(TestInventoryServiceImpl.class);

    @Test
    @DirtiesContext
    public void testDbBoxRegionReal() throws Exception {
        log.error("----------start testDbBoxRegionReal----------");
        setPrivateStaticField(InventoryServiceImpl.class, "SCHEDULE_RATE", 10);
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createDbBoxItemType1();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID);
        DbBoxRegion dbBoxRegion = dbPlanet.getBoxRegionCrud().createDbChild();
        dbBoxRegion.setItemFreeRange(100);
        dbBoxRegion.setMinInterval(200);
        dbBoxRegion.setMaxInterval(200);
        dbBoxRegion.setName("DbBoxRegion1");
        dbBoxRegion.setRegion(createDbRegion(new Rectangle(100, 100, 1000, 800)));
        DbBoxRegionCount dbBoxRegionCount = dbBoxRegion.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount.setDbBoxItemType(serverItemTypeService.getDbBoxItemType(TEST_BOX_ITEM_1_ID));
        dbBoxRegionCount.setCount(1);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.deactivatePlanet(TEST_PLANET_1_ID);
        planetSystemService.activatePlanet(TEST_PLANET_1_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Thread.sleep(1000);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbHistoryElement> dbHistoryElements = loadAll(DbHistoryElement.class);
        Assert.assertEquals(DbHistoryElement.Type.BOX_DROPPED, dbHistoryElements.get(0).getType());
        Assert.assertEquals(DbHistoryElement.Type.BOX_EXPIRED, dbHistoryElements.get(1).getType());
        Assert.assertEquals(DbHistoryElement.Type.BOX_DROPPED, dbHistoryElements.get(2).getType());
        Assert.assertEquals(DbHistoryElement.Type.BOX_EXPIRED, dbHistoryElements.get(3).getType());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }

    @Test
    @DirtiesContext
    public void testDbBoxRegionMock() throws Exception {
        setPrivateStaticField(InventoryServiceImpl.class, "SCHEDULE_RATE", 25);
        configureSimplePlanetNoResources();
        ServerPlanetServicesImpl serverPlanetServices = (ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createDbBoxItemType1();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        BoxItemType boxItemType = (BoxItemType) serverItemTypeService.getItemType(TEST_BOX_ITEM_1_ID);
        // SyncBoxItems
        SyncBoxItem mockSyncBoxItem1 = EasyMock.createStrictMock(SyncBoxItem.class);
        EasyMock.expect(mockSyncBoxItem1.isInTTL()).andReturn(true);
        EasyMock.expect(mockSyncBoxItem1.isInTTL()).andReturn(false);
        SyncBoxItem mockSyncBoxItem2 = EasyMock.createStrictMock(SyncBoxItem.class);
        EasyMock.expect(mockSyncBoxItem2.isInTTL()).andReturn(true);
        EasyMock.expect(mockSyncBoxItem2.isInTTL()).andReturn(true);
        EasyMock.expect(mockSyncBoxItem2.isInTTL()).andReturn(false);
        SyncBoxItem mockSyncBoxItem3 = EasyMock.createStrictMock(SyncBoxItem.class);
        EasyMock.expect(mockSyncBoxItem3.isInTTL()).andReturn(true);
        EasyMock.expect(mockSyncBoxItem3.isInTTL()).andReturn(false);
        // History Service
        HistoryService mockHistoryService = EasyMock.createStrictMock(HistoryService.class);
        mockHistoryService.addBoxDropped(EasyMock.eq(mockSyncBoxItem1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull());
        mockHistoryService.addBoxExpired(EasyMock.eq(mockSyncBoxItem1));
        mockHistoryService.addBoxDropped(EasyMock.eq(mockSyncBoxItem2), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull());
        mockHistoryService.addBoxExpired(EasyMock.eq(mockSyncBoxItem2));
        mockHistoryService.addBoxDropped(EasyMock.eq(mockSyncBoxItem3), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull());
        mockHistoryService.addBoxExpired(EasyMock.eq(mockSyncBoxItem3));
        setPrivateField(ServerGlobalServicesImpl.class, serverGlobalServices, "historyService", mockHistoryService);
        // ServerItemService
        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        EasyMock.expect(mockServerItemService.createSyncObject(EasyMock.eq(boxItemType), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull())).andReturn(mockSyncBoxItem1);
        mockServerItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem1), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));
        EasyMock.expect(mockServerItemService.createSyncObject(EasyMock.eq(boxItemType), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull())).andReturn(mockSyncBoxItem2);
        mockServerItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem2), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));
        EasyMock.expect(mockServerItemService.createSyncObject(EasyMock.eq(boxItemType), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull())).andReturn(mockSyncBoxItem3);
        mockServerItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem3), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));
        serverPlanetServices.setServerItemService(mockServerItemService);

        CollisionService mockCollisionService = EasyMock.createStrictMock(CollisionService.class);
        DbRegion dbRegion1 = createDbRegion(new Rectangle(100, 100, 1000, 800));
        Region region1 = dbRegion1.createRegion();
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(boxItemType, region1, 100, true, false)).andReturn(new Index(100, 100));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(boxItemType, region1, 100, true, false)).andReturn(new Index(100, 100));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(boxItemType, region1, 100, true, false)).andReturn(new Index(100, 100));
        serverPlanetServices.setCollisionService(mockCollisionService);

        EasyMock.replay(mockHistoryService, mockServerItemService, mockSyncBoxItem1, mockSyncBoxItem2, mockSyncBoxItem3, mockCollisionService);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID);
        DbBoxRegion dbBoxRegion = dbPlanet.getBoxRegionCrud().createDbChild();
        dbBoxRegion.setItemFreeRange(100);
        dbBoxRegion.setMinInterval(200);
        dbBoxRegion.setMaxInterval(200);
        dbBoxRegion.setName("DbBoxRegion1");
        dbBoxRegion.setRegion(regionService.getRegionCrud().readDbChild(dbRegion1.getId()));
        DbBoxRegionCount dbBoxRegionCount = dbBoxRegion.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount.setDbBoxItemType(serverItemTypeService.getDbBoxItemType(TEST_BOX_ITEM_1_ID));
        dbBoxRegionCount.setCount(1);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.deactivatePlanet(TEST_PLANET_1_ID);
        planetSystemService.activatePlanet(TEST_PLANET_1_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Thread.sleep(790);
        EasyMock.verify(mockHistoryService, mockServerItemService, mockSyncBoxItem1, mockSyncBoxItem2, mockSyncBoxItem3);
    }

    @Test
    @DirtiesContext
    public void testDbBoxRegionMockMultipleRegions() throws Exception {
        setPrivateStaticField(InventoryServiceImpl.class, "SCHEDULE_RATE", 25);
        configureSimplePlanetNoResources();
        ServerPlanetServicesImpl serverPlanetServices = (ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createDbBoxItemType1();
        createDbBoxItemType2();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // SyncBoxItems
        SyncBoxItem mockSyncBoxItem1 = EasyMock.createStrictMock(SyncBoxItem.class);
        EasyMock.expect(mockSyncBoxItem1.isInTTL()).andReturn(true);
        EasyMock.expect(mockSyncBoxItem1.isInTTL()).andReturn(false);
        SyncBoxItem mockSyncBoxItem2 = EasyMock.createStrictMock(SyncBoxItem.class);
        EasyMock.expect(mockSyncBoxItem2.isInTTL()).andReturn(true);
        EasyMock.expect(mockSyncBoxItem2.isInTTL()).andReturn(false);
        SyncBoxItem mockSyncBoxItem3 = EasyMock.createStrictMock(SyncBoxItem.class);
        EasyMock.expect(mockSyncBoxItem3.isInTTL()).andReturn(true);
        EasyMock.expect(mockSyncBoxItem3.isInTTL()).andReturn(false);
        SyncBoxItem mockSyncBoxItem4 = EasyMock.createStrictMock(SyncBoxItem.class);
        EasyMock.expect(mockSyncBoxItem4.isInTTL()).andReturn(true);
        EasyMock.expect(mockSyncBoxItem4.isInTTL()).andReturn(false);
        SyncBoxItem mockSyncBoxItem5 = EasyMock.createStrictMock(SyncBoxItem.class);
        EasyMock.expect(mockSyncBoxItem5.isInTTL()).andReturn(true);
        EasyMock.expect(mockSyncBoxItem5.isInTTL()).andReturn(false);
        SyncBoxItem mockSyncBoxItem6 = EasyMock.createStrictMock(SyncBoxItem.class);
        EasyMock.expect(mockSyncBoxItem6.isInTTL()).andReturn(true);
        EasyMock.expect(mockSyncBoxItem6.isInTTL()).andReturn(false);
        SyncBoxItem mockSyncBoxItem7 = EasyMock.createStrictMock(SyncBoxItem.class);
        EasyMock.expect(mockSyncBoxItem7.isInTTL()).andReturn(true);
        EasyMock.expect(mockSyncBoxItem7.isInTTL()).andReturn(false);
        // History Service
        HistoryService mockHistoryService = EasyMock.createNiceMock(HistoryService.class);
        // ServerItemService
        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        BoxItemType boxItemType1 = (BoxItemType) serverItemTypeService.getItemType(TEST_BOX_ITEM_1_ID);
        BoxItemType boxItemType2 = (BoxItemType) serverItemTypeService.getItemType(TEST_BOX_ITEM_2_ID);

        EasyMock.expect(mockServerItemService.createSyncObject(EasyMock.eq(boxItemType1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull())).andReturn(mockSyncBoxItem1);
        EasyMock.expect(mockServerItemService.createSyncObject(EasyMock.eq(boxItemType1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull())).andReturn(mockSyncBoxItem2);
        mockServerItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem1), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));
        mockServerItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem2), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));

        EasyMock.expect(mockServerItemService.createSyncObject(EasyMock.eq(boxItemType1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull())).andReturn(mockSyncBoxItem3);
        EasyMock.expect(mockServerItemService.createSyncObject(EasyMock.eq(boxItemType1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull())).andReturn(mockSyncBoxItem4);
        mockServerItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem3), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));
        mockServerItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem4), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));

        EasyMock.expect(mockServerItemService.createSyncObject(EasyMock.eq(boxItemType2), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull())).andReturn(mockSyncBoxItem7);
        mockServerItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem7), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));

        EasyMock.expect(mockServerItemService.createSyncObject(EasyMock.eq(boxItemType1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull())).andReturn(mockSyncBoxItem5);
        EasyMock.expect(mockServerItemService.createSyncObject(EasyMock.eq(boxItemType1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull())).andReturn(mockSyncBoxItem6);
        mockServerItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem5), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));
        mockServerItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem6), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));

        CollisionService mockCollisionService = EasyMock.createStrictMock(CollisionService.class);
        DbRegion dbRegion1 = createDbRegion(new Rectangle(100, 100, 1000, 800));
        DbRegion dbRegion2 = createDbRegion(new Rectangle(100, 100, 1000, 800));
        Region region1 = dbRegion1.createRegion();
        Region region2 = dbRegion2.createRegion();
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(boxItemType1, region1, 100, true, false)).andReturn(new Index(100, 100));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(boxItemType1, region1, 100, true, false)).andReturn(new Index(100, 100));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(boxItemType1, region1, 100, true, false)).andReturn(new Index(100, 100));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(boxItemType1, region1, 100, true, false)).andReturn(new Index(100, 100));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(boxItemType2, region2, 100, true, false)).andReturn(new Index(100, 100));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(boxItemType1, region1, 100, true, false)).andReturn(new Index(100, 100));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(boxItemType1, region1, 100, true, false)).andReturn(new Index(100, 100));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(boxItemType1, region1, 100, true, false)).andReturn(new Index(100, 100));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(boxItemType2, region2, 100, true, false)).andReturn(new Index(100, 100));
        serverPlanetServices.setCollisionService(mockCollisionService);

        EasyMock.replay(mockHistoryService, mockServerItemService, mockCollisionService, mockSyncBoxItem1, mockSyncBoxItem2, mockSyncBoxItem3, mockSyncBoxItem4, mockSyncBoxItem5, mockSyncBoxItem6, mockSyncBoxItem7);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        setPrivateField(ServerGlobalServicesImpl.class, serverGlobalServices, "historyService", mockHistoryService);
        serverPlanetServices.setServerItemService(mockServerItemService);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID);
        DbBoxRegion dbBoxRegion1 = dbPlanet.getBoxRegionCrud().createDbChild();
        dbBoxRegion1.setItemFreeRange(100);
        dbBoxRegion1.setMinInterval(200);
        dbBoxRegion1.setMaxInterval(200);
        dbBoxRegion1.setName("DbBoxRegion1");
        dbBoxRegion1.setRegion(regionService.getRegionCrud().readDbChild(dbRegion1.getId()));
        DbBoxRegionCount dbBoxRegionCount1 = dbBoxRegion1.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount1.setDbBoxItemType(serverItemTypeService.getDbBoxItemType(TEST_BOX_ITEM_1_ID));
        dbBoxRegionCount1.setCount(2);

        DbBoxRegion dbBoxRegion2 = dbPlanet.getBoxRegionCrud().createDbChild();
        dbBoxRegion2.setItemFreeRange(100);
        dbBoxRegion2.setMinInterval(500);
        dbBoxRegion2.setMaxInterval(500);
        dbBoxRegion2.setName("DbBoxRegion2");
        dbBoxRegion2.setRegion(regionService.getRegionCrud().readDbChild(dbRegion2.getId()));
        DbBoxRegionCount dbBoxRegionCount = dbBoxRegion2.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount.setDbBoxItemType(serverItemTypeService.getDbBoxItemType(TEST_BOX_ITEM_2_ID));
        dbBoxRegionCount.setCount(1);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.deactivatePlanet(TEST_PLANET_1_ID);
        planetSystemService.activatePlanet(TEST_PLANET_1_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Thread.sleep(790);

        EasyMock.verify(mockHistoryService, mockServerItemService, mockSyncBoxItem1, mockSyncBoxItem2, mockSyncBoxItem3, mockSyncBoxItem4, mockSyncBoxItem5, mockSyncBoxItem6, mockSyncBoxItem7);
    }

    @Test
    @DirtiesContext
    public void testDbBoxRegionMockLongRunningBox() throws Exception {
        setPrivateStaticField(InventoryServiceImpl.class, "SCHEDULE_RATE", 25);
        configureSimplePlanetNoResources();
        ServerPlanetServicesImpl serverPlanetServices = (ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createDbBoxItemType1();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // SyncBoxItems
        SyncBoxItem mockSyncBoxItem1 = EasyMock.createStrictMock(SyncBoxItem.class);
        EasyMock.expect(mockSyncBoxItem1.isInTTL()).andReturn(true).anyTimes();
        SyncBoxItem mockSyncBoxItem2 = EasyMock.createStrictMock(SyncBoxItem.class);
        EasyMock.expect(mockSyncBoxItem2.isInTTL()).andReturn(true).times(12);
        EasyMock.expect(mockSyncBoxItem2.isInTTL()).andReturn(false);
        SyncBoxItem mockSyncBoxItem3 = EasyMock.createStrictMock(SyncBoxItem.class);
        EasyMock.expect(mockSyncBoxItem3.isInTTL()).andReturn(false);
        // History Service
        HistoryService mockHistoryService = EasyMock.createNiceMock(HistoryService.class);
        // ServerItemService
        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        BoxItemType boxItemType1 = (BoxItemType) serverItemTypeService.getItemType(TEST_BOX_ITEM_1_ID);
        EasyMock.expect(mockServerItemService.createSyncObject(EasyMock.eq(boxItemType1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull())).andReturn(mockSyncBoxItem1);
        EasyMock.expect(mockServerItemService.createSyncObject(EasyMock.eq(boxItemType1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull())).andReturn(mockSyncBoxItem2);
        EasyMock.expect(mockServerItemService.createSyncObject(EasyMock.eq(boxItemType1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull())).andReturn(mockSyncBoxItem3);
        mockServerItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem3), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));
        mockServerItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem2), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));

        CollisionService mockCollisionService = EasyMock.createStrictMock(CollisionService.class);
        DbRegion dbRegion1 = createDbRegion(new Rectangle(100, 100, 1000, 800));
        Region region1 = dbRegion1.createRegion();
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(boxItemType1, region1, 100, true, false)).andReturn(new Index(100, 100));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(boxItemType1, region1, 100, true, false)).andReturn(new Index(100, 100));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(boxItemType1, region1, 100, true, false)).andReturn(new Index(100, 100));
        serverPlanetServices.setCollisionService(mockCollisionService);

        EasyMock.replay(mockHistoryService, mockServerItemService, mockCollisionService, mockSyncBoxItem1, mockSyncBoxItem2, mockSyncBoxItem3);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        setPrivateField(ServerGlobalServicesImpl.class, serverGlobalServices, "historyService", mockHistoryService);
        serverPlanetServices.setServerItemService(mockServerItemService);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID);
        DbBoxRegion dbBoxRegion1 = dbPlanet.getBoxRegionCrud().createDbChild();
        dbBoxRegion1.setItemFreeRange(100);
        dbBoxRegion1.setMinInterval(200);
        dbBoxRegion1.setMaxInterval(200);
        dbBoxRegion1.setName("DbBoxRegion1");
        dbBoxRegion1.setRegion(regionService.getRegionCrud().readDbChild(dbRegion1.getId()));
        DbBoxRegionCount dbBoxRegionCount1 = dbBoxRegion1.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount1.setDbBoxItemType(serverItemTypeService.getDbBoxItemType(TEST_BOX_ITEM_1_ID));
        dbBoxRegionCount1.setCount(1);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.deactivatePlanet(TEST_PLANET_1_ID);
        planetSystemService.activatePlanet(TEST_PLANET_1_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Thread.sleep(790);

        EasyMock.verify(mockHistoryService, mockServerItemService, mockCollisionService, mockSyncBoxItem1, mockSyncBoxItem2, mockSyncBoxItem3);
    }

    @Test
    @DirtiesContext
    public void testPickupReal() throws Exception {
        setPrivateStaticField(InventoryServiceImpl.class, "SCHEDULE_RATE", 25);
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createDbBoxItemType2();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertWholeItemCount(TEST_PLANET_1_ID, 0);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID);
        DbBoxRegion dbBoxRegion = dbPlanet.getBoxRegionCrud().createDbChild();
        dbBoxRegion.setItemFreeRange(100);
        dbBoxRegion.setMinInterval(200);
        dbBoxRegion.setMaxInterval(200);
        dbBoxRegion.setName("DbBoxRegion1");
        dbBoxRegion.setRegion(createDbRegion(new Rectangle(100, 100, 1000, 800)));
        DbBoxRegionCount dbBoxRegionCount = dbBoxRegion.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount.setDbBoxItemType(serverItemTypeService.getDbBoxItemType(TEST_BOX_ITEM_2_ID));
        dbBoxRegionCount.setCount(1);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.deactivatePlanet(TEST_PLANET_1_ID);
        planetSystemService.activatePlanet(TEST_PLANET_1_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        waitForHistoryType(DbHistoryElement.Type.BOX_DROPPED);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);
        List<SyncItem> syncItems = serverPlanetServices.getItemService().getItemsCopy();
        SyncBoxItem boxItem = (SyncBoxItem) syncItems.get(0);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertNoHistoryType(DbHistoryElement.Type.BOX_PICKED);
        sendPickupBoxCommand(TEST_PLANET_1_ID, getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), boxItem.getId());
        waitForActionServiceDone();
        assertHistoryType(DbHistoryElement.Type.BOX_PICKED);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testPickupMock() throws Exception {
        setPrivateStaticField(InventoryServiceImpl.class, "SCHEDULE_RATE", 25);
        configureSimplePlanetNoResources();
        ServerPlanetServicesImpl serverPlanetServices = (ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createDbBoxItemType1();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        SimpleBase simpleBase = new SimpleBase(1, 1);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        BoxItemType boxItemType1 = (BoxItemType) serverItemTypeService.getItemType(TEST_BOX_ITEM_1_ID);
        DbBoxItemType dbBoxItemTypeMock = new DbBoxItemType();
        setupDbItemTypeId(dbBoxItemTypeMock, TEST_BOX_ITEM_1_ID);
        // Picker
        SyncBaseItem mockPicker = EasyMock.createStrictMock(SyncBaseItem.class);
        EasyMock.expect(mockPicker.getBase()).andReturn(simpleBase).times(6);
        // SyncBoxItems
        SyncBoxItem mockSyncBoxItem1 = EasyMock.createStrictMock(SyncBoxItem.class);
        EasyMock.expect(mockSyncBoxItem1.isInTTL()).andReturn(true).anyTimes();
        EasyMock.expect(mockSyncBoxItem1.getItemType()).andReturn(boxItemType1);
        // History Service
        HistoryService mockHistoryService = EasyMock.createStrictMock(HistoryService.class);
        mockHistoryService.addBoxDropped(EasyMock.eq(mockSyncBoxItem1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull());
        mockHistoryService.addBoxPicked(EasyMock.eq(mockSyncBoxItem1), EasyMock.eq(mockPicker));
        // UserState
        UserState mockUserState = EasyMock.createStrictMock(UserState.class);
        // BaseService
        BaseService mockBaseService = EasyMock.createStrictMock(BaseService.class);
        EasyMock.expect(mockBaseService.isAbandoned(simpleBase)).andReturn(false);
        EasyMock.expect(mockBaseService.getUserState(simpleBase)).andReturn(mockUserState);
        // ServerItemService
        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        EasyMock.expect(mockServerItemService.createSyncObject(EasyMock.eq(boxItemType1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull())).andReturn(mockSyncBoxItem1);
        mockServerItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem1), EasyMock.eq(simpleBase), EasyMock.eq(true), EasyMock.eq(false));

        CollisionService mockCollisionService = EasyMock.createStrictMock(CollisionService.class);
        DbRegion dbRegion1 = createDbRegion(new Rectangle(100, 100, 1000, 800));
        Region region1 = dbRegion1.createRegion();
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(boxItemType1, region1, 100, true, false)).andReturn(new Index(100, 100));
        serverPlanetServices.setCollisionService(mockCollisionService);

        EasyMock.replay(mockHistoryService, mockServerItemService, mockSyncBoxItem1, mockPicker, mockBaseService, mockUserState, mockCollisionService);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        serverPlanetServices.setServerItemService(mockServerItemService);
        serverPlanetServices.setBaseService(mockBaseService);
        setPrivateField(GlobalInventoryServiceImpl.class, globalInventoryService, "historyService", mockHistoryService);
        setPrivateField(ServerGlobalServicesImpl.class, serverGlobalServices, "historyService", mockHistoryService);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID);
        DbBoxRegion dbBoxRegion1 = dbPlanet.getBoxRegionCrud().createDbChild();
        dbBoxRegion1.setItemFreeRange(100);
        dbBoxRegion1.setMinInterval(200);
        dbBoxRegion1.setMaxInterval(200);
        dbBoxRegion1.setName("DbBoxRegion1");
        dbBoxRegion1.setRegion(regionService.getRegionCrud().readDbChild(dbRegion1.getId()));
        DbBoxRegionCount dbBoxRegionCount1 = dbBoxRegion1.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount1.setDbBoxItemType(serverItemTypeService.getDbBoxItemType(TEST_BOX_ITEM_1_ID));
        dbBoxRegionCount1.setCount(1);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.deactivatePlanet(TEST_PLANET_1_ID);
        planetSystemService.activatePlanet(TEST_PLANET_1_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Thread.sleep(300);
        serverPlanetServices.getInventoryService().onSyncBoxItemPicked(mockSyncBoxItem1, mockPicker);

        EasyMock.verify(mockHistoryService, mockServerItemService, mockSyncBoxItem1, mockPicker, mockBaseService, mockUserState, mockCollisionService);
    }

    @Test
    @DirtiesContext
    public void testDropBoxOnKillReal() throws Exception {
        setPrivateStaticField(InventoryServiceImpl.class, "SCHEDULE_RATE", 25);
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createDbBoxItemType1();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType startupItem = serverItemTypeService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID);
        startupItem.setDropBoxPossibility(1.0);
        startupItem.setDbBoxItemType(serverItemTypeService.getDbBoxItemType(TEST_BOX_ITEM_1_ID));
        serverItemTypeService.saveDbItemType(startupItem);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Target item
        Id targetId = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createBase(new Index(4000, 4000));
        // Actor item
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(5000, 5000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        assertNoHistoryType(DbHistoryElement.Type.BOX_DROPPED);
        waitForActionServiceDone();
        sendAttackCommand(getFirstSynItemId(TEST_ATTACK_ITEM_ID), targetId);
        waitForHistoryType(DbHistoryElement.Type.BOX_DROPPED);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Killed without actor
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Target item
        createBase(new Index(1000, 5000));
        targetId = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, getAllHistoryEntriesOfType(DbHistoryElement.Type.BOX_DROPPED).size());
        ServerItemService serverItemService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getItemService();
        serverItemService.killSyncItem(serverItemService.getItem(targetId), null, true, false);
        Assert.assertEquals(1, getAllHistoryEntriesOfType(DbHistoryElement.Type.BOX_DROPPED).size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }

    @Test
    @DirtiesContext
    public void testDropBoxOnKillMock() throws Exception {
        setPrivateStaticField(InventoryServiceImpl.class, "SCHEDULE_RATE", 25);
        configureSimplePlanetNoResources();
        ServerPlanetServicesImpl serverPlanetServices = (ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createDbBoxItemType1();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBoxItemType dbBoxItemType1 = serverItemTypeService.getDbBoxItemType(TEST_BOX_ITEM_1_ID);

        // SyncBoxItem
        SyncBoxItem mockSyncBoxItem1 = EasyMock.createStrictMock(SyncBoxItem.class);
        EasyMock.expect(mockSyncBoxItem1.isInTTL()).andReturn(true).anyTimes();
        // Dropper
        DbBaseItemType dbDropperType = EasyMock.createStrictMock(DbBaseItemType.class);
        EasyMock.expect(dbDropperType.getDbBoxItemType()).andReturn(dbBoxItemType1).times(2);
        BaseItemType dropperType = EasyMock.createStrictMock(BaseItemType.class);
        EasyMock.expect(dropperType.getId()).andReturn(1);
        SyncBaseItem mockDropper = EasyMock.createStrictMock(SyncBaseItem.class);
        EasyMock.expect(mockDropper.getDropBoxPossibility()).andReturn(1.0);
        EasyMock.expect(mockDropper.getBaseItemType()).andReturn(dropperType);
        SyncItemArea dropperArea = new SyncItemArea(new BoundingBox(10, new double[]{}), new Index(100, 100));
        EasyMock.expect(mockDropper.getSyncItemArea()).andReturn(dropperArea);
        // History Service
        HistoryService mockHistoryService = EasyMock.createStrictMock(HistoryService.class);
        mockHistoryService.addBoxDropped(mockSyncBoxItem1, new Index(100, 100), mockDropper);
        // ServerItemService
        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        BoxItemType boxItemType1 = (BoxItemType) serverItemTypeService.getItemType(TEST_BOX_ITEM_1_ID);
        EasyMock.expect(mockServerItemService.createSyncObject(boxItemType1, new Index(100, 100), null, null)).andReturn(mockSyncBoxItem1);
        // ServerItemTypeService
        ServerItemTypeService mockServerItemTypeService = EasyMock.createStrictMock(ServerItemTypeService.class);
        EasyMock.expect(mockServerItemTypeService.getDbBaseItemType(1)).andReturn(dbDropperType);
        EasyMock.expect(mockServerItemTypeService.getItemType(dbBoxItemType1)).andReturn(boxItemType1);

        EasyMock.replay(mockHistoryService, mockServerItemTypeService, mockServerItemService, mockSyncBoxItem1, mockDropper, dropperType, dbDropperType);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        serverPlanetServices.setServerItemService(mockServerItemService);
        setPrivateField(ServerGlobalServicesImpl.class, serverGlobalServices, "historyService", mockHistoryService);
        setPrivateField(GlobalInventoryServiceImpl.class, globalInventoryService, "serverItemTypeService", mockServerItemTypeService);

        serverPlanetServices.getInventoryService().onSyncBaseItemKilled(mockDropper);
        EasyMock.verify(mockHistoryService, mockServerItemTypeService, mockServerItemService, mockSyncBoxItem1, mockDropper, dropperType, dbDropperType);
    }

    @Test
    @DirtiesContext
    public void backupRestoreSyncBoxItems() throws Exception {
        setPrivateStaticField(InventoryServiceImpl.class, "SCHEDULE_RATE", 25);
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createDbBoxItemType2();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertWholeItemCount(TEST_PLANET_1_ID, 0);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID);
        DbBoxRegion dbBoxRegion = dbPlanet.getBoxRegionCrud().createDbChild();
        dbBoxRegion.setItemFreeRange(100);
        dbBoxRegion.setMinInterval(500);
        dbBoxRegion.setMaxInterval(500);
        dbBoxRegion.setName("DbBoxRegion1");
        dbBoxRegion.setRegion(createDbRegion(new Rectangle(100, 100, 1000, 800)));
        DbBoxRegionCount dbBoxRegionCount = dbBoxRegion.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount.setDbBoxItemType(serverItemTypeService.getDbBoxItemType(TEST_BOX_ITEM_2_ID));
        dbBoxRegionCount.setCount(1);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.deactivatePlanet(TEST_PLANET_1_ID);
        planetSystemService.activatePlanet(TEST_PLANET_1_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        waitForHistoryType(DbHistoryElement.Type.BOX_DROPPED);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = backupService.getBackupSummary();
        assertBackupSummery(1, 0, 0, 0);
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertWholeItemCount(TEST_PLANET_1_ID, 0);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        waitForHistoryType(DbHistoryElement.Type.BOX_DROPPED);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void backupRestoreSyncBoxItemsAsTarget() throws Exception {
        setPrivateStaticField(InventoryServiceImpl.class, "SCHEDULE_RATE", 25);
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createDbBoxItemType2();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertWholeItemCount(TEST_PLANET_1_ID, 0);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID);
        DbBoxRegion dbBoxRegion = dbPlanet.getBoxRegionCrud().createDbChild();
        dbBoxRegion.setItemFreeRange(100);
        dbBoxRegion.setMinInterval(200);
        dbBoxRegion.setMaxInterval(200);
        dbBoxRegion.setName("DbBoxRegion1");
        dbBoxRegion.setRegion(createDbRegion(new Rectangle(100, 100, 100, 100)));
        DbBoxRegionCount dbBoxRegionCount = dbBoxRegion.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount.setDbBoxItemType(serverItemTypeService.getDbBoxItemType(TEST_BOX_ITEM_2_ID));
        dbBoxRegionCount.setCount(1);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.deactivatePlanet(TEST_PLANET_1_ID);
        planetSystemService.activatePlanet(TEST_PLANET_1_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        Id target = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        sendMoveCommand(target, new Index(5000, 5000));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Thread.sleep(250);
        List<SyncItem> syncItems = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getItemService().getItemsCopy();
        Assert.assertEquals(2, syncItems.size());
        SyncBoxItem syncBoxItem = null;
        for (SyncItem syncItem : syncItems) {
            if (syncItem instanceof SyncBoxItem) {
                syncBoxItem = (SyncBoxItem) syncItem;
                break;
            }
        }
        Assert.assertNotNull(syncBoxItem);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "xxx");
        sendPickupBoxCommand(TEST_PLANET_1_ID, target, syncBoxItem.getId());
        Assert.assertNotNull(((SyncBaseItem) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getItemService().getItem(target)).getSyncMovable().getSyncBoxItemId());
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = backupService.getBackupSummary();
        assertBackupSummery(1, 1, 1, 1);
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertWholeItemCount(TEST_PLANET_1_ID, 1);
        Assert.assertNull(((SyncBaseItem) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getItemService().getItemsCopy().get(0)).getSyncMovable().getSyncBoxItemId());

        Thread.sleep(230);
        syncItems = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getItemService().getItemsCopy();
        Assert.assertEquals(2, syncItems.size());
        Assert.assertTrue(syncItems.get(0) instanceof SyncBoxItem);
    }

    @Test
    @DirtiesContext
    public void pickupInventoryItemReal() throws Exception {
        setPrivateStaticField(InventoryServiceImpl.class, "SCHEDULE_RATE", 25);
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryItem dbInventoryItem = globalInventoryService.getItemCrud().createDbChild();
        DbInventoryArtifact dbInventoryArtifact = globalInventoryService.getArtifactCrud().createDbChild();
        DbBoxItemType dbBoxItemType = (DbBoxItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBoxItemType.class);
        dbBoxItemType.setTerrainType(TerrainType.LAND);
        setupImages(dbBoxItemType, 1);
        dbBoxItemType.setBounding(new BoundingBox(80, ANGELS_1));
        dbBoxItemType.setTtl(5000);
        DbBoxItemTypePossibility dbBoxItemTypePossibility1 = dbBoxItemType.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility1.setPossibility(1.0);
        dbBoxItemTypePossibility1.setDbInventoryItem(dbInventoryItem);
        DbBoxItemTypePossibility dbBoxItemTypePossibility2 = dbBoxItemType.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility2.setPossibility(1.0);
        dbBoxItemTypePossibility2.setDbInventoryArtifact(dbInventoryArtifact);
        DbBoxItemTypePossibility dbBoxItemTypePossibility3 = dbBoxItemType.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility3.setPossibility(1.0);
        dbBoxItemTypePossibility3.setCrystals(100);
        serverItemTypeService.saveDbItemType(dbBoxItemType);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertWholeItemCount(TEST_PLANET_1_ID, 0);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID);
        DbBoxRegion dbBoxRegion = dbPlanet.getBoxRegionCrud().createDbChild();
        dbBoxRegion.setItemFreeRange(100);
        dbBoxRegion.setMinInterval(200);
        dbBoxRegion.setMaxInterval(200);
        dbBoxRegion.setName("DbBoxRegion1");
        dbBoxRegion.setRegion(createDbRegion(new Rectangle(100, 100, 1000, 800)));
        DbBoxRegionCount dbBoxRegionCount = dbBoxRegion.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount.setDbBoxItemType(dbBoxItemType);
        dbBoxRegionCount.setCount(1);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.deactivatePlanet(TEST_PLANET_1_ID);
        planetSystemService.activatePlanet(TEST_PLANET_1_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        waitForHistoryType(DbHistoryElement.Type.BOX_DROPPED);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);
        List<SyncItem> syncItems = serverPlanetServices.getItemService().getItemsCopy();
        SyncBoxItem boxItem = (SyncBoxItem) syncItems.get(0);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertNoHistoryType(DbHistoryElement.Type.INVENTORY_ITEM_FROM_BOX);
        assertNoHistoryType(DbHistoryElement.Type.INVENTORY_ARTIFACT_FROM_BOX);
        assertNoHistoryType(DbHistoryElement.Type.CRYSTALS_FROM_BOX);
        sendPickupBoxCommand(TEST_PLANET_1_ID, getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), boxItem.getId());
        waitForActionServiceDone();
        waitForHistoryType(DbHistoryElement.Type.INVENTORY_ITEM_FROM_BOX);
        waitForHistoryType(DbHistoryElement.Type.INVENTORY_ARTIFACT_FROM_BOX);
        waitForHistoryType(DbHistoryElement.Type.CRYSTALS_FROM_BOX);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void pickupInventoryItemMock() throws Exception {
        setPrivateStaticField(InventoryServiceImpl.class, "SCHEDULE_RATE", 25);
        configureSimplePlanetNoResources();
        ServerPlanetServicesImpl serverPlanetServices = (ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        Base base = new Base(planetSystemService.getPlanet(TEST_PLANET_1_ID),1);
        SimpleBase simpleBase = base.getSimpleBase();
        DbBoxItemType dbBoxItemType1 = new DbBoxItemType();
        setupDbItemTypeId(dbBoxItemType1, 1);
        DbBoxItemTypePossibility dbBoxItemTypePossibility = dbBoxItemType1.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility.setPossibility(1.0);
        dbBoxItemTypePossibility.setCrystals(100);
        DbBoxItemType dbBoxItemType2 = new DbBoxItemType();
        setupDbItemTypeId(dbBoxItemType2, 2);
        dbBoxItemTypePossibility = dbBoxItemType2.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility.setPossibility(1.0);
        DbInventoryItem dbInventoryItem = new DbInventoryItem();
        dbInventoryItem.setName("InventoryItem");
        setPrivateField(DbInventoryItem.class, dbInventoryItem, "id", 33);
        dbBoxItemTypePossibility.setDbInventoryItem(dbInventoryItem);
        DbBoxItemType dbBoxItemType3 = new DbBoxItemType();
        setupDbItemTypeId(dbBoxItemType3, 3);
        dbBoxItemTypePossibility = dbBoxItemType3.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility.setPossibility(1.0);
        DbInventoryArtifact dbInventoryArtifact = new DbInventoryArtifact();
        dbInventoryArtifact.setName("InventoryArtifact");
        setPrivateField(DbInventoryArtifact.class, dbInventoryArtifact, "id", 44);
        dbBoxItemTypePossibility.setDbInventoryArtifact(dbInventoryArtifact);
        DbBoxItemType dbBoxItemType4 = new DbBoxItemType();
        setupDbItemTypeId(dbBoxItemType4, 4);
        dbBoxItemTypePossibility = dbBoxItemType4.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility.setPossibility(0.0);
        dbInventoryArtifact = new DbInventoryArtifact();
        dbInventoryArtifact.setName("InventoryArtifact");
        setPrivateField(DbInventoryArtifact.class, dbInventoryArtifact, "id", 55);
        dbBoxItemTypePossibility.setDbInventoryArtifact(dbInventoryArtifact);
        // BoxItemType
        BoxItemType boxItemType = EasyMock.createStrictMock(BoxItemType.class);
        EasyMock.expect(boxItemType.getId()).andReturn(1);
        EasyMock.expect(boxItemType.getId()).andReturn(2);
        EasyMock.expect(boxItemType.getId()).andReturn(3);
        EasyMock.expect(boxItemType.getId()).andReturn(4);
        // SyncBoxItems
        SyncBoxItem mockSyncBoxItem1 = EasyMock.createStrictMock(SyncBoxItem.class);
        EasyMock.expect(mockSyncBoxItem1.getItemType()).andReturn(boxItemType).times(4);
        // ServerItemTypeService
        ServerItemTypeService mockServerItemTypeService = EasyMock.createStrictMock(ServerItemTypeService.class);
        EasyMock.expect(mockServerItemTypeService.getDbBoxItemType(1)).andReturn(dbBoxItemType1);
        EasyMock.expect(mockServerItemTypeService.getDbBoxItemType(2)).andReturn(dbBoxItemType2);
        EasyMock.expect(mockServerItemTypeService.getDbBoxItemType(3)).andReturn(dbBoxItemType3);
        EasyMock.expect(mockServerItemTypeService.getDbBoxItemType(4)).andReturn(dbBoxItemType4);
        // ServerItemService
        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        mockServerItemService.killSyncItem(mockSyncBoxItem1, simpleBase, true, false);
        mockServerItemService.killSyncItem(mockSyncBoxItem1, simpleBase, true, false);
        mockServerItemService.killSyncItem(mockSyncBoxItem1, simpleBase, true, false);
        mockServerItemService.killSyncItem(mockSyncBoxItem1, simpleBase, true, false);
        // Picker
        SyncBaseItem picker = EasyMock.createStrictMock(SyncBaseItem.class);
        EasyMock.expect(picker.getBase()).andReturn(simpleBase).times(24);
        // SyncBoxItems
        UserState mockUserState = EasyMock.createStrictMock(UserState.class);
        mockUserState.addCrystals(100);
        EasyMock.expect(mockUserState.getBase()).andReturn(base);
        EasyMock.expect(mockUserState.getDbLevelId()).andReturn(TEST_LEVEL_2_REAL_ID);
        mockUserState.addInventoryItem(33);
        mockUserState.addInventoryArtifact(44);
        EasyMock.expect(mockUserState.getBase()).andReturn(base);
        EasyMock.expect(mockUserState.getDbLevelId()).andReturn(TEST_LEVEL_2_REAL_ID);
        // SyncBoxItems
        BaseService mockBaseService = EasyMock.createStrictMock(BaseService.class);
        EasyMock.expect(mockBaseService.isAbandoned(simpleBase)).andReturn(false);
        EasyMock.expect(mockBaseService.getUserState(simpleBase)).andReturn(mockUserState);
        EasyMock.expect(mockBaseService.isAbandoned(simpleBase)).andReturn(false);
        EasyMock.expect(mockBaseService.getUserState(simpleBase)).andReturn(mockUserState);
        EasyMock.expect(mockBaseService.isAbandoned(simpleBase)).andReturn(false);
        EasyMock.expect(mockBaseService.getUserState(simpleBase)).andReturn(mockUserState);
        EasyMock.expect(mockBaseService.isAbandoned(simpleBase)).andReturn(false);
        EasyMock.expect(mockBaseService.getUserState(simpleBase)).andReturn(mockUserState);
        // History Service
        HistoryService mockHistoryService = EasyMock.createStrictMock(HistoryService.class);
        mockHistoryService.addBoxPicked(mockSyncBoxItem1, picker);
        mockHistoryService.addCrystalsFromBox(mockUserState, 100);
        mockHistoryService.addBoxPicked(mockSyncBoxItem1, picker);
        mockHistoryService.addInventoryItemFromBox(mockUserState, "InventoryItem");
        mockHistoryService.addBoxPicked(mockSyncBoxItem1, picker);
        mockHistoryService.addInventoryArtifactFromBox(mockUserState, "InventoryArtifact");
        mockHistoryService.addBoxPicked(mockSyncBoxItem1, picker);
        // Connection Service
        ServerConnectionServiceTestHelper connectionService = new ServerConnectionServiceTestHelper();

        EasyMock.replay(mockHistoryService, mockServerItemTypeService, mockServerItemService, mockSyncBoxItem1, picker, mockBaseService, boxItemType, mockUserState);

        setPrivateField(GlobalInventoryServiceImpl.class, globalInventoryService, "historyService", mockHistoryService);
        setPrivateField(GlobalInventoryServiceImpl.class, globalInventoryService, "serverItemTypeService", mockServerItemTypeService);
        overrideConnectionService(serverPlanetServices, connectionService);
        serverPlanetServices.setBaseService(mockBaseService);
        serverPlanetServices.setServerItemService(mockServerItemService);

        serverPlanetServices.getInventoryService().onSyncBoxItemPicked(mockSyncBoxItem1, picker);
        Assert.assertEquals(1, connectionService.getPacketEntries().size());
        BoxPickedPacket boxPickedPacket = (BoxPickedPacket) connectionService.getPacketEntries(simpleBase, BoxPickedPacket.class).get(0).getPacket();
        Assert.assertEquals("You picked up a box! Items added to your Inventory:<ul><li>Crystals: 100</li></ul>", boxPickedPacket.getHtml());
        connectionService.clearReceivedPackets();
        serverPlanetServices.getInventoryService().onSyncBoxItemPicked(mockSyncBoxItem1, picker);
        Assert.assertEquals(1, connectionService.getPacketEntries().size());
        boxPickedPacket = (BoxPickedPacket) connectionService.getPacketEntries(simpleBase, BoxPickedPacket.class).get(0).getPacket();
        Assert.assertEquals("You picked up a box! Items added to your Inventory:<ul><li>Item: InventoryItem</li></ul>", boxPickedPacket.getHtml());
        connectionService.clearReceivedPackets();
        serverPlanetServices.getInventoryService().onSyncBoxItemPicked(mockSyncBoxItem1, picker);
        Assert.assertEquals(1, connectionService.getPacketEntries().size());
        boxPickedPacket = (BoxPickedPacket) connectionService.getPacketEntries(simpleBase, BoxPickedPacket.class).get(0).getPacket();
        Assert.assertEquals("You picked up a box! Items added to your Inventory:<ul><li>Artifact: InventoryArtifact</li></ul>", boxPickedPacket.getHtml());
        connectionService.clearReceivedPackets();
        serverPlanetServices.getInventoryService().onSyncBoxItemPicked(mockSyncBoxItem1, picker);
        Assert.assertEquals(1, connectionService.getPacketEntries().size());
        boxPickedPacket = (BoxPickedPacket) connectionService.getPacketEntries(simpleBase, BoxPickedPacket.class).get(0).getPacket();
        Assert.assertEquals("You picked up a box! Items added to your Inventory:<ul><li>No luck. Empty box found</li></ul>", boxPickedPacket.getHtml());

        EasyMock.verify(mockHistoryService, mockServerItemTypeService, mockServerItemService, mockSyncBoxItem1, picker, mockBaseService, boxItemType, mockUserState);
    }

    @Test
    @DirtiesContext
    public void backupRestoreUserState() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryArtifact dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().createDbChild();
        DbInventoryArtifact dbInventoryArtifact2 = globalInventoryService.getArtifactCrud().createDbChild();
        DbInventoryArtifact dbInventoryArtifact3 = globalInventoryService.getArtifactCrud().createDbChild();
        DbInventoryItem dbInventoryItem1 = globalInventoryService.getItemCrud().createDbChild();
        DbInventoryItem dbInventoryItem2 = globalInventoryService.getItemCrud().createDbChild();
        DbInventoryItem dbInventoryItem3 = globalInventoryService.getItemCrud().createDbChild();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        getOrCreateBase(); // Create Base
        UserState userState = userService.getUserState();
        userState.setCrystals(111);
        userState.addInventoryItem(dbInventoryArtifact1.getId());
        userState.addInventoryItem(dbInventoryArtifact2.getId());
        userState.addInventoryItem(dbInventoryArtifact3.getId());
        userState.addInventoryItem(dbInventoryArtifact3.getId());

        userState.addInventoryArtifact(dbInventoryItem1.getId());
        userState.addInventoryArtifact(dbInventoryItem1.getId());
        userState.addInventoryArtifact(dbInventoryItem2.getId());
        userState.addInventoryArtifact(dbInventoryItem2.getId());
        userState.addInventoryArtifact(dbInventoryItem3.getId());
        userState.addInventoryArtifact(dbInventoryItem3.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = backupService.getBackupSummary();
        assertBackupSummery(1, 1, 1, 1);
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        getOrCreateBase(); // Create Base
        userState = userService.getUserState();
        Assert.assertEquals(111, userState.getCrystals());

        Collection<Integer> numbers = new ArrayList<>(userState.getInventoryItemIds());
        Assert.assertEquals(4, numbers.size());
        numbers.removeAll(Arrays.asList(dbInventoryArtifact1.getId(), dbInventoryArtifact2.getId(), dbInventoryArtifact3.getId(), dbInventoryArtifact3.getId()));
        Assert.assertTrue(numbers.isEmpty());

        numbers = new ArrayList<>(userState.getInventoryArtifactIds());
        Assert.assertEquals(6, numbers.size());
        numbers.removeAll(Arrays.asList(dbInventoryItem1.getId(), dbInventoryItem1.getId(), dbInventoryItem2.getId(), dbInventoryItem2.getId(), dbInventoryItem3.getId(), dbInventoryItem3.getId()));
        Assert.assertTrue(numbers.isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void assembleInventoryItem() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryArtifact dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().createDbChild();
        DbInventoryArtifact dbInventoryArtifact2 = globalInventoryService.getArtifactCrud().createDbChild();
        DbInventoryArtifact dbInventoryArtifact3 = globalInventoryService.getArtifactCrud().createDbChild();
        DbInventoryItem dbInventoryItem1 = globalInventoryService.getItemCrud().createDbChild();
        DbInventoryArtifactCount dbInventoryArtifactCount = dbInventoryItem1.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(1);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact1);
        dbInventoryArtifactCount = dbInventoryItem1.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(2);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact2);
        dbInventoryArtifactCount = dbInventoryItem1.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(3);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact3);
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            globalInventoryService.assembleInventoryItem(dbInventoryItem1.getId());
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        UserState userState = userService.getUserState();
        assertInventoryItemCount(userState, dbInventoryItem1, 0);

        userState.addInventoryArtifact(dbInventoryArtifact1.getId());
        userState.addInventoryArtifact(dbInventoryArtifact2.getId());
        userState.addInventoryArtifact(dbInventoryArtifact3.getId());
        try {
            globalInventoryService.assembleInventoryItem(dbInventoryItem1.getId());
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        assertInventoryItemCount(userState, dbInventoryItem1, 0);
        userState.addInventoryArtifact(dbInventoryArtifact2.getId());
        userState.addInventoryArtifact(dbInventoryArtifact3.getId());
        try {
            globalInventoryService.assembleInventoryItem(dbInventoryItem1.getId());
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        assertInventoryItemCount(userState, dbInventoryItem1, 0);
        userState.addInventoryArtifact(dbInventoryArtifact3.getId());
        globalInventoryService.assembleInventoryItem(dbInventoryItem1.getId());
        Assert.assertTrue(userState.getInventoryArtifactIds().isEmpty());
        assertInventoryItemCount(userState, dbInventoryItem1, 1);

        userState.addInventoryArtifact(dbInventoryArtifact1.getId());
        userState.addInventoryArtifact(dbInventoryArtifact1.getId());
        userState.addInventoryArtifact(dbInventoryArtifact1.getId());
        userState.addInventoryArtifact(dbInventoryArtifact2.getId());
        userState.addInventoryArtifact(dbInventoryArtifact2.getId());
        userState.addInventoryArtifact(dbInventoryArtifact2.getId());
        userState.addInventoryArtifact(dbInventoryArtifact3.getId());
        userState.addInventoryArtifact(dbInventoryArtifact3.getId());
        userState.addInventoryArtifact(dbInventoryArtifact3.getId());
        globalInventoryService.assembleInventoryItem(dbInventoryItem1.getId());
        Assert.assertEquals(3, userState.getInventoryArtifactIds().size());
        assertInventoryArtifactCount(userState, dbInventoryArtifact1, 2);
        assertInventoryArtifactCount(userState, dbInventoryArtifact2, 1);
        assertInventoryArtifactCount(userState, dbInventoryArtifact3, 0);
        assertInventoryItemCount(userState, dbInventoryItem1, 2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }

    private void assertInventoryArtifactCount(UserState userState, DbInventoryArtifact dbInventoryArtifact, int count) {
        int currentCount = 0;
        for (Integer artifactId : userState.getInventoryArtifactIds()) {
            if (dbInventoryArtifact.getId().equals(artifactId)) {
                currentCount++;
            }
        }
        Assert.assertEquals(count, currentCount);
    }

    private void assertInventoryItemCount(UserState userState, DbInventoryItem dbInventoryItem, int count) {
        int currentCount = 0;
        for (Integer itemId : userState.getInventoryItemIds()) {
            if (dbInventoryItem.getId().equals(itemId)) {
                currentCount++;
            }
        }
        Assert.assertEquals(count, currentCount);
    }

    @Test
    @DirtiesContext
    public void getInventory() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Map<Integer, InventoryArtifactInfo> allArtifacts = new HashMap<>();
        DbInventoryArtifact dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().createDbChild();
        DbInventoryArtifact dbInventoryArtifact2 = globalInventoryService.getArtifactCrud().createDbChild();
        DbInventoryArtifact dbInventoryArtifact3 = globalInventoryService.getArtifactCrud().createDbChild();
        allArtifacts.put(dbInventoryArtifact1.getId(), dbInventoryArtifact1.generateInventoryArtifactInfo());
        allArtifacts.put(dbInventoryArtifact2.getId(), dbInventoryArtifact2.generateInventoryArtifactInfo());
        allArtifacts.put(dbInventoryArtifact3.getId(), dbInventoryArtifact3.generateInventoryArtifactInfo());
        DbInventoryItem dbInventoryItem1 = globalInventoryService.getItemCrud().createDbChild();
        DbInventoryArtifactCount dbInventoryArtifactCount = dbInventoryItem1.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(1);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact1);
        dbInventoryArtifactCount = dbInventoryItem1.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(2);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact2);
        dbInventoryArtifactCount = dbInventoryItem1.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(3);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact3);
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem1);
        DbInventoryItem dbInventoryItem2 = globalInventoryService.getItemCrud().createDbChild();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Verify new
        InventoryInfo inventoryInfo = globalInventoryService.getInventory(null, false);
        Assert.assertEquals(0, inventoryInfo.getCrystals());
        Assert.assertTrue(inventoryInfo.getOwnInventoryArtifacts().isEmpty());
        Assert.assertTrue(inventoryInfo.getOwnInventoryItems().isEmpty());
        Assert.assertEquals(2, inventoryInfo.getAllInventoryItemInfos().size());
        Assert.assertEquals(3, inventoryInfo.getAllInventoryArtifactInfos().size());
        // Add one artifact
        UserState userState = userService.getUserState();
        userState.setCrystals(15);
        userState.addInventoryArtifact(dbInventoryArtifact1.getId());
        // Verify
        inventoryInfo = globalInventoryService.getInventory(null, false);
        Assert.assertEquals(15, inventoryInfo.getCrystals());
        Assert.assertEquals(1, inventoryInfo.getOwnInventoryArtifacts().size());
        Assert.assertEquals(1, (int) inventoryInfo.getOwnInventoryArtifacts().get(dbInventoryArtifact1.generateInventoryArtifactInfo()));
        Assert.assertTrue(inventoryInfo.getOwnInventoryItems().isEmpty());
        Assert.assertEquals(2, inventoryInfo.getAllInventoryItemInfos().size());
        Assert.assertEquals(3, inventoryInfo.getAllInventoryArtifactInfos().size());
        // Add artifacts
        userState = userService.getUserState();
        userState.addInventoryArtifact(dbInventoryArtifact1.getId());
        userState.addInventoryArtifact(dbInventoryArtifact2.getId());
        // Verify
        inventoryInfo = globalInventoryService.getInventory(null, false);
        Assert.assertEquals(15, inventoryInfo.getCrystals());
        Assert.assertEquals(2, inventoryInfo.getOwnInventoryArtifacts().size());
        Assert.assertEquals(2, (int) inventoryInfo.getOwnInventoryArtifacts().get(dbInventoryArtifact1.generateInventoryArtifactInfo()));
        Assert.assertEquals(1, (int) inventoryInfo.getOwnInventoryArtifacts().get(dbInventoryArtifact2.generateInventoryArtifactInfo()));
        Assert.assertTrue(inventoryInfo.getOwnInventoryItems().isEmpty());
        Assert.assertEquals(2, inventoryInfo.getAllInventoryItemInfos().size());
        Assert.assertEquals(3, inventoryInfo.getAllInventoryArtifactInfos().size());
        // Add items
        userState = userService.getUserState();
        userState.addInventoryItem(dbInventoryItem2.getId());
        // Verify
        inventoryInfo = globalInventoryService.getInventory(null, false);
        Assert.assertEquals(15, inventoryInfo.getCrystals());
        Assert.assertEquals(2, inventoryInfo.getOwnInventoryArtifacts().size());
        Assert.assertEquals(2, (int) inventoryInfo.getOwnInventoryArtifacts().get(dbInventoryArtifact1.generateInventoryArtifactInfo()));
        Assert.assertEquals(1, (int) inventoryInfo.getOwnInventoryArtifacts().get(dbInventoryArtifact2.generateInventoryArtifactInfo()));
        Assert.assertEquals(1, inventoryInfo.getOwnInventoryItems().size());
        Assert.assertEquals(1, (int) inventoryInfo.getOwnInventoryItems().get(dbInventoryItem2.generateInventoryItemInfo(allArtifacts)));
        Assert.assertEquals(2, inventoryInfo.getAllInventoryItemInfos().size());
        Assert.assertEquals(3, inventoryInfo.getAllInventoryArtifactInfos().size());
        // Add items
        userState = userService.getUserState();
        userState.addInventoryItem(dbInventoryItem2.getId());
        userState.addInventoryItem(dbInventoryItem1.getId());
        // Verify
        inventoryInfo = globalInventoryService.getInventory(null, false);
        Assert.assertEquals(15, inventoryInfo.getCrystals());
        Assert.assertEquals(2, inventoryInfo.getOwnInventoryArtifacts().size());
        Assert.assertEquals(2, (int) inventoryInfo.getOwnInventoryArtifacts().get(dbInventoryArtifact1.generateInventoryArtifactInfo()));
        Assert.assertEquals(1, (int) inventoryInfo.getOwnInventoryArtifacts().get(dbInventoryArtifact2.generateInventoryArtifactInfo()));
        Assert.assertEquals(2, inventoryInfo.getOwnInventoryItems().size());
        Assert.assertEquals(2, (int) inventoryInfo.getOwnInventoryItems().get(dbInventoryItem2.generateInventoryItemInfo(allArtifacts)));
        Assert.assertEquals(1, (int) inventoryInfo.getOwnInventoryItems().get(dbInventoryItem1.generateInventoryItemInfo(allArtifacts)));
        Assert.assertEquals(2, inventoryInfo.getAllInventoryItemInfos().size());
        Assert.assertEquals(3, inventoryInfo.getAllInventoryArtifactInfos().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getInventoryFilter() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Artifacts
        DbInventoryArtifact dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().createDbChild();
        DbInventoryArtifact dbInventoryArtifact2 = globalInventoryService.getArtifactCrud().createDbChild();
        DbInventoryArtifact dbInventoryArtifact3 = globalInventoryService.getArtifactCrud().createDbChild();
        // Items
        DbInventoryItem dbInventoryItem1 = globalInventoryService.getItemCrud().createDbChild();
        dbInventoryItem1.setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        DbInventoryArtifactCount dbInventoryArtifactCount1 = dbInventoryItem1.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount1.setCount(1);
        dbInventoryArtifactCount1.setDbInventoryArtifact(dbInventoryArtifact1);
        dbInventoryArtifactCount1 = dbInventoryItem1.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount1.setCount(2);
        dbInventoryArtifactCount1.setDbInventoryArtifact(dbInventoryArtifact2);
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem1);
        DbInventoryItem dbInventoryItem2 = globalInventoryService.getItemCrud().createDbChild();
        DbInventoryArtifactCount dbInventoryArtifactCount2 = dbInventoryItem2.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount2.setCount(3);
        dbInventoryArtifactCount2.setDbInventoryArtifact(dbInventoryArtifact3);
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem2);
        // Create Box
        DbBoxItemType dbBoxItemType1 = createDbBoxItemType1();
        DbBoxItemTypePossibility dbBoxItemTypePossibility1 = dbBoxItemType1.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility1.setDbInventoryArtifact(dbInventoryArtifact1);
        dbBoxItemTypePossibility1.setPossibility(1.0);
        DbBoxItemTypePossibility dbBoxItemTypePossibility2 = dbBoxItemType1.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility2.setDbInventoryArtifact(dbInventoryArtifact2);
        dbBoxItemTypePossibility2.setPossibility(1.0);
        serverItemTypeService.saveDbItemType(dbBoxItemType1);
        // Create Planet 1 with box region
        DbPlanet dbPlanet1 = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID);
        DbBoxRegion dbBoxRegion1 = dbPlanet1.getBoxRegionCrud().createDbChild();
        dbBoxRegion1.setRegion(createDbRegion(new Rectangle(1, 2, 10, 20)));
        DbBoxRegionCount dbBoxRegionCount1 = dbBoxRegion1.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount1.setDbBoxItemType(dbBoxItemType1);
        dbBoxRegionCount1.setCount(10);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet1);
        // Create Planet 2 with box region
        DbPlanet dbPlanet2 = planetSystemService.getDbPlanetCrud().createDbChild();
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getOrCreateBase(); // Create Base
        // Verify new
        InventoryInfo inventoryInfo = globalInventoryService.getInventory(null, false);
        Assert.assertEquals(2, inventoryInfo.getAllInventoryItemInfos().size());
        Assert.assertEquals(3, inventoryInfo.getAllInventoryArtifactInfos().size());
        inventoryInfo = globalInventoryService.getInventory(null, true);
        Assert.assertEquals(2, inventoryInfo.getAllInventoryItemInfos().size());
        Assert.assertEquals(3, inventoryInfo.getAllInventoryArtifactInfos().size());
        inventoryInfo = globalInventoryService.getInventory(dbPlanet1.getId(), true);
        Assert.assertEquals(1, inventoryInfo.getAllInventoryItemInfos().size());
        Assert.assertEquals(2, inventoryInfo.getAllInventoryArtifactInfos().size());
        inventoryInfo = globalInventoryService.getInventory(dbPlanet2.getId(), true);
        Assert.assertEquals(0, inventoryInfo.getAllInventoryItemInfos().size());
        Assert.assertEquals(0, inventoryInfo.getAllInventoryArtifactInfos().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void useInventoryItem() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServicesImpl serverPlanetServices = (ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryItem dbInventoryItem1 = globalInventoryService.getItemCrud().createDbChild();
        dbInventoryItem1.setItemFreeRange(100);
        dbInventoryItem1.setBaseItemTypeCount(1);
        dbInventoryItem1.setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        dbInventoryItem1.setName("dbInventoryItem1");
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem1);
        DbInventoryItem dbInventoryItem2 = globalInventoryService.getItemCrud().createDbChild();
        dbInventoryItem2.setItemFreeRange(100);
        dbInventoryItem2.setBaseItemTypeCount(3);
        dbInventoryItem2.setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        dbInventoryItem2.setName("dbInventoryItem2");
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem2);
        DbInventoryItem dbInventoryItem3 = globalInventoryService.getItemCrud().createDbChild();
        dbInventoryItem3.setGoldAmount(20);
        dbInventoryItem3.setName("dbInventoryItem3");
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem3);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        Base base = new Base(planetSystemService.getPlanet(TEST_PLANET_1_ID), 1);
        SyncBaseItem attackerItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(1, 1));
        attackerItem.setBuildup(0.0);

        // ServerItemTypeService
        DbBaseItemType dbAttackItemType = (DbBaseItemType) serverItemTypeService.getDbItemType(TEST_ATTACK_ITEM_ID);
        ServerItemTypeService mockServerItemTypeService = EasyMock.createStrictMock(ServerItemTypeService.class);
        EasyMock.expect(mockServerItemTypeService.getItemType(dbAttackItemType)).andReturn(serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID)).times(4);
        setPrivateField(ServerGlobalServicesImpl.class, serverGlobalServices, "serverItemTypeService", mockServerItemTypeService);

        // ServerItemService
        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        EasyMock.expect(mockServerItemService.hasEnemyInRange(base.getSimpleBase(), new Index(1000, 1000), 164)).andReturn(false);
        EasyMock.expect(mockServerItemService.hasItemsInRectangleFast(new Rectangle(960, 950, 80, 100))).andReturn(false);
        EasyMock.expect(mockServerItemService.hasEnemyInRange(base.getSimpleBase(), new Index(1000, 1000), 164)).andReturn(false);
        EasyMock.expect(mockServerItemService.hasItemsInRectangleFast(new Rectangle(960, 950, 80, 100))).andReturn(true);
        EasyMock.expect(mockServerItemService.hasEnemyInRange(base.getSimpleBase(), new Index(1000, 1000), 164)).andReturn(true);
        EasyMock.expect(mockServerItemService.hasEnemyInRange(base.getSimpleBase(), new Index(1000, 1000), 164)).andReturn(false);
        EasyMock.expect(mockServerItemService.hasItemsInRectangleFast(new Rectangle(960, 950, 80, 100))).andReturn(false);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), new Index(1000, 1000), null, base.getSimpleBase())).andReturn(attackerItem);
        serverPlanetServices.setServerItemService(mockServerItemService);

        // Terrain Service
        ServerTerrainService mockTerrainService = EasyMock.createStrictMock(ServerTerrainService.class);
        EasyMock.expect(mockTerrainService.isFree(new Index(1000, 1000), serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID))).andReturn(false);
        EasyMock.expect(mockTerrainService.isFree(new Index(1000, 1000), serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID))).andReturn(true);
        serverPlanetServices.setTerrainService(mockTerrainService);

        // History Service
        HistoryService mockHistoryService = EasyMock.createStrictMock(HistoryService.class);
        mockHistoryService.addInventoryItemUsed(userService.getUserState(), "dbInventoryItem3");
        mockHistoryService.addInventoryItemUsed(userService.getUserState(), "dbInventoryItem1");
        setPrivateField(GlobalInventoryServiceImpl.class, globalInventoryService, "historyService", mockHistoryService);
        // Base Service
        BaseService mockBaseService = EasyMock.createStrictMock(BaseService.class);
        mockBaseService.depositResource(20, base.getSimpleBase());
        mockBaseService.sendAccountBaseUpdate(base.getSimpleBase());
        serverPlanetServices.setBaseService(mockBaseService);

        EasyMock.replay(mockServerItemTypeService, mockServerItemService, mockTerrainService, mockHistoryService, mockBaseService);

        // Use gold but no inventory item
        userService.getUserState().setBase(base);
        Assert.assertTrue(userService.getUserState().getInventoryItemIds().isEmpty());
        try {
            globalInventoryService.useInventoryItem(dbInventoryItem3.getId(), null);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("User does not have inventory item: DbInventoryItem{id=3, name='dbInventoryItem3'} user: UserState: user=null", e.getMessage());
        }
        // No base
        userService.getUserState().setBase(null);
        userService.getUserState().addInventoryItem(dbInventoryItem3.getId());
        try {
            globalInventoryService.useInventoryItem(dbInventoryItem3.getId(), null);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User does not have a base: UserState: user=null", e.getMessage());
        }
        // Use gold
        userService.getUserState().setBase(base);
        globalInventoryService.useInventoryItem(dbInventoryItem3.getId(), null);
        Assert.assertTrue(userService.getUserState().getInventoryItemIds().isEmpty());
        // Use BaseItemType inventory item invalid size
        userService.getUserState().addInventoryItem(dbInventoryItem1.getId());
        try {
            globalInventoryService.useInventoryItem(dbInventoryItem1.getId(), Collections.<Index>emptyList());
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("positionToBePlaced.size() != dbInventoryItem.getBaseItemTypeCount() 0 1", e.getMessage());
        }
        // Use item on wrong terrain
        try {
            globalInventoryService.useInventoryItem(dbInventoryItem1.getId(), Collections.singletonList(new Index(1000, 1000)));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Terrain is not free ItemType: TestAttackItem UserState: user=null", e.getMessage());
        }
        // Use item over other unit
        try {
            globalInventoryService.useInventoryItem(dbInventoryItem1.getId(), Collections.singletonList(new Index(1000, 1000)));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Can not place over other items ItemType: TestAttackItem UserState: user=null", e.getMessage());
        }
        //Enemy items
        try {
            globalInventoryService.useInventoryItem(dbInventoryItem1.getId(), Collections.singletonList(new Index(1000, 1000)));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Enemy items too near ItemType: TestAttackItem UserState: user=null", e.getMessage());
        }

        globalInventoryService.useInventoryItem(dbInventoryItem1.getId(), Collections.singletonList(new Index(1000, 1000)));
        Assert.assertTrue(userService.getUserState().getInventoryItemIds().isEmpty());
        Assert.assertEquals(1.0, attackerItem.getBuildup(), 0.001);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(mockServerItemTypeService, mockServerItemService, mockTerrainService, mockHistoryService, mockBaseService);
    }

    @Test
    @DirtiesContext
    public void useInventoryItemMultipleBaseItems() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServicesImpl serverPlanetServices = (ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryItem dbInventoryItem1 = globalInventoryService.getItemCrud().createDbChild();
        dbInventoryItem1.setItemFreeRange(100);
        dbInventoryItem1.setBaseItemTypeCount(1);
        dbInventoryItem1.setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        dbInventoryItem1.setName("dbInventoryItem1");
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem1);
        DbInventoryItem dbInventoryItem2 = globalInventoryService.getItemCrud().createDbChild();
        dbInventoryItem2.setItemFreeRange(100);
        dbInventoryItem2.setBaseItemTypeCount(3);
        dbInventoryItem2.setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        dbInventoryItem2.setName("dbInventoryItem2");
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        SyncBaseItem attackerItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(1, 1));
        attackerItem.setBuildup(0.0);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase simpleBase = getOrCreateBase();
        Id starterItem = getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID);
        sendMoveCommand(starterItem, new Index(5000, 5000));
        waitForActionServiceDone();

        // ServerItemService
        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        EasyMock.expect(mockServerItemService.hasEnemyInRange(simpleBase, new Index(1100, 1100), 228)).andReturn(false);
        EasyMock.expect(mockServerItemService.hasItemsInRectangleFast(new Rectangle(1020, 1000, 160, 200))).andReturn(false);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), new Index(1000, 1000), null, simpleBase)).andReturn(attackerItem);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), new Index(1200, 1000), null, simpleBase)).andReturn(attackerItem);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), new Index(1200, 1200), null, simpleBase)).andReturn(attackerItem);
        serverPlanetServices.setServerItemService(mockServerItemService);
        // Terrain Service
        ServerTerrainService mockTerrainService = EasyMock.createStrictMock(ServerTerrainService.class);
        EasyMock.expect(mockTerrainService.isFree(new Index(1000, 1000), serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID))).andReturn(true);
        EasyMock.expect(mockTerrainService.isFree(new Index(1200, 1000), serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID))).andReturn(true);
        EasyMock.expect(mockTerrainService.isFree(new Index(1200, 1200), serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID))).andReturn(true);
        serverPlanetServices.setTerrainService(mockTerrainService);
        // History Service
        HistoryService mockHistoryService = EasyMock.createStrictMock(HistoryService.class);
        mockHistoryService.addInventoryItemUsed(userService.getUserState(), "dbInventoryItem2");
        setPrivateField(GlobalInventoryServiceImpl.class, globalInventoryService, "historyService", mockHistoryService);

        EasyMock.replay(mockServerItemService, mockTerrainService, mockHistoryService);

        // Use gold but no inventory item
        Assert.assertTrue(userService.getUserState().getInventoryItemIds().isEmpty());
        userService.getUserState().addInventoryItem(dbInventoryItem2.getId());
        Collection<Index> positions = new ArrayList<>();
        positions.add(new Index(1000, 1000));
        positions.add(new Index(1200, 1000));
        positions.add(new Index(1200, 1200));
        globalInventoryService.useInventoryItem(dbInventoryItem2.getId(), positions);
        Assert.assertTrue(userService.getUserState().getInventoryItemIds().isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(mockServerItemService, mockTerrainService, mockHistoryService);
    }

    @Test
    @DirtiesContext
    public void buyInventoryItem() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryItem dbInventoryItem1 = globalInventoryService.getItemCrud().createDbChild();
        dbInventoryItem1.setName("GoldItem1");
        dbInventoryItem1.setGoldAmount(100);
        dbInventoryItem1.setImageContentType("imageData22");
        dbInventoryItem1.setImageData(new byte[]{1, 3, 4, 6, 7, 9});
        dbInventoryItem1.setCrystalCost(66);
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem1);

        DbInventoryItem dbInventoryItem2 = globalInventoryService.getItemCrud().createDbChild();
        dbInventoryItem2.setName("GoldItem2");
        dbInventoryItem2.setGoldAmount(10);
        dbInventoryItem2.setImageContentType("imageData33");
        dbInventoryItem2.setImageData(new byte[]{6, 7, 9});
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // History Service
        HistoryService mockHistoryService = EasyMock.createStrictMock(HistoryService.class);
        mockHistoryService.addInventoryItemBought(userService.getUserState(), "GoldItem1", 66);
        setPrivateField(GlobalInventoryServiceImpl.class, globalInventoryService, "historyService", mockHistoryService);
        EasyMock.replay(mockHistoryService);

        try {
            globalInventoryService.buyInventoryItem(dbInventoryItem2.getId());
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("The InventoryItem can not be bought: UserState: user=null dbInventoryItem: DbInventoryItem{id=2, name='GoldItem2'}", e.getMessage());
        }
        try {
            globalInventoryService.buyInventoryItem(dbInventoryItem1.getId());
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("The user does not have enough crystals to buy the inventory item. User: UserState: user=null dbInventoryItem: DbInventoryItem{id=1, name='GoldItem1'} crystals: 0", e.getMessage());
        }
        Assert.assertFalse(userService.getUserState().hasInventoryItemId(dbInventoryItem1.getId()));
        userService.getUserState().addCrystals(100);
        globalInventoryService.buyInventoryItem(dbInventoryItem1.getId());
        Assert.assertEquals(34, userService.getUserState().getCrystals());
        Assert.assertEquals(1, userService.getUserState().getInventoryItemIds().size());
        Assert.assertTrue(userService.getUserState().hasInventoryItemId(dbInventoryItem1.getId()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(mockHistoryService);
    }

    @Test
    @DirtiesContext
    public void buyInventoryArtifact() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryArtifact dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact1.setName("Artifact1");
        dbInventoryArtifact1.setRareness(DbInventoryArtifact.Rareness.UN_COMMON);
        dbInventoryArtifact1.setImageContentType("imageContent");
        dbInventoryArtifact1.setImageData(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
        dbInventoryArtifact1.setCrystalCost(12);
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact1);

        DbInventoryArtifact dbInventoryArtifact2 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact2.setName("Artifact2");
        dbInventoryArtifact2.setImageContentType("imageContent2");
        dbInventoryArtifact2.setImageData(new byte[]{7, 8, 9});
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // History Service
        HistoryService mockHistoryService = EasyMock.createStrictMock(HistoryService.class);
        mockHistoryService.addInventoryArtifactBought(userService.getUserState(), "Artifact1", 12);
        setPrivateField(GlobalInventoryServiceImpl.class, globalInventoryService, "historyService", mockHistoryService);
        EasyMock.replay(mockHistoryService);

        try {
            globalInventoryService.buyInventoryArtifact(dbInventoryArtifact2.getId());
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("The InventoryArtifact can not be bought: UserState: user=null dbInventoryItem: DbInventoryArtifact{id=2, name='Artifact2'}", e.getMessage());
        }
        try {
            globalInventoryService.buyInventoryArtifact(dbInventoryArtifact1.getId());
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("The user does not have enough crystals to buy the inventory artifact. User: UserState: user=null dbInventoryArtifact: DbInventoryArtifact{id=1, name='Artifact1'} crystals: 0", e.getMessage());
        }
        Assert.assertTrue(userService.getUserState().getInventoryArtifactIds().isEmpty());
        userService.getUserState().addCrystals(24);
        globalInventoryService.buyInventoryArtifact(dbInventoryArtifact1.getId());
        Assert.assertEquals(12, userService.getUserState().getCrystals());
        Assert.assertEquals(1, userService.getUserState().getInventoryArtifactIds().size());
        Assert.assertEquals(dbInventoryArtifact1.getId(), CommonJava.getFirst(userService.getUserState().getInventoryArtifactIds()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(mockHistoryService);
    }

    @Test
    @DirtiesContext
    public void testReactivate() throws Exception {
        setPrivateStaticField(InventoryServiceImpl.class, "SCHEDULE_RATE", 50);
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createDbBoxItemType2();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID);
        DbBoxRegion dbBoxRegion = dbPlanet.getBoxRegionCrud().createDbChild();
        dbBoxRegion.setItemFreeRange(100);
        dbBoxRegion.setMinInterval(300);
        dbBoxRegion.setMaxInterval(300);
        dbBoxRegion.setName("DbBoxRegion1");
        dbBoxRegion.setRegion(createDbRegion(new Rectangle(100, 100, 1000, 800)));
        DbBoxRegionCount dbBoxRegionCount = dbBoxRegion.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount.setDbBoxItemType(serverItemTypeService.getDbBoxItemType(TEST_BOX_ITEM_2_ID));
        dbBoxRegionCount.setCount(1);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.deactivatePlanet(TEST_PLANET_1_ID);
        planetSystemService.activatePlanet(TEST_PLANET_1_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Thread.sleep(400);
        List<SyncItem> items1 = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getItemService().getItemsCopy();
        Assert.assertEquals(1, items1.size());

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getInventoryService().reactivate(planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertWholeItemCount(TEST_PLANET_1_ID, 0);

        Thread.sleep(400);
        List<SyncItem> items2 = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getItemService().getItemsCopy();
        Assert.assertEquals(1, items2.size());

        Assert.assertFalse(CommonJava.getFirst(items1).isAlive());
        Assert.assertTrue(CommonJava.getFirst(items2).isAlive());

    }

}
