package com.btxtech.game.services.collision;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormation;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationFactory;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.debug.DebugService;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.ServerTerrainService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 23.08.2011
 * Time: 14:01:59
 */
public class TestAttackFormation extends AbstractServiceTest {
    // @Autowired
    // private DebugService debugService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private PlanetSystemService planetSystemService;

    @Test
    @DirtiesContext
    public void testAttackFormationNoBlockingObject() throws Throwable {
        configureSimplePlanetNoResources();

        ItemType targetItemType = serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID);
        targetItemType.setBoundingBox(new BoundingBox(200, ANGELS_24));
        SyncBaseItem target = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(1500, 1500), new Id(1, -100));
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 400), new Id(2, -100));

        int count = 18;
        int range = 200;
        double targetAngel = 0;
        double attackerAngel = 0;
        try {
            for (targetAngel = 0.0; targetAngel <= MathHelper.ONE_RADIANT; targetAngel += 0.1) {
                System.out.println("targetAngel: " + MathHelper.radToGrad(targetAngel));
                target.getSyncItemArea().turnTo(targetAngel);

                for (attackerAngel = 0.0; attackerAngel <= MathHelper.ONE_RADIANT; attackerAngel += 0.1) {
                    List<AttackFormationItem> items = new ArrayList<>();
                    for (int i = 0; i < count; i++) {
                        items.add(new AttackFormationItem(syncBaseItem, range));
                    }

                    AttackFormation attackFormation = AttackFormationFactory.create(target.getSyncItemArea(), attackerAngel, items);
                    List<SyncBaseItem> attackers = new ArrayList<>();
                    attackers.add(target);
                    while (attackFormation.hasNext()) {
                        AttackFormationItem attackFormationItem = attackFormation.calculateNextEntry();
                        SyncBaseItem attacker = createSyncBaseItem(TEST_ATTACK_ITEM_ID, attackFormationItem.getDestinationHint(), new Id(2, -100));
                        attacker.getSyncItemArea().turnTo(attackFormationItem.getDestinationAngel());
                        if (attackFormationItem.isInRange()) {
                            Assert.assertTrue("Not in range: " + attacker.getSyncItemArea().getDistance(target.getSyncItemArea()), attacker.getSyncItemArea().getDistanceRounded(target.getSyncItemArea()) <= range);
                            Assert.assertEquals(attacker.getSyncItemArea().getTurnToAngel(target.getSyncItemArea()), attackFormationItem.getDestinationAngel(), 0.001);

                        } else {
                            Assert.assertTrue("In range", attacker.getSyncItemArea().getDistance(target.getSyncItemArea()) > range);
                            Assert.fail("Overbooked not expected");
                        }
                        attackFormation.lastAccepted();
                        attackers.add(attacker);
                    }
                    Assert.assertEquals(count + 1, attackers.size());
                    assertOverlapping(attackers);
                }
            }
        } catch (Throwable t) {
            System.out.println("-----------------");
            System.out.println("targetAngel: " + MathHelper.radToGrad(targetAngel));
            System.out.println("attackerAngel: " + MathHelper.radToGrad(attackerAngel));
            throw t;
        }
    }


    @Test
    @DirtiesContext
    public void testAttackFormationAngel() throws Throwable {
        configureSimplePlanet();

        SyncBaseItem target = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(1000, 500), new Id(1, -100));
        SyncBaseItem attacker = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(200, 500), new Id(2, -100));

        List<AttackFormationItem> items = new ArrayList<>();
        items.add(new AttackFormationItem(attacker, 100));
        // North
        AttackFormation attackFormation = AttackFormationFactory.create(target.getSyncItemArea(), MathHelper.NORTH, items);
        Assert.assertTrue(attackFormation.hasNext());
        AttackFormationItem attackFormationItem = attackFormation.calculateNextEntry();
        Assert.assertTrue(attackFormationItem.isInRange());
        Assert.assertEquals(new Index(1000, 241), attackFormationItem.getDestinationHint());
        // East
        attackFormation = AttackFormationFactory.create(target.getSyncItemArea(), MathHelper.EAST, items);
        Assert.assertTrue(attackFormation.hasNext());
        attackFormationItem = attackFormation.calculateNextEntry();
        Assert.assertTrue(attackFormationItem.isInRange());
        Assert.assertEquals(new Index(1259, 500), attackFormationItem.getDestinationHint());
        // South
        attackFormation = AttackFormationFactory.create(target.getSyncItemArea(), MathHelper.SOUTH, items);
        Assert.assertTrue(attackFormation.hasNext());
        attackFormationItem = attackFormation.calculateNextEntry();
        Assert.assertTrue(attackFormationItem.isInRange());
        Assert.assertEquals(new Index(1000, 759), attackFormationItem.getDestinationHint());
        // West
        attackFormation = AttackFormationFactory.create(target.getSyncItemArea(), MathHelper.WEST, items);
        Assert.assertTrue(attackFormation.hasNext());
        attackFormationItem = attackFormation.calculateNextEntry();
        Assert.assertTrue(attackFormationItem.isInRange());
        Assert.assertEquals(new Index(741, 500), attackFormationItem.getDestinationHint());
    }

    @Test
    @DirtiesContext
    public void testAttackFormationNoBlockingObjectOverbooked() throws Throwable {
        configureSimplePlanet();

        ItemType targetItemType = serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID);
        targetItemType.setBoundingBox(new BoundingBox(200, ANGELS_24));
        SyncBaseItem target = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(1500, 1500), new Id(1, -100));

        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 400), new Id(2, -100));

        double targetAngel = MathHelper.gradToRad(31.512678732195273);
        double attackerAngel = MathHelper.gradToRad(0);

        int count = 25;
        int range = 200;
        target.getSyncItemArea().turnTo(targetAngel);

        List<AttackFormationItem> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            items.add(new AttackFormationItem(syncBaseItem, range));
        }

        AttackFormation attackFormation = AttackFormationFactory.create(target.getSyncItemArea(), attackerAngel, items);
        List<SyncBaseItem> attackers = new ArrayList<>();
        attackers.add(target);
        // debugService.drawSyncItemArea(target.getSyncItemArea(), Color.BLUE);
        // debugService.drawPosition(target.getSyncItemArea().getPosition(), Color.BLUE);

        while (attackFormation.hasNext()) {
            AttackFormationItem attackFormationItem = attackFormation.calculateNextEntry();
            SyncBaseItem attacker = createSyncBaseItem(TEST_ATTACK_ITEM_ID, attackFormationItem.getDestinationHint(), new Id(2, -100));
            attacker.getSyncItemArea().turnTo(attackFormationItem.getDestinationAngel());
            if (attackFormationItem.isInRange()) {
                double distance = attacker.getSyncItemArea().getDistanceRounded(target.getSyncItemArea());
                if (distance > range) {
                    Assert.assertEquals(attacker.getSyncItemArea().getTurnToAngel(target.getSyncItemArea()), attackFormationItem.getDestinationAngel(), 0.001);
                }
            }
            attackFormation.lastAccepted();
            attackers.add(attacker);
        }
        Assert.assertEquals(count + 1, attackers.size());
        assertOverlapping(attackers);
    }

    @Test
    @DirtiesContext
    public void testAttackFormationNoBlockingObjectSmallRange() throws Throwable {
        configureSimplePlanet();

        ItemType targetItemType = serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID);
        targetItemType.setBoundingBox(new BoundingBox(80, ANGELS_24));
        SyncBaseItem target = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(500, 500), new Id(1, -100));

        ItemType attackItemType = serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);
        attackItemType.setBoundingBox(new BoundingBox(38, ANGELS_24));
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 400), new Id(2, -100));

        double targetAngel = 0;
        double attackerAngel = 0;
        int count = 6;
        int range = 8;
        target.getSyncItemArea().turnTo(targetAngel);

        List<AttackFormationItem> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            items.add(new AttackFormationItem(syncBaseItem, range));
        }

        AttackFormation attackFormation = AttackFormationFactory.create(target.getSyncItemArea(), attackerAngel, items);
        List<SyncBaseItem> attackers = new ArrayList<>();
        attackers.add(target);
        //debugService.drawSyncItemArea(target.getSyncItemArea(), Color.BLUE);
        //debugService.drawPosition(target.getSyncItemArea().getPosition(), Color.BLUE);

        while (attackFormation.hasNext()) {
            AttackFormationItem attackFormationItem = attackFormation.calculateNextEntry();
            SyncBaseItem attacker = createSyncBaseItem(TEST_ATTACK_ITEM_ID, attackFormationItem.getDestinationHint(), new Id(2, -100));
            attacker.getSyncItemArea().turnTo(attackFormationItem.getDestinationAngel());
            if (attackFormationItem.isInRange()) {
                Assert.assertFalse("Not in range", attacker.getSyncItemArea().getDistance(target.getSyncItemArea()) > range);
            } else {
                Assert.assertTrue("In range", attacker.getSyncItemArea().getDistance(target.getSyncItemArea()) > range);
                Assert.fail("Overbooked not expected");
            }
            //debugService.drawSyncItemArea(attacker.getSyncItemArea(), Color.GREEN);
            //debugService.drawPosition(attacker.getSyncItemArea().getPosition(), Color.GREEN);

            attackFormation.lastAccepted();
            attackers.add(attacker);
        }
        Assert.assertEquals(count + 1, attackers.size());
        assertOverlapping(attackers);
        //displayOverlapping(attackers);
        //debugService.waitForClose();
    }


    private void assertOverlapping(List<SyncBaseItem> attackers) {
        for (int i = 0, attackersSize = attackers.size(); i < attackersSize; i++) {
            SyncBaseItem attackerToCheck = attackers.get(i);
            for (int j = i + 1; j < attackersSize; j++) {
                SyncBaseItem attacker = attackers.get(j);
                if (attackerToCheck.getSyncItemArea().contains(attacker)) {
                    Assert.fail("Item do overlap| " + attackerToCheck + " | " + attacker);
                }
            }
        }
    }

    @Test
    @DirtiesContext
    public void testFormationWithNegativePosition() throws Exception {
        configureSimplePlanetNoResources();
        List<SyncBaseItem> attackers = new ArrayList<>();
        SyncBaseItem target = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(500, 200), new Id(1, -100));
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 400), new Id(2, -100));

        List<AttackFormationItem> items = new ArrayList<>();
        for (int i = 0; i < 19; i++) {
            items.add(new AttackFormationItem(syncBaseItem, 200));
        }
        ServerTerrainService terrainService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getTerrainService();
        double angel = MathHelper.HALF_RADIANT;
        AttackFormation attackFormation = AttackFormationFactory.create(target.getSyncItemArea(), angel, items);
        while (attackFormation.hasNext()) {
            AttackFormationItem attackFormationItem = attackFormation.calculateNextEntry();
            if (!terrainService.isFree(attackFormationItem.getDestinationHint(), syncBaseItem.getItemType())) {
                continue;
            }

            SyncBaseItem attacker = createSyncBaseItem(TEST_ATTACK_ITEM_ID, attackFormationItem.getDestinationHint(), new Id(3, -100));

            attacker.getSyncItemArea().turnTo(target);
            attackFormation.lastAccepted();
            attackers.add(attacker);
        }
        assertOverlapping(attackers);
    }

    @Test
    @DirtiesContext
    public void testCircleFormationBlockingChannel() throws Throwable {
        configureSimplePlanetNoResources();

        Rectangle blockingRect1 = new Rectangle(0, 0, 300, 800);
        Rectangle blockingRect2 = new Rectangle(400, 0, 300, 800);

        ItemType targetItemType = serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);
        targetItemType.setBoundingBox(new BoundingBox(40, ANGELS_24));
        SyncBaseItem target = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(350, 400), new Id(1, -100));

        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 400), new Id(1, -100));

        int range = 160;
        double targetAngel = 0;
        double attackerAngel = 0;
        try {
            for (targetAngel = 0.0; targetAngel <= MathHelper.ONE_RADIANT; targetAngel += 0.1) {
                System.out.println("targetAngel: " + MathHelper.radToGrad(targetAngel));
                target.getSyncItemArea().turnTo(targetAngel);

                for (attackerAngel = 0.0; attackerAngel <= MathHelper.ONE_RADIANT; attackerAngel += 0.1) {
                    List<AttackFormationItem> items = new ArrayList<>();
                    items.add(new AttackFormationItem(syncBaseItem, range));

                    AttackFormation attackFormation = AttackFormationFactory.create(target.getSyncItemArea(), attackerAngel, items);
                    List<SyncBaseItem> attackers = new ArrayList<>();
                    attackers.add(target);
                    while (attackFormation.hasNext()) {
                        AttackFormationItem attackFormationItem = attackFormation.calculateNextEntry();
                        SyncBaseItem attacker = createSyncBaseItem(TEST_ATTACK_ITEM_ID, attackFormationItem.getDestinationHint(), new Id(2, -100));
                        attacker.getSyncItemArea().turnTo(attackFormationItem.getDestinationAngel());

                        if (attacker.getSyncItemArea().contains(blockingRect1) || attacker.getSyncItemArea().contains(blockingRect2)) {
                            continue;
                        }


                        if (attackFormationItem.isInRange()) {
                            Assert.assertTrue("Not in range: " + attacker.getSyncItemArea().getDistance(target.getSyncItemArea()), attacker.getSyncItemArea().getDistanceRounded(target.getSyncItemArea()) <= range);
                            Assert.assertEquals(attacker.getSyncItemArea().getTurnToAngel(target.getSyncItemArea()), attackFormationItem.getDestinationAngel(), 0.001);
                        } else {
                            Assert.assertTrue("In range", attacker.getSyncItemArea().getDistance(target.getSyncItemArea()) > range);
                            Assert.fail("Overbooked not expected");
                        }

                        attackFormation.lastAccepted();
                        attackers.add(attacker);
                    }
                    Assert.assertEquals(2, attackers.size());
                    assertOverlapping(attackers);
                }
            }
        } catch (Throwable t) {
            System.out.println("-----------------");
            System.out.println("targetAngel: " + MathHelper.radToGrad(targetAngel));
            System.out.println("attackerAngel: " + MathHelper.radToGrad(attackerAngel));
            throw t;
        }
    }
}
