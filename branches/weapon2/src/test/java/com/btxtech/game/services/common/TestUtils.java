package com.btxtech.game.services.common;

import junit.framework.Assert;
import org.junit.Test;

/**
 * User: beat
 * Date: 13.05.12
 * Time: 12:44
 */
public class TestUtils {

    @Test
    public void testParseIntSave() {
        Assert.assertEquals(22, Utils.parseIntSave("22"));
        Assert.assertEquals(15, Utils.parseIntSave("15;jsessionid=FCF04A304098BA3A1BC8B15BCD9FF449"));
    }
}
