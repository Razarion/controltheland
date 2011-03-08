package com.btxtech.game.services.tutorial;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.services.BaseTestService;
import com.btxtech.game.services.common.CrudServiceHelper;
import junit.framework.Assert;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * User: beat
 * Date: March 08, 2011
 * Time: 12:00:44 PM
 */
public class TestTutorialService extends BaseTestService {
    @Autowired
    private TutorialService tutorialService;

    @Test
    @DirtiesContext
    public void simpleCreate() {
        beforeOpenSessionInViewFilter();
        CrudServiceHelper<DbTutorialConfig> tutorialCrud = tutorialService.getDbTutorialCrudServiceHelper();
        Assert.assertEquals(0, tutorialCrud.readDbChildren().size());
        afterOpenSessionInViewFilter();

        beforeOpenSessionInViewFilter();
        tutorialCrud.createDbChild();
        Assert.assertEquals(1, tutorialCrud.readDbChildren().size());
        afterOpenSessionInViewFilter();
    }

    @Test
    @DirtiesContext
    public void simpleCreateModify() {
        beforeOpenSessionInViewFilter();
        CrudServiceHelper<DbTutorialConfig> tutorialCrud = tutorialService.getDbTutorialCrudServiceHelper();
        Assert.assertEquals(0, tutorialCrud.readDbChildren().size());
        afterOpenSessionInViewFilter();

        beforeOpenSessionInViewFilter();
        tutorialCrud.createDbChild();
        Assert.assertEquals(1, tutorialCrud.readDbChildren().size());
        afterOpenSessionInViewFilter();

        beforeOpenSessionInViewFilter();
        DbTutorialConfig dbTutorialConfig = tutorialCrud.readDbChildren().iterator().next();
        dbTutorialConfig.setEnemyBaseColor("EnemyBaseColor");
        dbTutorialConfig.setEnemyBaseName("EnemyBaseName");
        dbTutorialConfig.setEnemyBaseId(11);
        dbTutorialConfig.setFailOnMoneyBelowAndNoAttackUnits(100);
        dbTutorialConfig.setHeight(100);
        dbTutorialConfig.setWidth(200);
        dbTutorialConfig.setName("Name");
        dbTutorialConfig.setFailOnOwnItemsLost(true);
        dbTutorialConfig.setOwnBaseColor("OwnBaseColor");
        dbTutorialConfig.setOwnBaseId(12);
        dbTutorialConfig.setOwnBaseName("OwnBaseName");
        dbTutorialConfig.setTracking(false);
        tutorialCrud.updateDbChild(dbTutorialConfig);
        afterOpenSessionInViewFilter();

        beforeOpenSessionInViewFilter();
        dbTutorialConfig = tutorialCrud.readDbChildren().iterator().next();
        Assert.assertEquals("EnemyBaseColor", dbTutorialConfig.getEnemyBaseColor());
        Assert.assertEquals("EnemyBaseColor", dbTutorialConfig.getEnemyBaseColor());
        Assert.assertEquals("EnemyBaseName", dbTutorialConfig.getEnemyBaseName());
        Assert.assertEquals(11, dbTutorialConfig.getEnemyBaseId());
        Assert.assertEquals((Integer) 100, dbTutorialConfig.isFailOnMoneyBelowAndNoAttackUnits());
        Assert.assertEquals(100, dbTutorialConfig.getHeight());
        Assert.assertEquals(200, dbTutorialConfig.getWidth());
        Assert.assertEquals("EnemyBaseColor", dbTutorialConfig.getEnemyBaseColor());
        Assert.assertEquals("Name", dbTutorialConfig.getName());
        Assert.assertEquals(true, dbTutorialConfig.isFailOnOwnItemsLost());
        Assert.assertEquals("OwnBaseColor", dbTutorialConfig.getOwnBaseColor());
        Assert.assertEquals(12, dbTutorialConfig.getOwnBaseId());
        Assert.assertEquals("OwnBaseName", dbTutorialConfig.getOwnBaseName());
        Assert.assertEquals(false, dbTutorialConfig.isTracking());
        afterOpenSessionInViewFilter();
    }

    @Test
    @DirtiesContext
    public void simpleCreateTask() {
        CrudServiceHelper<DbTutorialConfig> tutorialCrud = tutorialService.getDbTutorialCrudServiceHelper();

        beforeOpenSessionInViewFilter();
        tutorialCrud.createDbChild();
        afterOpenSessionInViewFilter();

        beforeOpenSessionInViewFilter();
        DbTutorialConfig dbTutorialConfig = tutorialCrud.readDbChildren().iterator().next();
        CrudServiceHelper<DbTaskConfig> crudTask = dbTutorialConfig.getCrudServiceHelper();
        crudTask.createDbChild();
        tutorialCrud.updateDbChild(dbTutorialConfig);
        afterOpenSessionInViewFilter();

        beforeOpenSessionInViewFilter();
        dbTutorialConfig = tutorialCrud.readDbChildren().iterator().next();
        DbTaskConfig dbTaskConfig = dbTutorialConfig.getCrudServiceHelper().readDbChildren().iterator().next();
        CrudServiceHelper<DbItemTypeAndPosition> itemCrud = dbTaskConfig.getItemCrudServiceHelper();
        itemCrud.createDbChild();
        tutorialCrud.updateDbChild(dbTutorialConfig);
        afterOpenSessionInViewFilter();

        beforeOpenSessionInViewFilter();
        Assert.assertEquals(1, tutorialCrud.readDbChildren().iterator().next().getCrudServiceHelper().readDbChildren().iterator().next().getItemCrudServiceHelper().readDbChildren().size());
        afterOpenSessionInViewFilter();
    }

    @Test
    @DirtiesContext
    public void simpleEditItem() {
        CrudServiceHelper<DbTutorialConfig> tutorialCrud = tutorialService.getDbTutorialCrudServiceHelper();

        beforeOpenSessionInViewFilter();
        tutorialCrud.createDbChild();
        afterOpenSessionInViewFilter();

        beforeOpenSessionInViewFilter();
        DbTutorialConfig dbTutorialConfig = tutorialCrud.readDbChildren().iterator().next();
        CrudServiceHelper<DbTaskConfig> crudTask = dbTutorialConfig.getCrudServiceHelper();
        crudTask.createDbChild();
        tutorialCrud.updateDbChild(dbTutorialConfig);
        afterOpenSessionInViewFilter();

        beforeOpenSessionInViewFilter();
        dbTutorialConfig = tutorialCrud.readDbChildren().iterator().next();
        DbTaskConfig dbTaskConfig = tutorialCrud.readDbChildren().iterator().next().getCrudServiceHelper().readDbChildren().iterator().next();
        tutorialCrud.updateDbChild(dbTutorialConfig);
        int taskId = dbTaskConfig.getId();
        afterOpenSessionInViewFilter();

        beforeOpenSessionInViewFilter();
        dbTaskConfig = tutorialService.getDbTaskConfig(taskId);
        dbTaskConfig.getItemCrudServiceHelper().createDbChild();
        tutorialService.saveDbTaskConfig(dbTaskConfig);
        afterOpenSessionInViewFilter();

        beforeOpenSessionInViewFilter();
        dbTaskConfig = tutorialService.getDbTaskConfig(taskId);        
        DbItemTypeAndPosition dbItemTypeAndPosition = dbTaskConfig.getItemCrudServiceHelper().readDbChildren().iterator().next();
        dbItemTypeAndPosition.setAngel(250);
        dbItemTypeAndPosition.setBaseId(11);
        dbItemTypeAndPosition.setPosition(new Index(1,2));
        dbItemTypeAndPosition.setSyncItemId(12);
        tutorialService.saveDbTaskConfig(dbTaskConfig);
        afterOpenSessionInViewFilter();

        beforeOpenSessionInViewFilter();
        dbTaskConfig = tutorialService.getDbTaskConfig(taskId);
        dbItemTypeAndPosition = dbTaskConfig.getItemCrudServiceHelper().readDbChildren().iterator().next();
        Assert.assertEquals((Integer)250, dbItemTypeAndPosition.getAngel());
        Assert.assertEquals((Integer)11,dbItemTypeAndPosition.getBaseId());
        Assert.assertEquals(new Index(1,2),dbItemTypeAndPosition.getPosition());
        Assert.assertEquals(12,dbItemTypeAndPosition.getSyncItemId());
        afterOpenSessionInViewFilter();

        beforeOpenSessionInViewFilter();
        dbTaskConfig = tutorialService.getDbTaskConfig(taskId);
        dbItemTypeAndPosition = dbTaskConfig.getItemCrudServiceHelper().readDbChildren().iterator().next();
        dbItemTypeAndPosition.getPosition().setX(100);

        tutorialService.saveDbTaskConfig(dbTaskConfig);
        afterOpenSessionInViewFilter();

        beforeOpenSessionInViewFilter();
        dbTaskConfig = tutorialService.getDbTaskConfig(taskId);
        dbItemTypeAndPosition = dbTaskConfig.getItemCrudServiceHelper().readDbChildren().iterator().next();
        Assert.assertEquals(new Index(100,2),dbItemTypeAndPosition.getPosition());
        afterOpenSessionInViewFilter();
    }

}