package com.btxtech.game.services.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * User: beat
 * Date: 18.09.2011
 * Time: 23:19:12
 */
public class TestDateUtil {
    @Test
    public void specialDays() throws Exception {
        Assert.assertEquals(new Date(1316296800000L), DateUtil.dayStart(new Date(1316380989127L)));
        Assert.assertEquals(new Date(1316296800000L), DateUtil.dayStart(new Date(1316381085230L)));
        Assert.assertEquals(new Date(1316296800000L), DateUtil.dayStart(new Date(1316296800000L)));

        Assert.assertEquals(new Date(1315778400000L), DateUtil.weekStart(new Date(1316382001764L)));
        Assert.assertEquals(new Date(1315778400000L), DateUtil.weekStart(new Date(1316382217910L)));
        Assert.assertEquals(new Date(1315778400000L), DateUtil.weekStart(new Date(1315778400000L)));

        Assert.assertEquals(new Date(1317420000000L), DateUtil.monthStart(new Date(1318584373693L)));
        Assert.assertEquals(new Date(1314828000000L), DateUtil.monthStart(new Date(1316382373693L)));
        Assert.assertEquals(new Date(1314828000000L), DateUtil.monthStart(new Date(1314828000000L)));
    }

    @Test
    public void removeAdd() throws Exception {
        Assert.assertEquals(new Date(1318497973693L), DateUtil.removeOneDay(new Date(1318584373693L)));
        Assert.assertEquals(new Date(1316210400000L), DateUtil.removeOneDay(new Date(1316296800000L)));
        Assert.assertEquals(new Date(1314741600000L), DateUtil.removeOneDay(new Date(1314828000000L)));

        Assert.assertEquals(new Date(1318670773693L), DateUtil.addOneDay(new Date(1318584373693L)));
        Assert.assertEquals(new Date(1316296800000L), DateUtil.addOneDay(new Date(1316210400000L)));
        Assert.assertEquals(new Date(1314828000000L), DateUtil.addOneDay(new Date(1314741600000L)));

        Assert.assertEquals(new Date(1314136800000L), DateUtil.removeOneWeek(new Date(1314741600000L)));
        Assert.assertEquals(new Date(1317979573693L), DateUtil.removeOneWeek(new Date(1318584373693L)));
        Assert.assertEquals(new Date(1314223200000L), DateUtil.removeOneWeek(new Date(1314828000000L)));

        Assert.assertEquals(new Date(1316985789127L), DateUtil.addOneWeek(new Date(1316380989127L)));
        Assert.assertEquals(new Date(1315346400000L), DateUtil.addOneWeek(new Date(1314741600000L)));
        Assert.assertEquals(new Date(1314828000000L), DateUtil.addOneWeek(new Date(1314223200000L)));
    }

    @Test
    public void createDate() throws Exception {
        Assert.assertEquals(new Date(1314914400000L), DateUtil.createDate(2011, Calendar.SEPTEMBER, 2));
        Assert.assertEquals(new Date(69721200000L), DateUtil.createDate(1972, Calendar.MARCH, 18));
        Assert.assertEquals(new Date(1590962400000L), DateUtil.createDate(2020, Calendar.JUNE, 1));
        Assert.assertEquals(new Date(1078009200000L), DateUtil.createDate(2004, Calendar.FEBRUARY, 29));
    }

}
