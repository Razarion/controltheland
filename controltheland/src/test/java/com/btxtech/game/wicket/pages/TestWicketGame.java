package com.btxtech.game.wicket.pages;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.cms.impl.CmsServiceImpl;
import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.services.utg.tracker.DbPageAccess;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import junit.framework.Assert;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Locale;

/**
 * User: beat
 * Date: 13.07.13
 * Time: 10:28
 */
public class TestWicketGame extends AbstractServiceTest {
    @Autowired
    private UserTrackingService userTrackingService;
    @Autowired
    private CmsServiceImpl cmsService;

    @Test
    @DirtiesContext
    public void testLocale4Game() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        testLocale("en", "Loading java script");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getSession().setLocale(Locale.GERMAN);
        testLocale("de", "Lade Java Script");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void testLocale(String expectedLocale, String startupTaskText) {
        getWicketTester().startPage(Game.class);
        getWicketTester().assertRenderedPage(Game.class);
        getWicketTester().assertComponent("metaGwtLocale", WebMarkupContainer.class);
        WebMarkupContainer container = (WebMarkupContainer) getWicketTester().getComponentFromLastRenderedPage("metaGwtLocale");
        assertAttributeModifier(container, 0, "content", "locale=" + expectedLocale);
        getWicketTester().assertLabel("startupTaskText", startupTaskText);
    }

    @Test
    @DirtiesContext
    public void testNoHtml5() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.NO_HTML5_BROWSER);
        pageCrud.updateDbChild(dbPage);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.onJavaScriptDetected(false);
        getWicketTester().startPage(Game.class);
        getWicketTester().assertRenderedPage(CmsPage.class);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testParametersStartupTextEn() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        getWicketTester().startPage(Game.class);
        getWicketTester().assertLabel("startupTaskText", "Loading java script");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testParametersStartupTextDe() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getSession().setLocale(Locale.GERMAN);
        getWicketTester().startPage(Game.class);
        getWicketTester().assertLabel("startupTaskText", "Lade Java Script");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testParametersStartupSeqRealGame() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(Game.class);
        assertAttributeModifier("startupSeq", 0, "id", "startSeq");
        assertAttributeModifier("startupSeq", 1, "startSeq", "COLD_REAL");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testParametersStartupSeqTutorial() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        PageParameters pageParameters = new PageParameters();
        pageParameters.set(com.btxtech.game.jsre.client.Game.LEVEL_TASK_ID, TEST_LEVEL_TASK_1_1_SIMULATED_ID);
        getWicketTester().startPage(Game.class, pageParameters);
        assertAttributeModifier("startupSeq", 0, "id", "startSeq");
        assertAttributeModifier("startupSeq", 1, "startSeq", "COLD_SIMULATED");
        assertAttributeModifier("startupSeq", 2, "taskId", Integer.toString(TEST_LEVEL_TASK_1_1_SIMULATED_ID));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testTrackingRealGame() throws Exception {
        configureSimplePlanetNoResources();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(Game.class);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbPageAccess> dbPageAccesses = loadAll(DbPageAccess.class);
        Assert.assertEquals(1, dbPageAccesses.size());
        Assert.assertEquals(Game.class.getName(), dbPageAccesses.get(0).getPage());
        Assert.assertNull(dbPageAccesses.get(0).getAdditional());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testTrackingTutorial() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        PageParameters pageParameters = new PageParameters();
        pageParameters.set(com.btxtech.game.jsre.client.Game.LEVEL_TASK_ID, TEST_LEVEL_TASK_1_1_SIMULATED_ID);
        getWicketTester().startPage(Game.class, pageParameters);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbPageAccess> dbPageAccesses = loadAll(DbPageAccess.class);
        Assert.assertEquals(1, dbPageAccesses.size());
        Assert.assertEquals(Game.class.getName(), dbPageAccesses.get(0).getPage());
        Assert.assertEquals("LevelTaskId=" + TEST_LEVEL_TASK_1_1_SIMULATED_ID, dbPageAccesses.get(0).getAdditional());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
