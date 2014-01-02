package com.btxtech.game.services.tutorial;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.RadarMode;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.utg.tip.GameTipConfig;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.jsre.common.tutorial.AbstractTaskConfig;
import com.btxtech.game.jsre.common.tutorial.AutomatedBattleTaskConfig;
import com.btxtech.game.jsre.common.tutorial.AutomatedScrollTaskConfig;
import com.btxtech.game.jsre.common.tutorial.ConditionTaskConfig;
import com.btxtech.game.jsre.common.tutorial.ContainedInListenerTaskConfig;
import com.btxtech.game.jsre.common.tutorial.ScrollToEventTaskConfig;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.services.utg.condition.DbCountComparisonConfig;
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
    private ServerItemTypeService itemTypeService;

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
    public void createDeleteConditionTask() throws Exception {
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
        DbConditionTaskConfig dbTaskConfig1 = (DbConditionTaskConfig) dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().createDbChild(DbConditionTaskConfig.class);
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
        dbTaskConfig1 = (DbConditionTaskConfig) dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig1.getId());
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
        DbConditionTaskConfig dbTaskConfig2 = (DbConditionTaskConfig) dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().createDbChild(DbConditionTaskConfig.class);
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
        dbTaskConfig1 = (DbConditionTaskConfig) dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig1.getId());
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
        dbTaskConfig2 = (DbConditionTaskConfig) dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig2.getId());
        Assert.assertEquals("name2", dbTaskConfig2.getName());
        Assert.assertTrue(dbTaskConfig2.isClearGame());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Delete task
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = ruTutorialServiceHelper.readDbChild(dbTutorialConfig.getId(), DbTutorialConfig.class);
        dbTaskConfig2 = (DbConditionTaskConfig) dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig2.getId());
        dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().deleteDbChild(dbTaskConfig2);
        ruTutorialServiceHelper.updateDbEntity(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify task 1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = ruTutorialServiceHelper.readDbChild(dbTutorialConfig.getId(), DbTutorialConfig.class);
        Assert.assertEquals(1, dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChildren().size());
        dbTaskConfig1 = (DbConditionTaskConfig) dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig1.getId());
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
        CrudChildServiceHelper<DbAbstractTaskConfig> crudChildServiceHelper = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper();
        // Create ConditionTaskConfig
        DbConditionTaskConfig dbConditionTaskConfig = (DbConditionTaskConfig) crudChildServiceHelper.createDbChild(DbConditionTaskConfig.class);
        dbConditionTaskConfig.getI18nTitle().putString("i18n title");
        dbConditionTaskConfig.setScroll(new Index(1, 2));
        dbConditionTaskConfig.setHouseCount(5);
        dbConditionTaskConfig.setMoney(100);
        dbConditionTaskConfig.setMaxMoney(10000);
        dbConditionTaskConfig.setRadarMode(RadarMode.MAP);
        dbConditionTaskConfig.setTip(GameTipConfig.Tip.FABRICATE);
        dbConditionTaskConfig.setTipActor(serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        dbConditionTaskConfig.setTipResource(serverItemTypeService.getDbResourceItemType(TEST_RESOURCE_ITEM_ID));
        dbConditionTaskConfig.setTipTerrainPositionHint(new Index(321, 987));
        dbConditionTaskConfig.setTipToBeBuilt(serverItemTypeService.getDbBaseItemType(TEST_CONTAINER_ITEM_ID));
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbConditionConfig.setConditionTrigger(ConditionTrigger.MONEY_INCREASED);
        DbCountComparisonConfig dbCountComparisonConfig = new DbCountComparisonConfig();
        dbCountComparisonConfig.setCount(100);
        dbConditionConfig.setDbAbstractComparisonConfig(dbCountComparisonConfig);
        dbConditionTaskConfig.setConditionConfig(dbConditionConfig);
        dbConditionTaskConfig.setClearGame(true);
        // Create DbAutomatedBattleTaskConfig
        DbAutomatedBattleTaskConfig dbAutomatedBattleTaskConfig = (DbAutomatedBattleTaskConfig) crudChildServiceHelper.createDbChild(DbAutomatedBattleTaskConfig.class);
        dbAutomatedBattleTaskConfig.getI18nTitle().putString("i18n title2");
        dbAutomatedBattleTaskConfig.setScroll(new Index(3, 4));
        dbAutomatedBattleTaskConfig.setHouseCount(15);
        dbAutomatedBattleTaskConfig.setMoney(110);
        dbAutomatedBattleTaskConfig.setMaxMoney(20000);
        dbAutomatedBattleTaskConfig.setRadarMode(RadarMode.MAP_AND_UNITS);
        dbAutomatedBattleTaskConfig.setTip(GameTipConfig.Tip.BUILD);
        dbAutomatedBattleTaskConfig.setTipActor(serverItemTypeService.getDbBaseItemType(TEST_CONTAINER_ITEM_ID));
        dbAutomatedBattleTaskConfig.setTipTarget(serverItemTypeService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        dbAutomatedBattleTaskConfig.setTipResource(serverItemTypeService.getDbResourceItemType(TEST_RESOURCE_ITEM_ID));
        dbAutomatedBattleTaskConfig.setTipTerrainPositionHint(new Index(111, 222));
        dbAutomatedBattleTaskConfig.setTipToBeBuilt(serverItemTypeService.getDbBaseItemType(TEST_CONSUMER_TYPE_ID));
        dbAutomatedBattleTaskConfig.setClearGame(false);
        dbAutomatedBattleTaskConfig.setAttackBotHealthFactor(0.5);
        dbAutomatedBattleTaskConfig.setAttackBotItemPosition(new Index(10, 11));
        dbAutomatedBattleTaskConfig.setAttackBotName("qayxsw");
        dbAutomatedBattleTaskConfig.setTargetItemType(itemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        dbAutomatedBattleTaskConfig.setAttackBotItemType(itemTypeService.getDbBaseItemType(TEST_HARVESTER_ITEM_ID));
        // Create DbAutomatedScrollTaskConfig
        DbAutomatedScrollTaskConfig dbAutomatedScrollTaskConfig = (DbAutomatedScrollTaskConfig) crudChildServiceHelper.createDbChild(DbAutomatedScrollTaskConfig.class);
        dbAutomatedScrollTaskConfig.getI18nTitle().putString("i18n title3");
        dbAutomatedScrollTaskConfig.setScroll(new Index(5, 6));
        dbAutomatedScrollTaskConfig.setHouseCount(65);
        dbAutomatedScrollTaskConfig.setMoney(909);
        dbAutomatedScrollTaskConfig.setMaxMoney(20011);
        dbAutomatedScrollTaskConfig.setRadarMode(RadarMode.DISABLED);
        dbAutomatedScrollTaskConfig.setTip(GameTipConfig.Tip.GET_RESOURCE);
        dbAutomatedScrollTaskConfig.setTipActor(serverItemTypeService.getDbBaseItemType(TEST_CONSUMER_TYPE_ID));
        dbAutomatedScrollTaskConfig.setTipResource(serverItemTypeService.getDbResourceItemType(TEST_RESOURCE_ITEM_ID));
        dbAutomatedScrollTaskConfig.setTipTerrainPositionHint(new Index(145, 278));
        dbAutomatedScrollTaskConfig.setTipToBeBuilt(serverItemTypeService.getDbBaseItemType(TEST_CONTAINER_ITEM_ID));
        dbAutomatedScrollTaskConfig.setClearGame(true);
        dbAutomatedScrollTaskConfig.setAutomatedScrollToPosition(new Index(89, 32));
        // Create DbScrollToEventTaskConfig
        DbScrollToEventTaskConfig dbScrollToEventTaskConfig = (DbScrollToEventTaskConfig) crudChildServiceHelper.createDbChild(DbScrollToEventTaskConfig.class);
        dbScrollToEventTaskConfig.getI18nStorySplashTitle().putString("I18nStorySplashTitle");
        dbScrollToEventTaskConfig.getI18nStorySplashText().putString("I18nStorySplashText");
        dbScrollToEventTaskConfig.getI18nPraiseSplashTitle().putString("I18nPraiseSplashTitle");
        dbScrollToEventTaskConfig.getI18nPraiseSplashText().putString("I18nPraiseSplashText");
        dbScrollToEventTaskConfig.getI18nTitle().putString("i18n title4");
        dbScrollToEventTaskConfig.setScroll(new Index(7, 8));
        dbScrollToEventTaskConfig.setHouseCount(99);
        dbScrollToEventTaskConfig.setMoney(345);
        dbScrollToEventTaskConfig.setMaxMoney(254);
        dbScrollToEventTaskConfig.setRadarMode(RadarMode.NONE);
        dbScrollToEventTaskConfig.setTip(GameTipConfig.Tip.SCROLL);
        dbScrollToEventTaskConfig.setTipToBeBuilt(serverItemTypeService.getDbBaseItemType(TEST_CONTAINER_ITEM_ID));
        dbScrollToEventTaskConfig.setTipTerrainPositionHint(new Index(615, 354));
        dbScrollToEventTaskConfig.setClearGame(false);
        dbScrollToEventTaskConfig.setScrollToPosition(new Rectangle(5, 9, 11, 22));
        // Create DbScrollToEventTaskConfig
        DbContainedInListenerTaskConfig dbContainedInListenerTaskConfig = (DbContainedInListenerTaskConfig) crudChildServiceHelper.createDbChild(DbContainedInListenerTaskConfig.class);
        dbContainedInListenerTaskConfig.getI18nStorySplashTitle().putString("I18nStorySplashTitle");
        dbContainedInListenerTaskConfig.getI18nStorySplashText().putString("I18nStorySplashText");
        dbContainedInListenerTaskConfig.getI18nPraiseSplashTitle().putString("I18nPraiseSplashTitle");
        dbContainedInListenerTaskConfig.getI18nPraiseSplashText().putString("I18nPraiseSplashText");
        dbContainedInListenerTaskConfig.getI18nTitle().putString("i18n title4");
        dbContainedInListenerTaskConfig.setScroll(new Index(7, 8));
        dbContainedInListenerTaskConfig.setHouseCount(99);
        dbContainedInListenerTaskConfig.setMoney(345);
        dbContainedInListenerTaskConfig.setMaxMoney(254);
        dbContainedInListenerTaskConfig.setRadarMode(RadarMode.NONE);
        dbContainedInListenerTaskConfig.setTip(GameTipConfig.Tip.SCROLL);
        dbContainedInListenerTaskConfig.setTipToBeBuilt(serverItemTypeService.getDbBaseItemType(TEST_CONTAINER_ITEM_ID));
        dbContainedInListenerTaskConfig.setTipTerrainPositionHint(new Index(615, 354));
        dbContainedInListenerTaskConfig.setClearGame(false);
        dbContainedInListenerTaskConfig.setContainedIn(true);
        dbContainedInListenerTaskConfig.setCheckContainedInOnStart(false);
        dbContainedInListenerTaskConfig.setSyncItemTypeToWatch(serverItemTypeService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        //
        ruTutorialServiceHelper.updateDbEntity(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Add bots
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId());
        dbConditionTaskConfig = (DbConditionTaskConfig) dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbConditionTaskConfig.getId());
        DbBotConfig dbBotConfig1 = dbConditionTaskConfig.getBotCrud().createDbChild();
        dbBotConfig1.setRealm(createDbRegion(new Rectangle(0, 0, 100, 100)));
        ruTutorialServiceHelper.updateDbEntity(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId());
        dbConditionTaskConfig = (DbConditionTaskConfig) dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbConditionTaskConfig.getId());
        DbBotConfig dbBotConfig2 = dbConditionTaskConfig.getBotCrud().createDbChild();
        dbBotConfig2.setRealm(createDbRegion(new Rectangle(0, 0, 100, 100)));
        ruTutorialServiceHelper.updateDbEntity(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId());
        dbConditionTaskConfig = (DbConditionTaskConfig) dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbConditionTaskConfig.getId());
        DbTaskBotToStop dbTaskBotToStop1 = dbConditionTaskConfig.getBotToStopCrud().createDbChild();
        dbTaskBotToStop1.setDbBotConfig(dbConditionTaskConfig.getBotCrud().readDbChild(dbBotConfig1.getId()));
        DbTaskBotToStop dbTaskBotToStop2 = dbConditionTaskConfig.getBotToStopCrud().createDbChild();
        dbTaskBotToStop2.setDbBotConfig(dbConditionTaskConfig.getBotCrud().readDbChild(dbBotConfig2.getId()));
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
        Assert.assertEquals(5, abstractTaskConfigs.size());
        // Verify ConditionTaskConfig
        ConditionTaskConfig conditionTaskConfig = (ConditionTaskConfig) abstractTaskConfigs.get(0);
        Assert.assertEquals(new Index(1, 2), conditionTaskConfig.getScroll());
        Assert.assertEquals(100, conditionTaskConfig.getMoney());
        Assert.assertEquals("i18n title", conditionTaskConfig.getName());
        PlanetInfo planetInfo = conditionTaskConfig.createPlanetInfo();
        Assert.assertEquals(5, planetInfo.getHouseSpace());
        Assert.assertEquals(10000, planetInfo.getMaxMoney());
        Assert.assertEquals(RadarMode.MAP, planetInfo.getRadarMode());
        GameTipConfig gameTipConfig = conditionTaskConfig.getGameTipConfig();
        Assert.assertEquals(TEST_ATTACK_ITEM_ID, gameTipConfig.getActor());
        Assert.assertEquals(TEST_RESOURCE_ITEM_ID, gameTipConfig.getResourceId());
        Assert.assertEquals(TEST_CONTAINER_ITEM_ID, gameTipConfig.getToBeBuiltId());
        Assert.assertEquals(new Index(321, 987), gameTipConfig.getTerrainPositionHint());
        Assert.assertEquals(GameTipConfig.Tip.FABRICATE, gameTipConfig.getTip());
        Assert.assertTrue(conditionTaskConfig.isClearGame());
        Assert.assertEquals(ConditionTrigger.MONEY_INCREASED, conditionTaskConfig.getConditionConfig().getConditionTrigger());
        Assert.assertEquals(2, conditionTaskConfig.getBotConfigs().size());
        Assert.assertEquals((int) dbBotConfig1.getId(), CommonJava.getNth(conditionTaskConfig.getBotConfigs(), 0).getId());
        Assert.assertEquals((int) dbBotConfig2.getId(), CommonJava.getNth(conditionTaskConfig.getBotConfigs(), 1).getId());
        Assert.assertEquals(2, conditionTaskConfig.getBotIdsToStop().size());
        Assert.assertEquals(dbBotConfig1.getId(), CommonJava.getNth(conditionTaskConfig.getBotIdsToStop(), 0));
        Assert.assertEquals(dbBotConfig2.getId(), CommonJava.getNth(conditionTaskConfig.getBotIdsToStop(), 1));
        // Verify AutomatedBattleTaskConfig
        AutomatedBattleTaskConfig automatedBattleTaskConfig = (AutomatedBattleTaskConfig) abstractTaskConfigs.get(1);
        Assert.assertEquals(new Index(3, 4), automatedBattleTaskConfig.getScroll());
        Assert.assertEquals(110, automatedBattleTaskConfig.getMoney());
        Assert.assertEquals("i18n title2", automatedBattleTaskConfig.getName());
        planetInfo = automatedBattleTaskConfig.createPlanetInfo();
        Assert.assertEquals(15, planetInfo.getHouseSpace());
        Assert.assertEquals(20000, planetInfo.getMaxMoney());
        Assert.assertEquals(RadarMode.MAP_AND_UNITS, planetInfo.getRadarMode());
        gameTipConfig = automatedBattleTaskConfig.getGameTipConfig();
        Assert.assertEquals(TEST_CONTAINER_ITEM_ID, gameTipConfig.getActor());
        Assert.assertEquals(TEST_START_BUILDER_ITEM_ID, gameTipConfig.getTarget());
        Assert.assertEquals(TEST_RESOURCE_ITEM_ID, gameTipConfig.getResourceId());
        Assert.assertEquals(TEST_CONSUMER_TYPE_ID, gameTipConfig.getToBeBuiltId());
        Assert.assertEquals(new Index(111, 222), gameTipConfig.getTerrainPositionHint());
        Assert.assertEquals(GameTipConfig.Tip.BUILD, gameTipConfig.getTip());
        Assert.assertFalse(automatedBattleTaskConfig.isClearGame());
        Assert.assertEquals(0.5, automatedBattleTaskConfig.getAttackerHealthFactor(), 0.00001);
        Assert.assertEquals(new Index(10, 11), automatedBattleTaskConfig.getBotAttacker().getPosition());
        Assert.assertEquals(TEST_HARVESTER_ITEM_ID, automatedBattleTaskConfig.getBotAttacker().getItemTypeId());
        Assert.assertEquals(0, automatedBattleTaskConfig.getBotAttacker().getAngel(), 0.00001);
        Assert.assertEquals("qayxsw", automatedBattleTaskConfig.getBotName());
        Assert.assertEquals(TEST_ATTACK_ITEM_ID, automatedBattleTaskConfig.getTargetItemType());
        // Verify AutomatedScrollTaskConfig
        AutomatedScrollTaskConfig automatedScrollTaskConfig = (AutomatedScrollTaskConfig) abstractTaskConfigs.get(2);
        Assert.assertEquals(new Index(5, 6), automatedScrollTaskConfig.getScroll());
        Assert.assertEquals(909, automatedScrollTaskConfig.getMoney());
        Assert.assertEquals("i18n title3", automatedScrollTaskConfig.getName());
        planetInfo = automatedScrollTaskConfig.createPlanetInfo();
        Assert.assertEquals(65, planetInfo.getHouseSpace());
        Assert.assertEquals(20011, planetInfo.getMaxMoney());
        Assert.assertEquals(RadarMode.DISABLED, planetInfo.getRadarMode());
        gameTipConfig = automatedScrollTaskConfig.getGameTipConfig();
        Assert.assertEquals(TEST_CONSUMER_TYPE_ID, gameTipConfig.getActor());
        Assert.assertEquals(TEST_RESOURCE_ITEM_ID, gameTipConfig.getResourceId());
        Assert.assertEquals(TEST_CONTAINER_ITEM_ID, gameTipConfig.getToBeBuiltId());
        Assert.assertEquals(new Index(145, 278), gameTipConfig.getTerrainPositionHint());
        Assert.assertEquals(GameTipConfig.Tip.GET_RESOURCE, gameTipConfig.getTip());
        Assert.assertTrue(automatedScrollTaskConfig.isClearGame());
        Assert.assertEquals(new Index(89, 32), automatedScrollTaskConfig.getScrollToPosition());
        Assert.assertNull(automatedScrollTaskConfig.getStorySplashPopupInfo());
        Assert.assertNull(automatedScrollTaskConfig.getPraiseSplashPopupInfo());
        // Verify Automated Battle Task Config
        ScrollToEventTaskConfig scrollToEventTaskConfig = (ScrollToEventTaskConfig) abstractTaskConfigs.get(3);
        Assert.assertEquals(new Index(7, 8), scrollToEventTaskConfig.getScroll());
        Assert.assertEquals(345, scrollToEventTaskConfig.getMoney());
        Assert.assertEquals("i18n title4", scrollToEventTaskConfig.getName());
        planetInfo = scrollToEventTaskConfig.createPlanetInfo();
        Assert.assertEquals(99, planetInfo.getHouseSpace());
        Assert.assertEquals(254, planetInfo.getMaxMoney());
        Assert.assertEquals(RadarMode.NONE, planetInfo.getRadarMode());
        gameTipConfig = scrollToEventTaskConfig.getGameTipConfig();
        Assert.assertEquals(new Index(615, 354), gameTipConfig.getTerrainPositionHint());
        Assert.assertEquals(GameTipConfig.Tip.SCROLL, gameTipConfig.getTip());
        Assert.assertFalse(scrollToEventTaskConfig.isClearGame());
        Assert.assertEquals(new Rectangle(5, 9, 11, 22), scrollToEventTaskConfig.getScrollTargetRectangle());
        Assert.assertEquals("I18nStorySplashTitle", scrollToEventTaskConfig.getStorySplashPopupInfo().getTitle());
        Assert.assertEquals("I18nStorySplashText", scrollToEventTaskConfig.getStorySplashPopupInfo().getStoryText());
        Assert.assertEquals("I18nPraiseSplashTitle", scrollToEventTaskConfig.getPraiseSplashPopupInfo().getTitle());
        Assert.assertEquals("I18nPraiseSplashText", scrollToEventTaskConfig.getPraiseSplashPopupInfo().getPraiseText());
        // Verify Send Command Task Config
        ContainedInListenerTaskConfig containedInListenerTaskConfig = (ContainedInListenerTaskConfig) abstractTaskConfigs.get(4);
        Assert.assertEquals(new Index(7, 8), containedInListenerTaskConfig.getScroll());
        Assert.assertEquals(345, containedInListenerTaskConfig.getMoney());
        Assert.assertEquals("i18n title4", containedInListenerTaskConfig.getName());
        planetInfo = containedInListenerTaskConfig.createPlanetInfo();
        Assert.assertEquals(99, planetInfo.getHouseSpace());
        Assert.assertEquals(254, planetInfo.getMaxMoney());
        Assert.assertEquals(RadarMode.NONE, planetInfo.getRadarMode());
        gameTipConfig = containedInListenerTaskConfig.getGameTipConfig();
        Assert.assertEquals(new Index(615, 354), gameTipConfig.getTerrainPositionHint());
        Assert.assertEquals(GameTipConfig.Tip.SCROLL, gameTipConfig.getTip());
        Assert.assertFalse(containedInListenerTaskConfig.isClearGame());
        Assert.assertTrue(containedInListenerTaskConfig.isContainedIn());
        Assert.assertFalse(containedInListenerTaskConfig.isCheckOnStart());
        Assert.assertEquals(TEST_START_BUILDER_ITEM_ID, containedInListenerTaskConfig.getSyncItemTypeToWatch());
        Assert.assertEquals("I18nStorySplashTitle", containedInListenerTaskConfig.getStorySplashPopupInfo().getTitle());
        Assert.assertEquals("I18nStorySplashText", containedInListenerTaskConfig.getStorySplashPopupInfo().getStoryText());
        Assert.assertEquals("I18nPraiseSplashTitle", containedInListenerTaskConfig.getPraiseSplashPopupInfo().getTitle());
        Assert.assertEquals("I18nPraiseSplashText", containedInListenerTaskConfig.getPraiseSplashPopupInfo().getPraiseText());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}