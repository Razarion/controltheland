package com.btxtech.game.services.tutorial;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.item.ItemService;
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
    private ItemService itemService;
    @Autowired
    private RuServiceHelper<DbTutorialConfig> ruTutorialServiceHelper;
    @Autowired
    private RuServiceHelper<DbTaskConfig> ruTaskServiceHelper;
    @Autowired
    private RuServiceHelper<DbStepConfig> ruStepServiceHelper;


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
        dbTutorialConfig.setEnemyBaseName("enemy");
        dbTutorialConfig.setFailOnMoneyBelowAndNoAttackUnits(100);
        dbTutorialConfig.setFailOnOwnItemsLost(true);
        dbTutorialConfig.setHeight(100);
        dbTutorialConfig.setName("test");
        dbTutorialConfig.setOwnBaseId(1);
        dbTutorialConfig.setOwnBaseName("ownbase");
        dbTutorialConfig.setWidth(200);
        ruTutorialServiceHelper.updateDbEntity(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = ruTutorialServiceHelper.readDbChild(dbTutorialConfig.getId(), DbTutorialConfig.class);
        Assert.assertTrue(dbTutorialConfig.isTracking());
        Assert.assertEquals("enemy", dbTutorialConfig.getEnemyBaseName());
        Assert.assertEquals((Integer) 100, dbTutorialConfig.getFailOnMoneyBelowAndNoAttackUnits());
        Assert.assertTrue(dbTutorialConfig.isFailOnOwnItemsLost());
        Assert.assertEquals(100, dbTutorialConfig.getHeight());
        Assert.assertEquals("test", dbTutorialConfig.getName());
        Assert.assertEquals(1, dbTutorialConfig.getOwnBaseId());
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
    public void createDeleteTask() {
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
        dbTaskConfig.setFinishedImageContentType("finish");
        dbTaskConfig.setAccountBalance(100);
        dbTaskConfig.setName("name1");
        dbTaskConfig.setClearGame(true);
        dbTaskConfig.setScroll(new Index(1, 2));
        dbTaskConfig.setFinishImageDuration(500);
        dbTaskConfig.setFinishImageData(new byte[]{1, 2, 3, 4});
        dbTaskConfig.setHouseCount(5);
        dbTaskConfig.setTaskText("task text");
        ruTutorialServiceHelper.updateDbEntity(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = ruTutorialServiceHelper.readDbChild(dbTutorialConfig.getId(), DbTutorialConfig.class);
        dbTaskConfig = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig.getId());
        dbTaskConfig.setFinishedImageContentType("finish");
        dbTaskConfig.setAccountBalance(100);
        dbTaskConfig.setName("name1");
        dbTaskConfig.setClearGame(true);
        dbTaskConfig.setScroll(new Index(1, 2));
        dbTaskConfig.setFinishImageDuration(500);
        dbTaskConfig.setFinishImageData(new byte[]{1, 2, 3, 4});
        dbTaskConfig.setHouseCount(5);
        dbTaskConfig.setTaskText("task text");
        ruTaskServiceHelper.updateDbEntity(dbTaskConfig);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = ruTutorialServiceHelper.readDbChild(dbTutorialConfig.getId(), DbTutorialConfig.class);
        dbTaskConfig = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig.getId());
        Assert.assertEquals("finish", dbTaskConfig.getFinishedImageContentType());
        Assert.assertEquals(100, dbTaskConfig.getAccountBalance());
        Assert.assertEquals("name1", dbTaskConfig.getName());
        Assert.assertTrue(dbTaskConfig.isClearGame());
        Assert.assertEquals(new Index(1, 2), dbTaskConfig.getScroll());
        Assert.assertEquals(500, dbTaskConfig.getFinishImageDuration());
        Assert.assertArrayEquals(new byte[]{1, 2, 3, 4}, dbTaskConfig.getFinishImageData());
        Assert.assertEquals(5, dbTaskConfig.getHouseCount());
        Assert.assertEquals("task text", dbTaskConfig.getTaskText());
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void createStep() {
        beginHttpSession();

        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbTutorialConfig> tutorialCrud = tutorialService.getDbTutorialCrudRootServiceHelper();
        DbTutorialConfig dbTutorialConfig = tutorialCrud.createDbChild();
        DbTaskConfig dbTaskConfig = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().createDbChild();
        DbStepConfig dbStepConfig = dbTaskConfig.getStepConfigCrudServiceHelper().createDbChild();
        ruTutorialServiceHelper.updateDbEntity(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        ruStepServiceHelper.readDbChild(dbStepConfig.getId(), DbStepConfig.class);
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();
    }

}