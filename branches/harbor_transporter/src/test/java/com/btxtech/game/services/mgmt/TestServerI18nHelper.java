package com.btxtech.game.services.mgmt;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.services.AbstractServiceTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Locale;

/**
 * User: beat
 * Date: 14.01.13
 * Time: 23:20
 */
public class TestServerI18nHelper extends AbstractServiceTest {
    @Autowired
    private ServerI18nHelper serverI18nHelper;

    @Test
    @DirtiesContext
    public void testRequest() throws Exception {
        Locale.setDefault(Locale.GERMAN);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.CHINA);
        Assert.assertEquals("Guest", serverI18nHelper.getString("guest"));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.ENGLISH);
        Assert.assertEquals("Guest", serverI18nHelper.getString("guest"));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.GERMAN);
        Assert.assertEquals("Gast", serverI18nHelper.getString("guest"));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testNoRequestUserStateChina() throws Exception {
        configureSimplePlanetNoResources();
        Locale.setDefault(Locale.GERMAN);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.CHINA);
        createAndLoginUser("U1");
        SimpleBase simpleBase = getOrCreateBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertEquals("You lost your base. A new base was created.", serverI18nHelper.getStringNoRequest(simpleBase, "baseLost", null));
    }

    @Test
    @DirtiesContext
    public void testNoRequestUserStateEnglish() throws Exception {
        configureSimplePlanetNoResources();
        Locale.setDefault(Locale.GERMAN);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.ENGLISH);
        createAndLoginUser("U1");
        SimpleBase simpleBase = getOrCreateBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertEquals("You lost your base. A new base was created.", serverI18nHelper.getStringNoRequest(simpleBase, "baseLost", null));
    }

    @Test
    @DirtiesContext
    public void testNoRequestUserStateGerman() throws Exception {
        configureSimplePlanetNoResources();
        Locale.setDefault(Locale.GERMAN);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.GERMAN);
        createAndLoginUser("U1");
        SimpleBase simpleBase = getOrCreateBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertEquals("Deine Basis wurde ausgelöscht. Eine neue Basis wurde gestartet.", serverI18nHelper.getStringNoRequest(simpleBase, "baseLost", null));
    }

    @Test
    @DirtiesContext
    public void testNoRequestUserStateBaseDoesNotExistAnymore() throws Exception {
        configureSimplePlanetNoResources();
        Locale.setDefault(Locale.GERMAN);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.ENGLISH);
        SimpleBase simpleBase = getOrCreateBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertEquals("You lost your base. A new base was created.", serverI18nHelper.getStringNoRequest(simpleBase, "baseLost", null));
    }

}
