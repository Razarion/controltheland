package com.btxtech.game.crudtabletest;

import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.wicket.pages.mgmt.BotTable;
import com.btxtech.game.wicket.pages.mgmt.TerrainTileEditor;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 03.01.2012
 * Time: 14:54:32
 */
public class TestCurdRootTable extends AbstractServiceTest {
    private WicketTester tester;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private BotService botService;
    @Autowired
    private TerrainService terrainService;

    @Before
    public void setUp() {
        tester = new WicketTester();
        tester.getApplication().addComponentInstantiationListener(new SpringComponentInjector(tester.getApplication(), applicationContext, true));
    }

    @Test
    @DirtiesContext
    public void testSingleTable() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(BotTable.class);
        tester.assertRenderedPage(BotTable.class);
        // Create new Bot
        tester.newFormTester("form").submit("create");
        FormTester formTester = tester.newFormTester("form");
        formTester.setValue("bots:1:name", "Bot1");
        formTester.setValue("bots:1:realGameBot", true);
        formTester.submit("save");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, botService.getDbBotConfigCrudServiceHelper().readDbChildren().size());
        DbBotConfig dbBotConfig = botService.getDbBotConfigCrudServiceHelper().readDbChildren().iterator().next();
        Assert.assertEquals(dbBotConfig.getName(), "Bot1");
        Assert.assertTrue(dbBotConfig.isRealGameBot());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(BotTable.class);
        tester.assertRenderedPage(BotTable.class);
        // Edit Bot
        formTester = tester.newFormTester("form");
        formTester.setValue("bots:1:name", "Bot2");
        formTester.setValue("bots:1:realGameBot", false);
        formTester.submit("save");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, botService.getDbBotConfigCrudServiceHelper().readDbChildren().size());
        dbBotConfig = botService.getDbBotConfigCrudServiceHelper().readDbChildren().iterator().next();
        Assert.assertEquals(dbBotConfig.getName(), "Bot2");
        Assert.assertFalse(dbBotConfig.isRealGameBot());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(BotTable.class);
        tester.assertRenderedPage(BotTable.class);
        // Delete Bot
        tester.newFormTester("form").submit("bots:1:delete");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, botService.getDbBotConfigCrudServiceHelper().readDbChildren().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testMultipleCrudTable() throws Exception {
        configureComplexGameOneRealLevel();

        System.out.println("---------------- START ----------------");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(TerrainTileEditor.class);
        tester.assertRenderedPage(TerrainTileEditor.class);
        tester.debugComponentTrees();
        // Edit table
        FormTester formTester = tester.newFormTester("tileForm");
        tester.debugComponentTrees();
        formTester.setValue("surfaceImages:1:htmlBackgroundColor", "#123456");
        System.out.println("---------------- SAVE ----------------");
        formTester.submit("updateSurfaceImages");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals("#123456", CommonJava.getFirst(terrainService.getDbSurfaceImageCrudServiceHelper().readDbChildren()).getHtmlBackgroundColor());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }

}
