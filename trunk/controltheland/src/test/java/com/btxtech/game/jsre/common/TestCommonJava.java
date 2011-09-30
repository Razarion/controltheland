package com.btxtech.game.jsre.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: beat
 * Date: 30.09.2011
 * Time: 10:17:15
 */
public class TestCommonJava {
    @Test
    public void getMostInnerThrowable() {
        Exception e1 = new Exception("E1");
        Exception e2 = new Exception("E2", null);
        Throwable t1 = new Throwable("T1", e1);

        Assert.assertEquals(e1, CommonJava.getMostInnerThrowable(e1));
        Assert.assertEquals(e2, CommonJava.getMostInnerThrowable(e2));
        Assert.assertEquals(e1, CommonJava.getMostInnerThrowable(t1));
    }
}
