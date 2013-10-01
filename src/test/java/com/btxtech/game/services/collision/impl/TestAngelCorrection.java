package com.btxtech.game.services.collision.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Line;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.services.collision.impl.AngelCorrection;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.debug.DebugService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.awt.*;
import java.util.List;

/**
 * User: beat
 * Date: 02.05.2011
 * Time: 13:10:21
 */
public class TestAngelCorrection extends AbstractServiceTest {
    @Autowired
    private DebugService debugService;

    @Test
    @DirtiesContext
    public void toItemAngelSameTileLoop1() throws Throwable {
        BoundingBox boundingBox = new BoundingBox(0, ANGELS_24);
        Index start = new Index(1000, 1000);
        Index destination = new Index(3000, 3000);
        Index middle = new Index(2000, 2000);

        try {
            for (double angel = 0.0; angel < MathHelper.ONE_RADIANT; angel += 0.0001) {
                System.out.println("angel: " + MathHelper.radToGrad(angel));
                List<Index> path = AngelCorrection.toItemAngelSameTile(start, destination, boundingBox);
                assertPathAngels(path, boundingBox);
                start = start.rotateCounterClock(middle, angel);
                destination = destination.rotateCounterClock(middle, angel);
            }
        } catch (Throwable t) {
            System.out.println("start: " + start + " destination: " + destination);
            throw t;
        }
    }

    @Test
    @DirtiesContext
    public void toItemAngelSameTileLoop2() throws Throwable {
        BoundingBox boundingBox = new BoundingBox(0, ANGELS_24_2);
        Index start = new Index(1000, 1000);
        Index destination = new Index(3000, 3000);
        Index middle = new Index(2000, 2000);

        try {
            for (double angel = 0.0; angel <= MathHelper.ONE_RADIANT; angel += 0.0001) {
                System.out.println("angel: " + MathHelper.radToGrad(angel));
                List<Index> path = AngelCorrection.toItemAngelSameTile(start, destination, boundingBox);
                assertPathAngels(path, boundingBox);
                start = start.rotateCounterClock(middle, angel);
                destination = destination.rotateCounterClock(middle, angel);
            }
        } catch (Throwable t) {
            System.out.println("start: " + start + " destination: " + destination);
            throw t;
        }
    }

    @Test
    @DirtiesContext
    public void toItemAngelSameTile1() throws Throwable {
        BoundingBox boundingBox = new BoundingBox(0, ANGELS_24_2);
        Index start = new Index(2538, 1311);
        Index destination = new Index(841, 1348);

        List<Index> path = AngelCorrection.toItemAngelSameTile(start, destination, boundingBox);
        assertPathAngels(path, boundingBox);
    }

    private void assertPathAngels(List<Index> path, BoundingBox boundingBox) {
        Index start = path.get(0);
        Index destination = path.get(path.size() - 1);
        Index point1 = null;
        Line line = new Line(start, destination);
        for (Index index : path) {
            if (point1 != null) {
                Assert.assertFalse("Points are equals:" + point1, point1.equals(index));
                double angel = point1.getAngleToNord(index);
                double allowedAngel = boundingBox.getAllowedAngel(angel);
                double delta = MathHelper.getAngel(angel, allowedAngel);
                double distance = point1.getDistanceDouble(index);
                if (delta > MathHelper.gradToRad(0.1)) {
                    Index allowedPoint = point1.getPointFromAngelToNord(allowedAngel, distance);
                    if (allowedPoint.getDistanceDouble(index) > 1.5) {
                        System.out.println("distance: " + distance);
                        System.out.println("Allowed Point:" + allowedPoint + " actual:" + index);
                        System.out.println(point1 + " to " + index);
                        System.out.println("Distance: " + point1.getDistanceDouble(index));
                        Assert.fail("Delta too big: " + MathHelper.radToGrad(delta));
                    }
                }

                if (line.getShortestDistance(index) > 12) {
                    Assert.fail("Distance too big:" + line.getShortestDistance(index));
                }
            }
            point1 = index;
        }
    }

    private void displayPath(List<Index> path, Index start, Index destination) {
        Index point1 = null;
        boolean color = false;
        for (Index index : path) {
            if (point1 != null) {
                if (color) {
                    debugService.drawLine(new Line(point1, index), Color.RED);
                } else {
                    debugService.drawLine(new Line(point1, index), Color.BLUE);

                }
                color = !color;
            }
            point1 = index;
        }
        debugService.drawPosition(start, Color.GREEN);
        debugService.drawPosition(destination, Color.GREEN);
        debugService.drawLine(new Line(start, destination), Color.GREEN);
        debugService.waitForClose();
    }
}
