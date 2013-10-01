package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.jsre.client.dialogs.news.NewsEntryInfo;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.packets.UserAttentionPacket;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.cms.ContentService;
import com.btxtech.game.services.cms.content.DbBlogEntry;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collection;

/**
 * User: beat
 * Date: 27.05.13
 * Time: 01:06
 */
public class TestContentService extends AbstractServiceTest {
    @Autowired
    private ContentService contentService;
    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    public void getNewsEntryNoEntries() throws Exception {
        configureSimplePlanetNoResources();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createConnection();
        clearPackets();
        Assert.assertNull(contentService.getNewsEntry(0));
        assertPackagesIgnoreSyncItemInfoAndClear();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getNewsEntry() throws Exception {
        configureSimplePlanetNoResources();
        // Create news
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBlogEntry dbBlogEntry = contentService.getBlogEntryCrudRootServiceHelper().createDbChild();
        dbBlogEntry.setName("title1");
        dbBlogEntry.setHtml("html1");
        Thread.sleep(50);
        contentService.getBlogEntryCrudRootServiceHelper().updateDbChild(dbBlogEntry);
        dbBlogEntry = contentService.getBlogEntryCrudRootServiceHelper().createDbChild();
        dbBlogEntry.setName("title2");
        dbBlogEntry.setHtml("html2");
        Thread.sleep(50);
        contentService.getBlogEntryCrudRootServiceHelper().updateDbChild(dbBlogEntry);
        dbBlogEntry = contentService.getBlogEntryCrudRootServiceHelper().createDbChild();
        dbBlogEntry.setName("title3");
        dbBlogEntry.setHtml("html3");
        Thread.sleep(50);
        contentService.getBlogEntryCrudRootServiceHelper().updateDbChild(dbBlogEntry);
        dbBlogEntry = contentService.getBlogEntryCrudRootServiceHelper().createDbChild();
        dbBlogEntry.setName("title4");
        dbBlogEntry.setHtml("html4");
        Thread.sleep(50);
        contentService.getBlogEntryCrudRootServiceHelper().updateDbChild(dbBlogEntry);
        dbBlogEntry = contentService.getBlogEntryCrudRootServiceHelper().createDbChild();
        dbBlogEntry.setName("title5");
        dbBlogEntry.setHtml("html5");
        contentService.getBlogEntryCrudRootServiceHelper().updateDbChild(dbBlogEntry);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // TODO failed on: 23.07.20113
        NewsEntryInfo newsEntryInfo = contentService.getNewsEntry(0);
        Assert.assertEquals("title5", newsEntryInfo.getTitle());
        Assert.assertEquals("html5", newsEntryInfo.getContent());
        Assert.assertEquals(5, newsEntryInfo.getTotalEntries());
        newsEntryInfo = contentService.getNewsEntry(1);
        Assert.assertEquals("title4", newsEntryInfo.getTitle());
        Assert.assertEquals("html4", newsEntryInfo.getContent());
        Assert.assertEquals(5, newsEntryInfo.getTotalEntries());
        newsEntryInfo = contentService.getNewsEntry(4);
        Assert.assertEquals("title1", newsEntryInfo.getTitle());
        Assert.assertEquals("html1", newsEntryInfo.getContent());
        Assert.assertEquals(5, newsEntryInfo.getTotalEntries());
        Assert.assertNull(contentService.getNewsEntry(6));
        Assert.assertNull(contentService.getNewsEntry(100));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getNewsEntryCheckUserLastNews() throws Exception {
        configureSimplePlanetNoResources();
        // Create news
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBlogEntry dbBlogEntry = contentService.getBlogEntryCrudRootServiceHelper().createDbChild();
        dbBlogEntry.setName("title1");
        dbBlogEntry.setHtml("html1");
        contentService.getBlogEntryCrudRootServiceHelper().updateDbChild(dbBlogEntry);
        dbBlogEntry = contentService.getBlogEntryCrudRootServiceHelper().createDbChild();
        dbBlogEntry.setName("title2");
        dbBlogEntry.setHtml("html2");
        contentService.getBlogEntryCrudRootServiceHelper().updateDbChild(dbBlogEntry);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify user creation
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Thread.sleep(10);
        long before = System.currentTimeMillis();
        createAndLoginUser("U1");
        long after = System.currentTimeMillis();
        User user = userService.getUser();
        assertDate(before, after, user.getLastNews());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify getLastNews()
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Thread.sleep(10);
        loginUser("U1");
        createConnection();
        clearPackets();
        before = System.currentTimeMillis();
        contentService.getNewsEntry(0);
        after = System.currentTimeMillis();
        UserAttentionPacket userAttentionPacket = new UserAttentionPacket();
        userAttentionPacket.setNews(UserAttentionPacket.Type.CLEAR);
        assertPackagesIgnoreSyncItemInfoAndClear(userAttentionPacket);
        user = userService.getUser();
        assertDate(before, after, user.getLastNews());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify if DB works
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Thread.sleep(10);
        loginUser("U1");
        user = userService.getUser();
        assertDate(before, after, user.getLastNews());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify getLastNews() with 2nd last
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Thread.sleep(10);
        loginUser("U1");
        createConnection();
        clearPackets();
        contentService.getNewsEntry(2);
        assertPackagesIgnoreSyncItemInfoAndClear();
        user = userService.getUser();
        assertDate(before, after, user.getLastNews());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify if DB works
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Thread.sleep(10);
        loginUser("U1");
        user = userService.getUser();
        assertDate(before, after, user.getLastNews());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getNewsEntryCheckUserLastNewsNoUser() throws Exception {
        configureSimplePlanetNoResources();
        // Create news
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBlogEntry dbBlogEntry = contentService.getBlogEntryCrudRootServiceHelper().createDbChild();
        dbBlogEntry.setName("title1");
        dbBlogEntry.setHtml("html1");
        contentService.getBlogEntryCrudRootServiceHelper().updateDbChild(dbBlogEntry);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify getLastNews()
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createConnection();
        clearPackets();
        contentService.getNewsEntry(0);
        assertPackagesIgnoreSyncItemInfoAndClear();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void createUserAttentionPacketNoUser() throws Exception {
        configureSimplePlanetNoResources();
        // Create news
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBlogEntry dbBlogEntry = contentService.getBlogEntryCrudRootServiceHelper().createDbChild();
        dbBlogEntry.setName("title1");
        dbBlogEntry.setHtml("html1");
        contentService.getBlogEntryCrudRootServiceHelper().updateDbChild(dbBlogEntry);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify no user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        UserAttentionPacket userAttentionPacket = new UserAttentionPacket();
        contentService.fillUserAttentionPacket(null, userAttentionPacket);
        Assert.assertNull(userAttentionPacket.getNews());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void createUserAttentionPacket() throws Exception {
        configureSimplePlanetNoResources();
        // Create news
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBlogEntry dbBlogEntry = contentService.getBlogEntryCrudRootServiceHelper().createDbChild();
        dbBlogEntry.setName("title1");
        dbBlogEntry.setHtml("html1");
        contentService.getBlogEntryCrudRootServiceHelper().updateDbChild(dbBlogEntry);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        UserAttentionPacket userAttentionPacket = new UserAttentionPacket();
        contentService.fillUserAttentionPacket(userService.getUser(), userAttentionPacket);
        Assert.assertNull(userAttentionPacket.getNews());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create news
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Thread.sleep(10);
        dbBlogEntry = contentService.getBlogEntryCrudRootServiceHelper().createDbChild();
        dbBlogEntry.setName("title2");
        dbBlogEntry.setHtml("html2");
        contentService.getBlogEntryCrudRootServiceHelper().updateDbChild(dbBlogEntry);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        userAttentionPacket = new UserAttentionPacket();
        contentService.fillUserAttentionPacket(userService.getUser(), userAttentionPacket);
        Assert.assertEquals(UserAttentionPacket.Type.RAISE, userAttentionPacket.getNews());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }


    @Test
    @DirtiesContext
    public void createAndReadNews() throws Exception {
        configureSimplePlanetNoResources();
        // Verify user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        UserAttentionPacket userAttentionPacket = new UserAttentionPacket();
        contentService.fillUserAttentionPacket(userService.getUser(), userAttentionPacket);
        Assert.assertNull(userAttentionPacket.getNews());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create news
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Thread.sleep(10);
        DbBlogEntry dbBlogEntry = contentService.getBlogEntryCrudRootServiceHelper().createDbChild();
        dbBlogEntry.setName("title1");
        dbBlogEntry.setHtml("html1");
        contentService.getBlogEntryCrudRootServiceHelper().updateDbChild(dbBlogEntry);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        userAttentionPacket = new UserAttentionPacket();
        contentService.fillUserAttentionPacket(userService.getUser(), userAttentionPacket);
        Assert.assertEquals(UserAttentionPacket.Type.RAISE, userAttentionPacket.getNews());
        contentService.getNewsEntry(0);
        userAttentionPacket = new UserAttentionPacket();
        contentService.fillUserAttentionPacket(userService.getUser(), userAttentionPacket);
        Assert.assertNull(userAttentionPacket.getNews());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void createNewsEntryAndSendUserAttentionPacket() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createConnection();
        clearPackets();
        contentService.createNewsEntryAndSendUserAttentionPacket("title1", "message1");
        UserAttentionPacket userAttentionPacket = new UserAttentionPacket();
        userAttentionPacket.setNews(UserAttentionPacket.Type.RAISE);
        assertPackagesIgnoreSyncItemInfoAndClear(userAttentionPacket);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Collection<DbBlogEntry> dbBlogEntries = contentService.getBlogEntryCrudRootServiceHelper().readDbChildren();
        Assert.assertEquals(1, dbBlogEntries.size());
        DbBlogEntry dbBlogEntry = CommonJava.getFirst(dbBlogEntries);
        Assert.assertEquals("title1", dbBlogEntry.getName());
        Assert.assertEquals("message1", dbBlogEntry.getHtml());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }


}
