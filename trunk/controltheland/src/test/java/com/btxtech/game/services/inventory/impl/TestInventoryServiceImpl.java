package com.btxtech.game.services.inventory.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryArtifactInfo;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryInfo;
import com.btxtech.game.jsre.common.BoxPickedPacket;
import com.btxtech.game.jsre.common.CommonJava;
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
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.ConnectionServiceTestHelper;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.history.DbHistoryElement;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.inventory.DbBoxRegion;
import com.btxtech.game.services.inventory.DbBoxRegionCount;
import com.btxtech.game.services.inventory.DbInventoryArtifact;
import com.btxtech.game.services.inventory.DbInventoryArtifactCount;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.inventory.InventoryService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemTypePossibility;
import com.btxtech.game.services.mgmt.BackupSummary;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.territory.TerritoryService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
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
    private InventoryService inventoryService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private MgmtService mgmtService;
    @Autowired
    private UserService userService;
    @Autowired
    private BaseService baseService;
    //private Log log = LogFactory.getLog(TestInventoryServiceImpl.class);

    @Test
    @DirtiesContext
    public void testDbBoxRegionReal() throws Exception {
        InventoryServiceImpl.SCHEDULE_RATE = 10;
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createDbBoxItemType1();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertWholeItemCount(0);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBoxRegion dbBoxRegion = inventoryService.getBoxRegionCrud().createDbChild();
        dbBoxRegion.setItemFreeRange(100);
        dbBoxRegion.setMinInterval(200);
        dbBoxRegion.setMaxInterval(200);
        dbBoxRegion.setName("DbBoxRegion1");
        dbBoxRegion.setRegion(new Rectangle(100, 100, 1000, 1000));
        DbBoxRegionCount dbBoxRegionCount = dbBoxRegion.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount.setDbBoxItemType(itemService.getDbBoxItemType(TEST_BOX_ITEM_1_ID));
        dbBoxRegionCount.setCount(1);
        inventoryService.getBoxRegionCrud().updateDbChild(dbBoxRegion);
        inventoryService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Thread.sleep(150);
        assertWholeItemCount(0);
        Thread.sleep(100);
        List<SyncItem> allItems = itemService.getItemsCopy();
        Assert.assertEquals(1, allItems.size());
        Assert.assertEquals(TEST_BOX_ITEM_1_ID, allItems.get(0).getItemType().getId());
        Thread.sleep(100);
        assertWholeItemCount(0);
    }

    @Test
    @DirtiesContext
    public void testDbBoxRegionMock() throws Exception {
        InventoryServiceImpl.SCHEDULE_RATE = 25;
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createDbBoxItemType1();
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
        // ItemService
        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        DbBoxItemType dbBoxItemType = itemService.getDbBoxItemType(TEST_BOX_ITEM_1_ID);
        BoxItemType boxItemType = (BoxItemType) itemService.getItemType(TEST_BOX_ITEM_1_ID);

        EasyMock.expect(mockItemService.getItemType(dbBoxItemType)).andReturn(boxItemType);
        EasyMock.expect(mockItemService.createSyncObject(EasyMock.eq(boxItemType), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull(), EasyMock.eq(0))).andReturn(mockSyncBoxItem1);
        mockItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem1), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));

        EasyMock.expect(mockItemService.getItemType(dbBoxItemType)).andReturn(boxItemType);
        EasyMock.expect(mockItemService.createSyncObject(EasyMock.eq(boxItemType), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull(), EasyMock.eq(0))).andReturn(mockSyncBoxItem2);
        mockItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem2), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));

        EasyMock.expect(mockItemService.getItemType(dbBoxItemType)).andReturn(boxItemType);
        EasyMock.expect(mockItemService.createSyncObject(EasyMock.eq(boxItemType), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull(), EasyMock.eq(0))).andReturn(mockSyncBoxItem3);
        mockItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem3), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));

        EasyMock.replay(mockHistoryService, mockItemService, mockSyncBoxItem1, mockSyncBoxItem2, mockSyncBoxItem3);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        setPrivateField(InventoryServiceImpl.class, inventoryService, "historyService", mockHistoryService);
        setPrivateField(InventoryServiceImpl.class, inventoryService, "itemService", mockItemService);

        assertWholeItemCount(0);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBoxRegion dbBoxRegion = inventoryService.getBoxRegionCrud().createDbChild();
        dbBoxRegion.setItemFreeRange(100);
        dbBoxRegion.setMinInterval(200);
        dbBoxRegion.setMaxInterval(200);
        dbBoxRegion.setName("DbBoxRegion1");
        dbBoxRegion.setRegion(new Rectangle(100, 100, 1000, 1000));
        DbBoxRegionCount dbBoxRegionCount = dbBoxRegion.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount.setDbBoxItemType(itemService.getDbBoxItemType(TEST_BOX_ITEM_1_ID));
        dbBoxRegionCount.setCount(1);
        inventoryService.getBoxRegionCrud().updateDbChild(dbBoxRegion);
        inventoryService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Thread.sleep(790);

        EasyMock.verify(mockHistoryService, mockItemService, mockSyncBoxItem1, mockSyncBoxItem2, mockSyncBoxItem3);

    }

    @Test
    @DirtiesContext
    public void testDbBoxRegionMockMultipleRegions() throws Exception {
        InventoryServiceImpl.SCHEDULE_RATE = 25;
        configureRealGame();

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
        SyncBoxItem mockSyncBoxItem8 = EasyMock.createStrictMock(SyncBoxItem.class);
        EasyMock.expect(mockSyncBoxItem8.isInTTL()).andReturn(true);
        EasyMock.expect(mockSyncBoxItem8.isInTTL()).andReturn(false);
        SyncBoxItem mockSyncBoxItem9 = EasyMock.createStrictMock(SyncBoxItem.class);
        EasyMock.expect(mockSyncBoxItem9.isInTTL()).andReturn(true);
        EasyMock.expect(mockSyncBoxItem9.isInTTL()).andReturn(false);
        SyncBoxItem mockSyncBoxItem10 = EasyMock.createStrictMock(SyncBoxItem.class);
        EasyMock.expect(mockSyncBoxItem10.isInTTL()).andReturn(true);
        EasyMock.expect(mockSyncBoxItem10.isInTTL()).andReturn(false);
        // History Service
        HistoryService mockHistoryService = EasyMock.createNiceMock(HistoryService.class);
        // ItemService
        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        DbBoxItemType dbBoxItemType1 = itemService.getDbBoxItemType(TEST_BOX_ITEM_1_ID);
        BoxItemType boxItemType1 = (BoxItemType) itemService.getItemType(TEST_BOX_ITEM_1_ID);
        DbBoxItemType dbBoxItemType2 = itemService.getDbBoxItemType(TEST_BOX_ITEM_2_ID);
        BoxItemType boxItemType2 = (BoxItemType) itemService.getItemType(TEST_BOX_ITEM_2_ID);

        EasyMock.expect(mockItemService.getItemType(dbBoxItemType1)).andReturn(boxItemType1);
        EasyMock.expect(mockItemService.createSyncObject(EasyMock.eq(boxItemType1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull(), EasyMock.eq(0))).andReturn(mockSyncBoxItem1);
        EasyMock.expect(mockItemService.createSyncObject(EasyMock.eq(boxItemType1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull(), EasyMock.eq(0))).andReturn(mockSyncBoxItem2);
        EasyMock.expect(mockItemService.getItemType(dbBoxItemType2)).andReturn(boxItemType2);
        EasyMock.expect(mockItemService.createSyncObject(EasyMock.eq(boxItemType2), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull(), EasyMock.eq(0))).andReturn(mockSyncBoxItem7);
        mockItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem1), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));
        mockItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem2), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));
        mockItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem7), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));

        EasyMock.expect(mockItemService.getItemType(dbBoxItemType1)).andReturn(boxItemType1);
        EasyMock.expect(mockItemService.createSyncObject(EasyMock.eq(boxItemType1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull(), EasyMock.eq(0))).andReturn(mockSyncBoxItem3);
        EasyMock.expect(mockItemService.createSyncObject(EasyMock.eq(boxItemType1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull(), EasyMock.eq(0))).andReturn(mockSyncBoxItem4);
        EasyMock.expect(mockItemService.getItemType(dbBoxItemType2)).andReturn(boxItemType2);
        EasyMock.expect(mockItemService.createSyncObject(EasyMock.eq(boxItemType2), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull(), EasyMock.eq(0))).andReturn(mockSyncBoxItem8);
        mockItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem3), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));
        mockItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem4), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));
        mockItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem8), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));

        EasyMock.expect(mockItemService.getItemType(dbBoxItemType1)).andReturn(boxItemType1);
        EasyMock.expect(mockItemService.createSyncObject(EasyMock.eq(boxItemType1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull(), EasyMock.eq(0))).andReturn(mockSyncBoxItem10);
        mockItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem10), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));

        EasyMock.expect(mockItemService.getItemType(dbBoxItemType1)).andReturn(boxItemType1);
        EasyMock.expect(mockItemService.createSyncObject(EasyMock.eq(boxItemType1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull(), EasyMock.eq(0))).andReturn(mockSyncBoxItem5);
        EasyMock.expect(mockItemService.createSyncObject(EasyMock.eq(boxItemType1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull(), EasyMock.eq(0))).andReturn(mockSyncBoxItem6);
        EasyMock.expect(mockItemService.getItemType(dbBoxItemType2)).andReturn(boxItemType2);
        EasyMock.expect(mockItemService.createSyncObject(EasyMock.eq(boxItemType2), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull(), EasyMock.eq(0))).andReturn(mockSyncBoxItem9);
        mockItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem5), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));
        mockItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem6), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));
        mockItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem9), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));

        EasyMock.replay(mockHistoryService, mockItemService, mockSyncBoxItem1, mockSyncBoxItem2, mockSyncBoxItem3, mockSyncBoxItem4, mockSyncBoxItem5, mockSyncBoxItem6, mockSyncBoxItem7, mockSyncBoxItem8, mockSyncBoxItem9, mockSyncBoxItem10);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        setPrivateField(InventoryServiceImpl.class, inventoryService, "historyService", mockHistoryService);
        setPrivateField(InventoryServiceImpl.class, inventoryService, "itemService", mockItemService);

        assertWholeItemCount(0);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBoxRegion dbBoxRegion1 = inventoryService.getBoxRegionCrud().createDbChild();
        dbBoxRegion1.setItemFreeRange(100);
        dbBoxRegion1.setMinInterval(200);
        dbBoxRegion1.setMaxInterval(200);
        dbBoxRegion1.setName("DbBoxRegion1");
        dbBoxRegion1.setRegion(new Rectangle(100, 100, 1000, 1000));
        DbBoxRegionCount dbBoxRegionCount1 = dbBoxRegion1.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount1.setDbBoxItemType(itemService.getDbBoxItemType(TEST_BOX_ITEM_1_ID));
        dbBoxRegionCount1.setCount(2);
        DbBoxRegionCount dbBoxRegionCount2 = dbBoxRegion1.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount2.setDbBoxItemType(itemService.getDbBoxItemType(TEST_BOX_ITEM_2_ID));
        dbBoxRegionCount2.setCount(1);
        inventoryService.getBoxRegionCrud().updateDbChild(dbBoxRegion1);

        DbBoxRegion dbBoxRegion2 = inventoryService.getBoxRegionCrud().createDbChild();
        dbBoxRegion2.setItemFreeRange(100);
        dbBoxRegion2.setMinInterval(500);
        dbBoxRegion2.setMaxInterval(500);
        dbBoxRegion2.setName("DbBoxRegion1");
        dbBoxRegion2.setRegion(new Rectangle(100, 100, 1000, 1000));
        DbBoxRegionCount dbBoxRegionCount = dbBoxRegion2.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount.setDbBoxItemType(itemService.getDbBoxItemType(TEST_BOX_ITEM_1_ID));
        dbBoxRegionCount.setCount(1);
        inventoryService.getBoxRegionCrud().updateDbChild(dbBoxRegion2);


        inventoryService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Thread.sleep(790);

        EasyMock.verify(mockHistoryService, mockItemService, mockSyncBoxItem1, mockSyncBoxItem2, mockSyncBoxItem3, mockSyncBoxItem4, mockSyncBoxItem5, mockSyncBoxItem6, mockSyncBoxItem7, mockSyncBoxItem8, mockSyncBoxItem9, mockSyncBoxItem10);
    }

    @Test
    @DirtiesContext
    public void testDbBoxRegionMockLongRunningBox() throws Exception {
        InventoryServiceImpl.SCHEDULE_RATE = 25;
        configureRealGame();

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
        // ItemService
        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        DbBoxItemType dbBoxItemType1 = itemService.getDbBoxItemType(TEST_BOX_ITEM_1_ID);
        BoxItemType boxItemType1 = (BoxItemType) itemService.getItemType(TEST_BOX_ITEM_1_ID);

        EasyMock.expect(mockItemService.getItemType(dbBoxItemType1)).andReturn(boxItemType1);
        EasyMock.expect(mockItemService.createSyncObject(EasyMock.eq(boxItemType1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull(), EasyMock.eq(0))).andReturn(mockSyncBoxItem1);

        EasyMock.expect(mockItemService.getItemType(dbBoxItemType1)).andReturn(boxItemType1);
        EasyMock.expect(mockItemService.createSyncObject(EasyMock.eq(boxItemType1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull(), EasyMock.eq(0))).andReturn(mockSyncBoxItem2);

        EasyMock.expect(mockItemService.getItemType(dbBoxItemType1)).andReturn(boxItemType1);
        EasyMock.expect(mockItemService.createSyncObject(EasyMock.eq(boxItemType1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull(), EasyMock.eq(0))).andReturn(mockSyncBoxItem3);
        mockItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem3), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));

        mockItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem2), (SimpleBase) EasyMock.isNull(), EasyMock.eq(true), EasyMock.eq(false));

        EasyMock.replay(mockHistoryService, mockItemService, mockSyncBoxItem1, mockSyncBoxItem2, mockSyncBoxItem3);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        setPrivateField(InventoryServiceImpl.class, inventoryService, "historyService", mockHistoryService);
        setPrivateField(InventoryServiceImpl.class, inventoryService, "itemService", mockItemService);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBoxRegion dbBoxRegion1 = inventoryService.getBoxRegionCrud().createDbChild();
        dbBoxRegion1.setItemFreeRange(100);
        dbBoxRegion1.setMinInterval(200);
        dbBoxRegion1.setMaxInterval(200);
        dbBoxRegion1.setName("DbBoxRegion1");
        dbBoxRegion1.setRegion(new Rectangle(100, 100, 1000, 1000));
        DbBoxRegionCount dbBoxRegionCount1 = dbBoxRegion1.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount1.setDbBoxItemType(itemService.getDbBoxItemType(TEST_BOX_ITEM_1_ID));
        dbBoxRegionCount1.setCount(1);
        inventoryService.getBoxRegionCrud().updateDbChild(dbBoxRegion1);

        inventoryService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Thread.sleep(790);

        EasyMock.verify(mockHistoryService, mockItemService, mockSyncBoxItem1, mockSyncBoxItem2, mockSyncBoxItem3);
    }

    @Test
    @DirtiesContext
    public void testPickupReal() throws Exception {
        InventoryServiceImpl.SCHEDULE_RATE = 25;
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createDbBoxItemType2();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertWholeItemCount(0);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBoxRegion dbBoxRegion = inventoryService.getBoxRegionCrud().createDbChild();
        dbBoxRegion.setItemFreeRange(100);
        dbBoxRegion.setMinInterval(200);
        dbBoxRegion.setMaxInterval(200);
        dbBoxRegion.setName("DbBoxRegion1");
        dbBoxRegion.setRegion(new Rectangle(100, 100, 1000, 1000));
        DbBoxRegionCount dbBoxRegionCount = dbBoxRegion.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount.setDbBoxItemType(itemService.getDbBoxItemType(TEST_BOX_ITEM_2_ID));
        dbBoxRegionCount.setCount(1);
        inventoryService.getBoxRegionCrud().updateDbChild(dbBoxRegion);
        inventoryService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Thread.sleep(230);
        List<SyncItem> syncItems = itemService.getItemsCopy();
        Assert.assertEquals(1, syncItems.size());
        SyncBoxItem boxItem = (SyncBoxItem) syncItems.get(0);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertNoHistoryType(DbHistoryElement.Type.BOX_PICKED);
        sendPickupBoxCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), boxItem.getId());
        waitForHistoryType(DbHistoryElement.Type.BOX_PICKED);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testPickupMock() throws Exception {
        InventoryServiceImpl.SCHEDULE_RATE = 25;
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createDbBoxItemType1();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        SimpleBase simpleBase = new SimpleBase(1);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        BoxItemType boxItemType1 = (BoxItemType) itemService.getItemType(TEST_BOX_ITEM_1_ID);
        DbBoxItemType dbBoxItemTypeMock = new DbBoxItemType();
        setupDbItemTypeId(dbBoxItemTypeMock, TEST_BOX_ITEM_1_ID);
        // Picker
        SyncBaseItem mockPicker = EasyMock.createStrictMock(SyncBaseItem.class);
        EasyMock.expect(mockPicker.getBase()).andReturn(simpleBase).times(4);
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
        // ItemService
        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.getItemType(dbBoxItemTypeMock)).andReturn(boxItemType1);
        EasyMock.expect(mockItemService.createSyncObject(EasyMock.eq(boxItemType1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull(), EasyMock.eq(0))).andReturn(mockSyncBoxItem1);
        mockItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem1), EasyMock.eq(simpleBase), EasyMock.eq(true), EasyMock.eq(false));
        EasyMock.expect(mockItemService.getDbBoxItemType(TEST_BOX_ITEM_1_ID)).andReturn(dbBoxItemTypeMock);

        EasyMock.replay(mockHistoryService, mockItemService, mockSyncBoxItem1, mockPicker, mockBaseService, mockUserState);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        setPrivateField(InventoryServiceImpl.class, inventoryService, "historyService", mockHistoryService);
        setPrivateField(InventoryServiceImpl.class, inventoryService, "itemService", mockItemService);
        setPrivateField(InventoryServiceImpl.class, inventoryService, "baseService", mockBaseService);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBoxRegion dbBoxRegion1 = inventoryService.getBoxRegionCrud().createDbChild();
        dbBoxRegion1.setItemFreeRange(100);
        dbBoxRegion1.setMinInterval(200);
        dbBoxRegion1.setMaxInterval(200);
        dbBoxRegion1.setName("DbBoxRegion1");
        dbBoxRegion1.setRegion(new Rectangle(100, 100, 1000, 1000));
        DbBoxRegionCount dbBoxRegionCount1 = dbBoxRegion1.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount1.setDbBoxItemType(itemService.getDbBoxItemType(TEST_BOX_ITEM_1_ID));
        dbBoxRegionCount1.setCount(1);
        inventoryService.getBoxRegionCrud().updateDbChild(dbBoxRegion1);

        inventoryService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Thread.sleep(300);
        inventoryService.onSyncBoxItemPicked(mockSyncBoxItem1, mockPicker);

        EasyMock.verify(mockHistoryService, mockItemService, mockSyncBoxItem1, mockPicker, mockBaseService, mockUserState);
    }

    @Test
    @DirtiesContext
    public void testDropBoxOnKillReal() throws Exception {
        InventoryServiceImpl.SCHEDULE_RATE = 25;
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createDbBoxItemType1();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType startupItem = itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID);
        startupItem.setDropBoxPossibility(1.0);
        startupItem.setDbBoxItemType(itemService.getDbBoxItemType(TEST_BOX_ITEM_1_ID));
        itemService.saveDbItemType(startupItem);
        itemService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Target item
        Id targetId = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        sendMoveCommand(targetId, new Index(1000, 1000));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
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
        targetId = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        sendMoveCommand(targetId, new Index(1000, 5000));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, getAllHistoryEntriesOfType(DbHistoryElement.Type.BOX_DROPPED).size());
        itemService.killSyncItem(itemService.getItem(targetId), null, true, false);
        Assert.assertEquals(1, getAllHistoryEntriesOfType(DbHistoryElement.Type.BOX_DROPPED).size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }

    @Test
    @DirtiesContext
    public void testDropBoxOnKillMock() throws Exception {
        InventoryServiceImpl.SCHEDULE_RATE = 25;
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createDbBoxItemType1();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBoxItemType dbBoxItemType1 = itemService.getDbBoxItemType(TEST_BOX_ITEM_1_ID);

        // SyncBoxItem
        SyncBoxItem mockSyncBoxItem1 = EasyMock.createStrictMock(SyncBoxItem.class);
        EasyMock.expect(mockSyncBoxItem1.isInTTL()).andReturn(true).anyTimes();
        // Dropper
        DbBaseItemType dbDropperType = EasyMock.createStrictMock(DbBaseItemType.class);
        EasyMock.expect(dbDropperType.getDbBoxItemType()).andReturn(dbBoxItemType1);
        BaseItemType dropperType = EasyMock.createStrictMock(BaseItemType.class);
        EasyMock.expect(dropperType.getId()).andReturn(1);
        SyncBaseItem mockDropper = EasyMock.createStrictMock(SyncBaseItem.class);
        EasyMock.expect(mockDropper.getDropBoxPossibility()).andReturn(1.0);
        EasyMock.expect(mockDropper.getBaseItemType()).andReturn(dropperType);
        SyncItemArea dropperArea = new SyncItemArea(new BoundingBox(10, 10, 10, 10, new double[]{}), new Index(100, 100));
        EasyMock.expect(mockDropper.getSyncItemArea()).andReturn(dropperArea);
        // History Service
        HistoryService mockHistoryService = EasyMock.createStrictMock(HistoryService.class);
        mockHistoryService.addBoxDropped(mockSyncBoxItem1, new Index(100, 100), mockDropper);
        // ItemService
        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.getDbBaseItemType(1)).andReturn(dbDropperType);
        BoxItemType boxItemType1 = (BoxItemType) itemService.getItemType(TEST_BOX_ITEM_1_ID);
        EasyMock.expect(mockItemService.getItemType(dbBoxItemType1)).andReturn(boxItemType1);
        EasyMock.expect(mockItemService.createSyncObject(boxItemType1, new Index(100, 100), null, null, 0)).andReturn(mockSyncBoxItem1);

        EasyMock.replay(mockHistoryService, mockItemService, mockSyncBoxItem1, mockDropper, dropperType, dbDropperType);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        setPrivateField(InventoryServiceImpl.class, inventoryService, "historyService", mockHistoryService);
        setPrivateField(InventoryServiceImpl.class, inventoryService, "itemService", mockItemService);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        inventoryService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        inventoryService.onSyncBaseItemKilled(mockDropper);

        EasyMock.verify(mockHistoryService, mockItemService, mockSyncBoxItem1, mockDropper, dropperType, dbDropperType);
    }

    @Test
    @DirtiesContext
    public void backupRestoreSyncBoxItems() throws Exception {
        InventoryServiceImpl.SCHEDULE_RATE = 25;
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createDbBoxItemType2();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertWholeItemCount(0);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBoxRegion dbBoxRegion = inventoryService.getBoxRegionCrud().createDbChild();
        dbBoxRegion.setItemFreeRange(100);
        dbBoxRegion.setMinInterval(200);
        dbBoxRegion.setMaxInterval(200);
        dbBoxRegion.setName("DbBoxRegion1");
        dbBoxRegion.setRegion(new Rectangle(100, 100, 1000, 1000));
        DbBoxRegionCount dbBoxRegionCount = dbBoxRegion.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount.setDbBoxItemType(itemService.getDbBoxItemType(TEST_BOX_ITEM_2_ID));
        dbBoxRegionCount.setCount(1);
        inventoryService.getBoxRegionCrud().updateDbChild(dbBoxRegion);
        inventoryService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Thread.sleep(230);
        List<SyncItem> syncItems = itemService.getItemsCopy();
        Assert.assertEquals(1, syncItems.size());
        Assert.assertTrue(syncItems.get(0) instanceof SyncBoxItem);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = mgmtService.getBackupSummary();
        assertBackupSummery(1, 0, 0, 0);
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertWholeItemCount(0);

        Thread.sleep(230);
        syncItems = itemService.getItemsCopy();
        Assert.assertEquals(1, syncItems.size());
        Assert.assertTrue(syncItems.get(0) instanceof SyncBoxItem);
    }

    @Test
    @DirtiesContext
    public void backupRestoreSyncBoxItemsAsTarget() throws Exception {
        InventoryServiceImpl.SCHEDULE_RATE = 25;
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createDbBoxItemType2();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertWholeItemCount(0);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "xxx", "xxx", "");
        userService.login("U1", "xxx");
        Id target = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        sendMoveCommand(target, new Index(5000, 5000));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBoxRegion dbBoxRegion = inventoryService.getBoxRegionCrud().createDbChild();
        dbBoxRegion.setItemFreeRange(100);
        dbBoxRegion.setMinInterval(200);
        dbBoxRegion.setMaxInterval(200);
        dbBoxRegion.setName("DbBoxRegion1");
        dbBoxRegion.setRegion(new Rectangle(100, 100, 100, 100));
        DbBoxRegionCount dbBoxRegionCount = dbBoxRegion.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount.setDbBoxItemType(itemService.getDbBoxItemType(TEST_BOX_ITEM_2_ID));
        dbBoxRegionCount.setCount(1);
        inventoryService.getBoxRegionCrud().updateDbChild(dbBoxRegion);
        inventoryService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Thread.sleep(230);
        List<SyncItem> syncItems = itemService.getItemsCopy();
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
        userService.login("U1", "xxx");
        sendPickupBoxCommand(target, syncBoxItem.getId());
        Assert.assertNotNull(((SyncBaseItem) itemService.getItem(target)).getSyncMovable().getSyncBoxItemId());
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = mgmtService.getBackupSummary();
        assertBackupSummery(1, 1, 1, 1);
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertWholeItemCount(1);
        Assert.assertNull(((SyncBaseItem) itemService.getItemsCopy().get(0)).getSyncMovable().getSyncBoxItemId());

        Thread.sleep(230);
        syncItems = itemService.getItemsCopy();
        Assert.assertEquals(2, syncItems.size());
        Assert.assertTrue(syncItems.get(0) instanceof SyncBoxItem);
    }

    @Test
    @DirtiesContext
    public void pickupInventoryItemReal() throws Exception {
        InventoryServiceImpl.SCHEDULE_RATE = 25;
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryItem dbInventoryItem = inventoryService.getItemCrud().createDbChild();
        DbInventoryArtifact dbInventoryArtifact = inventoryService.getArtifactCrud().createDbChild();
        DbBoxItemType dbBoxItemType = (DbBoxItemType) itemService.getDbItemTypeCrud().createDbChild(DbBoxItemType.class);
        dbBoxItemType.setTerrainType(TerrainType.LAND);
        setupImages(dbBoxItemType, 1);
        dbBoxItemType.setBounding(new BoundingBox(100, 100, 80, 80, ANGELS_1));
        dbBoxItemType.setTtl(5000);
        DbBoxItemTypePossibility dbBoxItemTypePossibility1 = dbBoxItemType.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility1.setPossibility(1.0);
        dbBoxItemTypePossibility1.setDbInventoryItem(dbInventoryItem);
        DbBoxItemTypePossibility dbBoxItemTypePossibility2 = dbBoxItemType.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility2.setPossibility(1.0);
        dbBoxItemTypePossibility2.setDbInventoryArtifact(dbInventoryArtifact);
        DbBoxItemTypePossibility dbBoxItemTypePossibility3 = dbBoxItemType.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility3.setPossibility(1.0);
        dbBoxItemTypePossibility3.setRazarion(100);
        itemService.saveDbItemType(dbBoxItemType);
        itemService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertWholeItemCount(0);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBoxRegion dbBoxRegion = inventoryService.getBoxRegionCrud().createDbChild();
        dbBoxRegion.setItemFreeRange(100);
        dbBoxRegion.setMinInterval(200);
        dbBoxRegion.setMaxInterval(200);
        dbBoxRegion.setName("DbBoxRegion1");
        dbBoxRegion.setRegion(new Rectangle(100, 100, 1000, 1000));
        DbBoxRegionCount dbBoxRegionCount = dbBoxRegion.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount.setDbBoxItemType(dbBoxItemType);
        dbBoxRegionCount.setCount(1);
        inventoryService.getBoxRegionCrud().updateDbChild(dbBoxRegion);
        inventoryService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Thread.sleep(230);
        List<SyncItem> syncItems = itemService.getItemsCopy();
        Assert.assertEquals(1, syncItems.size());
        SyncBoxItem boxItem = (SyncBoxItem) syncItems.get(0);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertNoHistoryType(DbHistoryElement.Type.INVENTORY_ITEM_FROM_BOX);
        assertNoHistoryType(DbHistoryElement.Type.INVENTORY_ARTIFACT_FROM_BOX);
        assertNoHistoryType(DbHistoryElement.Type.RAZARION_FROM_BOX);
        sendPickupBoxCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), boxItem.getId());
        waitForActionServiceDone();
        waitForHistoryType(DbHistoryElement.Type.INVENTORY_ITEM_FROM_BOX);
        waitForHistoryType(DbHistoryElement.Type.INVENTORY_ARTIFACT_FROM_BOX);
        waitForHistoryType(DbHistoryElement.Type.RAZARION_FROM_BOX);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void pickupInventoryItemMock() throws Exception {
        InventoryServiceImpl.SCHEDULE_RATE = 25;
        configureRealGame();

        SimpleBase simpleBase = new SimpleBase(1);
        DbBoxItemType dbBoxItemType1 = new DbBoxItemType();
        setupDbItemTypeId(dbBoxItemType1, 1);
        DbBoxItemTypePossibility dbBoxItemTypePossibility = dbBoxItemType1.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility.setPossibility(1.0);
        dbBoxItemTypePossibility.setRazarion(100);
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
        // ItemService
        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        mockItemService.killSyncItem(mockSyncBoxItem1, simpleBase, true, false);
        EasyMock.expect(mockItemService.getDbBoxItemType(1)).andReturn(dbBoxItemType1);
        mockItemService.killSyncItem(mockSyncBoxItem1, simpleBase, true, false);
        EasyMock.expect(mockItemService.getDbBoxItemType(2)).andReturn(dbBoxItemType2);
        mockItemService.killSyncItem(mockSyncBoxItem1, simpleBase, true, false);
        EasyMock.expect(mockItemService.getDbBoxItemType(3)).andReturn(dbBoxItemType3);
        mockItemService.killSyncItem(mockSyncBoxItem1, simpleBase, true, false);
        EasyMock.expect(mockItemService.getDbBoxItemType(4)).andReturn(dbBoxItemType4);
        // Picker
        SyncBaseItem picker = EasyMock.createStrictMock(SyncBaseItem.class);
        EasyMock.expect(picker.getBase()).andReturn(simpleBase).times(16);
        // SyncBoxItems
        UserState mockUserState = EasyMock.createStrictMock(UserState.class);
        mockUserState.addRazarion(100);
        mockUserState.addInventoryItem(33);
        mockUserState.addInventoryArtifact(44);
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
        mockHistoryService.addRazarionFromBox(mockUserState, 100);
        mockHistoryService.addBoxPicked(mockSyncBoxItem1, picker);
        mockHistoryService.addInventoryItemFromBox(mockUserState, "InventoryItem");
        mockHistoryService.addBoxPicked(mockSyncBoxItem1, picker);
        mockHistoryService.addInventoryArtifactFromBox(mockUserState, "InventoryArtifact");
        mockHistoryService.addBoxPicked(mockSyncBoxItem1, picker);
        // Connection Service
        ConnectionServiceTestHelper connectionService = new ConnectionServiceTestHelper();

        EasyMock.replay(mockHistoryService, mockItemService, mockSyncBoxItem1, picker, mockBaseService, boxItemType, mockUserState);

        setPrivateField(InventoryServiceImpl.class, inventoryService, "historyService", mockHistoryService);
        setPrivateField(InventoryServiceImpl.class, inventoryService, "itemService", mockItemService);
        setPrivateField(InventoryServiceImpl.class, inventoryService, "baseService", mockBaseService);
        setPrivateField(InventoryServiceImpl.class, inventoryService, "connectionService", connectionService);

        inventoryService.onSyncBoxItemPicked(mockSyncBoxItem1, picker);
        Assert.assertEquals(1, connectionService.getPacketEntries().size());
        BoxPickedPacket boxPickedPacket = (BoxPickedPacket) connectionService.getPacketEntries(simpleBase, BoxPickedPacket.class).get(0).getPacket();
        Assert.assertEquals("You picked up a box! Items added to your Inventory:<ul><li>Razarion: 100</li></ul>", boxPickedPacket.getHtml());
        connectionService.clearReceivedPackets();
        inventoryService.onSyncBoxItemPicked(mockSyncBoxItem1, picker);
        Assert.assertEquals(1, connectionService.getPacketEntries().size());
        boxPickedPacket = (BoxPickedPacket) connectionService.getPacketEntries(simpleBase, BoxPickedPacket.class).get(0).getPacket();
        Assert.assertEquals("You picked up a box! Items added to your Inventory:<ul><li>Item: InventoryItem</li></ul>", boxPickedPacket.getHtml());
        connectionService.clearReceivedPackets();
        inventoryService.onSyncBoxItemPicked(mockSyncBoxItem1, picker);
        Assert.assertEquals(1, connectionService.getPacketEntries().size());
        boxPickedPacket = (BoxPickedPacket) connectionService.getPacketEntries(simpleBase, BoxPickedPacket.class).get(0).getPacket();
        Assert.assertEquals("You picked up a box! Items added to your Inventory:<ul><li>Artifact: InventoryArtifact</li></ul>", boxPickedPacket.getHtml());
        connectionService.clearReceivedPackets();
        inventoryService.onSyncBoxItemPicked(mockSyncBoxItem1, picker);
        Assert.assertEquals(1, connectionService.getPacketEntries().size());
        boxPickedPacket = (BoxPickedPacket) connectionService.getPacketEntries(simpleBase, BoxPickedPacket.class).get(0).getPacket();
        Assert.assertEquals("You picked up a box! Items added to your Inventory:<ul><li>No luck. Empty box found</li></ul>", boxPickedPacket.getHtml());

        EasyMock.verify(mockHistoryService, mockItemService, mockSyncBoxItem1, picker, mockBaseService, boxItemType, mockUserState);
    }

    @Test
    @DirtiesContext
    public void backupRestoreUserState() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryArtifact dbInventoryArtifact1 = inventoryService.getArtifactCrud().createDbChild();
        DbInventoryArtifact dbInventoryArtifact2 = inventoryService.getArtifactCrud().createDbChild();
        DbInventoryArtifact dbInventoryArtifact3 = inventoryService.getArtifactCrud().createDbChild();
        DbInventoryItem dbInventoryItem1 = inventoryService.getItemCrud().createDbChild();
        DbInventoryItem dbInventoryItem2 = inventoryService.getItemCrud().createDbChild();
        DbInventoryItem dbInventoryItem3 = inventoryService.getItemCrud().createDbChild();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "xxx", "xxx", "");
        userService.login("U1", "xxx");
        getMyBase(); // Create Base
        UserState userState = userService.getUserState();
        userState.setRazarion(111);
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
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = mgmtService.getBackupSummary();
        assertBackupSummery(1, 1, 1, 1);
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "xxx");
        getMyBase(); // Create Base
        userState = userService.getUserState();
        Assert.assertEquals(111, userState.getRazarion());

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
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryArtifact dbInventoryArtifact1 = inventoryService.getArtifactCrud().createDbChild();
        DbInventoryArtifact dbInventoryArtifact2 = inventoryService.getArtifactCrud().createDbChild();
        DbInventoryArtifact dbInventoryArtifact3 = inventoryService.getArtifactCrud().createDbChild();
        DbInventoryItem dbInventoryItem1 = inventoryService.getItemCrud().createDbChild();
        DbInventoryArtifactCount dbInventoryArtifactCount = dbInventoryItem1.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(1);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact1);
        dbInventoryArtifactCount = dbInventoryItem1.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(2);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact2);
        dbInventoryArtifactCount = dbInventoryItem1.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(3);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact3);
        inventoryService.getItemCrud().updateDbChild(dbInventoryItem1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            inventoryService.assembleInventoryItem(dbInventoryItem1.getId());
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
            inventoryService.assembleInventoryItem(dbInventoryItem1.getId());
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        assertInventoryItemCount(userState, dbInventoryItem1, 0);
        userState.addInventoryArtifact(dbInventoryArtifact2.getId());
        userState.addInventoryArtifact(dbInventoryArtifact3.getId());
        try {
            inventoryService.assembleInventoryItem(dbInventoryItem1.getId());
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        assertInventoryItemCount(userState, dbInventoryItem1, 0);
        userState.addInventoryArtifact(dbInventoryArtifact3.getId());
        inventoryService.assembleInventoryItem(dbInventoryItem1.getId());
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
        inventoryService.assembleInventoryItem(dbInventoryItem1.getId());
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
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Map<Integer, InventoryArtifactInfo> allArtifacts = new HashMap<>();
        DbInventoryArtifact dbInventoryArtifact1 = inventoryService.getArtifactCrud().createDbChild();
        DbInventoryArtifact dbInventoryArtifact2 = inventoryService.getArtifactCrud().createDbChild();
        DbInventoryArtifact dbInventoryArtifact3 = inventoryService.getArtifactCrud().createDbChild();
        allArtifacts.put(dbInventoryArtifact1.getId(), dbInventoryArtifact1.generateInventoryArtifactInfo());
        allArtifacts.put(dbInventoryArtifact2.getId(), dbInventoryArtifact2.generateInventoryArtifactInfo());
        allArtifacts.put(dbInventoryArtifact3.getId(), dbInventoryArtifact3.generateInventoryArtifactInfo());
        DbInventoryItem dbInventoryItem1 = inventoryService.getItemCrud().createDbChild();
        DbInventoryArtifactCount dbInventoryArtifactCount = dbInventoryItem1.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(1);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact1);
        dbInventoryArtifactCount = dbInventoryItem1.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(2);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact2);
        dbInventoryArtifactCount = dbInventoryItem1.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(3);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact3);
        inventoryService.getItemCrud().updateDbChild(dbInventoryItem1);
        DbInventoryItem dbInventoryItem2 = inventoryService.getItemCrud().createDbChild();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Verify new
        InventoryInfo inventoryInfo = inventoryService.getInventory();
        Assert.assertEquals(0, inventoryInfo.getRazarion());
        Assert.assertTrue(inventoryInfo.getOwnInventoryArtifacts().isEmpty());
        Assert.assertTrue(inventoryInfo.getOwnInventoryItems().isEmpty());
        Assert.assertEquals(2, inventoryInfo.getAllInventoryItemInfos().size());
        Assert.assertEquals(3, inventoryInfo.getAllInventoryArtifactInfos().size());
        // Add one artifact
        UserState userState = userService.getUserState();
        userState.setRazarion(15);
        userState.addInventoryArtifact(dbInventoryArtifact1.getId());
        // Verify
        inventoryInfo = inventoryService.getInventory();
        Assert.assertEquals(15, inventoryInfo.getRazarion());
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
        inventoryInfo = inventoryService.getInventory();
        Assert.assertEquals(15, inventoryInfo.getRazarion());
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
        inventoryInfo = inventoryService.getInventory();
        Assert.assertEquals(15, inventoryInfo.getRazarion());
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
        inventoryInfo = inventoryService.getInventory();
        Assert.assertEquals(15, inventoryInfo.getRazarion());
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
    public void useInventoryItem() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryItem dbInventoryItem1 = inventoryService.getItemCrud().createDbChild();
        dbInventoryItem1.setItemFreeRange(100);
        dbInventoryItem1.setBaseItemTypeCount(1);
        dbInventoryItem1.setDbBaseItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        dbInventoryItem1.setName("dbInventoryItem1");
        inventoryService.getItemCrud().updateDbChild(dbInventoryItem1);
        DbInventoryItem dbInventoryItem2 = inventoryService.getItemCrud().createDbChild();
        dbInventoryItem2.setItemFreeRange(100);
        dbInventoryItem2.setBaseItemTypeCount(3);
        dbInventoryItem2.setDbBaseItemType(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        dbInventoryItem2.setName("dbInventoryItem2");
        inventoryService.getItemCrud().updateDbChild(dbInventoryItem2);
        DbInventoryItem dbInventoryItem3 = inventoryService.getItemCrud().createDbChild();
        dbInventoryItem3.setGoldAmount(20);
        dbInventoryItem3.setName("dbInventoryItem3");
        inventoryService.getItemCrud().updateDbChild(dbInventoryItem3);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Base base = new Base(1);

        // ItemService
        DbBaseItemType dbAttackItemType = (DbBaseItemType) itemService.getDbItemType(TEST_ATTACK_ITEM_ID);
        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.getItemType(dbAttackItemType)).andReturn(itemService.getItemType(TEST_ATTACK_ITEM_ID));
        EasyMock.expect(mockItemService.getItemType(dbAttackItemType)).andReturn(itemService.getItemType(TEST_ATTACK_ITEM_ID));
        EasyMock.expect(mockItemService.getItemType(dbAttackItemType)).andReturn(itemService.getItemType(TEST_ATTACK_ITEM_ID));
        EasyMock.expect(mockItemService.hasItemsInRectangle(new Rectangle(960, 960, 80, 80))).andReturn(true);
        EasyMock.expect(mockItemService.getItemType(dbAttackItemType)).andReturn(itemService.getItemType(TEST_ATTACK_ITEM_ID));
        EasyMock.expect(mockItemService.hasItemsInRectangle(new Rectangle(960, 960, 80, 80))).andReturn(false);
        EasyMock.expect(mockItemService.hasEnemyInRange(base.getSimpleBase(), new Index(1000, 1000), 156)).andReturn(true);
        EasyMock.expect(mockItemService.getItemType(dbAttackItemType)).andReturn(itemService.getItemType(TEST_ATTACK_ITEM_ID));
        EasyMock.expect(mockItemService.hasItemsInRectangle(new Rectangle(960, 960, 80, 80))).andReturn(false);
        EasyMock.expect(mockItemService.hasEnemyInRange(base.getSimpleBase(), new Index(1000, 1000), 156)).andReturn(false);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_ATTACK_ITEM_ID), new Index(1000, 1000), null, base.getSimpleBase(), 0)).andReturn(null);

        setPrivateField(InventoryServiceImpl.class, inventoryService, "itemService", mockItemService);
        // Terrain Service
        TerrainService mockTerrainService = EasyMock.createStrictMock(TerrainService.class);
        EasyMock.expect(mockTerrainService.isFree(new Index(1000, 1000), itemService.getItemType(TEST_ATTACK_ITEM_ID))).andReturn(false);
        EasyMock.expect(mockTerrainService.isFree(new Index(1000, 1000), itemService.getItemType(TEST_ATTACK_ITEM_ID))).andReturn(true);
        EasyMock.expect(mockTerrainService.isFree(new Index(1000, 1000), itemService.getItemType(TEST_ATTACK_ITEM_ID))).andReturn(true);
        EasyMock.expect(mockTerrainService.isFree(new Index(1000, 1000), itemService.getItemType(TEST_ATTACK_ITEM_ID))).andReturn(true);
        EasyMock.expect(mockTerrainService.isFree(new Index(1000, 1000), itemService.getItemType(TEST_ATTACK_ITEM_ID))).andReturn(true);
        setPrivateField(InventoryServiceImpl.class, inventoryService, "terrainService", mockTerrainService);
        // Territory Service
        TerritoryService mockTerritoryService = EasyMock.createStrictMock(TerritoryService.class);
        EasyMock.expect(mockTerritoryService.isAllowed(new Index(1000, 1000), (BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID))).andReturn(false);
        EasyMock.expect(mockTerritoryService.isAllowed(new Index(1000, 1000), (BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID))).andReturn(true);
        EasyMock.expect(mockTerritoryService.isAllowed(new Index(1000, 1000), (BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID))).andReturn(true);
        EasyMock.expect(mockTerritoryService.isAllowed(new Index(1000, 1000), (BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID))).andReturn(true);
        setPrivateField(InventoryServiceImpl.class, inventoryService, "territoryService", mockTerritoryService);
        // History Service
        HistoryService mockHistoryService = EasyMock.createStrictMock(HistoryService.class);
        mockHistoryService.addInventoryItemUsed(userService.getUserState(), "dbInventoryItem3");
        mockHistoryService.addInventoryItemUsed(userService.getUserState(), "dbInventoryItem1");
        setPrivateField(InventoryServiceImpl.class, inventoryService, "historyService", mockHistoryService);
        // History Service
        BaseService mockBaseService = EasyMock.createStrictMock(BaseService.class);
        EasyMock.expect(mockBaseService.getBase(userService.getUserState())).andReturn(null);
        EasyMock.expect(mockBaseService.getBase(userService.getUserState())).andReturn(base);
        mockBaseService.depositResource(20, base.getSimpleBase());
        mockBaseService.sendAccountBaseUpdate(base.getSimpleBase());
        EasyMock.expect(mockBaseService.getBase(userService.getUserState())).andReturn(base);
        EasyMock.expect(mockBaseService.getBase(userService.getUserState())).andReturn(base);
        EasyMock.expect(mockBaseService.getBase(userService.getUserState())).andReturn(base);
        EasyMock.expect(mockBaseService.getBase(userService.getUserState())).andReturn(base);
        EasyMock.expect(mockBaseService.getBase(userService.getUserState())).andReturn(base);
        EasyMock.expect(mockBaseService.getBase(userService.getUserState())).andReturn(base);
        setPrivateField(InventoryServiceImpl.class, inventoryService, "baseService", mockBaseService);

        EasyMock.replay(mockItemService, mockTerrainService, mockTerritoryService, mockHistoryService, mockBaseService);

        // Use gold but no inventory item
        Assert.assertTrue(userService.getUserState().getInventoryItemIds().isEmpty());
        try {
            inventoryService.useInventoryItem(dbInventoryItem3.getId(), null);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("User does not have inventory item: DbInventoryItem{id=3, name='dbInventoryItem3'} user: UserState: user=null", e.getMessage());
        }
        // No base
        userService.getUserState().addInventoryItem(dbInventoryItem3.getId());
        try {
            inventoryService.useInventoryItem(dbInventoryItem3.getId(), null);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User does not have a base: UserState: user=null", e.getMessage());
        }
        // Use gold
        inventoryService.useInventoryItem(dbInventoryItem3.getId(), null);
        Assert.assertTrue(userService.getUserState().getInventoryItemIds().isEmpty());
        // Use BaseItemType inventory item invalid size
        userService.getUserState().addInventoryItem(dbInventoryItem1.getId());
        try {
            inventoryService.useInventoryItem(dbInventoryItem1.getId(), Collections.<Index>emptyList());
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("positionToBePlaced.size() != dbInventoryItem.getBaseItemTypeCount() 0 1", e.getMessage());
        }
        // Use item on wrong terrain
        try {
            inventoryService.useInventoryItem(dbInventoryItem1.getId(), Collections.singletonList(new Index(1000, 1000)));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Terrain is not free x: 1000 y: 1000 ItemType: TestAttackItem UserState: user=null", e.getMessage());
        }
        // Wrong territory
        try {
            inventoryService.useInventoryItem(dbInventoryItem1.getId(), Collections.singletonList(new Index(1000, 1000)));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Item not allowed on territory x: 1000 y: 1000 ItemType: TestAttackItem UserState: user=null", e.getMessage());
        }
        // Use item over other unit
        try {
            inventoryService.useInventoryItem(dbInventoryItem1.getId(), Collections.singletonList(new Index(1000, 1000)));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Can not place over other items x: 1000 y: 1000 ItemType: TestAttackItem UserState: user=null", e.getMessage());
        }
        //Enemy items
        try {
            inventoryService.useInventoryItem(dbInventoryItem1.getId(), Collections.singletonList(new Index(1000, 1000)));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Enemy items too near x: 1000 y: 1000 ItemType: TestAttackItem UserState: user=null", e.getMessage());
        }

        inventoryService.useInventoryItem(dbInventoryItem1.getId(), Collections.singletonList(new Index(1000, 1000)));
        Assert.assertTrue(userService.getUserState().getInventoryItemIds().isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(mockItemService, mockTerrainService, mockTerritoryService, mockHistoryService, mockBaseService);
    }

    @Test
    @DirtiesContext
    public void useInventoryItemMultipleBaseItems() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryItem dbInventoryItem1 = inventoryService.getItemCrud().createDbChild();
        dbInventoryItem1.setItemFreeRange(100);
        dbInventoryItem1.setBaseItemTypeCount(1);
        dbInventoryItem1.setDbBaseItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        dbInventoryItem1.setName("dbInventoryItem1");
        inventoryService.getItemCrud().updateDbChild(dbInventoryItem1);
        DbInventoryItem dbInventoryItem2 = inventoryService.getItemCrud().createDbChild();
        dbInventoryItem2.setItemFreeRange(100);
        dbInventoryItem2.setBaseItemTypeCount(3);
        dbInventoryItem2.setDbBaseItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        dbInventoryItem2.setName("dbInventoryItem2");
        inventoryService.getItemCrud().updateDbChild(dbInventoryItem2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase simpleBase = getMyBase();
        Id starterItem = getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID);
        sendMoveCommand(starterItem, new Index(5000, 5000));
        waitForActionServiceDone();

        // ItemService
        DbBaseItemType dbBuilderITemType = (DbBaseItemType) itemService.getDbItemType(TEST_ATTACK_ITEM_ID);
        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.getItemType(dbBuilderITemType)).andReturn(itemService.getItemType(TEST_ATTACK_ITEM_ID));
        EasyMock.expect(mockItemService.hasItemsInRectangle(new Rectangle(960, 960, 80, 80))).andReturn(false);
        EasyMock.expect(mockItemService.hasEnemyInRange(simpleBase, new Index(1000, 1000), 156)).andReturn(false);
        EasyMock.expect(mockItemService.hasItemsInRectangle(new Rectangle(1160, 960, 80, 80))).andReturn(false);
        EasyMock.expect(mockItemService.hasEnemyInRange(simpleBase, new Index(1200, 1000), 156)).andReturn(false);
        EasyMock.expect(mockItemService.hasItemsInRectangle(new Rectangle(1160, 1160, 80, 80))).andReturn(false);
        EasyMock.expect(mockItemService.hasEnemyInRange(simpleBase, new Index(1200, 1200), 156)).andReturn(false);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_ATTACK_ITEM_ID), new Index(1000, 1000), null, simpleBase, 0)).andReturn(null);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_ATTACK_ITEM_ID), new Index(1200, 1000), null, simpleBase, 0)).andReturn(null);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_ATTACK_ITEM_ID), new Index(1200, 1200), null, simpleBase, 0)).andReturn(null);
        setPrivateField(InventoryServiceImpl.class, inventoryService, "itemService", mockItemService);
        // Terrain Service
        TerrainService mockTerrainService = EasyMock.createStrictMock(TerrainService.class);
        EasyMock.expect(mockTerrainService.isFree(new Index(1000, 1000), itemService.getItemType(TEST_ATTACK_ITEM_ID))).andReturn(true);
        EasyMock.expect(mockTerrainService.isFree(new Index(1200, 1000), itemService.getItemType(TEST_ATTACK_ITEM_ID))).andReturn(true);
        EasyMock.expect(mockTerrainService.isFree(new Index(1200, 1200), itemService.getItemType(TEST_ATTACK_ITEM_ID))).andReturn(true);
        setPrivateField(InventoryServiceImpl.class, inventoryService, "terrainService", mockTerrainService);
        // Territory Service
        TerritoryService mockTerritoryService = EasyMock.createStrictMock(TerritoryService.class);
        EasyMock.expect(mockTerritoryService.isAllowed(new Index(1000, 1000), (BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID))).andReturn(true);
        EasyMock.expect(mockTerritoryService.isAllowed(new Index(1200, 1000), (BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID))).andReturn(true);
        EasyMock.expect(mockTerritoryService.isAllowed(new Index(1200, 1200), (BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID))).andReturn(true);
        setPrivateField(InventoryServiceImpl.class, inventoryService, "territoryService", mockTerritoryService);
        // History Service
        HistoryService mockHistoryService = EasyMock.createStrictMock(HistoryService.class);
        mockHistoryService.addInventoryItemUsed(userService.getUserState(), "dbInventoryItem2");
        setPrivateField(InventoryServiceImpl.class, inventoryService, "historyService", mockHistoryService);

        EasyMock.replay(mockItemService, mockTerrainService, mockTerritoryService, mockHistoryService);

        // Use gold but no inventory item
        Assert.assertTrue(userService.getUserState().getInventoryItemIds().isEmpty());
        userService.getUserState().addInventoryItem(dbInventoryItem2.getId());
        Collection<Index> positions = new ArrayList<>();
        positions.add(new Index(1000, 1000));
        positions.add(new Index(1200, 1000));
        positions.add(new Index(1200, 1200));
        inventoryService.useInventoryItem(dbInventoryItem2.getId(), positions);
        Assert.assertTrue(userService.getUserState().getInventoryItemIds().isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(mockItemService, mockTerrainService, mockTerritoryService, mockHistoryService);
    }

    @Test
    @DirtiesContext
    public void buyInventoryItem() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryItem dbInventoryItem1 = inventoryService.getItemCrud().createDbChild();
        dbInventoryItem1.setName("GoldItem1");
        dbInventoryItem1.setGoldAmount(100);
        dbInventoryItem1.setImageContentType("imageData22");
        dbInventoryItem1.setImageData(new byte[]{1, 3, 4, 6, 7, 9});
        dbInventoryItem1.setRazarionCoast(66);
        inventoryService.getItemCrud().updateDbChild(dbInventoryItem1);

        DbInventoryItem dbInventoryItem2 = inventoryService.getItemCrud().createDbChild();
        dbInventoryItem2.setName("GoldItem2");
        dbInventoryItem2.setGoldAmount(10);
        dbInventoryItem2.setImageContentType("imageData33");
        dbInventoryItem2.setImageData(new byte[]{6, 7, 9});
        inventoryService.getItemCrud().updateDbChild(dbInventoryItem2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // History Service
        HistoryService mockHistoryService = EasyMock.createStrictMock(HistoryService.class);
        mockHistoryService.addInventoryItemBought(userService.getUserState(), "GoldItem1", 66);
        setPrivateField(InventoryServiceImpl.class, inventoryService, "historyService", mockHistoryService);
        EasyMock.replay(mockHistoryService);

        try {
            inventoryService.buyInventoryItem(dbInventoryItem2.getId());
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("The InventoryItem can not be bought: UserState: user=null dbInventoryItem: DbInventoryItem{id=2, name='GoldItem2'}", e.getMessage());
        }
        try {
            inventoryService.buyInventoryItem(dbInventoryItem1.getId());
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("The user does not have enough razarion to buy the inventory item. User: UserState: user=null dbInventoryItem: DbInventoryItem{id=1, name='GoldItem1'} Razarion: 0", e.getMessage());
        }
        Assert.assertFalse(userService.getUserState().hasInventoryItemId(dbInventoryItem1.getId()));
        userService.getUserState().addRazarion(100);
        int razarion = inventoryService.buyInventoryItem(dbInventoryItem1.getId());
        Assert.assertEquals(34, razarion);
        Assert.assertEquals(34, userService.getUserState().getRazarion());
        Assert.assertEquals(1, userService.getUserState().getInventoryItemIds().size());
        Assert.assertTrue(userService.getUserState().hasInventoryItemId(dbInventoryItem1.getId()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(mockHistoryService);
    }

    @Test
    @DirtiesContext
    public void buyInventoryArtifact() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryArtifact dbInventoryArtifact1 = inventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact1.setName("Artifact1");
        dbInventoryArtifact1.setRareness(DbInventoryArtifact.Rareness.UN_COMMON);
        dbInventoryArtifact1.setImageContentType("imageContent");
        dbInventoryArtifact1.setImageData(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
        dbInventoryArtifact1.setRazarionCoast(12);
        inventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact1);

        DbInventoryArtifact dbInventoryArtifact2 = inventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact2.setName("Artifact2");
        dbInventoryArtifact2.setImageContentType("imageContent2");
        dbInventoryArtifact2.setImageData(new byte[]{7, 8, 9});
        inventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // History Service
        HistoryService mockHistoryService = EasyMock.createStrictMock(HistoryService.class);
        mockHistoryService.addInventoryArtifactBought(userService.getUserState(), "Artifact1", 12);
        setPrivateField(InventoryServiceImpl.class, inventoryService, "historyService", mockHistoryService);
        EasyMock.replay(mockHistoryService);

        try {
            inventoryService.buyInventoryArtifact(dbInventoryArtifact2.getId());
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("The InventoryArtifact can not be bought: UserState: user=null dbInventoryItem: DbInventoryArtifact{id=2, name='Artifact2'}", e.getMessage());
        }
        try {
            inventoryService.buyInventoryArtifact(dbInventoryArtifact1.getId());
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("The user does not have enough razarion to buy the inventory artifact. User: UserState: user=null dbInventoryArtifact: DbInventoryArtifact{id=1, name='Artifact1'} Razarion: 0", e.getMessage());
        }
        Assert.assertTrue(userService.getUserState().getInventoryArtifactIds().isEmpty());
        userService.getUserState().addRazarion(24);
        int razarion = inventoryService.buyInventoryArtifact(dbInventoryArtifact1.getId());
        Assert.assertEquals(12, razarion);
        Assert.assertEquals(12, userService.getUserState().getRazarion());
        Assert.assertEquals(1, userService.getUserState().getInventoryArtifactIds().size());
        Assert.assertEquals(dbInventoryArtifact1.getId(), CommonJava.getFirst(userService.getUserState().getInventoryArtifactIds()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(mockHistoryService);
    }
}
