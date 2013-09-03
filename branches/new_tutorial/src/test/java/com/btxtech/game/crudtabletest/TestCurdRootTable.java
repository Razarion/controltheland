package com.btxtech.game.crudtabletest;

import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.media.DbSound;
import com.btxtech.game.services.media.SoundService;
import com.btxtech.game.services.terrain.DbSurfaceImage;
import com.btxtech.game.services.terrain.TerrainImageService;
import com.btxtech.game.wicket.pages.mgmt.SoundLibrary;
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
    private SoundService soundService;
    @Autowired
    private TerrainImageService terrainImageService;


    @Before
    public void setUp() {
        tester = new WicketTester();
        tester.getApplication().getComponentInstantiationListeners().add(new SpringComponentInjector(tester.getApplication(), applicationContext, true));
    }

    @Test
    @DirtiesContext
    public void testSingleTable() throws Exception {

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(SoundLibrary.class);
        tester.assertRenderedPage(SoundLibrary.class);
        // Create new Sound
        tester.newFormTester("form").submit("createSound");
        FormTester formTester = tester.newFormTester("form");
        formTester.setValue("sounds:1:name", "Value1");
        formTester.submit("saveSounds");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, soundService.getSoundLibraryCrud().readDbChildren().size());
        DbSound dbSound = soundService.getSoundLibraryCrud().readDbChildren().iterator().next();
        Assert.assertEquals(dbSound.getName(), "Value1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(SoundLibrary.class);
        tester.assertRenderedPage(SoundLibrary.class);
        // Edit SoundLibrary
        formTester = tester.newFormTester("form");
        formTester.setValue("sounds:1:name", "Value2");
        formTester.submit("saveSounds");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, soundService.getSoundLibraryCrud().readDbChildren().size());
        dbSound = soundService.getSoundLibraryCrud().readDbChildren().iterator().next();
        Assert.assertEquals(dbSound.getName(), "Value2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(SoundLibrary.class);
        tester.assertRenderedPage(SoundLibrary.class);
        // Delete Sound
        tester.newFormTester("form").submit("sounds:1:delete");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, soundService.getSoundLibraryCrud().readDbChildren().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testMultipleCrudTable() throws Exception {
        // Setup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbSurfaceImage dbSurfaceImage = terrainImageService.getDbSurfaceImageCrudServiceHelper().createDbChild();
        dbSurfaceImage.setHtmlBackgroundColor("#000000");
        terrainImageService.getDbSurfaceImageCrudServiceHelper().updateDbChild(dbSurfaceImage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

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
        Assert.assertEquals("#123456", CommonJava.getFirst(terrainImageService.getDbSurfaceImageCrudServiceHelper().readDbChildren()).getHtmlBackgroundColor());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
