package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.UserService;
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
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private UserGuidanceService guidanceService;
    @Autowired
    private XpService xpService;

    @Test
    @DirtiesContext
    public void testKillItemXp() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType dbBaseItemType = serverItemTypeService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID);
        dbBaseItemType.setXpOnKilling(11);
        serverItemTypeService.getDbItemTypeCrud().updateDbChild(dbBaseItemType);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Create target
        // TODO failed on: 05.07.2013 (Enemy items too near ItemType: TestStartBuilderItem UserState: user=null)
        Collection<Id> targets = createTargets(20);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().getRealGameInfo(START_UID_1, null);
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
        Thread.sleep(200);
        Assert.assertEquals(220, userService.getUserState().getXp());

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
            getMovableService().getRealGameInfo(START_UID_1, null);
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

    @Test
    @DirtiesContext
    public void testKillItemXpWrongPlanet() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType dbBaseItemType = serverItemTypeService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID);
        dbBaseItemType.setXpOnKilling(11);
        serverItemTypeService.getDbItemTypeCrud().updateDbChild(dbBaseItemType);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Create target
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        guidanceService.promote(userService.getUserState(), TEST_LEVEL_2_REAL_ID);
        getOrCreateBase(); // Build base
        Id target = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        sendMoveCommand(target, new Index(8000, 8000));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        guidanceService.promote(userService.getUserState(), TEST_LEVEL_2_REAL_ID);
        // TODO Failed (realGameInfo == null): on 03.09.2013
        getOrCreateBase();  // Build base
        guidanceService.promote(userService.getUserState(), TEST_LEVEL_5_REAL_ID);
        Assert.assertEquals(TEST_PLANET_1_ID, getOrCreateBase().getPlanetId());
        Id builder = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        sendBuildCommand(builder, new Index(500, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        Id factory = getFirstSynItemId(TEST_FACTORY_ITEM_ID);
        sendFactoryCommand(factory, TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        Id attacker = getFirstSynItemId(TEST_ATTACK_ITEM_ID);
        Assert.assertEquals(0, userService.getUserState().getXp());

        sendAttackCommand(attacker, target);
        waitForActionServiceDone();
        Thread.sleep(200);
        Assert.assertEquals(0, userService.getUserState().getXp());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testKillAbandonmentBase() throws Exception {
        configureSimplePlanetNoResources();

        // Create actor
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase actorBase = getOrCreateBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertTrue(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService().isAbandoned(actorBase));

        // Create target
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createBase(new Index(2000, 2000));
        SyncBaseItem target = (SyncBaseItem) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getItemService().getItem(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        xpService.onItemKilled(actorBase, target, planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID));
    }

}
