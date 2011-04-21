package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Message;
import com.btxtech.game.jsre.client.common.info.RealityInfo;
import com.btxtech.game.jsre.common.BaseChangedPacket;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.XpBalancePacket;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 16.04.2011
 * Time: 12:33:58
 */
public class TestResurrection extends AbstractServiceTest {
    @Autowired
    private MovableService movableService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserGuidanceService userGuidanceService;

    @Test
    @DirtiesContext
    public void testOnlineSell() throws Exception {
        configureMinimalGame();

        System.out.println("***** testOnlineSell *****");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        SimpleBase simpleBase = ((RealityInfo) movableService.getGameInfo()).getBase();
        Id id = getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID);
        clearPackets();
        movableService.sellItem(id);

        Assert.assertEquals(0, baseService.getBases().size());

        try {
            movableService.getSyncInfo();
            Assert.fail("Disconnection expected");
        } catch (NoConnectionException e) {
            // OK
        }

        // Also second call should fail
        try {
            movableService.getSyncInfo();
            Assert.fail("Disconnection expected");
        } catch (NoConnectionException e) {
            // OK
        }

        SimpleBase newBase = ((RealityInfo) movableService.getGameInfo()).getBase();
        Assert.assertEquals(1, baseService.getBases().size());
        Assert.assertFalse(newBase.equals(simpleBase));

        Message message = new Message();
        message.setMessage("You lost your base. A new base was created.");
        assertPackagesIgnoreSyncItemInfoAndClear(message);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testOffline() throws Exception {
        configureMinimalGame();

        System.out.println("***** testOffline *****");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        // Target
        userService.createUser("U1", "test", "test", "");
        userService.login("U1", "test");
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        SimpleBase targetBase = ((RealityInfo) movableService.getGameInfo()).getBase();
        String targetColor = baseService.getBaseHtmlColor(targetBase);
        String targetName = baseService.getBaseName(targetBase);
        Id target = getFirstSynItemId(targetBase, TEST_START_BUILDER_ITEM_ID);
        clearPackets();
        assertWholeItemTypeCount(1);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        // Actor
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        Assert.assertEquals(2, baseService.getBases().size());
        SimpleBase actorBase = ((RealityInfo) movableService.getGameInfo()).getBase();
        Id actorBuilder = getFirstSynItemId(actorBase, TEST_START_BUILDER_ITEM_ID);
        sendBuildCommand(actorBuilder, new Index(100, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        Id actorFactory = getFirstSynItemId(actorBase, TEST_FACTORY_ITEM_ID);
        sendFactoryCommand(actorFactory, TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        Id actorAttacker = getFirstSynItemId(actorBase, TEST_ATTACK_ITEM_ID);
        clearPackets();
        assertWholeItemTypeCount(4);
        sendAttackCommand(actorAttacker, target);
        waitForActionServiceDone();
        Assert.assertEquals(1, baseService.getBases().size());
        assertWholeItemTypeCount(3);

        Message message = new Message();
        message.setMessage("You defeated U1");

        BaseChangedPacket baseChangedPacket = new BaseChangedPacket();
        baseChangedPacket.setType(BaseChangedPacket.Type.REMOVED);
        baseChangedPacket.setBaseAttributes(new BaseAttributes(targetBase, targetName, targetColor, false));

        XpBalancePacket xpBalancePacket = new XpBalancePacket();
        xpBalancePacket.setXp(1);

        Thread.sleep(3000);

        assertPackagesIgnoreSyncItemInfoAndClear(message, baseChangedPacket, xpBalancePacket);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        userService.login("U1", "test");
        SimpleBase targetBaseNew = ((RealityInfo) movableService.getGameInfo()).getBase();
        Assert.assertFalse(targetBaseNew.equals(targetBase));
        Assert.assertEquals(2, baseService.getBases().size());
        Message message2 = new Message();
        message2.setMessage("You lost your base. A new base was created.");
        assertPackagesIgnoreSyncItemInfoAndClear(message2);
        assertWholeItemTypeCount(4);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testResurrectionDuringSimulation() throws Exception {
        configureMinimalGame();

        System.out.println("***** testOnlineSell *****");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        // Target
        userService.createUser("U1", "test", "test", "");
        userService.login("U1", "test");
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        SimpleBase targetBase = getMyBase();
        UserState targetUserState = userService.getUserState();
        Id targetId = getFirstSynItemId(targetBase, TEST_START_BUILDER_ITEM_ID);
        userGuidanceService.promote(targetUserState, TEST_LEVEL_4_SIMULATED_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Kill
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        SimpleBase actorBase = getMyBase();
        Id actorBuilder = getFirstSynItemId(actorBase, TEST_START_BUILDER_ITEM_ID);
        sendBuildCommand(actorBuilder, new Index(100,100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        Id actorFactory = getFirstSynItemId(actorBase, TEST_FACTORY_ITEM_ID);
        sendFactoryCommand(actorFactory, TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        Id actorAttacker = getFirstSynItemId(actorBase, TEST_ATTACK_ITEM_ID);
        sendAttackCommand(actorAttacker, targetId);
        waitForActionServiceDone();
        
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        getMyBase();  // Connection
        Message message = new Message();
        message.setMessage("You lost your base. A new base was created.");
        assertPackagesIgnoreSyncItemInfoAndClear(message);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSurrender() throws Exception {
        configureMinimalGame();

        System.out.println("***** testSurrender *****");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        SimpleBase simpleBase = getMyBase(); // Connection
        movableService.surrenderBase();

        try {
            movableService.getSyncInfo();
            Assert.fail("Disconnection expected");
        } catch (NoConnectionException e) {
            // OK
        }

        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, baseService.getBases().size());
        assertWholeItemTypeCount(1);

        SimpleBase newBase = getMyBase(); // Connection
        Assert.assertFalse(simpleBase.equals(newBase));
        Assert.assertEquals(2, baseService.getBases().size());
        Message message2 = new Message();
        message2.setMessage("You lost your base. A new base was created.");
        assertPackagesIgnoreSyncItemInfoAndClear(message2);
        assertWholeItemTypeCount(2);

        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();

    }


}
