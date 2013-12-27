package com.btxtech.game.services.common;

import com.btxtech.game.jsre.client.I18nString;
import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.services.common.db.DbI18nString;
import com.btxtech.game.services.tutorial.DbConditionTaskConfig;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.user.SecurityRoles;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.WicketAuthenticatedWebSession;
import com.btxtech.game.wicket.pages.mgmt.I18nMgmtPage;
import junit.framework.Assert;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;
import java.util.Locale;

/**
 * User: beat
 * Date: 08.01.13
 * Time: 14:30
 */
public class TestI18n extends AbstractServiceTest {
    @Autowired
    private TutorialService tutorialService;
    @Autowired
    private CmsService cmsService;
    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    public void testCurd() {
        // Create
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().createDbChild();
        DbConditionTaskConfig dbTaskConfig = (DbConditionTaskConfig)dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().createDbChild(DbConditionTaskConfig.class);
        dbTaskConfig.getI18nTitle().putString("Quest");
        dbTaskConfig.getI18nTitle().putString(Locale.GERMAN, "Aufgabe");
        tutorialService.getDbTutorialCrudRootServiceHelper().updateDbChild(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertFalse(HibernateUtil.loadAll(getSessionFactory(), DbI18nString.class).isEmpty());
        dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId());
        dbTaskConfig = (DbConditionTaskConfig)dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig.getId());
        Assert.assertEquals("Quest", dbTaskConfig.getI18nTitle().getString());
        Assert.assertEquals("Aufgabe", dbTaskConfig.getI18nTitle().getString(Locale.GERMAN));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId());
        dbTaskConfig = (DbConditionTaskConfig)dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig.getId());
        dbTaskConfig.getI18nTitle().putString("Quest1");
        dbTaskConfig.getI18nTitle().putString(Locale.GERMAN, "Aufgabe1");
        tutorialService.getDbTutorialCrudRootServiceHelper().updateDbChild(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertFalse(HibernateUtil.loadAll(getSessionFactory(), DbI18nString.class).isEmpty());
        dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId());
        dbTaskConfig = (DbConditionTaskConfig)dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig.getId());
        Assert.assertEquals("Quest1", dbTaskConfig.getI18nTitle().getString());
        Assert.assertEquals("Aufgabe1", dbTaskConfig.getI18nTitle().getString(Locale.GERMAN));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Delete
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId());
        tutorialService.getDbTutorialCrudRootServiceHelper().deleteDbChild(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertTrue(HibernateUtil.loadAll(getSessionFactory(), DbI18nString.class).isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testDefault1() {
        // Create
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().createDbChild();
        DbConditionTaskConfig dbTaskConfig = (DbConditionTaskConfig)dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().createDbChild(DbConditionTaskConfig.class);
        dbTaskConfig.getI18nTitle().putString("Quest");
        tutorialService.getDbTutorialCrudRootServiceHelper().updateDbChild(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertFalse(HibernateUtil.loadAll(getSessionFactory(), DbI18nString.class).isEmpty());
        dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId());
        dbTaskConfig = (DbConditionTaskConfig)dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig.getId());
        Assert.assertEquals("Quest", dbTaskConfig.getI18nTitle().getString(Locale.GERMAN));
        Assert.assertEquals("Quest", dbTaskConfig.getI18nTitle().getString(Locale.ENGLISH));
        Assert.assertEquals("Quest", dbTaskConfig.getI18nTitle().getString());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testDefault2() {
        // Create
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().createDbChild();
        DbConditionTaskConfig dbTaskConfig = (DbConditionTaskConfig)dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().createDbChild(DbConditionTaskConfig.class);
        dbTaskConfig.getI18nTitle().putString(Locale.ENGLISH, "Quest");
        tutorialService.getDbTutorialCrudRootServiceHelper().updateDbChild(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertFalse(HibernateUtil.loadAll(getSessionFactory(), DbI18nString.class).isEmpty());
        dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId());
        dbTaskConfig = (DbConditionTaskConfig)dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().readDbChild(dbTaskConfig.getId());
        Assert.assertEquals("Quest", dbTaskConfig.getI18nTitle().getString(Locale.GERMAN));
        Assert.assertEquals("Quest", dbTaskConfig.getI18nTitle().getString(Locale.ENGLISH));
        Assert.assertEquals("Quest", dbTaskConfig.getI18nTitle().getString());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void isEmpty() {
        DbI18nString dbI18nString1 = new DbI18nString();
        DbI18nString dbI18nString2 = new DbI18nString();
        dbI18nString2.putString("");
        DbI18nString dbI18nString3 = new DbI18nString();
        dbI18nString3.putString("xxx");
        dbI18nString3.putString(Locale.ENGLISH, "xxx");
        DbI18nString dbI18nString4 = new DbI18nString();
        dbI18nString4.putString(Locale.ENGLISH, "");
        DbI18nString dbI18nString5 = new DbI18nString();
        dbI18nString5.putString(Locale.ENGLISH, "xxx");

        Assert.assertTrue(dbI18nString1.isEmpty());
        Assert.assertTrue(dbI18nString2.isEmpty());
        Assert.assertFalse(dbI18nString3.isEmpty());
        Assert.assertTrue(dbI18nString4.isEmpty());
        Assert.assertFalse(dbI18nString5.isEmpty());
    }

    @Test
    @DirtiesContext
    public void testI18nMgmtPage() throws Exception {
        configureSimplePlanetNoResources();

        // Create
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbI18nString dbI18nString1 = new DbI18nString();
        dbI18nString1.putString("xxxxx1");
        saveOrUpdateInTransaction(dbI18nString1);
        DbI18nString dbI18nString2 = new DbI18nString();
        dbI18nString2.putString("xxxxx2");
        saveOrUpdateInTransaction(dbI18nString2);
        DbI18nString dbI18nString3 = new DbI18nString();
        dbI18nString3.putString("xxxxx3");
        dbI18nString3.putString(Locale.GERMAN, "yyyyy3");
        saveOrUpdateInTransaction(dbI18nString3);
        DbI18nString dbI18nString4 = new DbI18nString();
        saveOrUpdateInTransaction(dbI18nString4);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Satisfy wicket application
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage1 = pageCrud.createDbChild();
        dbPage1.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage1.setName("Home");
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create user with sufficient rights
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("Admin", "admin");
        User user = userService.getUser();
        user.setRoles(Collections.singleton(SecurityRoles.ROLE_ADMINISTRATOR));
        userService.save(user);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("Admin", "admin");
        try {
            // First call crashes. Wickets need to set up wicket-session first
            getWicketTester().startPage(I18nMgmtPage.class);
        } catch (Exception e) {
            // Ignore
        }
        ((WicketAuthenticatedWebSession) AuthenticatedWebSession.get()).setSignIn(true);
        getWicketTester().startPage(I18nMgmtPage.class);
        getWicketTester().assertRenderedPage(I18nMgmtPage.class);

        assertTextArea(getWicketTester(), "form:table:1:default", "xxxxx1");
        assertTextAreaNull(getWicketTester(), "form:table:1:german");
        assertTextArea(getWicketTester(), "form:table:2:default", "xxxxx2");
        assertTextAreaNull(getWicketTester(), "form:table:2:german");
        assertTextArea(getWicketTester(), "form:table:3:default", "xxxxx3");
        assertTextArea(getWicketTester(), "form:table:3:german", "yyyyy3");

        FormTester formTester = getWicketTester().newFormTester("form");
        formTester.setValue("table:1:german", "yyyy1");
        formTester.setValue("table:2:default", "xxxxx2---");
        formTester.setValue("table:2:german", "yyyy2");
        formTester.submit("save");
        getWicketTester().debugComponentTrees();

        assertTextArea(getWicketTester(), "form:table:4:default", "xxxxx1");
        assertTextArea(getWicketTester(), "form:table:4:german", "yyyy1");
        assertTextArea(getWicketTester(), "form:table:5:default", "xxxxx2---");
        assertTextArea(getWicketTester(), "form:table:5:german", "yyyy2");
        assertTextArea(getWicketTester(), "form:table:6:default", "xxxxx3");
        assertTextArea(getWicketTester(), "form:table:6:german", "yyyyy3");

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void assertTextArea(WicketTester tester, String path, String value) {
        TextArea textArea = (TextArea) tester.getComponentFromLastRenderedPage(path);
        Assert.assertEquals(value, textArea.getModelObject());
    }

    private void assertTextAreaNull(WicketTester tester, String path) {
        TextArea textArea = (TextArea) tester.getComponentFromLastRenderedPage(path);
        Assert.assertNull(textArea.getModelObject());
    }

    @Test
    @DirtiesContext
    public void testCreateI18nString() throws Exception {
        // Create
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbI18nString dbI18nString1 = new DbI18nString();
        saveOrUpdateInTransaction(dbI18nString1);
        DbI18nString dbI18nString2 = new DbI18nString();
        dbI18nString2.putString("xxxxx2");
        saveOrUpdateInTransaction(dbI18nString2);
        DbI18nString dbI18nString3 = new DbI18nString();
        dbI18nString3.putString("xxxxx3");
        dbI18nString3.putString(Locale.GERMAN, "yyyyy3");
        saveOrUpdateInTransaction(dbI18nString3);
        DbI18nString dbI18nString4 = new DbI18nString();
        saveOrUpdateInTransaction(dbI18nString4);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        I18nString i18nString1 = HibernateUtil.get(getSessionFactory(), DbI18nString.class, dbI18nString1.getId()).createI18nString();
        I18nString i18nString2 = HibernateUtil.get(getSessionFactory(), DbI18nString.class, dbI18nString2.getId()).createI18nString();
        I18nString i18nString3 = HibernateUtil.get(getSessionFactory(), DbI18nString.class, dbI18nString3.getId()).createI18nString();

        Assert.assertEquals(null, i18nString1.getString());
        Assert.assertEquals(null, i18nString1.getString(I18nString.Language.DEFAULT));
        Assert.assertEquals(null, i18nString1.getString(I18nString.Language.DE));

        Assert.assertEquals("xxxxx2", i18nString2.getString());
        Assert.assertEquals("xxxxx2", i18nString2.getString(I18nString.Language.DEFAULT));
        Assert.assertEquals("xxxxx2", i18nString2.getString(I18nString.Language.DE));

        Assert.assertEquals("xxxxx3", i18nString3.getString());
        Assert.assertEquals("xxxxx3", i18nString3.getString(I18nString.Language.DEFAULT));
        Assert.assertEquals("yyyyy3", i18nString3.getString(I18nString.Language.DE));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }
}
