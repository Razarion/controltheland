package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.user.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 28.03.2011
 * Time: 17:10:51
 */
public class TestBotService extends AbstractServiceTest {
    @Autowired
    private BotService botService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private MgmtService mgmtService;
    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    public void testInRealm() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        setupMinimalBot(new Rectangle(1, 1, 3000, 3000));
        setupMinimalBot(new Rectangle(4000, 4000, 3000, 3000));

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertTrue(botService.isInRealm(new Index(2, 2)));
        Assert.assertTrue(botService.isInRealm(new Index(2999, 2999)));
        Assert.assertTrue(botService.isInRealm(new Index(4001, 4001)));
        Assert.assertTrue(botService.isInRealm(new Index(6999, 6999)));

        Assert.assertFalse(botService.isInRealm(new Index(3002, 3002)));
        Assert.assertFalse(botService.isInRealm(new Index(3998, 3998)));
        Assert.assertFalse(botService.isInRealm(new Index(7002, 7002)));
        Assert.assertFalse(botService.isInRealm(new Index(8000, 8000)));

    }

    @Test
    @DirtiesContext
    public void testSystemActivate() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        BotConfig botConfig = setupMinimalBot(new Rectangle(1, 1, 5000, 5000)).createBotConfig(itemService);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Wait for bot to complete
        waitForBotToBuildup(botConfig);

        HibernateUtil.openSession4InternalCall(getSessionFactory());
        try {
            botService.activate();
        } finally {
            HibernateUtil.closeSession4InternalCall(getSessionFactory());
        }
        // Wait for bot to complete
        waitForBotToBuildup(botConfig);
        assertWholeItemCount(4);

        HibernateUtil.openSession4InternalCall(getSessionFactory());
        try {
            botService.activate();
        } finally {
            HibernateUtil.closeSession4InternalCall(getSessionFactory());
        }
        // Wait for bot to complete
        waitForBotToBuildup(botConfig);
        assertWholeItemCount(4);
    }

    @Test
    @DirtiesContext
    public void testSystemActivateNoWait() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        BotConfig botConfig = setupMinimalBot(new Rectangle(1, 1, 5000, 5000)).createBotConfig(itemService);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        HibernateUtil.openSession4InternalCall(getSessionFactory());
        try {
            botService.activate();
            botService.activate();
            botService.activate();
            botService.activate();
            botService.activate();
            botService.activate();
            botService.activate();
            botService.activate();
        } finally {
            HibernateUtil.closeSession4InternalCall(getSessionFactory());
        }

        // Wait for bot to complete
        waitForBotToBuildup(botConfig);
        assertBaseCount(1);
        assertWholeItemCount(4);
    }

    @Test
    @DirtiesContext
    public void testDelete() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBotConfig dbBotConfig = setupMinimalBot(new Rectangle(1, 1, 5000, 5000));
        BotConfig botConfig = dbBotConfig.createBotConfig(itemService);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Wait for bot to complete
        waitForBotToBuildup(botConfig);
        assertWholeItemCount(4);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        botService.getDbBotConfigCrudServiceHelper().deleteDbChild(dbBotConfig);
        botService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertWholeItemCount(0);

        // Make sure backup still works
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
