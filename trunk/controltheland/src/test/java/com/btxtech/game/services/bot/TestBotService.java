package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.services.AbstractServiceTest;
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
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        setupMinimalBot(new Rectangle(1, 1, 3000, 3000), new Rectangle(1000, 1000, 1000, 1000));
        setupMinimalBot(new Rectangle(4000, 4000, 3000, 3000), new Rectangle(5000, 5000, 1000, 1000));

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
    public void testKillBot() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        DbBotConfig dbBotConfig = setupMinimalBot(new Rectangle(1, 1, 5000, 5000), new Rectangle(1000, 1000, 3000, 3000));

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Wait for bot to complete
        waitForBotToBuildup(dbBotConfig);
        assertWholeItemCount(3);

        // Kill all bot items
        dbBotConfig.setActionDelay(50); // UGLY
        Thread.sleep(20);
        itemService.killSyncItems(itemService.getItemsCopy());
        assertWholeItemCount(0);
        dbBotConfig.setActionDelay(10); // UGLY

        // Wait for bot to complete
        waitForBotToBuildup(dbBotConfig);
        assertWholeItemCount(3);

        // Kill all bot items
        dbBotConfig.setActionDelay(50); // UGLY
        Thread.sleep(20);
        itemService.killSyncItems(itemService.getItemsCopy());
        assertWholeItemCount(0);
        dbBotConfig.setActionDelay(10); // UGLY

        // Wait for bot to complete
        waitForBotToBuildup(dbBotConfig);
        assertWholeItemCount(3);

        // Kill all bot items
        dbBotConfig.setActionDelay(50); // UGLY
        Thread.sleep(20);
        itemService.killSyncItems(itemService.getItemsCopy());
        assertWholeItemCount(0);
        dbBotConfig.setActionDelay(10); // UGLY

        // Wait for bot to complete
        waitForBotToBuildup(dbBotConfig);
        assertWholeItemCount(3);

        // Kill all bot items
        dbBotConfig.setActionDelay(50); // UGLY
        Thread.sleep(20);
        itemService.killSyncItems(itemService.getItemsCopy());
        assertWholeItemCount(0);
        dbBotConfig.setActionDelay(10); // UGLY

        // Wait for bot to complete
        waitForBotToBuildup(dbBotConfig);
        assertWholeItemCount(3);
    }

    @Test
    @DirtiesContext
    public void testSaveRestoreBot() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        DbBotConfig dbBotConfig = setupMinimalBot(new Rectangle(1, 1, 5000, 5000), new Rectangle(1000, 1000, 3000, 3000));

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Wait for bot to complete
        waitForBotToBuildup(dbBotConfig);
        assertWholeItemCount(3);

        // Save
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        mgmtService.backup();
        mgmtService.restore(mgmtService.getBackupSummary().get(0).getDate());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Wait for bot to complete
        waitForBotToBuildup(dbBotConfig);
        assertWholeItemCount(3);
    }

    @Test
    @DirtiesContext
    public void testDelete() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        DbBotConfig dbBotConfig = setupMinimalBot(new Rectangle(1, 1, 5000, 5000), new Rectangle(1000, 1000, 3000, 3000));

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Wait for bot to complete
        waitForBotToBuildup(dbBotConfig);
        assertWholeItemCount(3);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        botService.getDbBotConfigCrudServiceHelper().deleteDbChild(dbBotConfig);
        botService.activate();

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertWholeItemCount(0);
        Assert.assertEquals(0, userService.getAllBotUserStates().size());

        // Make sure backup still works
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        mgmtService.backup();

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testRestoreInvalidBot() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        DbBotConfig dbBotConfig = setupMinimalBot(new Rectangle(1, 1, 5000, 5000), new Rectangle(1000, 1000, 3000, 3000));

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Wait for bot to complete
        waitForBotToBuildup(dbBotConfig);
        assertWholeItemCount(3);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        mgmtService.backup();

        try {
            botService.getDbBotConfigCrudServiceHelper().deleteDbChild(dbBotConfig);
            Assert.fail("Bot is in backup table and can not be deleted");
        } catch (Exception e) {
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }

    @Test
    @DirtiesContext
    public void testBotBuildupWrongConfig() throws Exception {
        throw new UnsupportedOperationException();
/*
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        DbBotConfig dbBotConfig = botService.getDbBotConfigCrudServiceHelper().createDbChild();
        dbBotConfig.setActionDelay(10);
        dbBotConfig.setRealm(new Rectangle(1, 1, 5000, 5000));
        dbBotConfig.setCore(new Rectangle(1000, 1000, 3000, 3000));
        dbBotConfig.setCoreSuperiority(2);
        dbBotConfig.setRealmSuperiority(1);
        DbBotItemConfig fundamental = dbBotConfig.getBaseFundamentalCrudServiceHelper().createDbChild();
        fundamental.setBaseItemType(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        fundamental.setCount(1);
        DbBotItemConfig baseBuildup = dbBotConfig.getBotItemCrud().createDbChild();
        baseBuildup.setBaseItemType(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        baseBuildup.setCount(1);
        DbBotItemConfig defence = dbBotConfig.getDefenceCrudServiceHelper().createDbChild();
        defence.setBaseItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        defence.setCount(1);
        botService.getDbBotConfigCrudServiceHelper().updateDbChild(dbBotConfig);
        botService.activate();

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Wait for bot to complete
        try {
            waitForBotToBuildup(dbBotConfig, 1000);
            Assert.fail("TimeoutException expected");
        } catch (TimeoutException e) {
            // unused
        }  */
    }

    @Test
    @DirtiesContext
    public void testBotBuildup() throws Exception {
        throw new UnsupportedOperationException();        
       /* configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        DbBotConfig dbBotConfig = botService.getDbBotConfigCrudServiceHelper().createDbChild();
        dbBotConfig.setActionDelay(10);
        dbBotConfig.setRealm(new Rectangle(1, 1, 5000, 5000));
        dbBotConfig.setCore(new Rectangle(1000, 1000, 3000, 3000));
        dbBotConfig.setCoreSuperiority(2);
        dbBotConfig.setRealmSuperiority(1);
        DbBotItemConfig fundamental = dbBotConfig.getBaseFundamentalCrudServiceHelper().createDbChild();
        fundamental.setBaseItemType(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        fundamental.setCount(1);
        DbBotItemConfig baseBuildup = dbBotConfig.getBotItemCrud().createDbChild();
        baseBuildup.setBaseItemType(itemService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
        baseBuildup.setCount(1);
        DbBotItemConfig defence = dbBotConfig.getDefenceCrudServiceHelper().createDbChild();
        defence.setBaseItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        defence.setCount(1);
        botService.getDbBotConfigCrudServiceHelper().updateDbChild(dbBotConfig);
        botService.activate();

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Wait for bot to complete
        waitForBotToBuildup(dbBotConfig);  */
    }

}
