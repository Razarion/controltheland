package com.btxtech.game.services.inventory;

import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
}
