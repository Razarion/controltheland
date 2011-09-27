package com.btxtech.game.services.collision;

import com.btxtech.game.jsre.client.common.Arc;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Line;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormation;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationFactory;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.formation.RoundedRectangleAttackFormation;
import com.btxtech.game.jsre.common.gameengine.formation.RoundedRectangleAttackFormationTrack;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.debug.DebugService;
import com.btxtech.game.services.item.ItemService;
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
    @Autowired
    private CollisionService collisionService;
    @Autowired
    private DebugService debugService;
    @Autowired
    private ItemService itemService;

    @Test
    @DirtiesContext
    public void testAttackFormationTrack() throws Exception {
        BoundingBox boundingBox = new BoundingBox(300, 200, 200, 100, 24);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(500, 500));
        RoundedRectangleAttackFormationTrack trackRoundedRectangle = new RoundedRectangleAttackFormationTrack(syncItemArea, 100, true);

        Assert.assertEquals(new Line(new Index(600, 350), new Index(400, 350)), trackRoundedRectangle.getSegments().get(0));
        Assert.assertEquals(new Arc(new Index(400, 350), new Index(300, 450), new Index(400, 450)), trackRoundedRectangle.getSegments().get(1));
        Assert.assertEquals(new Line(new Index(300, 450), new Index(300, 550)), trackRoundedRectangle.getSegments().get(2));
        Assert.assertEquals(new Arc(new Index(300, 550), new Index(400, 650), new Index(400, 550)), trackRoundedRectangle.getSegments().get(3));
        Assert.assertEquals(new Line(new Index(400, 650), new Index(600, 650)), trackRoundedRectangle.getSegments().get(4));
        Assert.assertEquals(new Arc(new Index(600, 650), new Index(700, 550), new Index(600, 550)), trackRoundedRectangle.getSegments().get(5));
        Assert.assertEquals(new Line(new Index(700, 550), new Index(700, 450)), trackRoundedRectangle.getSegments().get(6));
        Assert.assertEquals(new Arc(new Index(700, 450), new Index(600, 350), new Index(600, 450)), trackRoundedRectangle.getSegments().get(7));
    }

    @Test
    @DirtiesContext
    public void testAttackFormationTrackRotated() throws Exception {
        BoundingBox boundingBox = new BoundingBox(300, 200, 200, 100, 24);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(500, 500));
        syncItemArea.setAngel(MathHelper.gradToRad(80));
        RoundedRectangleAttackFormationTrack trackRoundedRectangle = new RoundedRectangleAttackFormationTrack(syncItemArea, 100, true);

        Assert.assertEquals(new Line(new Index(370, 376), new Index(335, 573)), trackRoundedRectangle.getSegments().get(0));
        Assert.assertEquals(new Arc(new Index(335, 573), new Index(416, 688), new Index(433, 590)), trackRoundedRectangle.getSegments().get(1));
        Assert.assertEquals(new Line(new Index(416, 688), new Index(515, 705)), trackRoundedRectangle.getSegments().get(2));
        Assert.assertEquals(new Arc(new Index(515, 705), new Index(630, 624), new Index(532, 607)), trackRoundedRectangle.getSegments().get(3));
        Assert.assertEquals(new Line(new Index(630, 624), new Index(665, 427)), trackRoundedRectangle.getSegments().get(4));
        Assert.assertEquals(new Arc(new Index(665, 427), new Index(584, 312), new Index(567, 410)), trackRoundedRectangle.getSegments().get(5));
        Assert.assertEquals(new Line(new Index(584, 312), new Index(485, 295)), trackRoundedRectangle.getSegments().get(6));
        Assert.assertEquals(new Arc(new Index(485, 295), new Index(370, 376), new Index(468, 393)), trackRoundedRectangle.getSegments().get(7));
    }

    // TODO @Test

    @DirtiesContext
    public void testAttackFormationTrack1Attacker() throws Exception {
        BoundingBox boundingBox = new BoundingBox(300, 200, 200, 100, 24);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(500, 500));
        RoundedRectangleAttackFormationTrack track = new RoundedRectangleAttackFormationTrack(syncItemArea, 100, true);
        //     Index position = track.start(0, 80, 100);
        //    Assert.assertEquals(new Index(500, 250), position);
    }

    // TODO  @Test

    @DirtiesContext
    public void testAttackFormationTrack1AttackerRot() throws Exception {
        BoundingBox boundingBox = new BoundingBox(300, 200, 200, 100, 24);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(500, 500));
        syncItemArea.setAngel(MathHelper.gradToRad(45));
        RoundedRectangleAttackFormationTrack track = new RoundedRectangleAttackFormationTrack(syncItemArea, 100, true);
        //     Index position = track.start(0, 80, 200);
        //     Assert.assertEquals(new Index(430, 112), position);
    }

    // TODO @Test

    @DirtiesContext
    public void testAttackFormationTrack5AttackerRot() throws Exception {
        BoundingBox boundingBox = new BoundingBox(300, 200, 200, 100, 24);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(500, 500));
        RoundedRectangleAttackFormationTrack track = new RoundedRectangleAttackFormationTrack(syncItemArea, 100, true);
//        Assert.assertEquals(new Index(500, 250), track.start(0, 100, 100));
//        Assert.assertEquals(new Index(400, 250), track.getNextPoint(100, 100));
//        Assert.assertEquals(new Index(260, 350), track.getNextPoint(100, 100));
//        Assert.assertEquals(new Index(200, 508), track.getNextPoint(100, 100));
//        Assert.assertEquals(new Index(271, 661), track.getNextPoint(100, 100));
//        Assert.assertEquals(new Index(417, 750), track.getNextPoint(100, 100));
//        Assert.assertEquals(new Index(517, 750), track.getNextPoint(100, 100));
//        Assert.assertEquals(new Index(682, 708), track.getNextPoint(100, 100));
//        Assert.assertEquals(new Index(752, 638), track.getNextPoint(100, 100));
//        Assert.assertEquals(new Index(800, 475), track.getNextPoint(100, 100));
    }

    // TODO @Test

    @DirtiesContext
    public void testAttackFormationTrack5AttackerRotCounter() throws Exception {
        BoundingBox boundingBox = new BoundingBox(300, 200, 200, 100, 24);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(500, 500));
        RoundedRectangleAttackFormationTrack track = new RoundedRectangleAttackFormationTrack(syncItemArea, 100, false);
//        Assert.assertEquals(new Index(500, 250), track.start(0, 100, 100));
//        Assert.assertEquals(new Index(670, 280), track.getNextPoint(100, 100));
//        Assert.assertEquals(new Index(740, 350), track.getNextPoint(100, 100));
//        Assert.assertEquals(new Index(800, 508), track.getNextPoint(100, 100));
//        Assert.assertEquals(new Index(729, 661), track.getNextPoint(100, 100));
//        Assert.assertEquals(new Index(583, 750), track.getNextPoint(100, 100));
//        Assert.assertEquals(new Index(483, 750), track.getNextPoint(100, 100));
//        Assert.assertEquals(new Index(318, 708), track.getNextPoint(100, 100));
//        Assert.assertEquals(new Index(248, 638), track.getNextPoint(100, 100));
//        Assert.assertEquals(new Index(200, 475), track.getNextPoint(100, 100));
    }

    // TODO @Test

    @DirtiesContext
    public void testAttackFormationTrackDifferentAngel() throws Exception {
        BoundingBox boundingBox = new BoundingBox(300, 200, 200, 100, 24);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(500, 500));
        for (double angel = 0.0; angel < MathHelper.ONE_RADIANT; angel += MathHelper.gradToRad(0.01)) {
            RoundedRectangleAttackFormationTrack track = new RoundedRectangleAttackFormationTrack(syncItemArea, 100, false);
            // Assert.assertNotNull(track.start(angel, 100, 100));
        }
    }

    @Test
    @DirtiesContext
    public void testAttackFormationNoBlockingObject() throws Throwable {
        configureMinimalGame();

        ItemType targetItemType = itemService.getItemType(TEST_SIMPLE_BUILDING_ID);
        targetItemType.setBoundingBox(new BoundingBox(100, 100, 200, 80, 1));
        SyncBaseItem target = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(1500, 1500), new Id(1, -100, -100));

        //ItemType attackItemType = itemService.getItemType(TEST_ATTACK_ITEM_ID);
        //attackItemType.setBoundingBox(new BoundingBox(100, 100, 80, 80, 24));
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 400), new Id(2, -100, -100));

        int count = 22;
        int range = 200;
        double targetAngel = 0;
        double attackerAngel = 0;
        try {
            for (targetAngel = 0.0; targetAngel <= MathHelper.ONE_RADIANT; targetAngel += 0.1) {
                System.out.println("targetAngel: " + MathHelper.radToGrad(targetAngel));
                target.getSyncItemArea().turnTo(targetAngel);

                for (attackerAngel = 0.0; attackerAngel <= MathHelper.ONE_RADIANT; attackerAngel += 0.1) {
                    List<AttackFormationItem> items = new ArrayList<AttackFormationItem>();
                    for (int i = 0; i < count; i++) {
                        items.add(new AttackFormationItem(syncBaseItem, range));
                    }

                    AttackFormation attackFormation = AttackFormationFactory.create(target.getSyncItemArea(), attackerAngel, items);
                    List<SyncBaseItem> attackers = new ArrayList<SyncBaseItem>();
                    attackers.add(target);
                    while (attackFormation.hasNext()) {
                        AttackFormationItem attackFormationItem = attackFormation.calculateNextEntry();
                        SyncBaseItem attacker = createSyncBaseItem(TEST_ATTACK_ITEM_ID, attackFormationItem.getDestinationHint(), new Id(2, -100, -100));
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


    // Wolfram Alpha
    // rectangle, center = {1500,1500}, width=100, height = 40, rotation angle 3.399999999999996 rad


    // TODO @Test
    @DirtiesContext
    public void testAttackFormationNoBlockingObject__TMP() throws Throwable {
        configureMinimalGame();

        ItemType targetItemType = itemService.getItemType(TEST_SIMPLE_BUILDING_ID);
        targetItemType.setBoundingBox(new BoundingBox(100, 100, 200, 80, 1));
        SyncBaseItem target = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(1500, 1500), new Id(1, -100, -100));

        //ItemType attackItemType = itemService.getItemType(TEST_ATTACK_ITEM_ID);
        //attackItemType.setBoundingBox(new BoundingBox(100, 100, 80, 80, 24));
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 400), new Id(2, -100, -100));

        double targetAngel = MathHelper.gradToRad(31.512678732195273);
        double attackerAngel = MathHelper.gradToRad(0);

        int count = 60;
        int range = 200;
        target.getSyncItemArea().turnTo(targetAngel);

        List<AttackFormationItem> items = new ArrayList<AttackFormationItem>();
        for (int i = 0; i < count; i++) {
            items.add(new AttackFormationItem(syncBaseItem, range));
        }

        AttackFormation attackFormation = AttackFormationFactory.create(target.getSyncItemArea(), attackerAngel, items);
        List<SyncBaseItem> attackers = new ArrayList<SyncBaseItem>();
        attackers.add(target);
        debugService.drawSyncItemArea(target.getSyncItemArea(), Color.BLUE);
        debugService.drawPosition(target.getSyncItemArea().getPosition(), Color.BLUE);
        debugService.drawSegments(((RoundedRectangleAttackFormation) attackFormation).getClockwiseTrack().getSegments());
        debugService.drawSegments(((RoundedRectangleAttackFormation) attackFormation).getCounterClockwiseTrack().getSegments());

        while (attackFormation.hasNext()) {
            AttackFormationItem attackFormationItem = attackFormation.calculateNextEntry();
            SyncBaseItem attacker = createSyncBaseItem(TEST_ATTACK_ITEM_ID, attackFormationItem.getDestinationHint(), new Id(2, -100, -100));
            attacker.getSyncItemArea().turnTo(attackFormationItem.getDestinationAngel());
            if (attackFormationItem.isInRange()) {
                //debugService.drawSyncItemArea(attacker.getSyncItemArea(), Color.GREEN);
                //debugService.drawPosition(attacker.getSyncItemArea().getPosition(), Color.GREEN);
                double distance = attacker.getSyncItemArea().getDistanceRounded(target.getSyncItemArea());
                if (distance > range) {
                    //debugService.waitForClose();
                    //Assert.fail("Not in range: " + attacker.getSyncItemArea().getDistance(target.getSyncItemArea()));
                    debugService.drawSyncItemArea(attacker.getSyncItemArea(), Color.MAGENTA);
                    debugService.drawPosition(attacker.getSyncItemArea().getPosition(), Color.MAGENTA);
                    Assert.assertEquals(attacker.getSyncItemArea().getTurnToAngel(target.getSyncItemArea()), attackFormationItem.getDestinationAngel(), 0.001);
                } else {
                    debugService.drawSyncItemArea(attacker.getSyncItemArea(), Color.GREEN);
                    debugService.drawPosition(attacker.getSyncItemArea().getPosition(), Color.GREEN);
                }
            } else {
                //Assert.assertTrue("In range", attacker.getSyncItemArea().getDistance(target.getSyncItemArea()) > range);
                //Assert.fail("Overbooked not expected");
                debugService.drawSyncItemArea(attacker.getSyncItemArea(), Color.GRAY);
                debugService.drawPosition(attacker.getSyncItemArea().getPosition(), Color.GRAY);
            }
            attackFormation.lastAccepted();
            attackers.add(attacker);
        }
        Assert.assertEquals(count + 1, attackers.size());
        displayOverlapping(attackers);
        debugService.waitForClose();
    }

    // @Test

    @DirtiesContext
    public void testAttackFormationNoBlockingObject__TMP_smallRange() throws Throwable {
        configureMinimalGame();

        ItemType targetItemType = itemService.getItemType(TEST_SIMPLE_BUILDING_ID);
        targetItemType.setBoundingBox(new BoundingBox(100, 100, 80, 80, 1));
        SyncBaseItem target = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(500, 500), new Id(1, -100, -100));

        ItemType attackItemType = itemService.getItemType(TEST_ATTACK_ITEM_ID);
        attackItemType.setBoundingBox(new BoundingBox(100, 100, 38, 68, 24));
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 400), new Id(2, -100, -100));

        double targetAngel = 0;
        double attackerAngel = 0;
        int count = 6;
        int range = 8;
        target.getSyncItemArea().turnTo(targetAngel);

        List<AttackFormationItem> items = new ArrayList<AttackFormationItem>();
        for (int i = 0; i < count; i++) {
            items.add(new AttackFormationItem(syncBaseItem, range));
        }

        AttackFormation attackFormation = AttackFormationFactory.create(target.getSyncItemArea(), attackerAngel, items);
        List<SyncBaseItem> attackers = new ArrayList<SyncBaseItem>();
        attackers.add(target);
        debugService.drawSyncItemArea(target.getSyncItemArea(), Color.BLUE);
        debugService.drawPosition(target.getSyncItemArea().getPosition(), Color.BLUE);
        // debugService.drawSegments(((RoundedRectangleAttackFormation) attackFormation).getClockwiseTrack().getSegments());
        // debugService.drawSegments(((RoundedRectangleAttackFormation) attackFormation).getCounterClockwiseTrack().getSegments());

        while (attackFormation.hasNext()) {
            AttackFormationItem attackFormationItem = attackFormation.calculateNextEntry();
            SyncBaseItem attacker = createSyncBaseItem(TEST_ATTACK_ITEM_ID, attackFormationItem.getDestinationHint(), new Id(2, -100, -100));
            attacker.getSyncItemArea().turnTo(attackFormationItem.getDestinationAngel());
            if (attackFormationItem.isInRange()) {
//                Assert.assertFalse("Not in range", attacker.getSyncItemArea().getDistance(target.getSyncItemArea()) > range);
            } else {
//                Assert.assertTrue("In range", attacker.getSyncItemArea().getDistance(target.getSyncItemArea()) > range);
//                Assert.fail("Overbooked not expected");
            }
            debugService.drawSyncItemArea(attacker.getSyncItemArea(), Color.RED);
            debugService.drawPosition(attacker.getSyncItemArea().getPosition(), Color.BLUE);

            attackFormation.lastAccepted();
            attackers.add(attacker);
        }
        Assert.assertEquals(count + 1, attackers.size());
        displayOverlapping(attackers);
        debugService.waitForClose();
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

    private void displayOverlapping(List<SyncBaseItem> attackers) {
        for (int i = 0, attackersSize = attackers.size(); i < attackersSize; i++) {
            SyncBaseItem attackerToCheck = attackers.get(i);
            for (int j = i + 1; j < attackersSize; j++) {
                SyncBaseItem attacker = attackers.get(j);
                if (attackerToCheck.getSyncItemArea().contains(attacker)) {
                    System.out.println("Overlap: [" + i + "]" + attackerToCheck + " ------ [" + j + "]" + attacker);
                    debugService.drawSyncItemArea(attacker.getSyncItemArea(), Color.RED);
                    debugService.drawPosition(attacker.getSyncItemArea().getPosition(), Color.RED);
                    debugService.drawSyncItemArea(attackerToCheck.getSyncItemArea(), Color.RED);
                    debugService.drawPosition(attackerToCheck.getSyncItemArea().getPosition(), Color.RED);
                }
            }
        }
    }

    // TODO @Test

    @DirtiesContext
    public void testCircleFormationNoBlockingObject2() throws Exception {
        configureMinimalGame();
        SyncBaseItem target = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(500, 500), new Id(1, -100, -100));
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 400), new Id(1, -100, -100));

        List<AttackFormationItem> items = new ArrayList<AttackFormationItem>();
        for (int i = 0; i < 19; i++) {
            items.add(new AttackFormationItem(syncBaseItem, 200));
        }
        AttackFormation attackFormation = AttackFormationFactory.create(target.getSyncItemArea(), 0, items);

        debugService.drawSyncItemArea(target.getSyncItemArea(), Color.BLACK);
        debugService.drawSegments(((RoundedRectangleAttackFormation) attackFormation).getClockwiseTrack().getSegments());
        debugService.drawSegments(((RoundedRectangleAttackFormation) attackFormation).getCounterClockwiseTrack().getSegments());
        while (attackFormation.hasNext()) {
            AttackFormationItem attackFormationItem = attackFormation.calculateNextEntry();
            SyncBaseItem attacker = createSyncBaseItem(TEST_ATTACK_ITEM_ID, attackFormationItem.getDestinationHint(), new Id(1, -100, -100));
            attacker.getSyncItemArea().turnTo(target);
            if (attackFormationItem.isInRange()) {
                debugService.drawSyncItemArea(attacker.getSyncItemArea(), Color.RED);
                debugService.drawPosition(attackFormationItem.getDestinationHint(), Color.RED);
            } else {
                debugService.drawSyncItemArea(attacker.getSyncItemArea(), Color.GRAY);
                debugService.drawPosition(attackFormationItem.getDestinationHint(), Color.GRAY);
            }
            attackFormation.lastAccepted();
        }
        debugService.waitForClose();
    }

    // TODO @Test

    @DirtiesContext
    public void testCircleFormationNoBlockingObjectNeverOverbooked() throws Exception {
        configureMinimalGame();
        SyncBaseItem target = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(500, 500), new Id(1, -100, -100));
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 400), new Id(1, -100, -100));

        List<AttackFormationItem> items = new ArrayList<AttackFormationItem>();
        for (int i = 0; i < 20; i++) {
            items.add(new AttackFormationItem(syncBaseItem, 200));
        }

        for (double angel = 0.0; angel < MathHelper.ONE_RADIANT; angel += 0.001) {
            AttackFormation attackFormation = AttackFormationFactory.create(target.getSyncItemArea(), angel, items);
            while (attackFormation.hasNext()) {
                AttackFormationItem attackFormationItem = attackFormation.calculateNextEntry();
                SyncBaseItem attacker = createSyncBaseItem(TEST_ATTACK_ITEM_ID, attackFormationItem.getDestinationHint(), new Id(1, -100, -100));
                attacker.getSyncItemArea().turnTo(target);
                if (!attackFormationItem.isInRange()) {
                    Assert.fail("Overbooked not expected");
                }
                attackFormation.lastAccepted();
            }
        }
    }

    // TODO @Test

    @DirtiesContext
    public void testCircleFormationBlockingChannel() throws Exception {
        configureMinimalGame();

        Rectangle rectangle1 = new Rectangle(0, 0, 1000, 300);
        Rectangle rectangle2 = new Rectangle(0, 300, 300, 500);
        Rectangle rectangle3 = new Rectangle(500, 300, 300, 500);

        SyncBaseItem target = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 400), new Id(1, -100, -100));
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 400), new Id(1, -100, -100));

        List<AttackFormationItem> items = new ArrayList<AttackFormationItem>();
        items.add(new AttackFormationItem(syncBaseItem, 160));
        items.add(new AttackFormationItem(syncBaseItem, 160));
        items.add(new AttackFormationItem(syncBaseItem, 160));
        items.add(new AttackFormationItem(syncBaseItem, 160));
        items.add(new AttackFormationItem(syncBaseItem, 160));
        items.add(new AttackFormationItem(syncBaseItem, 160));
        items.add(new AttackFormationItem(syncBaseItem, 160));
        AttackFormation attackFormation = AttackFormationFactory.create(target.getSyncItemArea(), MathHelper.SOUTH - 0.35, items);

        List<Rectangle> expected = new ArrayList<Rectangle>();
        expected.add(new Rectangle(395, 504, 100, 100));
        expected.add(new Rectangle(352, 651, 100, 100));
        expected.add(new Rectangle(313, 790, 100, 100));
        expected.add(new Rectangle(74, 864, 100, 100));
        expected.add(new Rectangle(174, 906, 100, 100));
        expected.add(new Rectangle(274, 928, 100, 100));
        expected.add(new Rectangle(374, 932, 100, 100));

        List<Rectangle> rectangles = new ArrayList<Rectangle>();
        int count = 0;
        while (attackFormation.hasNext()) {
            AttackFormationItem attackFormationItem = attackFormation.calculateNextEntry();
            Assert.assertNotNull(attackFormationItem.getDestinationHint());
            Assert.fail("See above");
//            if (rectangle1.adjoinsEclusive(attackFormationItem.getRectangle())
//                    || rectangle2.adjoinsEclusive(attackFormationItem.getRectangle())
//                    || rectangle3.adjoinsEclusive(attackFormationItem.getRectangle())) {
//                continue;
//            }
            if (count < 1) {
                Assert.assertTrue(attackFormationItem.isInRange());
            } else {
                Assert.assertFalse(attackFormationItem.isInRange());
            }
            count++;

            attackFormation.lastAccepted();
            rectangles.add(Rectangle.generateRectangleFromMiddlePoint(attackFormationItem.getDestinationHint(), 100, 100));
        }
        for (int i = 0, rectanglesSize = rectangles.size(); i < rectanglesSize; i++) {
            Assert.assertEquals(expected.get(i), rectangles.get(i));
        }
        Assert.assertFalse(Rectangle.adjoinsExclusive(rectangles));
    }

    // TODO @Test

    @DirtiesContext
    public void testSetupDestinationHints1() throws Exception {
        configureComplexGame();

        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(1, -100, -100));

        List<AttackFormationItem> items = new ArrayList<AttackFormationItem>();
        items.add(new AttackFormationItem(syncBaseItem, 100));
        items.add(new AttackFormationItem(syncBaseItem, 100));
        items.add(new AttackFormationItem(syncBaseItem, 100));
        items.add(new AttackFormationItem(syncBaseItem, 100));
        items.add(new AttackFormationItem(syncBaseItem, 100));
        items.add(new AttackFormationItem(syncBaseItem, 100));
        items.add(new AttackFormationItem(syncBaseItem, 100));

        SyncItem target = createSyncResourceItem(TEST_RESOURCE_ITEM_ID, new Index(1000, 1000), new Id(1, -100, -100));

        collisionService.setupDestinationHints(target, items);

        for (AttackFormationItem item : items) {
            // TODO do some asserts
            System.out.println(item.getDestinationHint());
        }
    }


}