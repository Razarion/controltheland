package com.btxtech.game.services.inventory.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.BoxItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.history.DbHistoryElement;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.inventory.DbBoxRegion;
import com.btxtech.game.services.inventory.DbBoxRegionCount;
import com.btxtech.game.services.inventory.InventoryService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemType;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

}
