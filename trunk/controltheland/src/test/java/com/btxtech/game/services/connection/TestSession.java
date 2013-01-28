package com.btxtech.game.services.connection;

import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.utg.tracker.DbSessionDetail;
import com.btxtech.game.wicket.WebCommon;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import javax.servlet.http.Cookie;
import java.util.List;

/**
 * User: beat
 * Date: 25.01.13
 * Time: 17:03
 */
public class TestSession extends AbstractServiceTest {
    @Autowired
    private Session session;

    @Test
    @DirtiesContext
    public void testAdCellNoParams() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(session.getAdCellBid());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Check history generation
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbSessionDetail> dbSessionDetails = HibernateUtil.loadAll(getSessionFactory(), DbSessionDetail.class);
        Assert.assertEquals(1, dbSessionDetails.size());
        Assert.assertNull(dbSessionDetails.get(0).getAdCellBid());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testAdCellUrlParams() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setWicketParameterAdCellBid("adCellStringBid");
        Assert.assertEquals("adCellStringBid", session.getAdCellBid());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Check history generation
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbSessionDetail> dbSessionDetails = HibernateUtil.loadAll(getSessionFactory(), DbSessionDetail.class);
        Assert.assertEquals(1, dbSessionDetails.size());
        Assert.assertEquals(dbSessionDetails.get(0).getAdCellBid(), "adCellStringBid");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

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
}
