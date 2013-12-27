package com.btxtech.game.wicket.pages;

import com.btxtech.game.jsre.client.common.info.SimpleUser;
import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.utg.tracker.DbPageAccess;
import junit.framework.Assert;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

/**
 * User: beat
 * Date: 13.07.13
 * Time: 09:53
 */
public class TestWicketInvitationStart extends AbstractServiceTest {
    @Test
    @DirtiesContext
    public void testTracking() throws Exception {
        configureSimplePlanetNoResources();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        PageParameters pageParameters = new PageParameters();
        pageParameters.set("xxx", "yyy");
        getWicketTester().startPage(InvitationStart.class, pageParameters);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbPageAccess> dbPageAccesses = loadAll(DbPageAccess.class);
        junit.framework.Assert.assertEquals(2, dbPageAccesses.size());
        junit.framework.Assert.assertEquals(InvitationStart.class.getName(), dbPageAccesses.get(0).getPage());
        junit.framework.Assert.assertEquals("xxx=[yyy]", dbPageAccesses.get(0).getAdditional());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testUrlGenerating() throws Exception {
        SimpleUser simpleUser = new SimpleUser("xxx", 1, true, false);
        Assert.assertEquals("http://www.razarion.com/game_cms_invitation/?user=1&type=mail", CmsUtil.generateInviteUrl(simpleUser, CmsUtil.MAIL_VALUE));
        Assert.assertEquals("http://www.razarion.com/game_cms_invitation/?user=1&type=url", CmsUtil.generateInviteUrl(simpleUser, CmsUtil.URL_VALUE));
    }

}
