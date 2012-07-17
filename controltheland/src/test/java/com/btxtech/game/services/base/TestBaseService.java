package com.btxtech.game.services.base;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.packets.AccountBalancePacket;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
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
    private BaseService baseService;
    @Autowired
    private ItemService itemService;

    @Test
    @DirtiesContext
    public void testSellBaseItem() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(userService.getUser());
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        // $1000
        SimpleBase simpleBase = getMyBase(); // Setup connection & create two account balance package
        Id id = getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID);
        sendBuildCommand(id, new Index(400, 400), TEST_FACTORY_ITEM_ID);
        // $998
        waitForActionServiceDone();
        Assert.assertEquals(998, baseService.getBase(simpleBase).getAccountBalance(), 0.1);
        clearPackets();
        getMovableService().sellItem(id);
        // $999
        AccountBalancePacket accountBalancePacket = new AccountBalancePacket();
        accountBalancePacket.setAccountBalance(998.5);
        assertPackagesIgnoreSyncItemInfoAndClear(accountBalancePacket/*, levelStatePacket*/);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSellLastBaseItem() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(userService.getUser());
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        SimpleBase simpleBase = getMyBase(); // Setup connection
        Id id = getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID);
        clearPackets();
        getMovableService().sellItem(id);

        try {
            getMovableService().getSyncInfo(START_UID_1);
            Assert.fail("NoConnectionException expected");
        } catch (NoConnectionException e) {
            Assert.assertEquals(NoConnectionException.Type.BASE_LOST, e.getType());
        }

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testGetBaseItems() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        SimpleBase simpleBase = getMyBase(); // Setup connection
        sendBuildCommand(getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID), new Index(100, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        // TODO failed on 02.07.2012
        Assert.assertEquals(2, baseService.getBaseItems().readDbChildren().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Assert.assertEquals(2, baseService.getBaseItems().readDbChildren().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }
}
