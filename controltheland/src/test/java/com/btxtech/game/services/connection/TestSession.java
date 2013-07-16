package com.btxtech.game.services.connection;

import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.PropertyService;
import com.btxtech.game.services.common.PropertyServiceEnum;
import com.btxtech.game.services.utg.DbFacebookSource;
import com.btxtech.game.services.utg.tracker.DbSessionDetail;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

/**
 * User: beat
 * Date: 25.01.13
 * Time: 17:03
 */
public class TestSession extends AbstractServiceTest {
    @Autowired
    private Session session;
    @Autowired
    private PropertyService propertyService;

    @Test
    @DirtiesContext
    public void testTrackingCookie() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setWicketParameterTrackingCookie("xxxyyybbbeee");
        Assert.assertEquals("xxxyyybbbeee", session.getTrackingCookieId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Check history generation
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbSessionDetail> dbSessionDetails = HibernateUtil.loadAll(getSessionFactory(), DbSessionDetail.class);
        Assert.assertEquals(1, dbSessionDetails.size());
        Assert.assertEquals(dbSessionDetails.get(0).getCookieId(), "xxxyyybbbeee");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testFacebookAppTracking() throws Exception {
        // Preparation
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.FACEBOOK_OPTIONAL_AD_URL_KEY, "fbAd");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter("/game_cms_facebook_app/?fb_source=bookmark_favorites&ref=bookmarks&count=0&fb_bmpos=6_0&fbAd=ad1");
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "v3-O8s1WrS9B2XnYXpRo61n2hKc9wboofRDHOxcF8XI.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImV4cGlyZXMiOjEzNDMxNTI4MDAsImlzc3VlZF9hdCI6MTM0MzE0NjY4Mywib2F1dGhfdG9rZW4iOiJBQUFFa3RlWVZ1WkNNQkFDS29mOGpkWDMxcnVTWkN3RXFuRnFWd3Z2NnBBNldNMTVaQ1V6bzlRNmliUXJiWGtRVkJOeEF0UDJmc2EzVzY3ZXJITW5EWkFvNlZHRzVPajg4U2FJMWZOYkVyYjhCeDBuOURRWkIyIiwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX0sInVzZXJfaWQiOiIxMDAwMDM2MzQwOTQxMzkifQ");
        getWicketTester().executeUrl("/game_cms_facebook_app/?fb_source=bookmark_favorites&ref=bookmarks&count=0&fb_bmpos=6_0&fbAd=ad1");
        DbFacebookSource dbFacebookSource = session.getDbFacebookSource();
        Assert.assertNotNull(dbFacebookSource);
        Assert.assertEquals("?fb_source=bookmark_favorites&ref=bookmarks&count=0&fb_bmpos=6_0&fbAd=ad1", dbFacebookSource.getWholeString());
        Assert.assertEquals("bookmark_favorites", dbFacebookSource.getFbSource());
        Assert.assertEquals("ad1", dbFacebookSource.getOptionalAdValue());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify Db
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, loadAll(DbSessionDetail.class).size());
        DbSessionDetail dbSessionDetail = loadAll(DbSessionDetail.class).get(0);
        Assert.assertEquals("?fb_source=bookmark_favorites&ref=bookmarks&count=0&fb_bmpos=6_0&fbAd=ad1", dbSessionDetail.getDbFacebookSource().getWholeString());
        Assert.assertEquals("bookmark_favorites", dbSessionDetail.getDbFacebookSource().getFbSource());
        Assert.assertEquals("ad1", dbSessionDetail.getDbFacebookSource().getOptionalAdValue());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }

}
