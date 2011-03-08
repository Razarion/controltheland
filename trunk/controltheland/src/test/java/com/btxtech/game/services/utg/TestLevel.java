package com.btxtech.game.services.utg;

import com.btxtech.game.services.BaseTestService;
import com.btxtech.game.services.common.CrudServiceHelper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;
import java.util.List;

/**
 * User: beat
 * Date: 07.03.2011
 * Time: 17:15:43
 */
public class TestLevel extends BaseTestService {
    @Autowired
    private UserGuidanceService userGuidanceService;

    @Test
    @DirtiesContext
    public void simple() {
        CrudServiceHelper<DbAbstractLevel> crudServiceHelper = userGuidanceService.getDbLevelCrudServiceHelper();
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbSimulationLevel.class);
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbRealGameLevel.class);

        Assert.assertEquals(2, crudServiceHelper.readDbChildren().size());
    }

    @Test
    @DirtiesContext
    public void simpleMoveLevels() {
        CrudServiceHelper<DbAbstractLevel> crudServiceHelper = userGuidanceService.getDbLevelCrudServiceHelper();
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbSimulationLevel.class);
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbRealGameLevel.class);
        Assert.assertEquals(2, crudServiceHelper.readDbChildren().size());

        List<DbAbstractLevel> dbAbstractLevels = (List<DbAbstractLevel>) crudServiceHelper.readDbChildren();
        Assert.assertTrue(dbAbstractLevels.get(0) instanceof DbSimulationLevel);
        Assert.assertTrue(dbAbstractLevels.get(1) instanceof DbRealGameLevel);
        Collections.swap(dbAbstractLevels, 0, 1);
        crudServiceHelper.updateDbChildren(dbAbstractLevels);
        
        dbAbstractLevels = (List<DbAbstractLevel>) crudServiceHelper.readDbChildren();
        Assert.assertTrue(dbAbstractLevels.get(0) instanceof DbRealGameLevel);
        Assert.assertTrue(dbAbstractLevels.get(1) instanceof DbSimulationLevel);

    }

    @Test
    @DirtiesContext
    public void simpleMoveLevels2() {
        CrudServiceHelper<DbAbstractLevel> crudServiceHelper = userGuidanceService.getDbLevelCrudServiceHelper();
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbSimulationLevel.class);
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbRealGameLevel.class);
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbSimulationLevel.class);
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbRealGameLevel.class);
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbSimulationLevel.class);
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbRealGameLevel.class);
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbSimulationLevel.class);
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbRealGameLevel.class);
        Assert.assertEquals(8, crudServiceHelper.readDbChildren().size());

        List<DbAbstractLevel> dbAbstractLevels = (List<DbAbstractLevel>) crudServiceHelper.readDbChildren();
        Assert.assertTrue(dbAbstractLevels.get(0) instanceof DbSimulationLevel);
        Assert.assertTrue(dbAbstractLevels.get(1) instanceof DbRealGameLevel);
        Assert.assertTrue(dbAbstractLevels.get(2) instanceof DbSimulationLevel);
        Assert.assertTrue(dbAbstractLevels.get(3) instanceof DbRealGameLevel);
        Assert.assertTrue(dbAbstractLevels.get(4) instanceof DbSimulationLevel);
        Assert.assertTrue(dbAbstractLevels.get(5) instanceof DbRealGameLevel);
        Assert.assertTrue(dbAbstractLevels.get(6) instanceof DbSimulationLevel);
        Assert.assertTrue(dbAbstractLevels.get(7) instanceof DbRealGameLevel);
        Collections.swap(dbAbstractLevels, 3, 4);
        crudServiceHelper.updateDbChildren(dbAbstractLevels);

        dbAbstractLevels = (List<DbAbstractLevel>) crudServiceHelper.readDbChildren();
        Assert.assertTrue(dbAbstractLevels.get(0) instanceof DbSimulationLevel);
        Assert.assertTrue(dbAbstractLevels.get(1) instanceof DbRealGameLevel);
        Assert.assertTrue(dbAbstractLevels.get(2) instanceof DbSimulationLevel);
        Assert.assertTrue(dbAbstractLevels.get(3) instanceof DbSimulationLevel);
        Assert.assertTrue(dbAbstractLevels.get(4) instanceof DbRealGameLevel);
        Assert.assertTrue(dbAbstractLevels.get(5) instanceof DbRealGameLevel);
        Assert.assertTrue(dbAbstractLevels.get(6) instanceof DbSimulationLevel);
        Assert.assertTrue(dbAbstractLevels.get(7) instanceof DbRealGameLevel);
    }
}
