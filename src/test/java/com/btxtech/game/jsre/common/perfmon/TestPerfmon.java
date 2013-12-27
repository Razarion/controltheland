package com.btxtech.game.jsre.common.perfmon;

import com.btxtech.game.services.AbstractServiceTest;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * User: beat
 * Date: 26.07.12
 * Time: 15:10
 */
public class TestPerfmon {

    @Test
    public void testSimple1() throws Exception {
        resetSingleton();
        Assert.assertTrue(Perfmon.getInstance().getSummary().isEmpty());
        Thread.sleep(250);
        Perfmon.getInstance().onEntered(PerfmonEnum.ACTION_HANDLER);
        Thread.sleep(250);
        Perfmon.getInstance().onLeft(PerfmonEnum.ACTION_HANDLER);
        Assert.assertEquals(1, Perfmon.getInstance().getSummary().size());
        assertInRange(250, 300, Perfmon.getInstance().getSummary().get(PerfmonEnum.ACTION_HANDLER));
        assertInRange(500, 600, Perfmon.getInstance().getTotalTime());
    }

    @Test
    public void testSimple2() throws Exception {
        resetSingleton();
        Assert.assertTrue(Perfmon.getInstance().getSummary().isEmpty());
        Perfmon.getInstance().onEntered(PerfmonEnum.ACTION_HANDLER);
        Thread.sleep(100);
        Perfmon.getInstance().onLeft(PerfmonEnum.ACTION_HANDLER);
        Perfmon.getInstance().onEntered(PerfmonEnum.ACTION_HANDLER);
        Thread.sleep(200);
        Perfmon.getInstance().onLeft(PerfmonEnum.ACTION_HANDLER);
        Perfmon.getInstance().onEntered(PerfmonEnum.ACTION_HANDLER);
        Thread.sleep(100);
        Perfmon.getInstance().onLeft(PerfmonEnum.ACTION_HANDLER);
        Assert.assertEquals(1, Perfmon.getInstance().getSummary().size());
        assertInRange(400, 500, Perfmon.getInstance().getSummary().get(PerfmonEnum.ACTION_HANDLER));
        assertInRange(400, 500, Perfmon.getInstance().getTotalTime());
    }

    @Test
    public void testMulti1() throws Exception {
        resetSingleton();
        Assert.assertTrue(Perfmon.getInstance().getSummary().isEmpty());
        Perfmon.getInstance().onEntered(PerfmonEnum.PACKET_INFO_HANDLING);
        Thread.sleep(100);
        Perfmon.getInstance().onLeft(PerfmonEnum.PACKET_INFO_HANDLING);
        Perfmon.getInstance().onEntered(PerfmonEnum.ACTION_HANDLER);
        Thread.sleep(200);
        Perfmon.getInstance().onLeft(PerfmonEnum.ACTION_HANDLER);
        Perfmon.getInstance().onEntered(PerfmonEnum.PACKET_INFO_HANDLING);
        Thread.sleep(300);
        Perfmon.getInstance().onLeft(PerfmonEnum.PACKET_INFO_HANDLING);
        Assert.assertEquals(2, Perfmon.getInstance().getSummary().size());
        assertInRange(200, 300, Perfmon.getInstance().getSummary().get(PerfmonEnum.ACTION_HANDLER));
        assertInRange(400, 500, Perfmon.getInstance().getSummary().get(PerfmonEnum.PACKET_INFO_HANDLING));
        assertInRange(600, 700, Perfmon.getInstance().getTotalTime());
    }

    @Test
    public void testMulti2() throws Exception {
        resetSingleton();
        Assert.assertTrue(Perfmon.getInstance().getSummary().isEmpty());
        Perfmon.getInstance().onEntered(PerfmonEnum.ACTION_HANDLER);
        Perfmon.getInstance().onEntered(PerfmonEnum.PACKET_INFO_HANDLING);
        Thread.sleep(100);
        Perfmon.getInstance().onLeft(PerfmonEnum.ACTION_HANDLER);
        Thread.sleep(200);
        Perfmon.getInstance().onLeft(PerfmonEnum.PACKET_INFO_HANDLING);

        Perfmon.getInstance().onEntered(PerfmonEnum.ACTION_HANDLER);
        Perfmon.getInstance().onEntered(PerfmonEnum.PACKET_INFO_HANDLING);
        Thread.sleep(200);
        Perfmon.getInstance().onLeft(PerfmonEnum.PACKET_INFO_HANDLING);
        Thread.sleep(300);
        Perfmon.getInstance().onLeft(PerfmonEnum.ACTION_HANDLER);

        Assert.assertEquals(2, Perfmon.getInstance().getSummary().size());
        assertInRange(600, 700, Perfmon.getInstance().getSummary().get(PerfmonEnum.ACTION_HANDLER));
        assertInRange(500, 600, Perfmon.getInstance().getSummary().get(PerfmonEnum.PACKET_INFO_HANDLING));
        assertInRange(800, 900, Perfmon.getInstance().getTotalTime());
    }

    @Test
    public void errors() throws Exception {
        resetSingleton();
        Assert.assertTrue(Perfmon.getInstance().getSummary().isEmpty());
        Perfmon.getInstance().onLeft(PerfmonEnum.ACTION_HANDLER);
        Assert.assertTrue(Perfmon.getInstance().getSummary().isEmpty());
        Perfmon.getInstance().onLeft(PerfmonEnum.PACKET_INFO_HANDLING);
        Assert.assertTrue(Perfmon.getInstance().getSummary().isEmpty());

        Perfmon.getInstance().onEntered(PerfmonEnum.PACKET_INFO_HANDLING);
        Thread.sleep(100);
        Perfmon.getInstance().onEntered(PerfmonEnum.PACKET_INFO_HANDLING);
        Thread.sleep(100);
        Perfmon.getInstance().onEntered(PerfmonEnum.PACKET_INFO_HANDLING);
        Thread.sleep(100);
        Perfmon.getInstance().onEntered(PerfmonEnum.PACKET_INFO_HANDLING);
        Thread.sleep(100);
        Perfmon.getInstance().onEntered(PerfmonEnum.PACKET_INFO_HANDLING);
        Thread.sleep(100);
        Perfmon.getInstance().onLeft(PerfmonEnum.PACKET_INFO_HANDLING);
        Assert.assertEquals(1, Perfmon.getInstance().getSummary().size());
        assertInRange(100, 200, Perfmon.getInstance().getSummary().get(PerfmonEnum.PACKET_INFO_HANDLING));
    }

    private void assertInRange(int min, int max, int value) {
        Assert.assertTrue("Value (" + value + ") expected to be bigger or equals than " + min, value >= min);
        Assert.assertTrue("Value (" + value + ") expected to be smaller or equals than " + max, value <= max);
    }

    private void resetSingleton() throws Exception {
        Map map = (Map) AbstractServiceTest.getPrivateField(Perfmon.class, Perfmon.getInstance(), "enterTimes");
        map.clear();
        map = (Map) AbstractServiceTest.getPrivateField(Perfmon.class, Perfmon.getInstance(), "workTimes");
        map.clear();
        AbstractServiceTest.setPrivateField(Perfmon.class, Perfmon.getInstance(), "startTime", (int)System.currentTimeMillis());
    }
}
