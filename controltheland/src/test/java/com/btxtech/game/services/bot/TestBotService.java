package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.history.DbHistoryElement;
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
            for (int i = 0; i < 1000; i++) {
                botService.activate();
            }
        } finally {
            HibernateUtil.closeSession4InternalCall(getSessionFactory());
        }

        // Wait for bot to complete
        // TODO my hangs here
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

    @Test
    @DirtiesContext
    public void testRageUp() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "xxx", "xxx", "");
        userService.login("U1", "xxx");
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(2000, 2000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        Id intruder = getFirstSynItemId(TEST_ATTACK_ITEM_ID);
        sendMoveCommand(getFirstSynItemId(TEST_ATTACK_ITEM_ID), new Index(5000, 5000));
        waitForActionServiceDone();
        SyncBaseItem intruderItem = (SyncBaseItem) itemService.getItem(intruder);
        intruderItem.setHealth(100000);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBotConfig dbBotConfig = botService.getDbBotConfigCrudServiceHelper().createDbChild();
        dbBotConfig.setName("config2");
        dbBotConfig.setActionDelay(10);
        dbBotConfig.setRealm(new Rectangle(0, 0, 1000, 1000));
        dbBotConfig.setRealGameBot(true);
        DbBotEnragementStateConfig enragementStateConfig1 = dbBotConfig.getEnrageStateCrud().createDbChild();
        enragementStateConfig1.setName("NormalTest");
        enragementStateConfig1.setEnrageUpKills(3);
        DbBotItemConfig dbBotItemConfig1 = enragementStateConfig1.getBotItemCrud().createDbChild();
        dbBotItemConfig1.setCount(2);
        dbBotItemConfig1.setBaseItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        dbBotItemConfig1.setRegion(new Rectangle(500, 500, 500, 500));
        dbBotItemConfig1.setCreateDirectly(true);
        DbBotEnragementStateConfig enragementStateConfig2 = dbBotConfig.getEnrageStateCrud().createDbChild();
        enragementStateConfig2.setName("AngryTest");
        enragementStateConfig2.setEnrageUpKills(10);
        DbBotItemConfig dbBotItemConfig2 = enragementStateConfig2.getBotItemCrud().createDbChild();
        dbBotItemConfig2.setCount(2);
        dbBotItemConfig2.setBaseItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID_2));
        dbBotItemConfig2.setRegion(new Rectangle(500, 500, 500, 500));
        dbBotItemConfig2.setCreateDirectly(true);
        botService.getDbBotConfigCrudServiceHelper().updateDbChild(dbBotConfig);
        botService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        Thread.sleep(500); // Wait for bot to build up

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "xxx");
        sendMoveCommand(intruder, new Index(500, 500));
        waitForHistoryType(DbHistoryElement.Type.BOT_ENRAGE_UP);
        sendMoveCommand(intruder, new Index(5000, 5000));
        waitForHistoryType(DbHistoryElement.Type.BOT_ENRAGE_NORMAL);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
