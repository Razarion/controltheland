package com.btxtech.game.services.inventory.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
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
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.history.DbHistoryElement;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.inventory.DbBoxRegion;
import com.btxtech.game.services.inventory.DbBoxRegionCount;
import com.btxtech.game.services.inventory.DbInventoryArtifact;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.inventory.InventoryService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemTypePossibility;
import com.btxtech.game.services.mgmt.BackupSummary;
import com.btxtech.game.services.mgmt.MgmtService;
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
import java.util.List;

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
    //private Log log = LogFactory.getLog(TestInventoryServiceImpl.class);

    @Test
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
        // Picker
        SyncBaseItem mockPicker = EasyMock.createStrictMock(SyncBaseItem.class);
        EasyMock.expect(mockPicker.getBase()).andReturn(simpleBase);
        // SyncBoxItems
        SyncBoxItem mockSyncBoxItem1 = EasyMock.createStrictMock(SyncBoxItem.class);
        EasyMock.expect(mockSyncBoxItem1.isInTTL()).andReturn(true).anyTimes();
        // History Service
        HistoryService mockHistoryService = EasyMock.createStrictMock(HistoryService.class);
        mockHistoryService.addBoxDropped(EasyMock.eq(mockSyncBoxItem1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull());
        mockHistoryService.addBoxPicked(EasyMock.eq(mockSyncBoxItem1), EasyMock.eq(mockPicker));
        // ItemService
        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        DbBoxItemType dbBoxItemType1 = itemService.getDbBoxItemType(TEST_BOX_ITEM_1_ID);
        BoxItemType boxItemType1 = (BoxItemType) itemService.getItemType(TEST_BOX_ITEM_1_ID);

        EasyMock.expect(mockItemService.getItemType(dbBoxItemType1)).andReturn(boxItemType1);
        EasyMock.expect(mockItemService.createSyncObject(EasyMock.eq(boxItemType1), (Index) EasyMock.anyObject(), (SyncBaseItem) EasyMock.isNull(), (SimpleBase) EasyMock.isNull(), EasyMock.eq(0))).andReturn(mockSyncBoxItem1);
        mockItemService.killSyncItem(EasyMock.eq(mockSyncBoxItem1), EasyMock.eq(simpleBase), EasyMock.eq(true), EasyMock.eq(false));

        EasyMock.replay(mockHistoryService, mockItemService, mockSyncBoxItem1, mockPicker);

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

        Thread.sleep(300);
        inventoryService.onSyncBoxItemPicked(mockSyncBoxItem1, mockPicker);

        EasyMock.verify(mockHistoryService, mockItemService, mockSyncBoxItem1, mockPicker);
    }

    @Test
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
    }

    @Test
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
        // BoxItemType
        BoxItemType boxItemType = EasyMock.createStrictMock(BoxItemType.class);
        EasyMock.expect(boxItemType.getId()).andReturn(1);
        EasyMock.expect(boxItemType.getId()).andReturn(2);
        EasyMock.expect(boxItemType.getId()).andReturn(3);
        // SyncBoxItems
        SyncBoxItem mockSyncBoxItem1 = EasyMock.createStrictMock(SyncBoxItem.class);
        EasyMock.expect(mockSyncBoxItem1.getItemType()).andReturn(boxItemType).times(3);
        // ItemService
        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        mockItemService.killSyncItem(mockSyncBoxItem1, simpleBase, true, false);
        EasyMock.expect(mockItemService.getDbBoxItemType(1)).andReturn(dbBoxItemType1);
        mockItemService.killSyncItem(mockSyncBoxItem1, simpleBase, true, false);
        EasyMock.expect(mockItemService.getDbBoxItemType(2)).andReturn(dbBoxItemType2);
        mockItemService.killSyncItem(mockSyncBoxItem1, simpleBase, true, false);
        EasyMock.expect(mockItemService.getDbBoxItemType(3)).andReturn(dbBoxItemType3);
        // Picker
        SyncBaseItem picker = EasyMock.createStrictMock(SyncBaseItem.class);
        EasyMock.expect(picker.getBase()).andReturn(simpleBase).times(9);
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
        // History Service
        HistoryService mockHistoryService = EasyMock.createStrictMock(HistoryService.class);
        mockHistoryService.addBoxPicked(mockSyncBoxItem1, picker);
        mockHistoryService.addRazarionFromBox(mockUserState, 100);
        mockHistoryService.addBoxPicked(mockSyncBoxItem1, picker);
        mockHistoryService.addInventoryItemFromBox(mockUserState, "InventoryItem");
        mockHistoryService.addBoxPicked(mockSyncBoxItem1, picker);
        mockHistoryService.addInventoryArtifactFromBox(mockUserState, "InventoryArtifact");

        EasyMock.replay(mockHistoryService, mockItemService, mockSyncBoxItem1, picker, mockBaseService, boxItemType, mockUserState);

        setPrivateField(InventoryServiceImpl.class, inventoryService, "historyService", mockHistoryService);
        setPrivateField(InventoryServiceImpl.class, inventoryService, "itemService", mockItemService);
        setPrivateField(InventoryServiceImpl.class, inventoryService, "baseService", mockBaseService);

        inventoryService.onSyncBoxItemPicked(mockSyncBoxItem1, picker);
        inventoryService.onSyncBoxItemPicked(mockSyncBoxItem1, picker);
        inventoryService.onSyncBoxItemPicked(mockSyncBoxItem1, picker);

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

}
