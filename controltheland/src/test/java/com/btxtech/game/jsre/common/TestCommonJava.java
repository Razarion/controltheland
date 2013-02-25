package com.btxtech.game.jsre.common;

import org.easymock.EasyMock;
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

    @Test
    public void compareDifferentExceptions1() {
        Exception e1 = new Exception("X1");
        Exception e2 = new Exception("X2");

        Assert.assertFalse(CommonJava.compareExceptions(e1, e2));
        Assert.assertFalse(CommonJava.compareExceptionsDeep(e1, e2));
    }

    @Test
    public void compareDifferentExceptions2() {
        Exception e1 = new Exception("X1");
        Exception e2 = new Exception("X1");

        Assert.assertFalse(CommonJava.compareExceptions(e1, e2));
        Assert.assertFalse(CommonJava.compareExceptionsDeep(e1, e2));
    }

    @Test
    public void compareDifferentExceptions3() {
        Exception e1 = new Exception();
        Exception e2 = new Exception("X1");

        Assert.assertFalse(CommonJava.compareExceptions(e1, e2));
        Assert.assertFalse(CommonJava.compareExceptionsDeep(e1, e2));
    }

    @Test
    public void compareDifferentExceptions4() {
        Exception e1 = new Exception("X1");
        Exception e2 = new Exception();

        Assert.assertFalse(CommonJava.compareExceptions(e1, e2));
        Assert.assertFalse(CommonJava.compareExceptionsDeep(e1, e2));
    }

    @Test
    public void compareDifferentExceptions5() {
        Exception e1 = new Exception();
        Exception e2 = new Exception();

        Assert.assertFalse(CommonJava.compareExceptions(e1, e2));
        Assert.assertFalse(CommonJava.compareExceptionsDeep(e1, e2));
    }

    @Test
    public void compareSameExceptions1() {
        Exception[] exceptions = generateExceptions(null, null);

        Assert.assertTrue(CommonJava.compareExceptions(exceptions[0], exceptions[1]));
        Assert.assertTrue(CommonJava.compareExceptionsDeep(exceptions[0], exceptions[1]));
    }

    @Test
    public void compareSameExceptions2() {
        Exception[] exceptions = generateExceptions("X1", "X1");

        Assert.assertTrue(CommonJava.compareExceptions(exceptions[0], exceptions[1]));
        Assert.assertTrue(CommonJava.compareExceptionsDeep(exceptions[0], exceptions[1]));
    }

    @Test
    public void compareSameExceptionsDiffMessage1() {
        Exception[] exceptions = generateExceptions("X1", "X2");

        Assert.assertFalse(CommonJava.compareExceptions(exceptions[0], exceptions[1]));
        Assert.assertFalse(CommonJava.compareExceptionsDeep(exceptions[0], exceptions[1]));
    }

    @Test
    public void compareSameExceptionsDiffMessage2() {
        Exception[] exceptions = generateExceptions("X1", null);

        Assert.assertFalse(CommonJava.compareExceptions(exceptions[0], exceptions[1]));
        Assert.assertFalse(CommonJava.compareExceptionsDeep(exceptions[0], exceptions[1]));
    }

    @Test
    public void compareSameExceptionsDiffMessage3() {
        Exception[] exceptions = generateExceptions(null, "X1");

        Assert.assertFalse(CommonJava.compareExceptions(exceptions[0], exceptions[1]));
        Assert.assertFalse(CommonJava.compareExceptionsDeep(exceptions[0], exceptions[1]));
    }

    private Exception[] generateExceptions(String message1, String message2) {
        Exception[] result = new Exception[2];

        for (int i = 0; i < result.length; i++) {
            result[i] = new Exception(i == 0 ? message1 : message2);
        }

        return result;
    }

    @Test
    public void compareInnerExceptions1() {
        Exception[] exceptions = generateInnerExceptions(null, null);

        Assert.assertTrue(CommonJava.compareExceptionsDeep(exceptions[0], exceptions[1]));
    }

    @Test
    public void compareInnerExceptions2() {
        Exception[] exceptions = generateInnerExceptions("X1", "X1");

        Assert.assertTrue(CommonJava.compareExceptionsDeep(exceptions[0], exceptions[1]));
    }

    @Test
    public void compareInnerExceptions3() {
        Exception[] exceptions = generateInnerExceptions("X1", "X2");

        Assert.assertFalse(CommonJava.compareExceptionsDeep(exceptions[0], exceptions[1]));
    }

    @Test
    public void compareInnerExceptions4() {
        Exception[] exceptions1 = generateInnerExceptions("X1", "X2");
        Exception[] exceptions2 = generateInnerExceptions("X1", "X2");

        Assert.assertFalse(CommonJava.compareExceptionsDeep(exceptions1[0], exceptions2[0]));
        Assert.assertFalse(CommonJava.compareExceptionsDeep(exceptions1[0], exceptions2[1]));
        Assert.assertFalse(CommonJava.compareExceptionsDeep(exceptions1[1], exceptions2[0]));
        Assert.assertFalse(CommonJava.compareExceptionsDeep(exceptions1[1], exceptions2[1]));
    }

    private Exception[] generateInnerExceptions(String message1, String message2) {
        Exception[] result = new Exception[2];

        for (int i = 0; i < result.length; i++) {
            result[i] = new Exception(new Exception(new Exception(i == 0 ? message1 : message2)));
        }

        return result;
    }

    @Test
    public void compareExceptionsEmptyStackTrace1() {
        Exception exceptions1 = EasyMock.createNiceMock(Exception.class);
        EasyMock.expect(exceptions1.getStackTrace()).andReturn(null).anyTimes();
        EasyMock.replay(exceptions1);

        Exception exceptions2 = EasyMock.createNiceMock(Exception.class);
        EasyMock.expect(exceptions2.getStackTrace()).andReturn(null).anyTimes();
        EasyMock.replay(exceptions2);

        Assert.assertTrue(CommonJava.compareExceptions(exceptions1, exceptions2));
        Assert.assertTrue(CommonJava.compareExceptionsDeep(exceptions1, exceptions2));
    }

    @Test
    public void compareExceptionsEmptyStackTrace2() {
        Exception exceptions1 = EasyMock.createNiceMock(Exception.class);
        EasyMock.expect(exceptions1.getStackTrace()).andReturn(new StackTraceElement[0]).anyTimes();
        EasyMock.replay(exceptions1);

        Exception exceptions2 = EasyMock.createNiceMock(Exception.class);
        EasyMock.expect(exceptions2.getStackTrace()).andReturn(new StackTraceElement[0]).anyTimes();
        EasyMock.replay(exceptions2);

        Assert.assertTrue(CommonJava.compareExceptions(exceptions1, exceptions2));
        Assert.assertTrue(CommonJava.compareExceptionsDeep(exceptions1, exceptions2));
    }

    @Test
    public void compareExceptionsEmptyStackTrace3() {
        Exception exception1 = EasyMock.createNiceMock(Exception.class);
        EasyMock.expect(exception1.getStackTrace()).andReturn(new StackTraceElement[]{null}).anyTimes();
        EasyMock.replay(exception1);

        Exception exception2 = EasyMock.createNiceMock(Exception.class);
        EasyMock.expect(exception2.getStackTrace()).andReturn(new StackTraceElement[0]).anyTimes();
        EasyMock.replay(exception2);

        Assert.assertFalse(CommonJava.compareExceptions(exception1, exception2));
        Assert.assertFalse(CommonJava.compareExceptionsDeep(exception1, exception2));
    }


    @Test
    public void isValidEmail() {
        // Valid
        Assert.assertTrue(CommonJava.isValidEmail("xxx@yyy.com"));
        Assert.assertTrue(CommonJava.isValidEmail("xxx.aaaa@yyy.com"));
        Assert.assertTrue(CommonJava.isValidEmail("xxx@aaaa.yyy.com"));
        Assert.assertTrue(CommonJava.isValidEmail("xxx@aaa-aaaa.yyy.com"));
        Assert.assertTrue(CommonJava.isValidEmail("xxx@aaa.bbb.yyy.com"));
        Assert.assertTrue(CommonJava.isValidEmail("xxx.yyy.xxx@aaa.bbb.yyy.com"));
        Assert.assertTrue(CommonJava.isValidEmail("x@yyy.qqq"));
        Assert.assertTrue(CommonJava.isValidEmail("x@y.qqq"));
        Assert.assertTrue(CommonJava.isValidEmail("x@y.aa"));
        Assert.assertTrue(CommonJava.isValidEmail("x-x@y.aa"));
        Assert.assertTrue(CommonJava.isValidEmail("x_x@y.aa"));
        //Assert.assertTrue(CommonJava.isValidEmail("x@123.123.123.123"));
        // Invalid
        Assert.assertFalse(CommonJava.isValidEmail(null));
        Assert.assertFalse(CommonJava.isValidEmail(""));
        Assert.assertFalse(CommonJava.isValidEmail("1234"));
        Assert.assertFalse(CommonJava.isValidEmail("aaaa"));
        Assert.assertFalse(CommonJava.isValidEmail("@"));
        Assert.assertFalse(CommonJava.isValidEmail("x@y.a"));
    }
}
