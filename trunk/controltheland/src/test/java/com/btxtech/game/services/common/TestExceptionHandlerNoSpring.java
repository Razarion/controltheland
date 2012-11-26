package com.btxtech.game.services.common;

import com.btxtech.game.services.AbstractServiceTest;
import org.apache.commons.logging.Log;
import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 26.11.12
 * Time: 12:36
 */
public class TestExceptionHandlerNoSpring {
    @Test
    public void handleException() throws Exception {
        String threadName = Thread.currentThread().getName();
        Exception exception = new Exception();

        Log mockLog = EasyMock.createStrictMock(Log.class);
        mockLog.error("--------------------------------------------------------------------");
        mockLog.error("ExceptionHandler.handleException() applicationContext is not set");
        mockLog.error("Thread: " + threadName);
        mockLog.error(null, exception);
        EasyMock.replay(mockLog);
        AbstractServiceTest.setPrivateStaticField(ExceptionHandler.class, "log", mockLog);
        ExceptionHandler.handleException(exception);
    }

    @Test
    public void handleExceptionMessage() throws Exception {
        String threadName = Thread.currentThread().getName();
        Exception exception = new Exception();

        Log mockLog = EasyMock.createStrictMock(Log.class);
        mockLog.error("--------------------------------------------------------------------");
        mockLog.error("ExceptionHandler.handleException() applicationContext is not set");
        mockLog.error("Thread: " + threadName);
        mockLog.error("test message", exception);
        EasyMock.replay(mockLog);
        AbstractServiceTest.setPrivateStaticField(ExceptionHandler.class, "log", mockLog);
        ExceptionHandler.handleException(exception, "test message");
    }

    @Test
    public void handleExceptionOnlyMessage() throws Exception {
        String threadName = Thread.currentThread().getName();
        Log mockLog = EasyMock.createStrictMock(Log.class);
        mockLog.error("--------------------------------------------------------------------");
        mockLog.error("ExceptionHandler.handleException() applicationContext is not set");
        mockLog.error("Thread: " + threadName);
        mockLog.error("test message", null);
        EasyMock.replay(mockLog);
        AbstractServiceTest.setPrivateStaticField(ExceptionHandler.class, "log", mockLog);
        ExceptionHandler.handleException(null, "test message");
    }
}