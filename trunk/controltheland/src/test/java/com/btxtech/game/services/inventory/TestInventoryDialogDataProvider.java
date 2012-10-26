package com.btxtech.game.services.inventory;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.services.AbstractServiceTest;
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
import com.btxtech.game.services.utg.UserGuidanceService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 15.05.12
 * Time: 12:54
 */
public class TestInventoryDialogDataProvider extends AbstractServiceTest {
    @Autowired
    private GlobalInventoryService globalInventoryService;
    @Autowired
    private ServerItemTypeService serverItemService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;

    @Test
    @DirtiesContext
    public void artifactPlanetRelation() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItemTypes();
        // Add artifact to box
        DbInventoryArtifact dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact1.setName("dbInventoryArtifact1");
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact1);
        // Create Box
        DbBoxItemType dbBoxItemType1 = createDbBoxItemType1();
        DbBoxItemTypePossibility dbBoxItemTypePossibility1 = dbBoxItemType1.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility1.setDbInventoryArtifact(dbInventoryArtifact1);
        dbBoxItemTypePossibility1.setPossibility(1.0);
        serverItemTypeService.saveDbItemType(dbBoxItemType1);
        // Create Planet with box region
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().createDbChild();
        DbBoxRegion dbBoxRegion1 = dbPlanet.getBoxRegionCrud().createDbChild();
        dbBoxRegion1.setItemFreeRange(111);
        dbBoxRegion1.setMaxInterval(22);
        dbBoxRegion1.setMinInterval(3);
        dbBoxRegion1.setName("DbBoxRegion1");
        DbRegion dbRegion1 = createDbRegion(new Rectangle(1, 2, 10, 20));
        dbBoxRegion1.setRegion(dbRegion1);
        DbBoxRegionCount dbBoxRegionCount1 = dbBoxRegion1.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount1.setDbBoxItemType(serverItemService.getDbBoxItemType(dbBoxItemType1.getId()));
        dbBoxRegionCount1.setCount(10);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().readDbChild(dbInventoryArtifact1.getId());
        Assert.assertEquals(1, dbInventoryArtifact1.getPlanets().size());
        Assert.assertEquals(dbPlanet.getId(), CommonJava.getFirst(dbInventoryArtifact1.getPlanets()).getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void artifactMultiplePlanetRelations() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItemTypes();
        // Add artifact to box
        DbInventoryArtifact dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact1.setName("dbInventoryArtifact1");
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact1);
        // Create Box
        DbBoxItemType dbBoxItemType1 = createDbBoxItemType1();
        DbBoxItemTypePossibility dbBoxItemTypePossibility1 = dbBoxItemType1.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility1.setDbInventoryArtifact(dbInventoryArtifact1);
        dbBoxItemTypePossibility1.setPossibility(1.0);
        serverItemTypeService.saveDbItemType(dbBoxItemType1);
        // Create Planet 1 with box region
        DbPlanet dbPlanet1 = planetSystemService.getDbPlanetCrud().createDbChild();
        DbBoxRegion dbBoxRegion1 = dbPlanet1.getBoxRegionCrud().createDbChild();
        dbBoxRegion1.setName("DbBoxRegion1");
        DbRegion dbRegion1 = createDbRegion(new Rectangle(1, 2, 10, 20));
        dbBoxRegion1.setRegion(dbRegion1);
        DbBoxRegionCount dbBoxRegionCount1 = dbBoxRegion1.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount1.setDbBoxItemType(serverItemService.getDbBoxItemType(dbBoxItemType1.getId()));
        dbBoxRegionCount1.setCount(10);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet1);
        // Create Planet 2 with box region
        DbPlanet dbPlanet2 = planetSystemService.getDbPlanetCrud().createDbChild();
        DbBoxRegion dbBoxRegion2 = dbPlanet2.getBoxRegionCrud().createDbChild();
        dbBoxRegion2.setName("DbBoxRegion2");
        DbRegion dbRegion2 = createDbRegion(new Rectangle(1, 2, 10, 20));
        dbBoxRegion2.setRegion(dbRegion2);
        DbBoxRegionCount dbBoxRegionCount2 = dbBoxRegion2.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount2.setDbBoxItemType(serverItemService.getDbBoxItemType(dbBoxItemType1.getId()));
        dbBoxRegionCount2.setCount(10);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().readDbChild(dbInventoryArtifact1.getId());
        Assert.assertEquals(2, dbInventoryArtifact1.getPlanets().size());
        dbPlanet1 = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet1.getId());
        dbPlanet2 = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet2.getId());
        Assert.assertTrue(dbInventoryArtifact1.getPlanets().contains(dbPlanet1));
        Assert.assertTrue(dbInventoryArtifact1.getPlanets().contains(dbPlanet2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void artifactBaseItemTypeRelation() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItemTypes();
        // Add artifact to box
        DbInventoryArtifact dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact1.setName("dbInventoryArtifact1");
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact1);
        // Create Box
        DbBoxItemType dbBoxItemType1 = createDbBoxItemType1();
        DbBoxItemTypePossibility dbBoxItemTypePossibility1 = dbBoxItemType1.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility1.setDbInventoryArtifact(dbInventoryArtifact1);
        dbBoxItemTypePossibility1.setPossibility(1.0);
        serverItemTypeService.saveDbItemType(dbBoxItemType1);
        // Set on base item type
        DbBaseItemType attackItem = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(TEST_ATTACK_ITEM_ID);
        attackItem.setDbBoxItemType(dbBoxItemType1);
        serverItemTypeService.getDbItemTypeCrud().updateDbChild(attackItem);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().readDbChild(dbInventoryArtifact1.getId());
        Assert.assertEquals(1, dbInventoryArtifact1.getBaseItemTypes().size());
        Assert.assertEquals(attackItem.getId(), CommonJava.getFirst(dbInventoryArtifact1.getBaseItemTypes()).getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void artifactMultipleBaseItemTypeRelations() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItemTypes();
        // Add artifact to box
        DbInventoryArtifact dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact1.setName("dbInventoryArtifact1");
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact1);
        // Create Box
        DbBoxItemType dbBoxItemType1 = createDbBoxItemType1();
        DbBoxItemTypePossibility dbBoxItemTypePossibility1 = dbBoxItemType1.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility1.setDbInventoryArtifact(dbInventoryArtifact1);
        dbBoxItemTypePossibility1.setPossibility(1.0);
        serverItemTypeService.saveDbItemType(dbBoxItemType1);
        // Set on base item type 1
        DbBaseItemType attackItem = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(TEST_ATTACK_ITEM_ID);
        attackItem.setDbBoxItemType(dbBoxItemType1);
        serverItemTypeService.getDbItemTypeCrud().updateDbChild(attackItem);
        // Set on base item type 2
        DbBaseItemType startItem = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(TEST_START_BUILDER_ITEM_ID);
        startItem.setDbBoxItemType(dbBoxItemType1);
        serverItemTypeService.getDbItemTypeCrud().updateDbChild(startItem);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().readDbChild(dbInventoryArtifact1.getId());
        Assert.assertEquals(2, dbInventoryArtifact1.getBaseItemTypes().size());
        attackItem = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(attackItem.getId());
        startItem = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(startItem.getId());
        Assert.assertTrue(dbInventoryArtifact1.getBaseItemTypes().contains(attackItem));
        Assert.assertTrue(dbInventoryArtifact1.getBaseItemTypes().contains(startItem));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void itemArtifactPlanetRelation() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItemTypes();
        // Add artifact to box
        DbInventoryArtifact dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact1.setName("dbInventoryArtifact1");
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact1);
        // Create inventory item
        DbInventoryItem dbInventoryItem1 = globalInventoryService.getItemCrud().createDbChild();
        DbInventoryArtifactCount dbInventoryArtifactCount = dbInventoryItem1.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(1);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact1);
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem1);
        // Create Box
        DbBoxItemType dbBoxItemType1 = createDbBoxItemType1();
        DbBoxItemTypePossibility dbBoxItemTypePossibility1 = dbBoxItemType1.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility1.setDbInventoryArtifact(dbInventoryArtifact1);
        dbBoxItemTypePossibility1.setPossibility(1.0);
        serverItemTypeService.saveDbItemType(dbBoxItemType1);
        // Create Planet with box region
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().createDbChild();
        DbBoxRegion dbBoxRegion1 = dbPlanet.getBoxRegionCrud().createDbChild();
        dbBoxRegion1.setRegion(createDbRegion(new Rectangle(1, 2, 10, 20)));
        DbBoxRegionCount dbBoxRegionCount1 = dbBoxRegion1.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount1.setDbBoxItemType(serverItemService.getDbBoxItemType(dbBoxItemType1.getId()));
        dbBoxRegionCount1.setCount(10);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbInventoryItem1 = globalInventoryService.getItemCrud().readDbChild(dbInventoryItem1.getId());
        Assert.assertEquals(1, dbInventoryItem1.getPlanetsViaArtifact().size());
        Assert.assertEquals(dbPlanet.getId(), CommonJava.getFirst(dbInventoryItem1.getPlanetsViaArtifact()).getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void itemArtifactPlanetsMultipleRelations() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItemTypes();
        // Add artifact to box
        DbInventoryArtifact dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact1.setName("dbInventoryArtifact1");
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact1);
        // Create inventory item
        DbInventoryItem dbInventoryItem1 = globalInventoryService.getItemCrud().createDbChild();
        DbInventoryArtifactCount dbInventoryArtifactCount = dbInventoryItem1.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(1);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact1);
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem1);
        // Create Box
        DbBoxItemType dbBoxItemType1 = createDbBoxItemType1();
        DbBoxItemTypePossibility dbBoxItemTypePossibility1 = dbBoxItemType1.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility1.setDbInventoryArtifact(dbInventoryArtifact1);
        dbBoxItemTypePossibility1.setPossibility(1.0);
        serverItemTypeService.saveDbItemType(dbBoxItemType1);
        // Create Planet with box region
        DbPlanet dbPlanet1 = planetSystemService.getDbPlanetCrud().createDbChild();
        DbBoxRegion dbBoxRegion1 = dbPlanet1.getBoxRegionCrud().createDbChild();
        dbBoxRegion1.setRegion(createDbRegion(new Rectangle(1, 2, 10, 20)));
        DbBoxRegionCount dbBoxRegionCount1 = dbBoxRegion1.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount1.setDbBoxItemType(serverItemService.getDbBoxItemType(dbBoxItemType1.getId()));
        dbBoxRegionCount1.setCount(10);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet1);
        // Create Planet 2 with box region
        DbPlanet dbPlanet2 = planetSystemService.getDbPlanetCrud().createDbChild();
        DbBoxRegion dbBoxRegion2 = dbPlanet2.getBoxRegionCrud().createDbChild();
        dbBoxRegion2.setRegion(createDbRegion(new Rectangle(1, 2, 10, 20)));
        DbBoxRegionCount dbBoxRegionCount2 = dbBoxRegion2.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount2.setDbBoxItemType(serverItemService.getDbBoxItemType(dbBoxItemType1.getId()));
        dbBoxRegionCount2.setCount(10);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbInventoryItem1 = globalInventoryService.getItemCrud().readDbChild(dbInventoryItem1.getId());
        Assert.assertEquals(2, dbInventoryItem1.getPlanetsViaArtifact().size());
        dbPlanet1 = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet1.getId());
        dbPlanet2 = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet2.getId());
        Assert.assertTrue(dbInventoryItem1.getPlanetsViaArtifact().contains(dbPlanet1));
        Assert.assertTrue(dbInventoryItem1.getPlanetsViaArtifact().contains(dbPlanet2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void itemArtifactBaseItemTypeRelation() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItemTypes();
        // Add artifact to box
        DbInventoryArtifact dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact1.setName("dbInventoryArtifact1");
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact1);
        // Create inventory item
        DbInventoryItem dbInventoryItem1 = globalInventoryService.getItemCrud().createDbChild();
        DbInventoryArtifactCount dbInventoryArtifactCount = dbInventoryItem1.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(1);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact1);
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem1);
        // Create Box
        DbBoxItemType dbBoxItemType1 = createDbBoxItemType1();
        DbBoxItemTypePossibility dbBoxItemTypePossibility1 = dbBoxItemType1.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility1.setDbInventoryArtifact(dbInventoryArtifact1);
        dbBoxItemTypePossibility1.setPossibility(1.0);
        serverItemTypeService.saveDbItemType(dbBoxItemType1);
        // Set on base item type
        DbBaseItemType attackItem = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(TEST_ATTACK_ITEM_ID);
        attackItem.setDbBoxItemType(dbBoxItemType1);
        serverItemTypeService.getDbItemTypeCrud().updateDbChild(attackItem);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbInventoryItem1 = globalInventoryService.getItemCrud().readDbChild(dbInventoryItem1.getId());
        Assert.assertEquals(1, dbInventoryItem1.getBaseItemTypesViaArtifact().size());
        Assert.assertEquals(attackItem.getId(), CommonJava.getFirst(dbInventoryItem1.getBaseItemTypesViaArtifact()).getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void itemArtifactBaseItemTypeMultipleRelations() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItemTypes();
        // Add artifact to box
        DbInventoryArtifact dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact1.setName("dbInventoryArtifact1");
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact1);
        // Create inventory item
        DbInventoryItem dbInventoryItem1 = globalInventoryService.getItemCrud().createDbChild();
        DbInventoryArtifactCount dbInventoryArtifactCount = dbInventoryItem1.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(1);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact1);
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem1);
        // Create Box
        DbBoxItemType dbBoxItemType1 = createDbBoxItemType1();
        DbBoxItemTypePossibility dbBoxItemTypePossibility1 = dbBoxItemType1.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility1.setDbInventoryArtifact(dbInventoryArtifact1);
        dbBoxItemTypePossibility1.setPossibility(1.0);
        serverItemTypeService.saveDbItemType(dbBoxItemType1);
        // Set on base item type
        DbBaseItemType attackItem = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(TEST_ATTACK_ITEM_ID);
        attackItem.setDbBoxItemType(dbBoxItemType1);
        serverItemTypeService.getDbItemTypeCrud().updateDbChild(attackItem);
        // Set on base item type 2
        DbBaseItemType startItem = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(TEST_START_BUILDER_ITEM_ID);
        startItem.setDbBoxItemType(dbBoxItemType1);
        serverItemTypeService.getDbItemTypeCrud().updateDbChild(startItem);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbInventoryItem1 = globalInventoryService.getItemCrud().readDbChild(dbInventoryItem1.getId());
        Assert.assertEquals(2, dbInventoryItem1.getBaseItemTypesViaArtifact().size());
        attackItem = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(attackItem.getId());
        startItem = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(startItem.getId());
        Assert.assertTrue(dbInventoryItem1.getBaseItemTypesViaArtifact().contains(attackItem));
        Assert.assertTrue(dbInventoryItem1.getBaseItemTypesViaArtifact().contains(startItem));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }


    @Test
    @DirtiesContext
    public void itemPlanetRelation() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItemTypes();
        // Create inventory item
        DbInventoryItem dbInventoryItem1 = globalInventoryService.getItemCrud().createDbChild();
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem1);
        // Create Box
        DbBoxItemType dbBoxItemType1 = createDbBoxItemType1();
        DbBoxItemTypePossibility dbBoxItemTypePossibility1 = dbBoxItemType1.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility1.setDbInventoryItem(dbInventoryItem1);
        dbBoxItemTypePossibility1.setPossibility(1.0);
        serverItemTypeService.saveDbItemType(dbBoxItemType1);
        // Create Planet with box region
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().createDbChild();
        DbBoxRegion dbBoxRegion1 = dbPlanet.getBoxRegionCrud().createDbChild();
        dbBoxRegion1.setRegion(createDbRegion(new Rectangle(1, 2, 10, 20)));
        DbBoxRegionCount dbBoxRegionCount1 = dbBoxRegion1.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount1.setDbBoxItemType(serverItemService.getDbBoxItemType(dbBoxItemType1.getId()));
        dbBoxRegionCount1.setCount(10);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbInventoryItem1 = globalInventoryService.getItemCrud().readDbChild(dbInventoryItem1.getId());
        Assert.assertEquals(1, dbInventoryItem1.getPlanets().size());
        Assert.assertEquals(dbPlanet.getId(), CommonJava.getFirst(dbInventoryItem1.getPlanets()).getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void itemPlanetsMultipleRelations() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItemTypes();
        // Create inventory item
        DbInventoryItem dbInventoryItem1 = globalInventoryService.getItemCrud().createDbChild();
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem1);
        // Create Box
        DbBoxItemType dbBoxItemType1 = createDbBoxItemType1();
        DbBoxItemTypePossibility dbBoxItemTypePossibility1 = dbBoxItemType1.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility1.setDbInventoryItem(dbInventoryItem1);
        dbBoxItemTypePossibility1.setPossibility(1.0);
        serverItemTypeService.saveDbItemType(dbBoxItemType1);
        // Create Planet with box region
        DbPlanet dbPlanet1 = planetSystemService.getDbPlanetCrud().createDbChild();
        DbBoxRegion dbBoxRegion1 = dbPlanet1.getBoxRegionCrud().createDbChild();
        dbBoxRegion1.setRegion(createDbRegion(new Rectangle(1, 2, 10, 20)));
        DbBoxRegionCount dbBoxRegionCount1 = dbBoxRegion1.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount1.setDbBoxItemType(serverItemService.getDbBoxItemType(dbBoxItemType1.getId()));
        dbBoxRegionCount1.setCount(10);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet1);
        // Create Planet 2 with box region
        DbPlanet dbPlanet2 = planetSystemService.getDbPlanetCrud().createDbChild();
        DbBoxRegion dbBoxRegion2 = dbPlanet2.getBoxRegionCrud().createDbChild();
        dbBoxRegion2.setRegion(createDbRegion(new Rectangle(1, 2, 10, 20)));
        DbBoxRegionCount dbBoxRegionCount2 = dbBoxRegion2.getBoxRegionCountCrud().createDbChild();
        dbBoxRegionCount2.setDbBoxItemType(serverItemService.getDbBoxItemType(dbBoxItemType1.getId()));
        dbBoxRegionCount2.setCount(10);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbInventoryItem1 = globalInventoryService.getItemCrud().readDbChild(dbInventoryItem1.getId());
        Assert.assertEquals(2, dbInventoryItem1.getPlanets().size());
        dbPlanet1 = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet1.getId());
        dbPlanet2 = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet2.getId());
        Assert.assertTrue(dbInventoryItem1.getPlanets().contains(dbPlanet1));
        Assert.assertTrue(dbInventoryItem1.getPlanets().contains(dbPlanet2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void itemBaseItemTypeRelation() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItemTypes();
        // Create inventory item
        DbInventoryItem dbInventoryItem1 = globalInventoryService.getItemCrud().createDbChild();
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem1);
        // Create Box
        DbBoxItemType dbBoxItemType1 = createDbBoxItemType1();
        DbBoxItemTypePossibility dbBoxItemTypePossibility1 = dbBoxItemType1.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility1.setDbInventoryItem(dbInventoryItem1);
        dbBoxItemTypePossibility1.setPossibility(1.0);
        serverItemTypeService.saveDbItemType(dbBoxItemType1);
        // Set on base item type
        DbBaseItemType attackItem = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(TEST_ATTACK_ITEM_ID);
        attackItem.setDbBoxItemType(dbBoxItemType1);
        serverItemTypeService.getDbItemTypeCrud().updateDbChild(attackItem);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbInventoryItem1 = globalInventoryService.getItemCrud().readDbChild(dbInventoryItem1.getId());
        Assert.assertEquals(1, dbInventoryItem1.getBaseItemTypes().size());
        Assert.assertEquals(attackItem.getId(), CommonJava.getFirst(dbInventoryItem1.getBaseItemTypes()).getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void itemBaseItemTypeMultipleRelations() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItemTypes();
        // Create inventory item
        DbInventoryItem dbInventoryItem1 = globalInventoryService.getItemCrud().createDbChild();
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem1);
        // Create Box
        DbBoxItemType dbBoxItemType1 = createDbBoxItemType1();
        DbBoxItemTypePossibility dbBoxItemTypePossibility1 = dbBoxItemType1.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility1.setDbInventoryItem(dbInventoryItem1);
        dbBoxItemTypePossibility1.setPossibility(1.0);
        serverItemTypeService.saveDbItemType(dbBoxItemType1);
        // Set on base item type
        DbBaseItemType attackItem = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(TEST_ATTACK_ITEM_ID);
        attackItem.setDbBoxItemType(dbBoxItemType1);
        serverItemTypeService.getDbItemTypeCrud().updateDbChild(attackItem);
        // Set on base item type 2
        DbBaseItemType startItem = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(TEST_START_BUILDER_ITEM_ID);
        startItem.setDbBoxItemType(dbBoxItemType1);
        serverItemTypeService.getDbItemTypeCrud().updateDbChild(startItem);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbInventoryItem1 = globalInventoryService.getItemCrud().readDbChild(dbInventoryItem1.getId());
        Assert.assertEquals(2, dbInventoryItem1.getBaseItemTypes().size());
        attackItem = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(attackItem.getId());
        startItem = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(startItem.getId());
        Assert.assertTrue(dbInventoryItem1.getBaseItemTypes().contains(attackItem));
        Assert.assertTrue(dbInventoryItem1.getBaseItemTypes().contains(startItem));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
