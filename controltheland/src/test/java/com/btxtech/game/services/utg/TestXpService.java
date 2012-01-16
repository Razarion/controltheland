package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.impl.XpServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 01.04.2011
 * Time: 14:17:33
 */
public class TestXpService extends AbstractServiceTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private MovableService movableService;
    @Autowired
    private XpService xpService;

    @Test
    @DirtiesContext
    public void testSetupXp() throws Exception {
        configureMinimalGame();

        // Verify settings from configureMinimalGame
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbXpSettings dbXpSettings = xpService.getXpPointSettings();
        Assert.assertEquals(1, dbXpSettings.getKillPriceFactor(), 0.0001);
        Assert.assertEquals(2000, dbXpSettings.getKillQueuePeriod());
        Assert.assertEquals(10000, dbXpSettings.getKillQueueSize());
        Assert.assertEquals(0.5, dbXpSettings.getBuiltPriceFactor(), 0.001);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbXpSettings = new DbXpSettings();
        dbXpSettings.setKillPriceFactor(0.1);
        dbXpSettings.setKillQueuePeriod(100);
        dbXpSettings.setKillQueueSize(1000);
        dbXpSettings.setBuiltPriceFactor(0.4);
        xpService.saveXpPointSettings(dbXpSettings);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbXpSettings = xpService.getXpPointSettings();
        Assert.assertEquals(0.1, dbXpSettings.getKillPriceFactor(), 0.0001);
        Assert.assertEquals(100, dbXpSettings.getKillQueuePeriod());
        Assert.assertEquals(1000, dbXpSettings.getKillQueueSize());
        Assert.assertEquals(0.4, dbXpSettings.getBuiltPriceFactor(), 0.001);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Check activate with no session
        ((XpServiceImpl) deAopProxy(xpService)).start();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbXpSettings = xpService.getXpPointSettings();
        Assert.assertEquals(0.1, dbXpSettings.getKillPriceFactor(), 0.0001);
        Assert.assertEquals(100, dbXpSettings.getKillQueuePeriod());
        Assert.assertEquals(1000, dbXpSettings.getKillQueueSize());
        Assert.assertEquals(0.4, dbXpSettings.getBuiltPriceFactor(), 0.001);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testLevelBuiltItemXp() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbXpSettings dbXpSettings = new DbXpSettings();
        dbXpSettings.setKillPriceFactor(0.1);
        dbXpSettings.setKillQueuePeriod(100);
        dbXpSettings.setKillQueueSize(1000);
        dbXpSettings.setBuiltPriceFactor(1);
        xpService.saveXpPointSettings(dbXpSettings);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Create Items
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        // Create StartItem gets 1 XP
        Assert.assertEquals(1, userService.getUserState().getXp());
        Id builder = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        sendBuildCommand(builder, new Index(500, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        Assert.assertEquals(3, userService.getUserState().getXp());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testKillItemXp() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbXpSettings dbXpSettings = new DbXpSettings();
        dbXpSettings.setKillPriceFactor(2);
        dbXpSettings.setKillQueuePeriod(50);
        dbXpSettings.setKillQueueSize(1000);
        dbXpSettings.setBuiltPriceFactor(0);
        xpService.saveXpPointSettings(dbXpSettings);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Create target
        Collection<Id> targets = createTargets(20);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        Id builder = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        sendBuildCommand(builder, new Index(500, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        Id factory = getFirstSynItemId(TEST_FACTORY_ITEM_ID);
        sendFactoryCommand(factory, TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        Id attacker = getFirstSynItemId(TEST_ATTACK_ITEM_ID);
        Assert.assertEquals(0, userService.getUserState().getXp());
        for (Id target : targets) {
            try {
                sendAttackCommand(attacker, target);
                waitForActionServiceDone();
            } catch (ItemDoesNotExistException ignore) {
                // Ignore. Item may was killed by accident
            }
        }
        Thread.sleep(100);
        Assert.assertEquals(40, userService.getUserState().getXp());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private Collection<Id> createTargets(int count) {
        Collection<Id> targets = new ArrayList<Id>();
        for (int i = 0; i < count; i++) {
            beginHttpSession();
            beginHttpRequestAndOpenSessionInViewFilter();
            movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
            Id target = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
            targets.add(target);
            endHttpRequestAndOpenSessionInViewFilter();
            endHttpSession();
        }
        if (targets.size() != count) {
            throw new IllegalStateException("Actual created targets and specified target count do not match. Actual: " + targets.size() + " Specified: " + count);
        }
        return targets;
    }


}
