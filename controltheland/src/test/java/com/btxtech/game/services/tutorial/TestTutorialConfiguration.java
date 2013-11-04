package com.btxtech.game.services.tutorial;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.RadarMode;
import com.btxtech.game.jsre.client.utg.tip.GameTipConfig;
import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.jsre.common.tutorial.AbstractTaskConfig;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.item.ServerItemTypeService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Locale;

/**
 * User: beat
 * Date: March 08, 2011
 * Time: 12:00:44 PM
 */
public class TestTutorialConfiguration extends AbstractServiceTest {
    @Autowired
    private TutorialService tutorialService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private RuServiceHelper<DbTutorialConfig> ruTutorialServiceHelper;
    @Autowired
    private RuServiceHelper<DbTaskConfig> ruTaskServiceHelper;

    @Test
    @DirtiesContext
    public void createDeleteTutorial() {
        beginHttpSession();

        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbTutorialConfig> tutorialCrud = tutorialService.getDbTutorialCrudRootServiceHelper();
        Assert.assertEquals(0, tutorialCrud.readDbChildren().size());
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        DbTutorialConfig dbTutorialConfig = tutorialCrud.createDbChild();
        dbTutorialConfig.setTracking(true);
        dbTutorialConfig.setName("test");
        dbTutorialConfig.setOwnBaseName("ownbase");
        dbTutorialConfig.setDisableScroll(true);
        ruTutorialServiceHelper.updateDbEntity(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = ruTutorialServiceHelper.readDbChild(dbTutorialConfig.getId(), DbTutorialConfig.class);
        Assert.assertTrue(dbTutorialConfig.isTracking());
        Assert.assertEquals("test", dbTutorialConfig.getName());
        Assert.assertEquals("ownbase", dbTutorialConfig.getOwnBaseName());
        Assert.assertTrue(dbTutorialConfig.isDisableScroll());
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = ruTutorialServiceHelper.readDbChild(dbTutorialConfig.getId(), DbTutorialConfig.class);
        tutorialService.getDbTutorialCrudRootServiceHelper().deleteDbChild(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, tutorialCrud.readDbChildren().size());
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void createDeleteTask() throws Exception {
        configureSimplePlanetNoResources();

        // Create tutorial
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbTutorialConfig> tutorialCrud = tutorialService.getDbTutorialCrudRootServiceHelper();
        DbTutorialConfig dbTutorialConfig = tutorialCrud.createDbChild();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Create task 1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId());
        DbTaskConfig dbTaskConfig1 = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().createDbChild();
        dbTaskConfig1.setName("name1");
        dbTaskConfig1.setScroll(new Index(1, 2));
        dbTaskConfig1.setHouseCount(5);
        dbTaskConfig1.setTip(GameTipConfig.Tip.BUILD);
        dbTaskConfig1.setTipActor(serverItemTypeService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
        dbTaskConfig1.setTipResource(serverItemTypeService.getDbResourceItemType(TEST_RESOURCE_ITEM_ID));
        dbTaskConfig1.setTipTerrainPositionHint(new Index(111, 222));
        dbTaskConfig1.setTipToBeBuilt(serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        ruTutorialServiceHelper.updateDbEntity(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify task 1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = ruTutorialServiceHelper.readDbChild(dbTutorialConfig.getId(), DbTutorialConfig.class);
        Assert.assertEquals(1, dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChildren().size());
        dbTaskConfig1 = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig1.getId());
        Assert.assertEquals("name1", dbTaskConfig1.getName());
        Assert.assertEquals(new Index(1, 2), dbTaskConfig1.getScroll());
        Assert.assertEquals(5, dbTaskConfig1.getHouseCount());
        Assert.assertEquals(GameTipConfig.Tip.BUILD, dbTaskConfig1.getTip());
        Assert.assertEquals(TEST_FACTORY_ITEM_ID, (int) dbTaskConfig1.getTipActor().getId());
        Assert.assertEquals(TEST_RESOURCE_ITEM_ID, (int) dbTaskConfig1.getTipResource().getId());
        Assert.assertEquals(new Index(111, 222), dbTaskConfig1.getTipTerrainPositionHint());
        Assert.assertEquals(TEST_ATTACK_ITEM_ID, (int) dbTaskConfig1.getTipToBeBuilt().getId());
        Assert.assertFalse(dbTaskConfig1.isClearGame());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Create task 2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = ruTutorialServiceHelper.readDbChild(dbTutorialConfig.getId(), DbTutorialConfig.class);
        DbTaskConfig dbTaskConfig2 = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().createDbChild();
        dbTaskConfig2.setName("name2");
        dbTaskConfig2.setClearGame(true);
        ruTutorialServiceHelper.updateDbEntity(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify task 1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = ruTutorialServiceHelper.readDbChild(dbTutorialConfig.getId(), DbTutorialConfig.class);
        Assert.assertEquals(2, dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChildren().size());
        dbTaskConfig1 = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig1.getId());
        Assert.assertEquals("name1", dbTaskConfig1.getName());
        Assert.assertEquals(new Index(1, 2), dbTaskConfig1.getScroll());
        Assert.assertEquals(5, dbTaskConfig1.getHouseCount());
        Assert.assertEquals(GameTipConfig.Tip.BUILD, dbTaskConfig1.getTip());
        Assert.assertEquals(TEST_FACTORY_ITEM_ID, (int) dbTaskConfig1.getTipActor().getId());
        Assert.assertEquals(TEST_RESOURCE_ITEM_ID, (int) dbTaskConfig1.getTipResource().getId());
        Assert.assertEquals(new Index(111, 222), dbTaskConfig1.getTipTerrainPositionHint());
        Assert.assertEquals(TEST_ATTACK_ITEM_ID, (int) dbTaskConfig1.getTipToBeBuilt().getId());
        Assert.assertFalse(dbTaskConfig1.isClearGame());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify task 2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = ruTutorialServiceHelper.readDbChild(dbTutorialConfig.getId(), DbTutorialConfig.class);
        Assert.assertEquals(2, dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChildren().size());
        dbTaskConfig2 = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig2.getId());
        Assert.assertEquals("name2", dbTaskConfig2.getName());
        Assert.assertTrue(dbTaskConfig2.isClearGame());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Delete task
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = ruTutorialServiceHelper.readDbChild(dbTutorialConfig.getId(), DbTutorialConfig.class);
        dbTaskConfig2 = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig2.getId());
        dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().deleteDbChild(dbTaskConfig2);
        ruTutorialServiceHelper.updateDbEntity(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify task 1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = ruTutorialServiceHelper.readDbChild(dbTutorialConfig.getId(), DbTutorialConfig.class);
        Assert.assertEquals(1, dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChildren().size());
        dbTaskConfig1 = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig1.getId());
        Assert.assertEquals("name1", dbTaskConfig1.getName());
        Assert.assertEquals(new Index(1, 2), dbTaskConfig1.getScroll());
        Assert.assertEquals(5, dbTaskConfig1.getHouseCount());
        Assert.assertEquals(GameTipConfig.Tip.BUILD, dbTaskConfig1.getTip());
        Assert.assertEquals(TEST_FACTORY_ITEM_ID, (int) dbTaskConfig1.getTipActor().getId());
        Assert.assertEquals(TEST_RESOURCE_ITEM_ID, (int) dbTaskConfig1.getTipResource().getId());
        Assert.assertEquals(new Index(111, 222), dbTaskConfig1.getTipTerrainPositionHint());
        Assert.assertEquals(TEST_ATTACK_ITEM_ID, (int) dbTaskConfig1.getTipToBeBuilt().getId());
        Assert.assertFalse(dbTaskConfig1.isClearGame());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void createTutorialConfig() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbTutorialConfig> tutorialCrud = tutorialService.getDbTutorialCrudRootServiceHelper();
        DbTutorialConfig dbTutorialConfig = tutorialCrud.createDbChild();
        dbTutorialConfig.setTracking(true);
        dbTutorialConfig.setShowTip(true);
        dbTutorialConfig.setOwnBaseName("ownbase");
        CrudChildServiceHelper<DbTaskConfig> crudChildServiceHelper = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper();
        DbTaskConfig dbTaskConfig = crudChildServiceHelper.createDbChild();
        ruTutorialServiceHelper.updateDbEntity(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = ruTutorialServiceHelper.readDbChild(dbTutorialConfig.getId(), DbTutorialConfig.class);
        dbTaskConfig = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig.getId());
        dbTaskConfig.getI18nTitle().putString("i18n title");
        dbTaskConfig.setScroll(new Index(1, 2));
        dbTaskConfig.setHouseCount(5);
        dbTaskConfig.setMoney(100);
        dbTaskConfig.setMaxMoney(10000);
        dbTaskConfig.setRadarMode(RadarMode.MAP);
        dbTaskConfig.setTip(GameTipConfig.Tip.FABRICATE);
        dbTaskConfig.setTipActor(serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        dbTaskConfig.setTipResource(serverItemTypeService.getDbResourceItemType(TEST_RESOURCE_ITEM_ID));
        dbTaskConfig.setTipTerrainPositionHint(new Index(321, 987));
        dbTaskConfig.setTipToBeBuilt(serverItemTypeService.getDbBaseItemType(TEST_CONTAINER_ITEM_ID));
        dbTaskConfig.setTipShowWatchQuestVisualisationCockpit(true);
        dbTaskConfig.setClearGame(true);
        ruTutorialServiceHelper.updateDbEntity(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = ruTutorialServiceHelper.readDbChild(dbTutorialConfig.getId(), DbTutorialConfig.class);
        TutorialConfig tutorialConfig = dbTutorialConfig.getTutorialConfig(serverItemTypeService, Locale.ENGLISH);
        Assert.assertEquals("ownbase", tutorialConfig.getOwnBaseName());
        Assert.assertTrue(tutorialConfig.isEventTracking());
        Assert.assertTrue(tutorialConfig.isShowTip());
        Assert.assertFalse(tutorialConfig.isDisableScroll());
        List<AbstractTaskConfig> abstractTaskConfigs = tutorialConfig.getTasks();
        Assert.assertEquals(1, abstractTaskConfigs.size());
        AbstractTaskConfig abstractTaskConfig = abstractTaskConfigs.get(0);
        Assert.assertEquals(100, abstractTaskConfig.getMoney());
        Assert.assertEquals("i18n title", abstractTaskConfig.getName());
        PlanetInfo planetInfo = abstractTaskConfig.createPlanetInfo();
        Assert.assertEquals(5, planetInfo.getHouseSpace());
        Assert.assertEquals(10000, planetInfo.getMaxMoney());
        Assert.assertEquals(RadarMode.MAP, planetInfo.getRadarMode());
        GameTipConfig gameTipConfig = abstractTaskConfig.getGameTipConfig();
        Assert.assertEquals(TEST_ATTACK_ITEM_ID, gameTipConfig.getActor());
        Assert.assertEquals(TEST_RESOURCE_ITEM_ID, gameTipConfig.getResourceId());
        Assert.assertEquals(TEST_CONTAINER_ITEM_ID, gameTipConfig.getToBeBuiltId());
        Assert.assertEquals(new Index(321, 987), gameTipConfig.getTerrainPositionHint());
        Assert.assertEquals(GameTipConfig.Tip.FABRICATE, gameTipConfig.getTip());
        Assert.assertTrue(gameTipConfig.isHighlightQuestVisualisationCockpit());
        Assert.assertTrue(abstractTaskConfig.isClearGame());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}