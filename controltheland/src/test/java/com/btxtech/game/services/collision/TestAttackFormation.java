package com.btxtech.game.services.collision;

import com.btxtech.game.jsre.client.common.Arc;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Line;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.AttackFormation;
import com.btxtech.game.jsre.common.gameengine.AttackFormationTrack;
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
        AttackFormationTrack track = new AttackFormationTrack(syncItemArea, 100, true);

        Assert.assertEquals(new Line(new Index(600, 350), new Index(400, 350)), track.getSegments().get(0));
        Assert.assertEquals(new Arc(new Index(400, 350), new Index(300, 450), new Index(400, 450)), track.getSegments().get(1));
        Assert.assertEquals(new Line(new Index(300, 450), new Index(300, 550)), track.getSegments().get(2));
        Assert.assertEquals(new Arc(new Index(300, 550), new Index(400, 650), new Index(400, 550)), track.getSegments().get(3));
        Assert.assertEquals(new Line(new Index(400, 650), new Index(600, 650)), track.getSegments().get(4));
        Assert.assertEquals(new Arc(new Index(600, 650), new Index(700, 550), new Index(600, 550)), track.getSegments().get(5));
        Assert.assertEquals(new Line(new Index(700, 550), new Index(700, 450)), track.getSegments().get(6));
        Assert.assertEquals(new Arc(new Index(700, 450), new Index(600, 350), new Index(600, 450)), track.getSegments().get(7));
    }

    @Test
    @DirtiesContext
    public void testAttackFormationTrackRotated() throws Exception {
        BoundingBox boundingBox = new BoundingBox(300, 200, 200, 100, 24);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(500, 500));
        syncItemArea.setAngel(MathHelper.gradToRad(80));
        AttackFormationTrack track = new AttackFormationTrack(syncItemArea, 100, true);

        Assert.assertEquals(new Line(new Index(370, 376), new Index(335, 573)), track.getSegments().get(0));
        Assert.assertEquals(new Arc(new Index(335, 573), new Index(416, 688), new Index(433, 590)), track.getSegments().get(1));
        Assert.assertEquals(new Line(new Index(416, 688), new Index(515, 705)), track.getSegments().get(2));
        Assert.assertEquals(new Arc(new Index(515, 705), new Index(630, 624), new Index(532, 607)), track.getSegments().get(3));
        Assert.assertEquals(new Line(new Index(630, 624), new Index(665, 427)), track.getSegments().get(4));
        Assert.assertEquals(new Arc(new Index(665, 427), new Index(584, 312), new Index(567, 410)), track.getSegments().get(5));
        Assert.assertEquals(new Line(new Index(584, 312), new Index(485, 295)), track.getSegments().get(6));
        Assert.assertEquals(new Arc(new Index(485, 295), new Index(370, 376), new Index(468, 393)), track.getSegments().get(7));
    }

    @Test
    @DirtiesContext
    public void testAttackFormationTrack1Attacker() throws Exception {
        BoundingBox boundingBox = new BoundingBox(300, 200, 200, 100, 24);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(500, 500));
        AttackFormationTrack track = new AttackFormationTrack(syncItemArea, 100, true);
        Index position = track.start(0, 100);
        Assert.assertEquals(new Index(500, 250), position);
    }

    @Test
    @DirtiesContext
    public void testAttackFormationTrack1AttackerRot() throws Exception {
        BoundingBox boundingBox = new BoundingBox(300, 200, 200, 100, 24);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(500, 500));
        syncItemArea.setAngel(MathHelper.gradToRad(45));
        AttackFormationTrack track = new AttackFormationTrack(syncItemArea, 100, true);
        Index position = track.start(0, 200);
        Assert.assertEquals(new Index(430, 112), position);
    }

    // TODO @Test
    @DirtiesContext
    public void testAttackFormationTrack5AttackerRot() throws Exception {
        BoundingBox boundingBox = new BoundingBox(300, 200, 200, 100, 24);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(500, 500));
        AttackFormationTrack track = new AttackFormationTrack(syncItemArea, 100, true);
        Assert.assertEquals(new Index(500, 250), track.start(0, 100));
        Assert.assertEquals(new Index(400, 250), track.getNextPoint(100, 100, 200));
        Assert.assertEquals(new Index(260, 350), track.getNextPoint(100, 100, 200));
        Assert.assertEquals(new Index(200, 508), track.getNextPoint(100, 100, 200));
        Assert.assertEquals(new Index(271, 661), track.getNextPoint(100, 100, 200));
        Assert.assertEquals(new Index(417, 750), track.getNextPoint(100, 100, 200));
        Assert.assertEquals(new Index(517, 750), track.getNextPoint(100, 100, 200));
        Assert.assertEquals(new Index(682, 708), track.getNextPoint(100, 100, 200));
        Assert.assertEquals(new Index(752, 638), track.getNextPoint(100, 100, 200));
        Assert.assertEquals(new Index(800, 475), track.getNextPoint(100, 100, 200));
    }

    // TODO @Test
    @DirtiesContext
    public void testAttackFormationTrack5AttackerRotCounter() throws Exception {
        BoundingBox boundingBox = new BoundingBox(300, 200, 200, 100, 24);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(500, 500));
        AttackFormationTrack track = new AttackFormationTrack(syncItemArea, 100, false);
        Assert.assertEquals(new Index(500, 250), track.start(0, 100));
        Assert.assertEquals(new Index(670, 280), track.getNextPoint(100, 100, 200));
        Assert.assertEquals(new Index(740, 350), track.getNextPoint(100, 100, 200));
        Assert.assertEquals(new Index(800, 508), track.getNextPoint(100, 100, 200));
        Assert.assertEquals(new Index(729, 661), track.getNextPoint(100, 100, 200));
        Assert.assertEquals(new Index(583, 750), track.getNextPoint(100, 100, 200));
        Assert.assertEquals(new Index(483, 750), track.getNextPoint(100, 100, 200));
        Assert.assertEquals(new Index(318, 708), track.getNextPoint(100, 100, 200));
        Assert.assertEquals(new Index(248, 638), track.getNextPoint(100, 100, 200));
        Assert.assertEquals(new Index(200, 475), track.getNextPoint(100, 100, 200));
    }

    @Test
    @DirtiesContext
    public void testAttackFormationTrackDifferentAngel() throws Exception {
        BoundingBox boundingBox = new BoundingBox(300, 200, 200, 100, 24);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(500, 500));
        for (double angel = 0.0; angel < MathHelper.ONE_RADIANT; angel += MathHelper.gradToRad(0.01)) {
            AttackFormationTrack track = new AttackFormationTrack(syncItemArea, 100, false);
            Assert.assertNotNull(track.start(angel, 100));
        }
    }

    @Test
    @DirtiesContext
    public void testCircleFormationNoBlockingObject() throws Throwable {
        configureMinimalGame();

        ItemType targetItemType = itemService.getItemType(TEST_SIMPLE_BUILDING_ID);
        targetItemType.setBoundingBox(new BoundingBox(100, 100, 200, 80, 1));
        SyncBaseItem target = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(1500, 1500), new Id(1, -100, -100));
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 400), new Id(2, -100, -100));
        double targetAngel = 0;
        double attackerAngel = 0;
        try {
            for (targetAngel = 0.0; targetAngel <= MathHelper.ONE_RADIANT; targetAngel += 0.05) {
                System.out.println("targetAngel: " + MathHelper.radToGrad(targetAngel));
                target.getSyncItemArea().turnTo(targetAngel);

                for (attackerAngel = 0.0; attackerAngel <= MathHelper.ONE_RADIANT; attackerAngel += 0.05) {
                    List<AttackFormation.AttackFormationItem> items = new ArrayList<AttackFormation.AttackFormationItem>();
                    final int count = 19;
                    for (int i = 0; i < count; i++) {
                        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, 200));
                    }

                    AttackFormation attackFormation = new AttackFormation(target.getSyncItemArea(), attackerAngel, items);
                    List<SyncBaseItem> attackers = new ArrayList<SyncBaseItem>();
                    attackers.add(target);
                    while (attackFormation.hasNext()) {
                        AttackFormation.AttackFormationItem attackFormationItem = attackFormation.calculateNextEntry();
                        SyncBaseItem attacker = createSyncBaseItem(TEST_ATTACK_ITEM_ID, attackFormationItem.getDestinationHint(), new Id(2, -100, -100));
                        attacker.getSyncItemArea().turnTo(attackFormationItem.getDestinationAngel());
                        if (attackFormationItem.isInRange()) {
                            Assert.assertFalse("Not in range", attacker.getSyncItemArea().getDistance(target.getSyncItemArea()) > 200);
                        } else {
                            Assert.assertTrue("In range", attacker.getSyncItemArea().getDistance(target.getSyncItemArea()) > 200);
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
//        while (attackFormation.hasNext()) {
//            attackFormationItem = attackFormation.calculateNextEntry();
//            System.out.println("attackFormationItem = attackFormation.calculateNextEntry();");
//            System.out.println("Assert.assertEquals(new Index(" + attackFormationItem.getDestinationHint().getX() + "," + attackFormationItem.getDestinationHint().getY() + "), attackFormationItem.getDestinationHint());");
//            System.out.println("Assert.assertTrue(attackFormation.hasNext());");
//            System.out.println("attackFormation.lastAccepted();");
//            System.out.println();
//            attackFormation.lastAccepted();
//
//        }
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

        List<AttackFormation.AttackFormationItem> items = new ArrayList<AttackFormation.AttackFormationItem>();
        for (int i = 0; i < 19; i++) {
            items.add(new AttackFormation.AttackFormationItem(syncBaseItem, 200));
        }
        AttackFormation attackFormation = new AttackFormation(target.getSyncItemArea(), 0, items);

        debugService.drawSyncItemArea(target.getSyncItemArea(), Color.BLACK);
        debugService.drawSegments(attackFormation.getClockwiseTrack().getSegments());
        debugService.drawSegments(attackFormation.getCounterClockwiseTrack().getSegments());
        while (attackFormation.hasNext()) {
            AttackFormation.AttackFormationItem attackFormationItem = attackFormation.calculateNextEntry();
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

        List<AttackFormation.AttackFormationItem> items = new ArrayList<AttackFormation.AttackFormationItem>();
        for (int i = 0; i < 20; i++) {
            items.add(new AttackFormation.AttackFormationItem(syncBaseItem, 200));
        }

        for (double angel = 0.0; angel < MathHelper.ONE_RADIANT; angel += 0.001) {
            AttackFormation attackFormation = new AttackFormation(target.getSyncItemArea(), angel, items);
            while (attackFormation.hasNext()) {
                AttackFormation.AttackFormationItem attackFormationItem = attackFormation.calculateNextEntry();
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
    public void testCircleFormationBlocking() throws Exception {
        configureMinimalGame();

        SyncBaseItem target = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 400), new Id(1, -100, -100));
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 400), new Id(1, -100, -100));

        List<AttackFormation.AttackFormationItem> items = new ArrayList<AttackFormation.AttackFormationItem>();
        int range = 200;
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, range));
        AttackFormation attackFormation = new AttackFormation(target.getSyncItemArea(), MathHelper.SOUTH_EAST, items);

        List<Rectangle> expected = new ArrayList<Rectangle>();
        expected.add(new Rectangle(491, 491, 100, 100));
        expected.add(new Rectangle(391, 546, 100, 100));
        expected.add(new Rectangle(546, 391, 100, 100));
        expected.add(new Rectangle(450, 676, 100, 100));
        expected.add(new Rectangle(550, 626, 100, 100));
        expected.add(new Rectangle(642, 526, 100, 100));
        expected.add(new Rectangle(682, 426, 100, 100));
        expected.add(new Rectangle(690, 326, 100, 100));
        expected.add(new Rectangle(450, 822, 100, 100));
        expected.add(new Rectangle(550, 789, 100, 100));
        expected.add(new Rectangle(650, 727, 100, 100));
        expected.add(new Rectangle(744, 627, 100, 100));
        expected.add(new Rectangle(798, 527, 100, 100));
        expected.add(new Rectangle(826, 427, 100, 100));
        expected.add(new Rectangle(831, 327, 100, 100));
        expected.add(new Rectangle(450, 965, 100, 100));
        expected.add(new Rectangle(550, 940, 100, 100));
        expected.add(new Rectangle(650, 896, 100, 100));
        expected.add(new Rectangle(750, 828, 100, 100));
        expected.add(new Rectangle(846, 728, 100, 100));
        expected.add(new Rectangle(908, 628, 100, 100));
        expected.add(new Rectangle(947, 528, 100, 100));
        expected.add(new Rectangle(968, 428, 100, 100));
        expected.add(new Rectangle(973, 328, 100, 100));
        expected.add(new Rectangle(450, 1107, 100, 100));
        expected.add(new Rectangle(550, 1087, 100, 100));
        expected.add(new Rectangle(650, 1053, 100, 100));
        expected.add(new Rectangle(750, 1001, 100, 100));
        expected.add(new Rectangle(850, 928, 100, 100));
        expected.add(new Rectangle(946, 828, 100, 100));
        expected.add(new Rectangle(1014, 728, 100, 100));
        expected.add(new Rectangle(1062, 628, 100, 100));
        expected.add(new Rectangle(1093, 528, 100, 100));
        expected.add(new Rectangle(1110, 428, 100, 100));
        expected.add(new Rectangle(1114, 328, 100, 100));
        expected.add(new Rectangle(450, 1249, 100, 100));
        expected.add(new Rectangle(550, 1233, 100, 100));
        expected.add(new Rectangle(650, 1204, 100, 100));
        expected.add(new Rectangle(750, 1162, 100, 100));
        expected.add(new Rectangle(850, 1104, 100, 100));
        expected.add(new Rectangle(950, 1028, 100, 100));
        expected.add(new Rectangle(1047, 928, 100, 100));

        int count = 0;
        List<Rectangle> rectangles = new ArrayList<Rectangle>();
        //rectangles.add(new Rectangle(0, 0, 1000, 300));
        //rectangles.add(new Rectangle(0, 300, 300, 1000));
        rectangles.add(new Rectangle(100, 300, 100, 200));
        rectangles.add(new Rectangle(200, 600, 100, 100));
        rectangles.add(new Rectangle(340, 110, 100, 130));
        debugService.drawSyncItemArea(target.getSyncItemArea(), Color.BLACK);
        debugService.drawRectangles(rectangles);
        while (attackFormation.hasNext()) {
            AttackFormation.AttackFormationItem attackFormationItem = attackFormation.calculateNextEntry();
            //////
            SyncBaseItem attacker = createSyncBaseItem(TEST_ATTACK_ITEM_ID, attackFormationItem.getDestinationHint(), new Id(1, -100, -100));
            attacker.getSyncItemArea().turnTo(target);

            boolean collision = false;
            for (Rectangle rectangle : rectangles) {
                if (attacker.getSyncItemArea().contains(rectangle)) {
                    collision = true;
                    break;
                }
            }
            if (collision) {
                continue;
            }

            debugService.drawPosition(attackFormationItem.getDestinationHint(), Color.BLUE);
            //debugService.drawSyncItemArea(attacker.getSyncItemArea());
            //////

            // TODO    Assert.assertNotNull(attackFormationItem.getDestinationHint());
//     TODO       if (rectangle1.adjoinsEclusive(attackFormationItem.getRectangle()) || rectangle2.adjoinsEclusive(attackFormationItem.getRectangle())) {
//                continue;
//            }
            // TODO if (count < 3) {
            // TODO     Assert.assertTrue(attackFormationItem.isInRange());
            // TODO } else {
            // TODO     Assert.assertFalse(attackFormationItem.isInRange());
            // TODO }
            // TODO count++;
            attackFormation.lastAccepted();
            // TODO rectangles.add(Rectangle.generateRectangleFromMiddlePoint(attackFormationItem.getDestinationHint(), 100, 100));
        }
        // TODO for (int i = 0, rectanglesSize = rectangles.size(); i < rectanglesSize; i++) {
        // TODO     Assert.assertEquals(expected.get(i), rectangles.get(i));
        // TODO }
        // TODO Assert.assertFalse(Rectangle.adjoinsExclusive(rectangles));
        debugService.waitForClose();
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

        List<AttackFormation.AttackFormationItem> items = new ArrayList<AttackFormation.AttackFormationItem>();
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, 160));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, 160));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, 160));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, 160));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, 160));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, 160));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, 160));
        AttackFormation attackFormation = new AttackFormation(target.getSyncItemArea(), MathHelper.SOUTH - 0.35, items);

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
            AttackFormation.AttackFormationItem attackFormationItem = attackFormation.calculateNextEntry();
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

        List<AttackFormation.AttackFormationItem> items = new ArrayList<AttackFormation.AttackFormationItem>();
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, 100));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, 100));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, 100));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, 100));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, 100));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, 100));
        items.add(new AttackFormation.AttackFormationItem(syncBaseItem, 100));

        SyncItem target = createSyncResourceItem(TEST_RESOURCE_ITEM_ID, new Index(1000, 1000), new Id(1, -100, -100));

        collisionService.setupDestinationHints(target, items);

        for (AttackFormation.AttackFormationItem item : items) {
            // TODO do some asserts
            System.out.println(item.getDestinationHint());
        }
    }


}
