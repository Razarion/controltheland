package com.btxtech.game.services.tutorial;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.services.BaseTestService;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: March 08, 2011
 * Time: 12:00:44 PM
 */
public class TestTutorialService extends BaseTestService {
    @Autowired
    private TutorialService tutorialService;
    @Autowired
    private ItemService itemService;
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
        tutorialCrud.createDbChild();
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, tutorialCrud.readDbChildren().size());
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChildren().iterator().next();
        dbTutorialConfig.setTracking(true);
        dbTutorialConfig.setEnemyBaseColor("red");
        dbTutorialConfig.setEnemyBaseName("enemy");
        dbTutorialConfig.setFailOnMoneyBelowAndNoAttackUnits(100);
        dbTutorialConfig.setFailOnOwnItemsLost(true);
        dbTutorialConfig.setHeight(100);
        dbTutorialConfig.setName("test");
        dbTutorialConfig.setOwnBaseColor("green");
        dbTutorialConfig.setOwnBaseId(1);
        dbTutorialConfig.setOwnBaseName("ownbase");
        dbTutorialConfig.setWidth(200);
        ruTutorialServiceHelper.updateDbEntity(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = ruTutorialServiceHelper.readDbChild(dbTutorialConfig.getId(), DbTutorialConfig.class);
        Assert.assertTrue(dbTutorialConfig.isTracking());
        Assert.assertEquals("red", dbTutorialConfig.getEnemyBaseColor());
        Assert.assertEquals("enemy", dbTutorialConfig.getEnemyBaseName());
        Assert.assertEquals((Integer) 100, dbTutorialConfig.getFailOnMoneyBelowAndNoAttackUnits());
        Assert.assertTrue(dbTutorialConfig.isFailOnOwnItemsLost());
        Assert.assertEquals(100, dbTutorialConfig.getHeight());
        Assert.assertEquals("test", dbTutorialConfig.getName());
        Assert.assertEquals("green", dbTutorialConfig.getOwnBaseColor());
        Assert.assertEquals(1, dbTutorialConfig.getOwnBaseId());
        Assert.assertEquals("ownbase", dbTutorialConfig.getOwnBaseName());
        Assert.assertEquals(200, dbTutorialConfig.getWidth());
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChildren().iterator().next();
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
        tutorialCrud.createDbChild();
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChildren().iterator().next();
        CrudChildServiceHelper crudChildServiceHelper = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper();
        crudChildServiceHelper.createDbChild();
        ruTutorialServiceHelper.updateDbEntity(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChildren().iterator().next();
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = ruTutorialServiceHelper.readDbChild(dbTutorialConfig.getId(), DbTutorialConfig.class);
        DbTaskConfig dbTaskConfig = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChildren().iterator().next();
        dbTaskConfig.setFinishedImageContentType("finish");
        dbTaskConfig.setAccountBalance(100);
        dbTaskConfig.setName("name1");
        dbTaskConfig.setClearGame(true);
        dbTaskConfig.setScroll(new Index(1, 2));
        dbTaskConfig.setOptionAllowed(true);
        dbTaskConfig.setScrollingAllowed(true);
        dbTaskConfig.setSellingAllowed(true);
        dbTaskConfig.setFinishImageDuration(500);
        dbTaskConfig.setFinishImageData(new byte[]{1, 2, 3, 4});
        dbTaskConfig.setHouseCount(5);
        dbTaskConfig.setTaskText("task text");
        ruTutorialServiceHelper.updateDbEntity(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = ruTutorialServiceHelper.readDbChild(dbTutorialConfig.getId(), DbTutorialConfig.class);
        dbTaskConfig = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChildren().iterator().next();
        dbTaskConfig.setFinishedImageContentType("finish");
        dbTaskConfig.setAccountBalance(100);
        dbTaskConfig.setName("name1");
        dbTaskConfig.setClearGame(true);
        dbTaskConfig.setScroll(new Index(1, 2));
        dbTaskConfig.setOptionAllowed(true);
        dbTaskConfig.setScrollingAllowed(true);
        dbTaskConfig.setSellingAllowed(true);
        dbTaskConfig.setFinishImageDuration(500);
        dbTaskConfig.setFinishImageData(new byte[]{1, 2, 3, 4});
        dbTaskConfig.setHouseCount(5);
        dbTaskConfig.setTaskText("task text");
        ruTaskServiceHelper.updateDbEntity(dbTaskConfig);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = ruTutorialServiceHelper.readDbChild(dbTutorialConfig.getId(), DbTutorialConfig.class);
        dbTaskConfig = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChildren().iterator().next();
        Assert.assertEquals("finish", dbTaskConfig.getFinishedImageContentType());
        Assert.assertEquals(100, dbTaskConfig.getAccountBalance());
        Assert.assertEquals("name1", dbTaskConfig.getName());
        Assert.assertTrue(dbTaskConfig.isClearGame());
        Assert.assertEquals(new Index(1, 2), dbTaskConfig.getScroll());
        Assert.assertTrue(dbTaskConfig.isOptionAllowed());
        Assert.assertTrue(dbTaskConfig.isScrollingAllowed());
        Assert.assertTrue(dbTaskConfig.isSellingAllowed());
        Assert.assertEquals(500, dbTaskConfig.getFinishImageDuration());
        Assert.assertArrayEquals(new byte[]{1, 2, 3, 4}, dbTaskConfig.getFinishImageData());
        Assert.assertEquals(5, dbTaskConfig.getHouseCount());
        Assert.assertEquals("task text", dbTaskConfig.getTaskText());
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();
    }
}