package com.btxtech.game.services.inventory.impl;

import com.btxtech.game.services.planet.db.DbBoxRegion;
import junit.framework.Assert;
import org.junit.Test;

/**
 * User: beat
 * Date: 18.05.12
 * Time: 12:56
 */
public class TestBoxRegion {

    @Test
    public void testNextDropTime1() {
        DbBoxRegion dbBoxRegion = new DbBoxRegion();
        dbBoxRegion.setMinInterval(100);
        dbBoxRegion.setMaxInterval(200);
        for (int i = 0; i < 1000; i++) {
            BoxRegion boxRegion = new BoxRegion(dbBoxRegion);
            long nextDropTime = boxRegion.getNextDropTime() - System.currentTimeMillis();
            Assert.assertTrue("nextDropTime: " + nextDropTime, nextDropTime > 90);
            Assert.assertTrue("nextDropTime: " + nextDropTime, nextDropTime < 210);
        }
    }

    @Test
    public void testNextDropTime2() {
        DbBoxRegion dbBoxRegion = new DbBoxRegion();
        dbBoxRegion.setMinInterval(300);
        dbBoxRegion.setMaxInterval(300);
        for (int i = 0; i < 1000; i++) {
            BoxRegion boxRegion = new BoxRegion(dbBoxRegion);
            long nextDropTime = boxRegion.getNextDropTime() - System.currentTimeMillis();
            Assert.assertTrue("nextDropTime: " + nextDropTime, nextDropTime > 290);
            Assert.assertTrue("nextDropTime: " + nextDropTime, nextDropTime < 310);
        }
    }

    @Test
    public void testInvalidTime() {
        DbBoxRegion dbBoxRegion = new DbBoxRegion();
        dbBoxRegion.setName("Test111");
        dbBoxRegion.setMinInterval(200);
        dbBoxRegion.setMaxInterval(100);
        try {
            new BoxRegion(dbBoxRegion);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    public void isDropTimeReached() throws InterruptedException {
        DbBoxRegion dbBoxRegion = new DbBoxRegion();
        dbBoxRegion.setMinInterval(50);
        dbBoxRegion.setMaxInterval(100);
        BoxRegion boxRegion = new BoxRegion(dbBoxRegion);
        for (int i = 0; i < 50; i++) {
            boxRegion.setupNextDropTime();
            long nextDropTime = boxRegion.getNextDropTime() - System.currentTimeMillis();
            Assert.assertFalse(boxRegion.isDropTimeReached());
            Thread.sleep(nextDropTime + 10);
            Assert.assertTrue(boxRegion.isDropTimeReached());
        }
    }

    @Test
    public void isDropTimeReachedSameTime() throws InterruptedException {
        DbBoxRegion dbBoxRegion = new DbBoxRegion();
        dbBoxRegion.setMinInterval(100);
        dbBoxRegion.setMaxInterval(100);
        for (int i = 0; i < 100; i++) {
            BoxRegion boxRegion = new BoxRegion(dbBoxRegion);
            Assert.assertFalse(boxRegion.isDropTimeReached());
            Thread.sleep(100);
            Assert.assertTrue(boxRegion.isDropTimeReached());
        }
    }

}
