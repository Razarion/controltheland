package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.planet.PlanetSystemService;
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
    private UserService userService;
    @Autowired
    private XpService xpService;
    @Autowired
    private PlanetSystemService planetSystemService;

    @Test
    @DirtiesContext
    public void testSetupXp() throws Exception {
        // Verify settings from configureMinimalGame
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbXpSettings dbXpSettings = xpService.getXpPointSettings();
        Assert.assertEquals(0.1, dbXpSettings.getKillPriceFactor(), 0.0001);
        Assert.assertEquals(2000, dbXpSettings.getKillQueuePeriod());
        Assert.assertEquals(10000, dbXpSettings.getKillQueueSize());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbXpSettings = new DbXpSettings();
        dbXpSettings.setKillPriceFactor(0.1);
        dbXpSettings.setKillQueuePeriod(100);
        dbXpSettings.setKillQueueSize(1000);
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
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testKillItemXp() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbXpSettings dbXpSettings = new DbXpSettings();
        dbXpSettings.setKillPriceFactor(2);
        dbXpSettings.setKillQueuePeriod(50);
        dbXpSettings.setKillQueueSize(1000);
        xpService.saveXpPointSettings(dbXpSettings);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Create target
        Collection<Id> targets = createTargets(20);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().getRealGameInfo(START_UID_1);
        Id builder = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        sendBuildCommand(builder, new Index(500, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        Id factory = getFirstSynItemId(TEST_FACTORY_ITEM_ID);
        sendFactoryCommand(factory, TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        Id attacker = getFirstSynItemId(TEST_ATTACK_ITEM_ID);
        Assert.assertEquals(0, userService.getUserState().getXp());

        while (true) {
            Id target = getNearestTarget(attacker, targets);
            if (target == null) {
                break;
            }
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

    private Id getNearestTarget(Id attacker, Collection<Id> targets) throws ItemDoesNotExistException {
        Index attackerPos = planetSystemService.getServerPlanetServices().getItemService().getItem(attacker).getSyncItemArea().getPosition();
        int distance = Integer.MAX_VALUE;
        Id resultTarget = null;
        for (Id target : targets) {
            try {
                SyncBaseItem syncBaseItem = (SyncBaseItem) planetSystemService.getServerPlanetServices().getItemService().getItem(target);
                int tmpDistance = syncBaseItem.getSyncItemArea().getPosition().getDistance(attackerPos);
                if (tmpDistance < distance) {
                    resultTarget = target;
                    distance = tmpDistance;
                }
            } catch (ItemDoesNotExistException e) {
                // Ignore
            }
        }
        return resultTarget;
    }

    private Collection<Id> createTargets(int count) throws Exception {
        Collection<Id> targets = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            beginHttpSession();
            beginHttpRequestAndOpenSessionInViewFilter();
            getMovableService().getRealGameInfo(START_UID_1);
            Id target = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
            sendMoveCommand(target, new Index(8000, 8000));
            targets.add(target);
            waitForActionServiceDone();
            endHttpRequestAndOpenSessionInViewFilter();
            endHttpSession();
        }
        if (targets.size() != count) {
            throw new IllegalStateException("Actual created targets and specified target count do not match. Actual: " + targets.size() + " Specified: " + count);
        }
        return targets;
    }
}
