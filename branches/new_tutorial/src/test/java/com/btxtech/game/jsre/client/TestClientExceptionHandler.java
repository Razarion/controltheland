package com.btxtech.game.jsre.client;

import com.btxtech.game.services.AbstractServiceTest;
import org.easymock.EasyMock;
import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 30.11.12
 * Time: 13:20
 */
public class TestClientExceptionHandler {
    @Test
    public void handleExceptionOnlyOnce1() throws Exception {
        Exception ex1 = new Exception();
        Exception ex2 = new Exception();

        Logger mockLog = EasyMock.createStrictMock(Logger.class);
        mockLog.log(Level.SEVERE, "!!Further exception will be suppressed!! null: null: null: class java.lang.Exception", ex1);
        mockLog.log(Level.SEVERE, "!!Further exception will be suppressed!! null: null: null: class java.lang.Exception", ex2);
        EasyMock.replay(mockLog);
        AbstractServiceTest.setPrivateStaticField(ClientExceptionHandler.class, "log", mockLog);

        ClientExceptionHandler.handleExceptionOnlyOnce(ex1);
        ClientExceptionHandler.handleExceptionOnlyOnce(ex2);
        EasyMock.verify(mockLog);

        AbstractServiceTest.setPrivateStaticField(ClientExceptionHandler.class, "log", Logger.getLogger(ClientExceptionHandler.class.getName()));
    }

    @Test
    public void handleExceptionOnlyOnce2() throws Exception {
        Exception[] exceptions = generateExceptions("X1", "X1");

        Logger mockLog = EasyMock.createStrictMock(Logger.class);
        mockLog.log(Level.SEVERE, "!!Further exception will be suppressed!! null: X1: null: class java.lang.Exception", exceptions[0]);
        EasyMock.replay(mockLog);
        AbstractServiceTest.setPrivateStaticField(ClientExceptionHandler.class, "log", mockLog);

        ClientExceptionHandler.handleExceptionOnlyOnce(exceptions[0]);
        ClientExceptionHandler.handleExceptionOnlyOnce(exceptions[1]);
        EasyMock.verify(mockLog);

        AbstractServiceTest.setPrivateStaticField(ClientExceptionHandler.class, "log", Logger.getLogger(ClientExceptionHandler.class.getName()));
    }

    @Test
    public void handleExceptionOnlyOnce3() throws Exception {
        Exception[] exceptions = generateExceptions("X1", "X2");

        Logger mockLog = EasyMock.createStrictMock(Logger.class);
        mockLog.log(Level.SEVERE, "!!Further exception will be suppressed!! null: X1: null: class java.lang.Exception", exceptions[0]);
        mockLog.log(Level.SEVERE, "!!Further exception will be suppressed!! null: X2: null: class java.lang.Exception", exceptions[1]);
        EasyMock.replay(mockLog);
        AbstractServiceTest.setPrivateStaticField(ClientExceptionHandler.class, "log", mockLog);

        ClientExceptionHandler.handleExceptionOnlyOnce(exceptions[0]);
        ClientExceptionHandler.handleExceptionOnlyOnce(exceptions[1]);
        EasyMock.verify(mockLog);

        AbstractServiceTest.setPrivateStaticField(ClientExceptionHandler.class, "log", Logger.getLogger(ClientExceptionHandler.class.getName()));
    }

    @Test
    public void handleExceptionOnlyOnceInner() throws Exception {
        Exception[] exceptions = generateInnerExceptions("X1", "X1");

        Logger mockLog = EasyMock.createStrictMock(Logger.class);
        mockLog.log(Level.SEVERE, "!!Further exception will be suppressed!! null: java.lang.Exception: java.lang.Exception: X1: java.lang.Exception: java.lang.Exception: X1: class java.lang.Exception", exceptions[0]);
        EasyMock.replay(mockLog);
        AbstractServiceTest.setPrivateStaticField(ClientExceptionHandler.class, "log", mockLog);

        ClientExceptionHandler.handleExceptionOnlyOnce(exceptions[0]);
        ClientExceptionHandler.handleExceptionOnlyOnce(exceptions[1]);
        EasyMock.verify(mockLog);

        AbstractServiceTest.setPrivateStaticField(ClientExceptionHandler.class, "log", Logger.getLogger(ClientExceptionHandler.class.getName()));
    }

    @Test
    public void handleExceptionOnlyOnceMessage1() throws Exception {
        Exception ex1 = new Exception();
        Exception ex2 = new Exception();

        Logger mockLog = EasyMock.createStrictMock(Logger.class);
        mockLog.log(Level.SEVERE, "!!Further exception will be suppressed!! xx1: null: null: class java.lang.Exception", ex1);
        mockLog.log(Level.SEVERE, "!!Further exception will be suppressed!! xx2: null: null: class java.lang.Exception", ex2);
        EasyMock.replay(mockLog);
        AbstractServiceTest.setPrivateStaticField(ClientExceptionHandler.class, "log", mockLog);

        ClientExceptionHandler.handleExceptionOnlyOnce("xx1", ex1);
        ClientExceptionHandler.handleExceptionOnlyOnce("xx2", ex2);
        EasyMock.verify(mockLog);

        AbstractServiceTest.setPrivateStaticField(ClientExceptionHandler.class, "log", Logger.getLogger(ClientExceptionHandler.class.getName()));
    }

    @Test
    public void handleExceptionOnlyOnceMessage2() throws Exception {
        Exception[] exceptions = generateExceptions("X1", "X1");

        Logger mockLog = EasyMock.createStrictMock(Logger.class);
        mockLog.log(Level.SEVERE, "!!Further exception will be suppressed!! m1: X1: null: class java.lang.Exception", exceptions[0]);
        EasyMock.replay(mockLog);
        AbstractServiceTest.setPrivateStaticField(ClientExceptionHandler.class, "log", mockLog);

        ClientExceptionHandler.handleExceptionOnlyOnce("m1", exceptions[0]);
        ClientExceptionHandler.handleExceptionOnlyOnce("m1", exceptions[1]);
        EasyMock.verify(mockLog);

        AbstractServiceTest.setPrivateStaticField(ClientExceptionHandler.class, "log", Logger.getLogger(ClientExceptionHandler.class.getName()));
    }

    @Test
    public void handleExceptionOnlyOnceMessage3() throws Exception {
        Exception[] exceptions = generateExceptions("X1", "X1");

        Logger mockLog = EasyMock.createStrictMock(Logger.class);
        mockLog.log(Level.SEVERE, "!!Further exception will be suppressed!! m1: X1: null: class java.lang.Exception", exceptions[0]);
        mockLog.log(Level.SEVERE, "!!Further exception will be suppressed!! m2: X1: null: class java.lang.Exception", exceptions[1]);
        EasyMock.replay(mockLog);
        AbstractServiceTest.setPrivateStaticField(ClientExceptionHandler.class, "log", mockLog);

        ClientExceptionHandler.handleExceptionOnlyOnce("m1", exceptions[0]);
        ClientExceptionHandler.handleExceptionOnlyOnce("m2", exceptions[1]);
        EasyMock.verify(mockLog);

        AbstractServiceTest.setPrivateStaticField(ClientExceptionHandler.class, "log", Logger.getLogger(ClientExceptionHandler.class.getName()));
    }

    @Test
    public void handleExceptionOnlyOnceOnlyMessage1() throws Exception {
        Logger mockLog = EasyMock.createStrictMock(Logger.class);
        mockLog.log(Level.SEVERE, "!!Further exception will be suppressed!! yy1", (Throwable) null);
        mockLog.log(Level.SEVERE, "!!Further exception will be suppressed!! yy2", (Throwable) null);
        EasyMock.replay(mockLog);
        AbstractServiceTest.setPrivateStaticField(ClientExceptionHandler.class, "log", mockLog);

        ClientExceptionHandler.handleExceptionOnlyOnce("yy1");
        ClientExceptionHandler.handleExceptionOnlyOnce("yy2");
        EasyMock.verify(mockLog);

        AbstractServiceTest.setPrivateStaticField(ClientExceptionHandler.class, "log", Logger.getLogger(ClientExceptionHandler.class.getName()));
    }

    @Test
    public void handleExceptionOnlyOnceOnlyMessage2() throws Exception {
        Logger mockLog = EasyMock.createStrictMock(Logger.class);
        mockLog.log(Level.SEVERE, "!!Further exception will be suppressed!! zz1", (Throwable) null);
        EasyMock.replay(mockLog);
        AbstractServiceTest.setPrivateStaticField(ClientExceptionHandler.class, "log", mockLog);

        ClientExceptionHandler.handleExceptionOnlyOnce("zz1");
        ClientExceptionHandler.handleExceptionOnlyOnce("zz1");
        EasyMock.verify(mockLog);

        AbstractServiceTest.setPrivateStaticField(ClientExceptionHandler.class, "log", Logger.getLogger(ClientExceptionHandler.class.getName()));
    }

    private Exception[] generateExceptions(String message1, String message2) {
        Exception[] result = new Exception[2];

        for (int i = 0; i < result.length; i++) {
            result[i] = new Exception(i == 0 ? message1 : message2);
        }

        return result;
    }

    private Exception[] generateInnerExceptions(String message1, String message2) {
        Exception[] result = new Exception[2];

        for (int i = 0; i < result.length; i++) {
            result[i] = new Exception(new Exception(new Exception(i == 0 ? message1 : message2)));
        }

        return result;
    }

}
