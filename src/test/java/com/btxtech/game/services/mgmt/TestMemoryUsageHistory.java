package com.btxtech.game.services.mgmt;

import com.btxtech.game.services.common.DateUtil;
import com.btxtech.game.services.mgmt.impl.MemoryUsageContainer;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * User: beat
 * Date: 06.03.2012
 * Time: 15:01:39
 */
public class TestMemoryUsageHistory {
    @Test
    public void testMemoryUsageContainer() {
        MemoryUsageContainer container = new MemoryUsageContainer(10);
        container.addSample(new Date(10000), 1, 2, 3, 4);
        container.addSample(new Date(20000), 2, 3, 4, 5);
        container.addSample(new Date(30000), 3, 4, 5, 6);
        container.addSample(new Date(40000), 5, 6, 7, 8);

        MemoryUsageHistory memoryUsageHistory = container.generateMemoryUsageHistory();
        List<String> dateStrings = memoryUsageHistory.getSignificantDates(4, new SimpleDateFormat(DateUtil.TIME_FORMAT_STRING));
        Assert.assertEquals(4, dateStrings.size());
        Assert.assertEquals("01:00:10", dateStrings.get(0));
        Assert.assertEquals("01:00:20", dateStrings.get(1));
        Assert.assertEquals("01:00:30", dateStrings.get(2));
        Assert.assertEquals("01:00:40", dateStrings.get(3));

        dateStrings = memoryUsageHistory.getSignificantDates(3, new SimpleDateFormat(DateUtil.TIME_FORMAT_STRING));
        Assert.assertEquals(3, dateStrings.size());
        Assert.assertEquals("01:00:10", dateStrings.get(0));
        Assert.assertEquals("01:00:25", dateStrings.get(1));
        Assert.assertEquals("01:00:40", dateStrings.get(2));

        dateStrings = memoryUsageHistory.getSignificantDates(6, new SimpleDateFormat(DateUtil.TIME_FORMAT_STRING));
        Assert.assertEquals(6, dateStrings.size());
        Assert.assertEquals("01:00:10", dateStrings.get(0));
        Assert.assertEquals("01:00:16", dateStrings.get(1));
        Assert.assertEquals("01:00:22", dateStrings.get(2));
        Assert.assertEquals("01:00:28", dateStrings.get(3));
        Assert.assertEquals("01:00:34", dateStrings.get(4));
        Assert.assertEquals("01:00:40", dateStrings.get(5));

        List<String> valueStrings = memoryUsageHistory.getSignificantValues(4);
        Assert.assertEquals(4, valueStrings.size());
        Assert.assertEquals("0", valueStrings.get(0));
        Assert.assertEquals("2", valueStrings.get(1));
        Assert.assertEquals("4", valueStrings.get(2));
        Assert.assertEquals("8", valueStrings.get(3));

        valueStrings = memoryUsageHistory.getSignificantValues(2);
        Assert.assertEquals(2, valueStrings.size());
        Assert.assertEquals("0", valueStrings.get(0));
        Assert.assertEquals("8", valueStrings.get(1));

        valueStrings = memoryUsageHistory.getSignificantValues(5);
        Assert.assertEquals(5, valueStrings.size());
        Assert.assertEquals("0", valueStrings.get(0));
        Assert.assertEquals("2", valueStrings.get(1));
        Assert.assertEquals("4", valueStrings.get(2));
        Assert.assertEquals("6", valueStrings.get(3));
        Assert.assertEquals("8", valueStrings.get(4));

        Assert.assertEquals(4, memoryUsageHistory.getInits().size());
        Assert.assertEquals(12, (int)memoryUsageHistory.getInits().get(0));
        Assert.assertEquals(25, (int)memoryUsageHistory.getInits().get(1));
        Assert.assertEquals(37, (int)memoryUsageHistory.getInits().get(2));
        Assert.assertEquals(62, (int)memoryUsageHistory.getInits().get(3));

        Assert.assertEquals(4, memoryUsageHistory.getUseds().size());
        Assert.assertEquals(25, (int)memoryUsageHistory.getUseds().get(0));
        Assert.assertEquals(37, (int)memoryUsageHistory.getUseds().get(1));
        Assert.assertEquals(50, (int)memoryUsageHistory.getUseds().get(2));
        Assert.assertEquals(75, (int)memoryUsageHistory.getUseds().get(3));

        Assert.assertEquals(4, memoryUsageHistory.getCommitteds().size());
        Assert.assertEquals(37, (int)memoryUsageHistory.getCommitteds().get(0));
        Assert.assertEquals(50, (int)memoryUsageHistory.getCommitteds().get(1));
        Assert.assertEquals(62, (int)memoryUsageHistory.getCommitteds().get(2));
        Assert.assertEquals(87, (int)memoryUsageHistory.getCommitteds().get(3));

        Assert.assertEquals(4, memoryUsageHistory.getMaxs().size());
        Assert.assertEquals(50, (int)memoryUsageHistory.getMaxs().get(0));
        Assert.assertEquals(62, (int)memoryUsageHistory.getMaxs().get(1));
        Assert.assertEquals(75, (int)memoryUsageHistory.getMaxs().get(2));
        Assert.assertEquals(100, (int)memoryUsageHistory.getMaxs().get(3));
    }

    @Test
    public void testMemoryUsageContainer1Entry() {
        MemoryUsageContainer container = new MemoryUsageContainer(10);
        container.addSample(new Date(10000), 1, 2, 3, 4);

        MemoryUsageHistory memoryUsageHistory = container.generateMemoryUsageHistory();
        List<String> dateStrings = memoryUsageHistory.getSignificantDates(4, new SimpleDateFormat(DateUtil.TIME_FORMAT_STRING));
        Assert.assertEquals(4, dateStrings.size());
        Assert.assertEquals("01:00:10", dateStrings.get(0));
        Assert.assertEquals("?", dateStrings.get(1));
        Assert.assertEquals("?", dateStrings.get(2));
        Assert.assertEquals("?", dateStrings.get(3));

        dateStrings = memoryUsageHistory.getSignificantDates(3, new SimpleDateFormat(DateUtil.TIME_FORMAT_STRING));
        Assert.assertEquals(3, dateStrings.size());
        Assert.assertEquals("01:00:10", dateStrings.get(0));
        Assert.assertEquals("?", dateStrings.get(1));
        Assert.assertEquals("?", dateStrings.get(2));

        dateStrings = memoryUsageHistory.getSignificantDates(6, new SimpleDateFormat(DateUtil.TIME_FORMAT_STRING));
        Assert.assertEquals(6, dateStrings.size());
        Assert.assertEquals("01:00:10", dateStrings.get(0));
        Assert.assertEquals("?", dateStrings.get(1));
        Assert.assertEquals("?", dateStrings.get(2));
        Assert.assertEquals("?", dateStrings.get(3));
        Assert.assertEquals("?", dateStrings.get(4));
        Assert.assertEquals("?", dateStrings.get(5));
    }

    @Test
    public void testMemoryUsageContainer0Entries() {
        MemoryUsageContainer container = new MemoryUsageContainer(10);

        MemoryUsageHistory memoryUsageHistory = container.generateMemoryUsageHistory();
        List<String> dateStrings = memoryUsageHistory.getSignificantDates(4, new SimpleDateFormat(DateUtil.TIME_FORMAT_STRING));
        Assert.assertEquals(4, dateStrings.size());
        Assert.assertEquals("?", dateStrings.get(0));
        Assert.assertEquals("?", dateStrings.get(1));
        Assert.assertEquals("?", dateStrings.get(2));
        Assert.assertEquals("?", dateStrings.get(3));

        dateStrings = memoryUsageHistory.getSignificantDates(3, new SimpleDateFormat(DateUtil.TIME_FORMAT_STRING));
        Assert.assertEquals(3, dateStrings.size());
        Assert.assertEquals("?", dateStrings.get(0));
        Assert.assertEquals("?", dateStrings.get(1));
        Assert.assertEquals("?", dateStrings.get(2));

        dateStrings = memoryUsageHistory.getSignificantDates(6, new SimpleDateFormat(DateUtil.TIME_FORMAT_STRING));
        Assert.assertEquals(6, dateStrings.size());
        Assert.assertEquals("?", dateStrings.get(0));
        Assert.assertEquals("?", dateStrings.get(1));
        Assert.assertEquals("?", dateStrings.get(2));
        Assert.assertEquals("?", dateStrings.get(3));
        Assert.assertEquals("?", dateStrings.get(4));
        Assert.assertEquals("?", dateStrings.get(5));
    }

}
