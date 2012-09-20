package com.btxtech.game.services.tutorial;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.utg.tip.GameTipConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.item.ServerItemTypeService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

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
        dbTutorialConfig.setHeight(100);
        dbTutorialConfig.setName("test");
        dbTutorialConfig.setOwnBaseName("ownbase");
        dbTutorialConfig.setWidth(200);
        ruTutorialServiceHelper.updateDbEntity(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = ruTutorialServiceHelper.readDbChild(dbTutorialConfig.getId(), DbTutorialConfig.class);
        Assert.assertTrue(dbTutorialConfig.isTracking());
        Assert.assertEquals(100, dbTutorialConfig.getHeight());
        Assert.assertEquals("test", dbTutorialConfig.getName());
        Assert.assertEquals("ownbase", dbTutorialConfig.getOwnBaseName());
        Assert.assertEquals(200, dbTutorialConfig.getWidth());
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
        configureSimplePlanet();

        beginHttpSession();

        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbTutorialConfig> tutorialCrud = tutorialService.getDbTutorialCrudRootServiceHelper();
        DbTutorialConfig dbTutorialConfig = tutorialCrud.createDbChild();
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId());
        CrudChildServiceHelper<DbTaskConfig> crudChildServiceHelper = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper();
        DbTaskConfig dbTaskConfig = crudChildServiceHelper.createDbChild();
        ruTutorialServiceHelper.updateDbEntity(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = ruTutorialServiceHelper.readDbChild(dbTutorialConfig.getId(), DbTutorialConfig.class);
        dbTaskConfig = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig.getId());
        dbTaskConfig.setName("name1");
        dbTaskConfig.setScroll(new Index(1, 2));
        dbTaskConfig.setHouseCount(5);
        dbTaskConfig.setTip(GameTipConfig.Tip.BUILD);
        dbTaskConfig.setTipActor(serverItemTypeService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
        dbTaskConfig.setTipResource(serverItemTypeService.getDbResourceItemType(TEST_RESOURCE_ITEM_ID));
        dbTaskConfig.setTipTerrainPositionHint(new Index(111, 222));
        dbTaskConfig.setTipToBeBuilt(serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        ruTutorialServiceHelper.updateDbEntity(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = ruTutorialServiceHelper.readDbChild(dbTutorialConfig.getId(), DbTutorialConfig.class);
        dbTaskConfig = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig.getId());
        dbTaskConfig.setName("name1");
        dbTaskConfig.setScroll(new Index(1, 2));
        dbTaskConfig.setHouseCount(5);
        ruTaskServiceHelper.updateDbEntity(dbTaskConfig);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = ruTutorialServiceHelper.readDbChild(dbTutorialConfig.getId(), DbTutorialConfig.class);
        dbTaskConfig = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig.getId());
        Assert.assertEquals("name1", dbTaskConfig.getName());
        Assert.assertEquals(new Index(1, 2), dbTaskConfig.getScroll());
        Assert.assertEquals(5, dbTaskConfig.getHouseCount());

        Assert.assertEquals(GameTipConfig.Tip.BUILD, dbTaskConfig.getTip());
        Assert.assertEquals(TEST_FACTORY_ITEM_ID, (int) dbTaskConfig.getTipActor().getId());
        Assert.assertEquals(TEST_RESOURCE_ITEM_ID, (int) dbTaskConfig.getTipResource().getId());
        Assert.assertEquals(new Index(111, 222), dbTaskConfig.getTipTerrainPositionHint());
        Assert.assertEquals(TEST_ATTACK_ITEM_ID, (int) dbTaskConfig.getTipToBeBuilt().getId());
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();
    }
}