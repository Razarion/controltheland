package com.btxtech.game.services.bot;

import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.tutorial.DbConditionTaskConfig;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 14.09.12
 * Time: 13:27
 */
public class TestBotInMissionAndPlanetCrud extends AbstractServiceTest {
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private TutorialService tutorialService;

    @Test
    @DirtiesContext
    public void testCrud() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().createDbChild();
        DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().createDbChild();
        DbConditionTaskConfig dbTaskConfig = (DbConditionTaskConfig) dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().createDbChild(DbConditionTaskConfig.class);
        tutorialService.getDbTutorialCrudRootServiceHelper().updateDbChild(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId());
        dbTaskConfig = (DbConditionTaskConfig) tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId()).getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig.getId());
        Assert.assertTrue(dbPlanet.getBotCrud().readDbChildren().isEmpty());
        Assert.assertTrue(dbTaskConfig.getBotCrud().readDbChildren().isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        DbBotConfig planetBot1 = dbPlanet.getBotCrud().createDbChild();
        planetBot1.setName("planetBot1");
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId());
        dbTaskConfig = (DbConditionTaskConfig) tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId()).getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig.getId());
        Assert.assertEquals(1, dbPlanet.getBotCrud().readDbChildren().size());
        Assert.assertEquals("planetBot1", dbPlanet.getBotCrud().readDbChild(planetBot1.getId()).getName());
        Assert.assertTrue(dbTaskConfig.getBotCrud().readDbChildren().isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId());
        dbTaskConfig = (DbConditionTaskConfig) tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId()).getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig.getId());
        DbBotConfig taskBot1 = dbTaskConfig.getBotCrud().createDbChild();
        taskBot1.setName("taskBot1");
        tutorialService.getDbTutorialCrudRootServiceHelper().updateDbChild(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId());
        dbTaskConfig = (DbConditionTaskConfig) tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId()).getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig.getId());
        Assert.assertEquals(1, dbPlanet.getBotCrud().readDbChildren().size());
        Assert.assertEquals("planetBot1", dbPlanet.getBotCrud().readDbChild(planetBot1.getId()).getName());
        Assert.assertEquals(1, dbTaskConfig.getBotCrud().readDbChildren().size());
        Assert.assertEquals("taskBot1", dbTaskConfig.getBotCrud().readDbChild(taskBot1.getId()).getName());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        dbPlanet.getBotCrud().deleteDbChild(dbPlanet.getBotCrud().readDbChild(planetBot1.getId()));
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId());
        dbTaskConfig = (DbConditionTaskConfig) tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId()).getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig.getId());
        Assert.assertTrue(dbPlanet.getBotCrud().readDbChildren().isEmpty());
        Assert.assertEquals(1, dbTaskConfig.getBotCrud().readDbChildren().size());
        Assert.assertEquals("taskBot1", dbTaskConfig.getBotCrud().readDbChild(taskBot1.getId()).getName());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId());
        dbTaskConfig = (DbConditionTaskConfig) tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId()).getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig.getId());
        dbTaskConfig.getBotCrud().deleteDbChild(dbTaskConfig.getBotCrud().readDbChild(taskBot1.getId()));
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId());
        dbTaskConfig = (DbConditionTaskConfig) tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId()).getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig.getId());
        Assert.assertTrue(dbPlanet.getBotCrud().readDbChildren().isEmpty());
        Assert.assertTrue(dbTaskConfig.getBotCrud().readDbChildren().isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
