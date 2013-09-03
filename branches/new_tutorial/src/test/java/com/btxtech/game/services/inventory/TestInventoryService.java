package com.btxtech.game.services.inventory;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryArtifactInfo;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryItemInfo;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.inventory.impl.DbInventoryNewUser;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemTypePossibility;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.db.DbBoxRegion;
import com.btxtech.game.services.planet.db.DbBoxRegionCount;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.terrain.DbRegion;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 15.05.12
 * Time: 12:54
 */
public class TestInventoryService extends AbstractServiceTest {
    @Autowired
    private GlobalInventoryService globalInventoryService;
    @Autowired
    private ServerItemTypeService serverItemService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private UserService userService;
    @Autowired
    private PlanetSystemService planetSystemService;

    @Test
    @DirtiesContext
    public void crudArtifacts() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, globalInventoryService.getArtifactCrud().readDbChildren().size());
        Assert.assertEquals(0, globalInventoryService.getItemCrud().readDbChildren().size());

        DbInventoryArtifact dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact1.setName("Artifact1");
        dbInventoryArtifact1.setRareness(DbInventoryArtifact.Rareness.UN_COMMON);
        dbInventoryArtifact1.setImageContentType("imageContent");
        dbInventoryArtifact1.setImageData(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
        dbInventoryArtifact1.setRazarionCoast(12);
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
        Assert.assertEquals(2, globalInventoryService.getArtifactCrud().readDbChildren().size());
        Assert.assertEquals(0, globalInventoryService.getItemCrud().readDbChildren().size());

        List<DbInventoryArtifact> artifactList = (List<DbInventoryArtifact>) globalInventoryService.getArtifactCrud().readDbChildren();
        Assert.assertEquals("Artifact1", artifactList.get(0).getName());
        Assert.assertEquals(DbInventoryArtifact.Rareness.UN_COMMON, artifactList.get(0).getRareness());
        Assert.assertEquals("imageContent", artifactList.get(0).getImageContentType());
        Assert.assertEquals(12, (int) artifactList.get(0).getRazarionCoast());
        Assert.assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, artifactList.get(0).getImageData());

        Assert.assertEquals("Artifact2", artifactList.get(1).getName());
        Assert.assertEquals(DbInventoryArtifact.Rareness.COMMON, artifactList.get(1).getRareness());
        Assert.assertEquals("imageContent2", artifactList.get(1).getImageContentType());
        Assert.assertNull(artifactList.get(1).getRazarionCoast());
        Assert.assertArrayEquals(new byte[]{7, 8, 9}, artifactList.get(1).getImageData());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Delete first
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        globalInventoryService.getArtifactCrud().deleteDbChild(dbInventoryArtifact1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, globalInventoryService.getArtifactCrud().readDbChildren().size());
        Assert.assertEquals(0, globalInventoryService.getItemCrud().readDbChildren().size());

        artifactList = (List<DbInventoryArtifact>) globalInventoryService.getArtifactCrud().readDbChildren();
        Assert.assertEquals("Artifact2", artifactList.get(0).getName());
        Assert.assertEquals(DbInventoryArtifact.Rareness.COMMON, artifactList.get(0).getRareness());
        Assert.assertEquals("imageContent2", artifactList.get(0).getImageContentType());
        Assert.assertNull(artifactList.get(0).getRazarionCoast());
        Assert.assertArrayEquals(new byte[]{7, 8, 9}, artifactList.get(0).getImageData());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Delete second
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        globalInventoryService.getArtifactCrud().deleteDbChild(dbInventoryArtifact2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, globalInventoryService.getArtifactCrud().readDbChildren().size());
        Assert.assertEquals(0, globalInventoryService.getItemCrud().readDbChildren().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void crudItems() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, globalInventoryService.getArtifactCrud().readDbChildren().size());
        Assert.assertEquals(0, globalInventoryService.getItemCrud().readDbChildren().size());

        DbInventoryArtifact dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact1.setName("Artifact1");
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact1);

        DbInventoryArtifact dbInventoryArtifact2 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact2.setName("Artifact2");
        dbInventoryArtifact2.setRazarionCoast(2);
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact2);

        DbInventoryArtifact dbInventoryArtifact3 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact3.setName("Artifact3");
        dbInventoryArtifact3.setRazarionCoast(3);
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact3);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryItem dbInventoryItem1 = globalInventoryService.getItemCrud().createDbChild();
        dbInventoryItem1.setName("GoldItem1");
        dbInventoryItem1.setGoldAmount(100);
        dbInventoryItem1.setImageContentType("imageData22");
        dbInventoryItem1.setImageData(new byte[]{1, 3, 4, 6, 7, 9});
        dbInventoryItem1.setGoldLevel(userGuidanceService.getDbLevel(TEST_LEVEL_2_REAL_ID));
        dbInventoryItem1.setRazarionCoast(43);
        DbInventoryArtifactCount dbInventoryArtifactCount = dbInventoryItem1.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(2);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact1);
        dbInventoryArtifactCount = dbInventoryItem1.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(1);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact2);
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem1);

        DbInventoryItem dbInventoryItem2 = globalInventoryService.getItemCrud().createDbChild();
        dbInventoryItem2.setName("GoldItem2");
        dbInventoryItem2.setGoldAmount(10);
        dbInventoryItem2.setImageContentType("imageData33");
        dbInventoryItem2.setImageData(new byte[]{6, 7, 9});
        dbInventoryItem2.setGoldLevel(userGuidanceService.getDbLevel(TEST_LEVEL_2_REAL_ID));
        dbInventoryArtifactCount = dbInventoryItem2.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(3);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact1);
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem2);

        DbInventoryItem dbInventoryItem3 = globalInventoryService.getItemCrud().createDbChild();
        dbInventoryItem3.setName("ItemType1");
        dbInventoryItem3.setBaseItemTypeCount(2);
        dbInventoryItem3.setRazarionCoast(108);
        dbInventoryItem3.setDbBaseItemType((DbBaseItemType) serverItemService.getDbItemType(TEST_ATTACK_ITEM_ID));
        dbInventoryArtifactCount = dbInventoryItem3.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(1);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact3);
        dbInventoryArtifactCount = dbInventoryItem3.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(2);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact1);
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem3);

        DbInventoryItem dbInventoryItem4 = globalInventoryService.getItemCrud().createDbChild();
        dbInventoryItem4.setName("ItemType2");
        dbInventoryItem4.setBaseItemTypeCount(1);
        dbInventoryItem4.setDbBaseItemType((DbBaseItemType) serverItemService.getDbItemType(TEST_FACTORY_ITEM_ID));
        dbInventoryArtifactCount = dbInventoryItem4.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(1);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact1);
        dbInventoryArtifactCount = dbInventoryItem4.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(2);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact2);
        dbInventoryArtifactCount = dbInventoryItem4.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(3);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact3);
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem4);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbInventoryItem> itemList = (List<DbInventoryItem>) globalInventoryService.getItemCrud().readDbChildren();
        Assert.assertEquals(4, itemList.size());
        Assert.assertEquals("GoldItem1", itemList.get(0).getName());
        Assert.assertEquals(100, itemList.get(0).getGoldAmount());
        Assert.assertEquals("imageData22", itemList.get(0).getImageContentType());
        Assert.assertEquals(43, (int) itemList.get(0).getRazarionCoast());
        Assert.assertEquals(2, (int)itemList.get(0).getRazarionCostViaArtifacts());
        Assert.assertArrayEquals(new byte[]{1, 3, 4, 6, 7, 9}, itemList.get(0).getImageData());
        Assert.assertEquals(userGuidanceService.getDbLevel(TEST_LEVEL_2_REAL_ID), itemList.get(0).getGoldLevel());
        List<DbInventoryArtifactCount> dbInventoryArtifactCountFromDb = new ArrayList<>(itemList.get(0).getArtifactCountCrud().readDbChildren());
        Assert.assertEquals(2, dbInventoryArtifactCountFromDb.size());
        Assert.assertEquals(2, dbInventoryArtifactCountFromDb.get(0).getCount());
        Assert.assertEquals(dbInventoryArtifact1, dbInventoryArtifactCountFromDb.get(0).getDbInventoryArtifact());
        Assert.assertEquals(1, dbInventoryArtifactCountFromDb.get(1).getCount());
        Assert.assertEquals(dbInventoryArtifact2, dbInventoryArtifactCountFromDb.get(1).getDbInventoryArtifact());

        Assert.assertEquals("GoldItem2", itemList.get(1).getName());
        Assert.assertEquals(10, itemList.get(1).getGoldAmount());
        Assert.assertNull(itemList.get(1).getRazarionCoast());
        Assert.assertNull(itemList.get(1).getRazarionCostViaArtifacts());
        Assert.assertEquals("imageData33", itemList.get(1).getImageContentType());
        Assert.assertArrayEquals(new byte[]{6, 7, 9}, itemList.get(1).getImageData());
        Assert.assertEquals(userGuidanceService.getDbLevel(TEST_LEVEL_2_REAL_ID), itemList.get(1).getGoldLevel());
        dbInventoryArtifactCountFromDb = new ArrayList<>(itemList.get(1).getArtifactCountCrud().readDbChildren());
        Assert.assertEquals(1, dbInventoryArtifactCountFromDb.size());
        Assert.assertEquals(3, dbInventoryArtifactCountFromDb.get(0).getCount());
        Assert.assertEquals(dbInventoryArtifact1, dbInventoryArtifactCountFromDb.get(0).getDbInventoryArtifact());

        Assert.assertEquals("ItemType1", itemList.get(2).getName());
        Assert.assertEquals(2, itemList.get(2).getBaseItemTypeCount());
        Assert.assertEquals(108, (int) itemList.get(2).getRazarionCoast());
        Assert.assertEquals(3, (int)itemList.get(2).getRazarionCostViaArtifacts());
        Assert.assertEquals(TEST_ATTACK_ITEM_ID, (int) itemList.get(2).getDbBaseItemType().getId());
        dbInventoryArtifactCountFromDb = new ArrayList<>(itemList.get(2).getArtifactCountCrud().readDbChildren());
        Assert.assertEquals(2, dbInventoryArtifactCountFromDb.size());
        Assert.assertEquals(1, dbInventoryArtifactCountFromDb.get(0).getCount());
        Assert.assertEquals(dbInventoryArtifact3, dbInventoryArtifactCountFromDb.get(0).getDbInventoryArtifact());
        Assert.assertEquals(2, dbInventoryArtifactCountFromDb.get(1).getCount());
        Assert.assertEquals(dbInventoryArtifact1, dbInventoryArtifactCountFromDb.get(1).getDbInventoryArtifact());

        Assert.assertEquals("ItemType2", itemList.get(3).getName());
        Assert.assertEquals(1, itemList.get(3).getBaseItemTypeCount());
        Assert.assertNull(itemList.get(3).getRazarionCoast());
        Assert.assertEquals(13, (int)itemList.get(3).getRazarionCostViaArtifacts());
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
        globalInventoryService.getItemCrud().deleteDbChild(dbInventoryItem1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        itemList = (List<DbInventoryItem>) globalInventoryService.getItemCrud().readDbChildren();
        Assert.assertEquals(3, itemList.size());
        Assert.assertEquals("GoldItem2", itemList.get(0).getName());
        Assert.assertNull(itemList.get(0).getRazarionCoast());
        Assert.assertEquals(10, itemList.get(0).getGoldAmount());
        Assert.assertEquals("imageData33", itemList.get(0).getImageContentType());
        Assert.assertArrayEquals(new byte[]{6, 7, 9}, itemList.get(0).getImageData());
        Assert.assertEquals(userGuidanceService.getDbLevel(TEST_LEVEL_2_REAL_ID), itemList.get(0).getGoldLevel());
        dbInventoryArtifactCountFromDb = new ArrayList<>(itemList.get(0).getArtifactCountCrud().readDbChildren());
        Assert.assertEquals(1, dbInventoryArtifactCountFromDb.size());
        Assert.assertEquals(3, dbInventoryArtifactCountFromDb.get(0).getCount());
        Assert.assertEquals(dbInventoryArtifact1, dbInventoryArtifactCountFromDb.get(0).getDbInventoryArtifact());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryItem dbInventoryItem = globalInventoryService.getItemCrud().readDbChild(dbInventoryItem2.getId());
        DbInventoryArtifactCount dbInventoryArtifactCount1 = CommonJava.getFirst(dbInventoryItem.getArtifactCountCrud().readDbChildren());
        dbInventoryItem.getArtifactCountCrud().deleteDbChild(dbInventoryArtifactCount1);
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        itemList = (List<DbInventoryItem>) globalInventoryService.getItemCrud().readDbChildren();
        Assert.assertEquals(3, itemList.size());
        Assert.assertEquals("GoldItem2", itemList.get(0).getName());
        Assert.assertEquals(10, itemList.get(0).getGoldAmount());
        Assert.assertNull(itemList.get(0).getRazarionCoast());
        Assert.assertEquals("imageData33", itemList.get(0).getImageContentType());
        Assert.assertArrayEquals(new byte[]{6, 7, 9}, itemList.get(0).getImageData());
        Assert.assertEquals(userGuidanceService.getDbLevel(TEST_LEVEL_2_REAL_ID), itemList.get(0).getGoldLevel());
        dbInventoryArtifactCountFromDb = new ArrayList<>(itemList.get(0).getArtifactCountCrud().readDbChildren());
        Assert.assertEquals(0, dbInventoryArtifactCountFromDb.size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void boxRegionCrud() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItemTypes();
        createDbBoxItemType1();
        createDbBoxItemType2();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().createDbChild();
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        Assert.assertEquals(0, dbPlanet.getBoxRegionCrud().readDbChildren().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        DbBoxRegion dbBoxRegion1 = dbPlanet.getBoxRegionCrud().createDbChild();
        dbBoxRegion1.setItemFreeRange(111);
        dbBoxRegion1.setMaxInterval(22);
        dbBoxRegion1.setMinInterval(3);
        dbBoxRegion1.setName("DbBoxRegion1");
        DbRegion dbRegion1 = createDbRegion(new Rectangle(1, 2, 10, 20));
        dbBoxRegion1.setRegion(dbRegion1);
        DbBoxRegionCount dbBoxRegionCount1 = dbBoxRegion1.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount1.setDbBoxItemType(serverItemService.getDbBoxItemType(TEST_BOX_ITEM_1_ID));
        dbBoxRegionCount1.setCount(10);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        List<DbBoxRegion> itemList = (List<DbBoxRegion>) dbPlanet.getBoxRegionCrud().readDbChildren();
        Assert.assertEquals(1, itemList.size());
        Assert.assertEquals(111, itemList.get(0).getItemFreeRange());
        Assert.assertEquals(22, itemList.get(0).getMaxInterval());
        Assert.assertEquals(3, itemList.get(0).getMinInterval());
        Assert.assertEquals("DbBoxRegion1", itemList.get(0).getName());
        Assert.assertEquals(dbRegion1.getId(), itemList.get(0).getRegion().getId());
        List<DbBoxRegionCount> dbBoxRegionCounts = new ArrayList<>(itemList.get(0).getBoxRegionCountCrud().readDbChildren());
        Assert.assertEquals(1, dbBoxRegionCounts.size());
        Assert.assertEquals(10, dbBoxRegionCounts.get(0).getCount());
        Assert.assertEquals(TEST_BOX_ITEM_1_ID, (int) dbBoxRegionCounts.get(0).getDbBoxItemType().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        DbBoxRegion dbBoxRegion2 = dbPlanet.getBoxRegionCrud().createDbChild();
        dbBoxRegion2.setItemFreeRange(222);
        dbBoxRegion2.setMaxInterval(33);
        dbBoxRegion2.setMinInterval(4);
        dbBoxRegion2.setName("DbBoxRegion2");
        DbRegion dbRegion2 = createDbRegion(new Rectangle(4, 7, 30, 199));
        dbBoxRegion2.setRegion(dbRegion2);
        DbBoxRegionCount dbBoxRegionCount2 = dbBoxRegion2.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount2.setDbBoxItemType(serverItemService.getDbBoxItemType(TEST_BOX_ITEM_1_ID));
        dbBoxRegionCount2.setCount(5);
        DbBoxRegionCount dbBoxRegionCount3 = dbBoxRegion2.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount3.setDbBoxItemType(serverItemService.getDbBoxItemType(TEST_BOX_ITEM_2_ID));
        dbBoxRegionCount3.setCount(6);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        itemList = (List<DbBoxRegion>) dbPlanet.getBoxRegionCrud().readDbChildren();
        Assert.assertEquals(2, itemList.size());
        Assert.assertEquals(111, itemList.get(0).getItemFreeRange());
        Assert.assertEquals(22, itemList.get(0).getMaxInterval());
        Assert.assertEquals(3, itemList.get(0).getMinInterval());
        Assert.assertEquals("DbBoxRegion1", itemList.get(0).getName());
        Assert.assertEquals(dbRegion1.getId(), itemList.get(0).getRegion().getId());
        dbBoxRegionCounts = new ArrayList<>(itemList.get(0).getBoxRegionCountCrud().readDbChildren());
        Assert.assertEquals(1, dbBoxRegionCounts.size());
        Assert.assertEquals(10, dbBoxRegionCounts.get(0).getCount());
        Assert.assertEquals(TEST_BOX_ITEM_1_ID, (int) dbBoxRegionCounts.get(0).getDbBoxItemType().getId());

        Assert.assertEquals(222, itemList.get(1).getItemFreeRange());
        Assert.assertEquals(33, itemList.get(1).getMaxInterval());
        Assert.assertEquals(4, itemList.get(1).getMinInterval());
        Assert.assertEquals("DbBoxRegion2", itemList.get(1).getName());
        Assert.assertEquals(dbRegion2.getId(), itemList.get(1).getRegion().getId());
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
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        DbBoxRegion dbBoxRegion1FromDb = dbPlanet.getBoxRegionCrud().readDbChild(dbBoxRegion1.getId());
        dbBoxRegion1FromDb.getBoxRegionCountCrud().deleteDbChild(dbBoxRegionCount1);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        itemList = (List<DbBoxRegion>) dbPlanet.getBoxRegionCrud().readDbChildren();
        Assert.assertEquals(2, itemList.size());
        Assert.assertEquals(111, itemList.get(0).getItemFreeRange());
        Assert.assertEquals(22, itemList.get(0).getMaxInterval());
        Assert.assertEquals(3, itemList.get(0).getMinInterval());
        Assert.assertEquals("DbBoxRegion1", itemList.get(0).getName());
        Assert.assertEquals(dbRegion1.getId(), itemList.get(0).getRegion().getId());
        dbBoxRegionCounts = new ArrayList<>(itemList.get(0).getBoxRegionCountCrud().readDbChildren());
        Assert.assertEquals(0, dbBoxRegionCounts.size());

        Assert.assertEquals(222, itemList.get(1).getItemFreeRange());
        Assert.assertEquals(33, itemList.get(1).getMaxInterval());
        Assert.assertEquals(4, itemList.get(1).getMinInterval());
        Assert.assertEquals("DbBoxRegion2", itemList.get(1).getName());
        Assert.assertEquals(dbRegion2.getId(),            itemList.get(1).getRegion().getId());
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
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        dbPlanet.getBoxRegionCrud().deleteDbChild(dbPlanet.getBoxRegionCrud().readDbChild(dbBoxRegion1.getId()));
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        itemList = (List<DbBoxRegion>) dbPlanet.getBoxRegionCrud().readDbChildren();
        Assert.assertEquals(1, itemList.size());
        Assert.assertEquals(222, itemList.get(0).getItemFreeRange());
        Assert.assertEquals(33, itemList.get(0).getMaxInterval());
        Assert.assertEquals(4, itemList.get(0).getMinInterval());
        Assert.assertEquals("DbBoxRegion2", itemList.get(0).getName());
        Assert.assertEquals(dbRegion2.getId(), itemList.get(0).getRegion().getId());
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
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryItem dbInventoryItem = globalInventoryService.getItemCrud().createDbChild();
        DbInventoryArtifact dbInventoryArtifact = globalInventoryService.getArtifactCrud().createDbChild();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBoxItemType dbBoxItemType = (DbBoxItemType) serverItemService.getDbItemTypeCrud().createDbChild(DbBoxItemType.class);
        serverItemService.saveDbItemType(dbBoxItemType);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBoxItemType = (DbBoxItemType) serverItemService.getDbItemTypeCrud().readDbChild(dbBoxItemType.getId());
        DbBoxItemTypePossibility dbBoxItemTypePossibility1 = dbBoxItemType.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility1.setPossibility(0.1);
        dbBoxItemTypePossibility1.setDbInventoryItem(dbInventoryItem);
        serverItemService.getDbItemTypeCrud().updateDbChild(dbBoxItemType);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBoxItemType = (DbBoxItemType) serverItemService.getDbItemTypeCrud().readDbChild(dbBoxItemType.getId());
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
        dbBoxItemType = (DbBoxItemType) serverItemService.getDbItemTypeCrud().readDbChild(dbBoxItemType.getId());
        DbBoxItemTypePossibility dbBoxItemTypePossibility2 = dbBoxItemType.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility2.setPossibility(0.2);
        dbBoxItemTypePossibility2.setDbInventoryArtifact(dbInventoryArtifact);
        serverItemService.getDbItemTypeCrud().updateDbChild(dbBoxItemType);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBoxItemType = (DbBoxItemType) serverItemService.getDbItemTypeCrud().readDbChild(dbBoxItemType.getId());
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
        dbBoxItemType = (DbBoxItemType) serverItemService.getDbItemTypeCrud().readDbChild(dbBoxItemType.getId());
        DbBoxItemTypePossibility dbBoxItemTypePossibility3 = dbBoxItemType.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility3.setPossibility(0.3);
        dbBoxItemTypePossibility3.setRazarion(253000);
        serverItemService.getDbItemTypeCrud().updateDbChild(dbBoxItemType);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBoxItemType = (DbBoxItemType) serverItemService.getDbItemTypeCrud().readDbChild(dbBoxItemType.getId());
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

    @Test
    @DirtiesContext
    public void generateItemAndArtifact() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryArtifact dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact1.setName("Artifact1");
        dbInventoryArtifact1.setRareness(DbInventoryArtifact.Rareness.UN_COMMON);
        dbInventoryArtifact1.setImageContentType("imageContent");
        dbInventoryArtifact1.setImageData(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
        dbInventoryArtifact1.setRazarionCoast(11);
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact1);

        DbInventoryArtifact dbInventoryArtifact2 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact2.setName("Artifact2");
        dbInventoryArtifact2.setRareness(DbInventoryArtifact.Rareness.COMMON);
        dbInventoryArtifact2.setImageContentType("imageContent2");
        dbInventoryArtifact2.setImageData(new byte[]{7, 8, 9});
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact2);

        DbInventoryArtifact dbInventoryArtifact3 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact3.setName("Artifact3");
        dbInventoryArtifact3.setRareness(DbInventoryArtifact.Rareness.EPIC);
        dbInventoryArtifact3.setRazarionCoast(44);
        dbInventoryArtifact3.setImageContentType("imageContent2");
        dbInventoryArtifact3.setImageData(new byte[]{7, 8, 9});
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact3);

        DbInventoryArtifact dbInventoryArtifact4 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact4.setName("Artifact4");
        dbInventoryArtifact4.setRareness(DbInventoryArtifact.Rareness.LEGENDARY);
        dbInventoryArtifact4.setImageContentType("imageContent2");
        dbInventoryArtifact4.setImageData(new byte[]{7, 8, 9});
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact4);

        DbInventoryItem dbInventoryItem1 = globalInventoryService.getItemCrud().createDbChild();
        dbInventoryItem1.setName("GoldItem1");
        dbInventoryItem1.setGoldAmount(100);
        dbInventoryItem1.setImageContentType("imageData22");
        dbInventoryItem1.setImageData(new byte[]{1, 3, 4, 6, 7, 9});
        dbInventoryItem1.setRazarionCoast(66);
        dbInventoryItem1.setGoldLevel(userGuidanceService.getDbLevel(TEST_LEVEL_2_REAL_ID));
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem1);

        DbInventoryItem dbInventoryItem2 = globalInventoryService.getItemCrud().createDbChild();
        dbInventoryItem2.setName("GoldItem2");
        dbInventoryItem2.setGoldAmount(10);
        dbInventoryItem2.setImageContentType("imageData33");
        dbInventoryItem2.setImageData(new byte[]{6, 7, 9});
        dbInventoryItem2.setGoldLevel(userGuidanceService.getDbLevel(TEST_LEVEL_2_REAL_ID));
        DbInventoryArtifactCount dbInventoryArtifactCount = dbInventoryItem2.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(3);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact1);
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem2);

        DbInventoryItem dbInventoryItem3 = globalInventoryService.getItemCrud().createDbChild();
        dbInventoryItem3.setName("ItemType1");
        dbInventoryItem3.setBaseItemTypeCount(2);
        dbInventoryItem3.setItemFreeRange(111);
        dbInventoryItem3.setRazarionCoast(77);
        dbInventoryItem3.setDbBaseItemType((DbBaseItemType) serverItemService.getDbItemType(TEST_ATTACK_ITEM_ID));
        dbInventoryArtifactCount = dbInventoryItem3.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(1);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact3);
        dbInventoryArtifactCount = dbInventoryItem3.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(2);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact1);
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem3);

        DbInventoryItem dbInventoryItem4 = globalInventoryService.getItemCrud().createDbChild();
        dbInventoryItem4.setName("ItemType2");
        dbInventoryItem4.setBaseItemTypeCount(1);
        dbInventoryItem4.setItemFreeRange(222);
        dbInventoryItem4.setDbBaseItemType((DbBaseItemType) serverItemService.getDbItemType(TEST_FACTORY_ITEM_ID));
        dbInventoryArtifactCount = dbInventoryItem4.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(1);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact1);
        dbInventoryArtifactCount = dbInventoryItem4.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(2);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact2);
        dbInventoryArtifactCount = dbInventoryItem4.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(3);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact3);
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem4);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().readDbChild(dbInventoryArtifact1.getId());
        dbInventoryArtifact2 = globalInventoryService.getArtifactCrud().readDbChild(dbInventoryArtifact2.getId());
        dbInventoryArtifact3 = globalInventoryService.getArtifactCrud().readDbChild(dbInventoryArtifact3.getId());
        dbInventoryArtifact4 = globalInventoryService.getArtifactCrud().readDbChild(dbInventoryArtifact4.getId());

        Map<Integer, InventoryArtifactInfo> allArtifacts = new HashMap<>();
        InventoryArtifactInfo inventoryArtifactInfo1 = dbInventoryArtifact1.generateInventoryArtifactInfo();
        allArtifacts.put(inventoryArtifactInfo1.getInventoryArtifactId(), inventoryArtifactInfo1);
        InventoryArtifactInfo inventoryArtifactInfo2 = dbInventoryArtifact2.generateInventoryArtifactInfo();
        allArtifacts.put(inventoryArtifactInfo2.getInventoryArtifactId(), inventoryArtifactInfo2);
        InventoryArtifactInfo inventoryArtifactInfo3 = dbInventoryArtifact3.generateInventoryArtifactInfo();
        allArtifacts.put(inventoryArtifactInfo3.getInventoryArtifactId(), inventoryArtifactInfo3);
        InventoryArtifactInfo inventoryArtifactInfo4 = dbInventoryArtifact4.generateInventoryArtifactInfo();
        allArtifacts.put(inventoryArtifactInfo4.getInventoryArtifactId(), inventoryArtifactInfo4);

        Assert.assertEquals((int) dbInventoryArtifact1.getId(), inventoryArtifactInfo1.getInventoryArtifactId());
        Assert.assertEquals("Artifact1", inventoryArtifactInfo1.getInventoryArtifactName());
        Assert.assertEquals("#70d460", inventoryArtifactInfo1.getHtmlRarenessColor());
        Assert.assertEquals(11, inventoryArtifactInfo1.getRazarionCoast());
        Assert.assertTrue(inventoryArtifactInfo1.hasRazarionCoast());

        Assert.assertEquals((int) dbInventoryArtifact2.getId(), inventoryArtifactInfo2.getInventoryArtifactId());
        Assert.assertEquals("Artifact2", inventoryArtifactInfo2.getInventoryArtifactName());
        Assert.assertEquals("#d6f6ff", inventoryArtifactInfo2.getHtmlRarenessColor());
        Assert.assertFalse(inventoryArtifactInfo2.hasRazarionCoast());

        Assert.assertEquals((int) dbInventoryArtifact3.getId(), inventoryArtifactInfo3.getInventoryArtifactId());
        Assert.assertEquals("Artifact3", inventoryArtifactInfo3.getInventoryArtifactName());
        Assert.assertEquals("#a042cc", inventoryArtifactInfo3.getHtmlRarenessColor());
        Assert.assertEquals(44, inventoryArtifactInfo3.getRazarionCoast());
        Assert.assertTrue(inventoryArtifactInfo1.hasRazarionCoast());

        Assert.assertEquals((int) dbInventoryArtifact4.getId(), inventoryArtifactInfo4.getInventoryArtifactId());
        Assert.assertEquals("Artifact4", inventoryArtifactInfo4.getInventoryArtifactName());
        Assert.assertEquals("#f07d4e", inventoryArtifactInfo4.getHtmlRarenessColor());
        Assert.assertFalse(inventoryArtifactInfo4.hasRazarionCoast());

        dbInventoryItem1 = globalInventoryService.getItemCrud().readDbChild(dbInventoryItem1.getId());
        dbInventoryItem2 = globalInventoryService.getItemCrud().readDbChild(dbInventoryItem2.getId());
        dbInventoryItem3 = globalInventoryService.getItemCrud().readDbChild(dbInventoryItem3.getId());
        dbInventoryItem4 = globalInventoryService.getItemCrud().readDbChild(dbInventoryItem4.getId());

        InventoryItemInfo inventoryItemInfo1 = dbInventoryItem1.generateInventoryItemInfo(allArtifacts);
        InventoryItemInfo inventoryItemInfo2 = dbInventoryItem2.generateInventoryItemInfo(allArtifacts);
        InventoryItemInfo inventoryItemInfo3 = dbInventoryItem3.generateInventoryItemInfo(allArtifacts);
        InventoryItemInfo inventoryItemInfo4 = dbInventoryItem4.generateInventoryItemInfo(allArtifacts);

        Assert.assertEquals((int) dbInventoryItem1.getId(), inventoryItemInfo1.getInventoryItemId());
        Assert.assertEquals(100, inventoryItemInfo1.getGoldAmount());
        Assert.assertEquals("GoldItem1", inventoryItemInfo1.getInventoryItemName());
        Assert.assertEquals(0, inventoryItemInfo1.getArtifacts().size());
        Assert.assertTrue(inventoryItemInfo1.hasRazarionCoast());
        Assert.assertEquals(66, inventoryItemInfo1.getRazarionCoast());

        Assert.assertEquals((int) dbInventoryItem2.getId(), inventoryItemInfo2.getInventoryItemId());
        Assert.assertEquals(10, inventoryItemInfo2.getGoldAmount());
        Assert.assertEquals("GoldItem2", inventoryItemInfo2.getInventoryItemName());
        Assert.assertEquals(1, inventoryItemInfo2.getArtifacts().size());
        Assert.assertEquals(3, (int) inventoryItemInfo2.getArtifacts().get(inventoryArtifactInfo1));
        Assert.assertFalse(inventoryItemInfo2.hasRazarionCoast());

        Assert.assertEquals((int) dbInventoryItem3.getId(), inventoryItemInfo3.getInventoryItemId());
        Assert.assertEquals("ItemType1", inventoryItemInfo3.getInventoryItemName());
        Assert.assertEquals(2, inventoryItemInfo3.getItemCount());
        Assert.assertEquals(111, inventoryItemInfo3.getItemFreeRange());
        Assert.assertEquals(2, inventoryItemInfo3.getArtifacts().size());
        Assert.assertEquals(2, (int) inventoryItemInfo3.getArtifacts().get(inventoryArtifactInfo1));
        Assert.assertEquals(1, (int) inventoryItemInfo3.getArtifacts().get(inventoryArtifactInfo3));
        Assert.assertTrue(inventoryItemInfo3.hasRazarionCoast());
        Assert.assertEquals(77, inventoryItemInfo3.getRazarionCoast());

        Assert.assertEquals((int) dbInventoryItem4.getId(), inventoryItemInfo4.getInventoryItemId());
        Assert.assertEquals("ItemType2", inventoryItemInfo4.getInventoryItemName());
        Assert.assertEquals(1, inventoryItemInfo4.getItemCount());
        Assert.assertEquals(222, inventoryItemInfo4.getItemFreeRange());
        Assert.assertEquals(3, inventoryItemInfo4.getArtifacts().size());
        Assert.assertEquals(1, (int) inventoryItemInfo4.getArtifacts().get(inventoryArtifactInfo1));
        Assert.assertEquals(2, (int) inventoryItemInfo4.getArtifacts().get(inventoryArtifactInfo2));
        Assert.assertEquals(3, (int) inventoryItemInfo4.getArtifacts().get(inventoryArtifactInfo3));
        Assert.assertFalse(inventoryItemInfo4.hasRazarionCoast());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void setupNewUser() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryArtifact dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact1.setName("Artifact1");
        dbInventoryArtifact1.setRareness(DbInventoryArtifact.Rareness.UN_COMMON);
        dbInventoryArtifact1.setImageContentType("imageContent");
        dbInventoryArtifact1.setImageData(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact1);

        DbInventoryArtifact dbInventoryArtifact2 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact2.setName("Artifact2");
        dbInventoryArtifact2.setRareness(DbInventoryArtifact.Rareness.COMMON);
        dbInventoryArtifact2.setImageContentType("imageContent2");
        dbInventoryArtifact2.setImageData(new byte[]{7, 8, 9});
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact2);

        DbInventoryItem dbInventoryItem1 = globalInventoryService.getItemCrud().createDbChild();
        dbInventoryItem1.setName("ItemType1");
        dbInventoryItem1.setBaseItemTypeCount(2);
        dbInventoryItem1.setItemFreeRange(111);
        dbInventoryItem1.setDbBaseItemType((DbBaseItemType) serverItemService.getDbItemType(TEST_ATTACK_ITEM_ID));
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem1);

        DbInventoryNewUser inventoryNewUser1 = globalInventoryService.getNewUserCrud().createDbChild();
        inventoryNewUser1.setRazarion(33);
        globalInventoryService.getNewUserCrud().updateDbChild(inventoryNewUser1);
        DbInventoryNewUser inventoryNewUser2 = globalInventoryService.getNewUserCrud().createDbChild();
        inventoryNewUser2.setDbInventoryItem(dbInventoryItem1);
        inventoryNewUser2.setCount(2);
        globalInventoryService.getNewUserCrud().updateDbChild(inventoryNewUser2);
        DbInventoryNewUser inventoryNewUser3 = globalInventoryService.getNewUserCrud().createDbChild();
        inventoryNewUser3.setDbInventoryArtifact(dbInventoryArtifact1);
        inventoryNewUser3.setCount(1);
        globalInventoryService.getNewUserCrud().updateDbChild(inventoryNewUser3);
        DbInventoryNewUser inventoryNewUser4 = globalInventoryService.getNewUserCrud().createDbChild();
        inventoryNewUser4.setDbInventoryArtifact(dbInventoryArtifact2);
        inventoryNewUser4.setCount(3);
        globalInventoryService.getNewUserCrud().updateDbChild(inventoryNewUser4);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getOrCreateBase();
        UserState userState = userService.getUserState();
        Assert.assertEquals(33, userState.getRazarion());

        Assert.assertEquals(2, userState.getInventoryItemIds().size());
        Assert.assertTrue(userState.hasInventoryItemId(dbInventoryItem1.getId()));
        userState.removeInventoryItemId(dbInventoryItem1.getId());
        Assert.assertTrue(userState.hasInventoryItemId(dbInventoryItem1.getId()));
        userState.removeInventoryItemId(dbInventoryItem1.getId());
        Assert.assertFalse(userState.hasInventoryItemId(dbInventoryItem1.getId()));
        Assert.assertTrue(userState.getInventoryItemIds().isEmpty());

        Assert.assertEquals(4, userState.getInventoryArtifactIds().size());
        Collection<Integer> artifacts = new ArrayList<>();
        artifacts.add(dbInventoryArtifact1.getId());
        artifacts.add(dbInventoryArtifact2.getId());
        artifacts.add(dbInventoryArtifact2.getId());
        artifacts.add(dbInventoryArtifact2.getId());
        Assert.assertTrue(userState.removeArtifactIds(artifacts));
        Assert.assertTrue(userState.getInventoryArtifactIds().isEmpty());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
