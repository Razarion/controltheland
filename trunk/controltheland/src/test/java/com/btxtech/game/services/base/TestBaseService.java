package com.btxtech.game.services.base;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.AccountBalancePacket;
import com.btxtech.game.jsre.common.BaseChangedPacket;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.user.UserService;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 07.04.2011
 * Time: 13:28:45
 */
public class TestBaseService extends AbstractServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private MovableService movableService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private ItemService itemService;

    @Test
    @DirtiesContext
    public void testSellBaseItem() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(userService.getUser());
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        // $1000
        SimpleBase simpleBase = getMyBase(); // Setup connection
        Id id = getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID);
        sendBuildCommand(id, new Index(400, 400), TEST_FACTORY_ITEM_ID);
        // $998
        waitForActionServiceDone();
        Assert.assertEquals(998, baseService.getBase(simpleBase).getAccountBalance(), 0.1);
        movableService.sellItem(id);

        // $999
        AccountBalancePacket accountBalancePacket = new AccountBalancePacket();
        accountBalancePacket.setAccountBalance(998.5);
        assertPackagesIgnoreSyncItemInfoAndClear(accountBalancePacket);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSellLastBaseItem() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(userService.getUser());
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        SimpleBase simpleBase = getMyBase(); // Setup connection
        Id id = getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID);
        clearPackets();
        movableService.sellItem(id);

        try {
            movableService.getSyncInfo();
            Assert.fail("NoConnectionException expected");
        } catch (NoConnectionException e) {
            // OK
        }

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSurrenderAndCollecting() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        setupResource();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        SimpleBase simpleBase = getMyBase(); // Setup connection
        sendBuildCommand(getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID), new Index(100, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(simpleBase, TEST_FACTORY_ITEM_ID), TEST_HARVESTER_ITEM_ID);
        waitForActionServiceDone();
        Id moneyId = getFirstSynItemId(simpleBase, TEST_RESOURCE_ITEM_ID);
        sendCollectCommand(getFirstSynItemId(simpleBase, TEST_HARVESTER_ITEM_ID), moneyId);
        movableService.surrenderBase();
        waitForActionServiceDone();
        try {
            itemService.getItem(moneyId);
            Assert.fail("ItemDoesNotExistException expected");
        } catch (ItemDoesNotExistException e) {
            // OK
        }

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
