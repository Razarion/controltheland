package com.btxtech.game;

import com.btxtech.game.services.AbstractServiceTest;
import junit.framework.Assert;
import org.junit.ComparisonFailure;
import org.junit.Test;

/**
 * User: beat
 * Date: 26.01.13
 * Time: 16:20
 */

public class TestTest {

    @Test
    public void assertStringIgnoreWhitespace() {
        AbstractServiceTest.assertStringIgnoreWhitespace("  hallo.du xxxx\n\rcccc", "hallo.duxxxxcccc");
        AbstractServiceTest.assertStringIgnoreWhitespace("\tx.{}.XX\naaa.bbb", "x.{   }.XX\taaa.bbb");
        String expected = "XXX.user.track({\n" +
                "    'pid':'987',\n" +
                "    'eventid':'3820',\n" +
                "    'referenz':'1'\n" +
                "});";
        String actual = "XXX.user.track({\n" +
                "'pid':'987'," +
                "'eventid':'3820'," +
                "'referenz':'1'" +
                "});";
        AbstractServiceTest.assertStringIgnoreWhitespace(expected, actual);
    }

    @Test
    public void assertStringIgnoreWhitespaceFail() {
        String expected = "XXX.user.track({\n" +
                "    'pid':'988',\n" +
                "    'eventid':'3820',\n" +
                "    'referenz':'1'\n" +
                "});";
        String actual = "XXX.user.track({\n" +
                "'pid':'987'," +
                "'eventid':'3820'," +
                "'referenz':'1'" +
                "});";
        try {
            AbstractServiceTest.assertStringIgnoreWhitespace(expected, actual);
            Assert.fail("ComparisonFailure expected");
        } catch (ComparisonFailure e) {

        }
    }
}
