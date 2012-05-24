package com.btxtech.game.services.inventory;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemTypePossibility;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 15.05.12
 * Time: 12:54
 */
public class TestInventoryService extends AbstractServiceTest {
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserGuidanceService userGuidanceService;

    @Test
    @DirtiesContext
    public void crudArtifacts() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, inventoryService.getArtifactCrud().readDbChildren().size());
        Assert.assertEquals(0, inventoryService.getItemCrud().readDbChildren().size());

        DbInventoryArtifact dbInventoryArtifact1 = inventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact1.setName("Artifact1");
        dbInventoryArtifact1.setRareness(DbInventoryArtifact.Rareness.SECOND);
        dbInventoryArtifact1.setImageContentType("imageContent");
        dbInventoryArtifact1.setImageData(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
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
        Assert.assertEquals(2, inventoryService.getArtifactCrud().readDbChildren().size());
        Assert.assertEquals(0, inventoryService.getItemCrud().readDbChildren().size());

        List<DbInventoryArtifact> artifactList = (List<DbInventoryArtifact>) inventoryService.getArtifactCrud().readDbChildren();
        Assert.assertEquals("Artifact1", artifactList.get(0).getName());
        Assert.assertEquals(DbInventoryArtifact.Rareness.SECOND, artifactList.get(0).getRareness());
        Assert.assertEquals("imageContent", artifactList.get(0).getImageContentType());
        Assert.assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, artifactList.get(0).getImageData());

        Assert.assertEquals("Artifact2", artifactList.get(1).getName());
        Assert.assertEquals(DbInventoryArtifact.Rareness.FIRST, artifactList.get(1).getRareness());
        Assert.assertEquals("imageContent2", artifactList.get(1).getImageContentType());
        Assert.assertArrayEquals(new byte[]{7, 8, 9}, artifactList.get(1).getImageData());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Delete first
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        inventoryService.getArtifactCrud().deleteDbChild(dbInventoryArtifact1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, inventoryService.getArtifactCrud().readDbChildren().size());
        Assert.assertEquals(0, inventoryService.getItemCrud().readDbChildren().size());

        artifactList = (List<DbInventoryArtifact>) inventoryService.getArtifactCrud().readDbChildren();
        Assert.assertEquals("Artifact2", artifactList.get(0).getName());
        Assert.assertEquals(DbInventoryArtifact.Rareness.FIRST, artifactList.get(0).getRareness());
        Assert.assertEquals("imageContent2", artifactList.get(0).getImageContentType());
        Assert.assertArrayEquals(new byte[]{7, 8, 9}, artifactList.get(0).getImageData());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Delete second
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        inventoryService.getArtifactCrud().deleteDbChild(dbInventoryArtifact2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, inventoryService.getArtifactCrud().readDbChildren().size());
        Assert.assertEquals(0, inventoryService.getItemCrud().readDbChildren().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void crudItems() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, inventoryService.getArtifactCrud().readDbChildren().size());
        Assert.assertEquals(0, inventoryService.getItemCrud().readDbChildren().size());

        DbInventoryArtifact dbInventoryArtifact1 = inventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact1.setName("Artifact1");
        inventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact1);

        DbInventoryArtifact dbInventoryArtifact2 = inventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact2.setName("Artifact2");
        inventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact2);

        DbInventoryArtifact dbInventoryArtifact3 = inventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact3.setName("Artifact3");
        inventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact3);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryItem dbInventoryItem1 = inventoryService.getItemCrud().createDbChild();
        dbInventoryItem1.setName("GoldItem1");
        dbInventoryItem1.setGoldAmount(100);
        dbInventoryItem1.setGoldImageContentType("imageData22");
        dbInventoryItem1.setGoldImageData(new byte[]{1, 3, 4, 6, 7, 9});
        dbInventoryItem1.setGoldLevel(userGuidanceService.getDbLevel(TEST_LEVEL_1_SIMULATED_ID));
        DbInventoryArtifactCount dbInventoryArtifactCount = dbInventoryItem1.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(2);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact1);
        dbInventoryArtifactCount = dbInventoryItem1.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(1);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact2);
        inventoryService.getItemCrud().updateDbChild(dbInventoryItem1);

        DbInventoryItem dbInventoryItem2 = inventoryService.getItemCrud().createDbChild();
        dbInventoryItem2.setName("GoldItem2");
        dbInventoryItem2.setGoldAmount(10);
        dbInventoryItem2.setGoldImageContentType("imageData33");
        dbInventoryItem2.setGoldImageData(new byte[]{6, 7, 9});
        dbInventoryItem2.setGoldLevel(userGuidanceService.getDbLevel(TEST_LEVEL_2_REAL_ID));
        dbInventoryArtifactCount = dbInventoryItem2.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(3);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact1);
        inventoryService.getItemCrud().updateDbChild(dbInventoryItem2);

        DbInventoryItem dbInventoryItem3 = inventoryService.getItemCrud().createDbChild();
        dbInventoryItem3.setName("ItemType1");
        dbInventoryItem3.setBaseItemTypeCount(2);
        dbInventoryItem3.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_ATTACK_ITEM_ID));
        dbInventoryArtifactCount = dbInventoryItem3.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(1);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact3);
        dbInventoryArtifactCount = dbInventoryItem3.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(2);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact1);
        inventoryService.getItemCrud().updateDbChild(dbInventoryItem3);

        DbInventoryItem dbInventoryItem4 = inventoryService.getItemCrud().createDbChild();
        dbInventoryItem4.setName("ItemType2");
        dbInventoryItem4.setBaseItemTypeCount(1);
        dbInventoryItem4.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_FACTORY_ITEM_ID));
        dbInventoryArtifactCount = dbInventoryItem4.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(1);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact1);
        dbInventoryArtifactCount = dbInventoryItem4.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(2);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact2);
        dbInventoryArtifactCount = dbInventoryItem4.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(3);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact3);
        inventoryService.getItemCrud().updateDbChild(dbInventoryItem4);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbInventoryItem> itemList = (List<DbInventoryItem>) inventoryService.getItemCrud().readDbChildren();
        Assert.assertEquals(4, itemList.size());
        Assert.assertEquals("GoldItem1", itemList.get(0).getName());
        Assert.assertEquals(100, itemList.get(0).getGoldAmount());
        Assert.assertEquals("imageData22", itemList.get(0).getGoldImageContentType());
        Assert.assertArrayEquals(new byte[]{1, 3, 4, 6, 7, 9}, itemList.get(0).getGoldImageData());
        Assert.assertEquals(userGuidanceService.getDbLevel(TEST_LEVEL_1_SIMULATED_ID), itemList.get(0).getGoldLevel());
        List<DbInventoryArtifactCount> dbInventoryArtifactCountFromDb = new ArrayList<>(itemList.get(0).getArtifactCountCrud().readDbChildren());
        Assert.assertEquals(2, dbInventoryArtifactCountFromDb.size());
        Assert.assertEquals(2, dbInventoryArtifactCountFromDb.get(0).getCount());
        Assert.assertEquals(dbInventoryArtifact1, dbInventoryArtifactCountFromDb.get(0).getDbInventoryArtifact());
        Assert.assertEquals(1, dbInventoryArtifactCountFromDb.get(1).getCount());
        Assert.assertEquals(dbInventoryArtifact2, dbInventoryArtifactCountFromDb.get(1).getDbInventoryArtifact());

        Assert.assertEquals("GoldItem2", itemList.get(1).getName());
        Assert.assertEquals(10, itemList.get(1).getGoldAmount());
        Assert.assertEquals("imageData33", itemList.get(1).getGoldImageContentType());
        Assert.assertArrayEquals(new byte[]{6, 7, 9}, itemList.get(1).getGoldImageData());
        Assert.assertEquals(userGuidanceService.getDbLevel(TEST_LEVEL_2_REAL_ID), itemList.get(1).getGoldLevel());
        dbInventoryArtifactCountFromDb = new ArrayList<>(itemList.get(1).getArtifactCountCrud().readDbChildren());
        Assert.assertEquals(1, dbInventoryArtifactCountFromDb.size());
        Assert.assertEquals(3, dbInventoryArtifactCountFromDb.get(0).getCount());
        Assert.assertEquals(dbInventoryArtifact1, dbInventoryArtifactCountFromDb.get(0).getDbInventoryArtifact());

        Assert.assertEquals("ItemType1", itemList.get(2).getName());
        Assert.assertEquals(2, itemList.get(2).getBaseItemTypeCount());
        Assert.assertEquals(TEST_ATTACK_ITEM_ID, (int) itemList.get(2).getDbBaseItemType().getId());
        dbInventoryArtifactCountFromDb = new ArrayList<>(itemList.get(2).getArtifactCountCrud().readDbChildren());
        Assert.assertEquals(2, dbInventoryArtifactCountFromDb.size());
        Assert.assertEquals(1, dbInventoryArtifactCountFromDb.get(0).getCount());
        Assert.assertEquals(dbInventoryArtifact3, dbInventoryArtifactCountFromDb.get(0).getDbInventoryArtifact());
        Assert.assertEquals(2, dbInventoryArtifactCountFromDb.get(1).getCount());
        Assert.assertEquals(dbInventoryArtifact1, dbInventoryArtifactCountFromDb.get(1).getDbInventoryArtifact());

        Assert.assertEquals("ItemType2", itemList.get(3).getName());
        Assert.assertEquals(1, itemList.get(3).getBaseItemTypeCount());
        Assert.assertEquals(TEST_FACTORY_ITEM_ID, (int) itemList.get(3).getDbBaseItemType().getId());
        dbInventoryArtifactCountFromDb = new ArrayList<>(itemList.get(3).getArtifactCountCrud().readDbChildren());
        Assert.assertEquals(3, dbInventoryArtifactCountFromDb.size());
        Assert.assertEquals(1, dbInventoryArtifactCountFromDb.get(0).getCount());
        Assert.assertEquals(dbInventoryArtifact1, dbInventoryArtifactCountFromDb.get(0).getDbInventoryArtifact());
        Assert.assertEquals(2, dbInventoryArtifactCountFromDb.get(1).getCount());
        Assert.assertEquals(dbInventoryArtifact2, dbInventoryArtifactCountFromDb.get(1).getDbInventoryArtifact());
        Assert.assertEquals(3, dbInventoryArtifactCountFromDb.get(2).getCount());
        Assert.assertEquals(dbInventoryArtifact3, dbInventoryArtifactCountFromDb.get(2).getDbInventoryArtifact());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Remove and test if artifacts are still in DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        inventoryService.getItemCrud().deleteDbChild(dbInventoryItem1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        itemList = (List<DbInventoryItem>) inventoryService.getItemCrud().readDbChildren();
        Assert.assertEquals(3, itemList.size());
        Assert.assertEquals("GoldItem2", itemList.get(0).getName());
        Assert.assertEquals(10, itemList.get(0).getGoldAmount());
        Assert.assertEquals("imageData33", itemList.get(0).getGoldImageContentType());
        Assert.assertArrayEquals(new byte[]{6, 7, 9}, itemList.get(0).getGoldImageData());
        Assert.assertEquals(userGuidanceService.getDbLevel(TEST_LEVEL_2_REAL_ID), itemList.get(0).getGoldLevel());
        dbInventoryArtifactCountFromDb = new ArrayList<>(itemList.get(0).getArtifactCountCrud().readDbChildren());
        Assert.assertEquals(1, dbInventoryArtifactCountFromDb.size());
        Assert.assertEquals(3, dbInventoryArtifactCountFromDb.get(0).getCount());
        Assert.assertEquals(dbInventoryArtifact1, dbInventoryArtifactCountFromDb.get(0).getDbInventoryArtifact());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryItem dbInventoryItem = inventoryService.getItemCrud().readDbChild(dbInventoryItem2.getId());
        DbInventoryArtifactCount dbInventoryArtifactCount1 = CommonJava.getFirst(dbInventoryItem.getArtifactCountCrud().readDbChildren());
        dbInventoryItem.getArtifactCountCrud().deleteDbChild(dbInventoryArtifactCount1);
        inventoryService.getItemCrud().updateDbChild(dbInventoryItem);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        itemList = (List<DbInventoryItem>) inventoryService.getItemCrud().readDbChildren();
        Assert.assertEquals(3, itemList.size());
        Assert.assertEquals("GoldItem2", itemList.get(0).getName());
        Assert.assertEquals(10, itemList.get(0).getGoldAmount());
        Assert.assertEquals("imageData33", itemList.get(0).getGoldImageContentType());
        Assert.assertArrayEquals(new byte[]{6, 7, 9}, itemList.get(0).getGoldImageData());
        Assert.assertEquals(userGuidanceService.getDbLevel(TEST_LEVEL_2_REAL_ID), itemList.get(0).getGoldLevel());
        dbInventoryArtifactCountFromDb = new ArrayList<>(itemList.get(0).getArtifactCountCrud().readDbChildren());
        Assert.assertEquals(0, dbInventoryArtifactCountFromDb.size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void boxRegionCrud() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createDbBoxItemType1();
        createDbBoxItemType2();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, inventoryService.getBoxRegionCrud().readDbChildren().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBoxRegion dbBoxRegion1 = inventoryService.getBoxRegionCrud().createDbChild();
        dbBoxRegion1.setItemFreeRange(111);
        dbBoxRegion1.setMaxInterval(22);
        dbBoxRegion1.setMinInterval(3);
        dbBoxRegion1.setName("DbBoxRegion1");
        dbBoxRegion1.setRegion(new Rectangle(1, 2, 10, 20));
        DbBoxRegionCount dbBoxRegionCount1 = dbBoxRegion1.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount1.setDbBoxItemType(itemService.getDbBoxItemType(TEST_BOX_ITEM_1_ID));
        dbBoxRegionCount1.setCount(10);
        inventoryService.getBoxRegionCrud().updateDbChild(dbBoxRegion1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbBoxRegion> itemList = (List<DbBoxRegion>) inventoryService.getBoxRegionCrud().readDbChildren();
        Assert.assertEquals(1, itemList.size());
        Assert.assertEquals(111, itemList.get(0).getItemFreeRange());
        Assert.assertEquals(22, itemList.get(0).getMaxInterval());
        Assert.assertEquals(3, itemList.get(0).getMinInterval());
        Assert.assertEquals("DbBoxRegion1", itemList.get(0).getName());
        Assert.assertEquals(new Rectangle(1, 2, 10, 20), itemList.get(0).getRegion());
        List<DbBoxRegionCount> dbBoxRegionCounts = new ArrayList<>(itemList.get(0).getBoxRegionCountCrud().readDbChildren());
        Assert.assertEquals(1, dbBoxRegionCounts.size());
        Assert.assertEquals(10, dbBoxRegionCounts.get(0).getCount());
        Assert.assertEquals(TEST_BOX_ITEM_1_ID, (int) dbBoxRegionCounts.get(0).getDbBoxItemType().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBoxRegion dbBoxRegion2 = inventoryService.getBoxRegionCrud().createDbChild();
        dbBoxRegion2.setItemFreeRange(222);
        dbBoxRegion2.setMaxInterval(33);
        dbBoxRegion2.setMinInterval(4);
        dbBoxRegion2.setName("DbBoxRegion2");
        dbBoxRegion2.setRegion(new Rectangle(4, 7, 30, 199));
        DbBoxRegionCount dbBoxRegionCount2 = dbBoxRegion2.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount2.setDbBoxItemType(itemService.getDbBoxItemType(TEST_BOX_ITEM_1_ID));
        dbBoxRegionCount2.setCount(5);
        DbBoxRegionCount dbBoxRegionCount3 = dbBoxRegion2.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount3.setDbBoxItemType(itemService.getDbBoxItemType(TEST_BOX_ITEM_2_ID));
        dbBoxRegionCount3.setCount(6);
        inventoryService.getBoxRegionCrud().updateDbChild(dbBoxRegion2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        itemList = (List<DbBoxRegion>) inventoryService.getBoxRegionCrud().readDbChildren();
        Assert.assertEquals(2, itemList.size());
        Assert.assertEquals(111, itemList.get(0).getItemFreeRange());
        Assert.assertEquals(22, itemList.get(0).getMaxInterval());
        Assert.assertEquals(3, itemList.get(0).getMinInterval());
        Assert.assertEquals("DbBoxRegion1", itemList.get(0).getName());
        Assert.assertEquals(new Rectangle(1, 2, 10, 20), itemList.get(0).getRegion());
        dbBoxRegionCounts = new ArrayList<>(itemList.get(0).getBoxRegionCountCrud().readDbChildren());
        Assert.assertEquals(1, dbBoxRegionCounts.size());
        Assert.assertEquals(10, dbBoxRegionCounts.get(0).getCount());
        Assert.assertEquals(TEST_BOX_ITEM_1_ID, (int) dbBoxRegionCounts.get(0).getDbBoxItemType().getId());

        Assert.assertEquals(222, itemList.get(1).getItemFreeRange());
        Assert.assertEquals(33, itemList.get(1).getMaxInterval());
        Assert.assertEquals(4, itemList.get(1).getMinInterval());
        Assert.assertEquals("DbBoxRegion2", itemList.get(1).getName());
        Assert.assertEquals(new Rectangle(4, 7, 30, 199), itemList.get(1).getRegion());
        dbBoxRegionCounts = new ArrayList<>(itemList.get(1).getBoxRegionCountCrud().readDbChildren());
        Assert.assertEquals(2, dbBoxRegionCounts.size());
        Assert.assertEquals(5, dbBoxRegionCounts.get(0).getCount());
        Assert.assertEquals(TEST_BOX_ITEM_1_ID, (int) dbBoxRegionCounts.get(0).getDbBoxItemType().getId());
        Assert.assertEquals(6, dbBoxRegionCounts.get(1).getCount());
        Assert.assertEquals(TEST_BOX_ITEM_2_ID, (int) dbBoxRegionCounts.get(1).getDbBoxItemType().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBoxRegion dbBoxRegion1FromDb = inventoryService.getBoxRegionCrud().readDbChild(dbBoxRegion1.getId());
        dbBoxRegion1FromDb.getBoxRegionCountCrud().deleteDbChild(dbBoxRegionCount1);
        inventoryService.getBoxRegionCrud().updateDbChild(dbBoxRegion1FromDb);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        itemList = (List<DbBoxRegion>) inventoryService.getBoxRegionCrud().readDbChildren();
        Assert.assertEquals(2, itemList.size());
        Assert.assertEquals(111, itemList.get(0).getItemFreeRange());
        Assert.assertEquals(22, itemList.get(0).getMaxInterval());
        Assert.assertEquals(3, itemList.get(0).getMinInterval());
        Assert.assertEquals("DbBoxRegion1", itemList.get(0).getName());
        Assert.assertEquals(new Rectangle(1, 2, 10, 20), itemList.get(0).getRegion());
        dbBoxRegionCounts = new ArrayList<>(itemList.get(0).getBoxRegionCountCrud().readDbChildren());
        Assert.assertEquals(0, dbBoxRegionCounts.size());

        Assert.assertEquals(222, itemList.get(1).getItemFreeRange());
        Assert.assertEquals(33, itemList.get(1).getMaxInterval());
        Assert.assertEquals(4, itemList.get(1).getMinInterval());
        Assert.assertEquals("DbBoxRegion2", itemList.get(1).getName());
        Assert.assertEquals(new Rectangle(4, 7, 30, 199), itemList.get(1).getRegion());
        dbBoxRegionCounts = new ArrayList<>(itemList.get(1).getBoxRegionCountCrud().readDbChildren());
        Assert.assertEquals(2, dbBoxRegionCounts.size());
        Assert.assertEquals(5, dbBoxRegionCounts.get(0).getCount());
        Assert.assertEquals(TEST_BOX_ITEM_1_ID, (int) dbBoxRegionCounts.get(0).getDbBoxItemType().getId());
        Assert.assertEquals(6, dbBoxRegionCounts.get(1).getCount());
        Assert.assertEquals(TEST_BOX_ITEM_2_ID, (int) dbBoxRegionCounts.get(1).getDbBoxItemType().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        inventoryService.getBoxRegionCrud().deleteDbChild(inventoryService.getBoxRegionCrud().readDbChild(dbBoxRegion1.getId()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        itemList = (List<DbBoxRegion>) inventoryService.getBoxRegionCrud().readDbChildren();
        Assert.assertEquals(1, itemList.size());
        Assert.assertEquals(222, itemList.get(0).getItemFreeRange());
        Assert.assertEquals(33, itemList.get(0).getMaxInterval());
        Assert.assertEquals(4, itemList.get(0).getMinInterval());
        Assert.assertEquals("DbBoxRegion2", itemList.get(0).getName());
        Assert.assertEquals(new Rectangle(4, 7, 30, 199), itemList.get(0).getRegion());
        dbBoxRegionCounts = new ArrayList<>(itemList.get(0).getBoxRegionCountCrud().readDbChildren());
        Assert.assertEquals(2, dbBoxRegionCounts.size());
        Assert.assertEquals(5, dbBoxRegionCounts.get(0).getCount());
        Assert.assertEquals(TEST_BOX_ITEM_1_ID, (int) dbBoxRegionCounts.get(0).getDbBoxItemType().getId());
        Assert.assertEquals(6, dbBoxRegionCounts.get(1).getCount());
        Assert.assertEquals(TEST_BOX_ITEM_2_ID, (int) dbBoxRegionCounts.get(1).getDbBoxItemType().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void dbBoxItemType() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryItem dbInventoryItem = inventoryService.getItemCrud().createDbChild();
        DbInventoryArtifact dbInventoryArtifact = inventoryService.getArtifactCrud().createDbChild();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBoxItemType dbBoxItemType = (DbBoxItemType) itemService.getDbItemTypeCrud().createDbChild(DbBoxItemType.class);
        itemService.saveDbItemType(dbBoxItemType);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBoxItemType = (DbBoxItemType) itemService.getDbItemTypeCrud().readDbChild(dbBoxItemType.getId());
        DbBoxItemTypePossibility dbBoxItemTypePossibility1 = dbBoxItemType.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility1.setPossibility(0.1);
        dbBoxItemTypePossibility1.setDbInventoryItem(dbInventoryItem);
        itemService.getDbItemTypeCrud().updateDbChild(dbBoxItemType);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBoxItemType = (DbBoxItemType) itemService.getDbItemTypeCrud().readDbChild(dbBoxItemType.getId());
        Assert.assertEquals(1, dbBoxItemType.getBoxPossibilityCrud().readDbChildren().size());
        dbBoxItemTypePossibility1 = dbBoxItemType.getBoxPossibilityCrud().readDbChild(dbBoxItemTypePossibility1.getId());
        Assert.assertEquals(0.1, dbBoxItemTypePossibility1.getPossibility(), 0.001);
        Assert.assertEquals(dbInventoryItem.getId(), dbBoxItemTypePossibility1.getDbInventoryItem().getId());
        Assert.assertNull(dbBoxItemTypePossibility1.getDbInventoryArtifact());
        Assert.assertNull(dbBoxItemTypePossibility1.getRazarion());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBoxItemType = (DbBoxItemType) itemService.getDbItemTypeCrud().readDbChild(dbBoxItemType.getId());
        DbBoxItemTypePossibility dbBoxItemTypePossibility2 = dbBoxItemType.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility2.setPossibility(0.2);
        dbBoxItemTypePossibility2.setDbInventoryArtifact(dbInventoryArtifact);
        itemService.getDbItemTypeCrud().updateDbChild(dbBoxItemType);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBoxItemType = (DbBoxItemType) itemService.getDbItemTypeCrud().readDbChild(dbBoxItemType.getId());
        Assert.assertEquals(2, dbBoxItemType.getBoxPossibilityCrud().readDbChildren().size());
        dbBoxItemTypePossibility1 = dbBoxItemType.getBoxPossibilityCrud().readDbChild(dbBoxItemTypePossibility1.getId());
        Assert.assertEquals(0.1, dbBoxItemTypePossibility1.getPossibility(), 0.001);
        Assert.assertEquals(dbInventoryItem.getId(), dbBoxItemTypePossibility1.getDbInventoryItem().getId());
        Assert.assertNull(dbBoxItemTypePossibility1.getDbInventoryArtifact());
        Assert.assertNull(dbBoxItemTypePossibility1.getRazarion());
        dbBoxItemTypePossibility2 = dbBoxItemType.getBoxPossibilityCrud().readDbChild(dbBoxItemTypePossibility2.getId());
        Assert.assertEquals(0.2, dbBoxItemTypePossibility2.getPossibility(), 0.001);
        Assert.assertNull(dbBoxItemTypePossibility2.getDbInventoryItem());
        Assert.assertEquals(dbInventoryArtifact.getId(), dbBoxItemTypePossibility2.getDbInventoryArtifact().getId());
        Assert.assertNull(dbBoxItemTypePossibility2.getRazarion());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBoxItemType = (DbBoxItemType) itemService.getDbItemTypeCrud().readDbChild(dbBoxItemType.getId());
        DbBoxItemTypePossibility dbBoxItemTypePossibility3 = dbBoxItemType.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility3.setPossibility(0.3);
        dbBoxItemTypePossibility3.setRazarion(253000);
        itemService.getDbItemTypeCrud().updateDbChild(dbBoxItemType);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBoxItemType = (DbBoxItemType) itemService.getDbItemTypeCrud().readDbChild(dbBoxItemType.getId());
        Assert.assertEquals(3, dbBoxItemType.getBoxPossibilityCrud().readDbChildren().size());
        dbBoxItemTypePossibility1 = dbBoxItemType.getBoxPossibilityCrud().readDbChild(dbBoxItemTypePossibility1.getId());
        Assert.assertEquals(0.1, dbBoxItemTypePossibility1.getPossibility(), 0.001);
        Assert.assertEquals(dbInventoryItem.getId(), dbBoxItemTypePossibility1.getDbInventoryItem().getId());
        Assert.assertNull(dbBoxItemTypePossibility1.getDbInventoryArtifact());
        Assert.assertNull(dbBoxItemTypePossibility1.getRazarion());
        dbBoxItemTypePossibility2 = dbBoxItemType.getBoxPossibilityCrud().readDbChild(dbBoxItemTypePossibility2.getId());
        Assert.assertEquals(0.2, dbBoxItemTypePossibility2.getPossibility(), 0.001);
        Assert.assertNull(dbBoxItemTypePossibility2.getDbInventoryItem());
        Assert.assertEquals(dbInventoryArtifact.getId(), dbBoxItemTypePossibility2.getDbInventoryArtifact().getId());
        Assert.assertNull(dbBoxItemTypePossibility2.getRazarion());
        dbBoxItemTypePossibility3 = dbBoxItemType.getBoxPossibilityCrud().readDbChild(dbBoxItemTypePossibility3.getId());
        Assert.assertEquals(0.3, dbBoxItemTypePossibility3.getPossibility(), 0.001);
        Assert.assertNull(dbBoxItemTypePossibility3.getDbInventoryItem());
        Assert.assertNull(dbBoxItemTypePossibility3.getDbInventoryArtifact());
        Assert.assertEquals(253000, (int) dbBoxItemTypePossibility3.getRazarion());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}