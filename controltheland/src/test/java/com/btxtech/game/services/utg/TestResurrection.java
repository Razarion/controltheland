package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.packets.BaseChangedPacket;
import com.btxtech.game.jsre.common.packets.Message;
import com.btxtech.game.jsre.common.packets.XpPacket;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.planet.BaseService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.UserService;
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
    private UserService userService;
    @Autowired
    private PlanetSystemService planetSystemService;

    @Test
    @DirtiesContext
    public void testOnlineSell() throws Exception {
        configureSimplePlanetNoResources();
        BaseService baseService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase simpleBase = getMovableService().getRealGameInfo(START_UID_1).getBase();
        Id id = getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID);
        clearPackets();
        getMovableService().sellItem(id);

        Assert.assertEquals(0, baseService.getBases().size());

        try {
            getMovableService().getSyncInfo(START_UID_1, false);
            Assert.fail("Disconnection expected");
        } catch (NoConnectionException e) {
            Assert.assertEquals(NoConnectionException.Type.BASE_LOST, e.getType());
        }

        // Also second call should fail
        try {
            getMovableService().getSyncInfo(START_UID_1, false);
            Assert.fail("Disconnection expected");
        } catch (NoConnectionException e) {
            Assert.assertEquals(NoConnectionException.Type.BASE_LOST, e.getType());
        }

        SimpleBase newBase = getMovableService().getRealGameInfo(START_UID_1).getBase();
        Assert.assertEquals(1, baseService.getBases().size());
        Assert.assertFalse(newBase.equals(simpleBase));

        Message message = new Message();
        message.setMessage("You lost your base. A new base was created.");
        // TODO failed: 02.06.2012
        assertPackagesIgnoreSyncItemInfoAndClear(message);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testOffline() throws Exception {
        configureSimplePlanetNoResources();
        BaseService baseService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        // Target
        userService.createUser("U1", "test", "test", "");
        userService.login("U1", "test");
        SimpleBase targetBase = getMovableService().getRealGameInfo(START_UID_1).getBase();
        String targetName = baseService.getBaseName(targetBase);
        Id target = getFirstSynItemId(targetBase, TEST_START_BUILDER_ITEM_ID);
        sendMoveCommand(target, new Index(1000, 1000));
        clearPackets();
        assertWholeItemCount(TEST_PLANET_1_ID, 1);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        // Actor
        SimpleBase actorBase = getMovableService().getRealGameInfo(START_UID_1).getBase();
        Assert.assertEquals(2, baseService.getBases().size());
        Id actorBuilder = getFirstSynItemId(actorBase, TEST_START_BUILDER_ITEM_ID);
        sendBuildCommand(actorBuilder, new Index(100, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        // TODO failed on 25.07.2012
        Id actorFactory = getFirstSynItemId(actorBase, TEST_FACTORY_ITEM_ID);
        sendFactoryCommand(actorFactory, TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        Id actorAttacker = getFirstSynItemId(actorBase, TEST_ATTACK_ITEM_ID);
        clearPackets();
        assertWholeItemCount(TEST_PLANET_1_ID, 4);
        sendAttackCommand(actorAttacker, target);
        waitForActionServiceDone();
        Assert.assertEquals(1, baseService.getBases().size());
        assertWholeItemCount(TEST_PLANET_1_ID, 3);

        Message message = new Message();
        message.setMessage("You defeated U1");

        BaseChangedPacket baseChangedPacket = new BaseChangedPacket();
        baseChangedPacket.setType(BaseChangedPacket.Type.REMOVED);
        baseChangedPacket.setBaseAttributes(new BaseAttributes(targetBase, targetName, false));
        XpPacket xpPacket = new XpPacket();
        xpPacket.setXp(1);
        xpPacket.setXp2LevelUp(Integer.MAX_VALUE);
        Thread.sleep(3000);

        assertPackagesIgnoreSyncItemInfoAndClear(message, baseChangedPacket, xpPacket);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        userService.login("U1", "test");
        SimpleBase targetBaseNew = getMovableService().getRealGameInfo(START_UID_1).getBase();
        Assert.assertFalse(targetBaseNew.equals(targetBase));
        Assert.assertEquals(2, baseService.getBases().size());
        Message message2 = new Message();
        message2.setMessage("You lost your base. A new base was created.");
        // TODO failed on 18.10.2012
        assertPackagesIgnoreSyncItemInfoAndClear(true, message2);
        assertWholeItemCount(TEST_PLANET_1_ID, 4);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
