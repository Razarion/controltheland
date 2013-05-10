package com.btxtech.game.services.unlock;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.unlock.impl.UnlockContainer;
import com.btxtech.game.jsre.common.packets.UnlockContainerPacket;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.history.DbHistoryElement;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.inventory.GlobalInventoryService;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * User: beat
 * Date: 11.02.13
 * Time: 18:45
 */
public class TestServerUnlockService4Item extends AbstractServiceTest {
    @Autowired
    private ServerUnlockService unlockService;
    @Autowired
    private ServerItemTypeService itemTypeService;
    @Autowired
    private GlobalInventoryService globalInventoryService;

    @Test
    @DirtiesContext
    public void testUnlockBuild() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType factory = itemTypeService.getDbBaseItemType(TEST_FACTORY_ITEM_ID);
        factory.setUnlockRazarion(10);
        itemTypeService.saveDbItemType(factory);
        itemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Try to build an locked item
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        assertWholeItemCount(TEST_PLANET_1_ID, 1);
        // Unlock item
        getUserState().setRazarion(100);
        UnlockContainer unlockContainer = unlockService.unlockItemType(TEST_FACTORY_ITEM_ID);
        Assert.assertEquals(1, unlockContainer.getItemTypes().size());
        Assert.assertTrue(unlockContainer.getItemTypes().contains(TEST_FACTORY_ITEM_ID));
        // Build an locked item
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        Assert.assertEquals(1, getAllSynItemId(TEST_FACTORY_ITEM_ID).size());
        Assert.assertEquals(90, getUserState().getRazarion());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testUnlockFabricate() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType attacker = itemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID);
        attacker.setUnlockRazarion(10);
        itemTypeService.saveDbItemType(attacker);
        itemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // preparation
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        // Tray to build a unit
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        assertWholeItemCount(TEST_PLANET_1_ID, 2);
        // Unlock item
        getUserState().setRazarion(100);
        UnlockContainer unlockContainer = unlockService.unlockItemType(TEST_ATTACK_ITEM_ID);
        Assert.assertEquals(1, unlockContainer.getItemTypes().size());
        Assert.assertTrue(unlockContainer.getItemTypes().contains(TEST_ATTACK_ITEM_ID));
        // Build an locked item
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        Assert.assertEquals(1, getAllSynItemId(TEST_ATTACK_ITEM_ID).size());
        Assert.assertEquals(90, getUserState().getRazarion());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void unlockNoRazarion() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType attacker = itemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID);
        attacker.setUnlockRazarion(10);
        itemTypeService.saveDbItemType(attacker);
        itemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getUserState().setRazarion(0);
        try {
            unlockService.unlockItemType(TEST_ATTACK_ITEM_ID);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Not enough razarion to unlock: ItemType: TestAttackItem user: UserState: user=null", e.getMessage());
        }
        Assert.assertEquals(0, getUserState().getRazarion());
        assertItems(unlockService.getUnlockContainer(getOrCreateBase()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void unlockNotLocked() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getUserState().setRazarion(100);
        try {
            unlockService.unlockItemType(TEST_ATTACK_ITEM_ID);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Base item type can not be unlocked: ItemType: TestAttackItem", e.getMessage());
        }
        assertItems(unlockService.getUnlockContainer(getOrCreateBase()));
        Assert.assertEquals(100, getUserState().getRazarion());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void isItemLocked() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType attacker = itemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID);
        attacker.setUnlockRazarion(10);
        itemTypeService.saveDbItemType(attacker);
        DbBaseItemType factory = itemTypeService.getDbBaseItemType(TEST_FACTORY_ITEM_ID);
        factory.setUnlockRazarion(8);
        itemTypeService.saveDbItemType(factory);
        itemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getUserState().setRazarion(100);
        getOrCreateBase(); // Create Base
        unlockService.unlockItemType(TEST_FACTORY_ITEM_ID);
        Assert.assertTrue(unlockService.isItemLocked((BaseItemType) itemTypeService.getItemType(TEST_ATTACK_ITEM_ID), getOrCreateBase()));
        Assert.assertFalse(unlockService.isItemLocked((BaseItemType) itemTypeService.getItemType(TEST_FACTORY_ITEM_ID), getOrCreateBase()));
        Assert.assertFalse(unlockService.isItemLocked((BaseItemType) itemTypeService.getItemType(TEST_CONTAINER_ITEM_ID), getOrCreateBase()));
        Assert.assertFalse(unlockService.isItemLocked((BaseItemType) itemTypeService.getItemType(TEST_HARVESTER_ITEM_ID), getOrCreateBase()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void isItemLockedBot() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType attacker = itemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID);
        attacker.setUnlockRazarion(10);
        itemTypeService.saveDbItemType(attacker);
        DbBaseItemType factory = itemTypeService.getDbBaseItemType(TEST_FACTORY_ITEM_ID);
        factory.setUnlockRazarion(8);
        itemTypeService.saveDbItemType(factory);
        itemTypeService.activate();
        DbBotConfig dbBotConfig = setupMinimalNoAttackBot(TEST_PLANET_1_ID, new Rectangle(0, 0, 1000, 1000));
        waitForBotToBuildup(TEST_PLANET_1_ID, dbBotConfig.createBotConfig(itemTypeService));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase botBase = getFirstBotBase(TEST_PLANET_1_ID);
        Assert.assertFalse(unlockService.isItemLocked((BaseItemType) itemTypeService.getItemType(TEST_ATTACK_ITEM_ID), botBase));
        Assert.assertFalse(unlockService.isItemLocked((BaseItemType) itemTypeService.getItemType(TEST_FACTORY_ITEM_ID), botBase));
        Assert.assertFalse(unlockService.isItemLocked((BaseItemType) itemTypeService.getItemType(TEST_CONTAINER_ITEM_ID), botBase));
        Assert.assertFalse(unlockService.isItemLocked((BaseItemType) itemTypeService.getItemType(TEST_HARVESTER_ITEM_ID), botBase));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testHistory() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType attacker = itemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID);
        attacker.setUnlockRazarion(10);
        itemTypeService.saveDbItemType(attacker);
        DbBaseItemType factory = itemTypeService.getDbBaseItemType(TEST_FACTORY_ITEM_ID);
        factory.setUnlockRazarion(8);
        itemTypeService.saveDbItemType(factory);
        itemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        getUserState().setRazarion(100);
        getOrCreateBase(); // Create Base
        unlockService.unlockItemType(TEST_ATTACK_ITEM_ID);
        unlockService.unlockItemType(TEST_FACTORY_ITEM_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbHistoryElement> history = HibernateUtil.loadAll(getSessionFactory(), DbHistoryElement.class);
        DbHistoryElement dbHistoryElement = history.get(2);
        Assert.assertEquals(TEST_ATTACK_ITEM, dbHistoryElement.getItemTypeName());
        Assert.assertEquals(TEST_ATTACK_ITEM_ID, (int) dbHistoryElement.getItemTypeId());
        Assert.assertEquals(DbHistoryElement.Type.UNLOCKED_ITEM, dbHistoryElement.getType());
        Assert.assertEquals(90, (int) dbHistoryElement.getRazarion());
        Assert.assertEquals(10, (int) dbHistoryElement.getDeltaRazarion());
        dbHistoryElement = history.get(3);
        Assert.assertEquals(TEST_FACTORY_ITEM, dbHistoryElement.getItemTypeName());
        Assert.assertEquals(TEST_FACTORY_ITEM_ID, (int) dbHistoryElement.getItemTypeId());
        Assert.assertEquals(DbHistoryElement.Type.UNLOCKED_ITEM, dbHistoryElement.getType());
        Assert.assertEquals(82, (int) dbHistoryElement.getRazarion());
        Assert.assertEquals(8, (int) dbHistoryElement.getDeltaRazarion());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testBackend() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType attacker = itemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID);
        attacker.setUnlockRazarion(10);
        itemTypeService.saveDbItemType(attacker);
        DbBaseItemType factory = itemTypeService.getDbBaseItemType(TEST_FACTORY_ITEM_ID);
        factory.setUnlockRazarion(8);
        itemTypeService.saveDbItemType(factory);
        itemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getOrCreateBase(); // Create Base
        // Assert empty
        Collection<DbBaseItemType> unlockedItems = unlockService.getUnlockDbBaseItemTypes(getUserState());
        Assert.assertTrue(unlockedItems.isEmpty());
        // Add item
        Thread.sleep(1000); // wait for AccountBalancePacket
        clearPackets();
        unlockService.setUnlockedBaseItemTypesBackend(Arrays.asList(itemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID)), getUserState());
        // Verify
        UnlockContainerPacket unlockContainerPacket = new UnlockContainerPacket();
        UnlockContainer unlockContainer = new UnlockContainer();
        unlockContainer.setItemTypes(Arrays.asList(TEST_ATTACK_ITEM_ID));
        unlockContainerPacket.setUnlockContainer(unlockContainer);
        assertPackagesIgnoreSyncItemInfoAndClear(unlockContainerPacket);
        unlockedItems = unlockService.getUnlockDbBaseItemTypes(getUserState());
        Assert.assertEquals(1, unlockedItems.size());
        Assert.assertTrue(unlockedItems.contains(itemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID)));
        Assert.assertFalse(unlockService.isItemLocked((BaseItemType) itemTypeService.getItemType(TEST_ATTACK_ITEM_ID), getOrCreateBase()));
        // Add item
        clearPackets();
        unlockService.setUnlockedBaseItemTypesBackend(Arrays.asList(itemTypeService.getDbBaseItemType(TEST_FACTORY_ITEM_ID), itemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID)), getUserState());
        // Verify
        unlockContainerPacket = new UnlockContainerPacket();
        unlockContainer = new UnlockContainer();
        unlockContainer.setItemTypes(Arrays.asList(TEST_ATTACK_ITEM_ID, TEST_FACTORY_ITEM_ID));
        unlockContainerPacket.setUnlockContainer(unlockContainer);
        assertPackagesIgnoreSyncItemInfoAndClear(unlockContainerPacket);
        unlockedItems = unlockService.getUnlockDbBaseItemTypes(getUserState());
        Assert.assertEquals(2, unlockedItems.size());
        Assert.assertTrue(unlockedItems.contains(itemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID)));
        Assert.assertTrue(unlockedItems.contains(itemTypeService.getDbBaseItemType(TEST_FACTORY_ITEM_ID)));
        Assert.assertFalse(unlockService.isItemLocked((BaseItemType) itemTypeService.getItemType(TEST_ATTACK_ITEM_ID), getOrCreateBase()));
        Assert.assertFalse(unlockService.isItemLocked((BaseItemType) itemTypeService.getItemType(TEST_FACTORY_ITEM_ID), getOrCreateBase()));
        // Set no items
        clearPackets();
        unlockService.setUnlockedBaseItemTypesBackend(new ArrayList<DbBaseItemType>(), getUserState());
        // Verify
        unlockContainerPacket = new UnlockContainerPacket();
        unlockContainer = new UnlockContainer();
        unlockContainer.setItemTypes(new ArrayList<Integer>());
        unlockContainerPacket.setUnlockContainer(unlockContainer);
        assertPackagesIgnoreSyncItemInfoAndClear(unlockContainerPacket);
        unlockedItems = unlockService.getUnlockDbBaseItemTypes(getUserState());
        Assert.assertEquals(0, unlockedItems.size());
        Assert.assertTrue(unlockService.isItemLocked((BaseItemType) itemTypeService.getItemType(TEST_ATTACK_ITEM_ID), getOrCreateBase()));
        Assert.assertTrue(unlockService.isItemLocked((BaseItemType) itemTypeService.getItemType(TEST_FACTORY_ITEM_ID), getOrCreateBase()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testInventory() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType attacker = itemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID);
        attacker.setUnlockRazarion(10);
        itemTypeService.saveDbItemType(attacker);
        itemTypeService.activate();
        DbInventoryItem dbInventoryItem = globalInventoryService.getItemCrud().createDbChild();
        dbInventoryItem.setName("ItemType1");
        dbInventoryItem.setBaseItemTypeCount(1);
        dbInventoryItem.setItemFreeRange(111);
        dbInventoryItem.setRazarionCoast(77);
        dbInventoryItem.setDbBaseItemType((DbBaseItemType) itemTypeService.getDbItemType(TEST_ATTACK_ITEM_ID));
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getOrCreateBase(); // Force base
        getUserState().setRazarion(100);
        Assert.assertTrue(unlockService.isItemLocked((BaseItemType) itemTypeService.getItemType(TEST_ATTACK_ITEM_ID), getOrCreateBase()));
        globalInventoryService.buyInventoryItem(dbInventoryItem.getId());
        globalInventoryService.useInventoryItem(dbInventoryItem.getId(), Arrays.asList(new Index(2000, 2000)));
        assertWholeItemCount(TEST_PLANET_1_ID, 2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void assertItems(UnlockContainer unlockContainer) throws Exception {
        Set<Integer> itemTypes = (Set<Integer>) getPrivateField(UnlockContainer.class, unlockContainer, "itemTypes");
        Assert.assertEquals(0, itemTypes.size());
    }
}
