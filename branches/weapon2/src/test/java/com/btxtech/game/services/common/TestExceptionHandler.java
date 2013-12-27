package com.btxtech.game.services.common;

import com.btxtech.game.services.AbstractServiceTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 26.11.12
 * Time: 12:36
 */
public class TestExceptionHandler extends AbstractServiceTest {
    @Test
    @DirtiesContext
    public void handleExceptionNoSession() throws Exception {
        String threadName = Thread.currentThread().getName();
        Exception exception = new Exception();

        Log mockLog = EasyMock.createStrictMock(Log.class);
        mockLog.error("--------------------------------------------------------------------");
        mockLog.error("Thread: " + threadName);
        mockLog.error(null, exception);
        EasyMock.replay(mockLog);
        AbstractServiceTest.setPrivateStaticField(ExceptionHandler.class, "log", mockLog);

        ExceptionHandler.handleException(exception);
        EasyMock.verify(mockLog);

        AbstractServiceTest.setPrivateStaticField(ExceptionHandler.class, "log", LogFactory.getLog(ExceptionHandler.class));
    }

    @Test
    @DirtiesContext
    public void handleExceptionSession() throws Exception {
        String threadName = Thread.currentThread().getName();
        Exception exception = new Exception();
        Log mockLog = EasyMock.createStrictMock(Log.class);
        mockLog.error("--------------------------------------------------------------------");
        mockLog.error("Thread: " + threadName);
        mockLog.error("URI: ");
        mockLog.error("URL: http://localhost:80");
        mockLog.error("User Agent: null");
        mockLog.error(EasyMock.startsWith("Session Id: "));
        mockLog.error(EasyMock.startsWith("IP:"));
        mockLog.error("Referer: null");
        mockLog.error("User: unregistered");
        mockLog.error(null, exception);
        EasyMock.replay(mockLog);
        AbstractServiceTest.setPrivateStaticField(ExceptionHandler.class, "log", mockLog);

        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        ExceptionHandler.handleException(exception);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(mockLog);
        AbstractServiceTest.setPrivateStaticField(ExceptionHandler.class, "log", LogFactory.getLog(ExceptionHandler.class));
    }

}