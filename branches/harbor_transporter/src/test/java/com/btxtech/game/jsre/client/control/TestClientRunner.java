package com.btxtech.game.jsre.client.control;

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.control.task.AbstractStartupTask;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.junit.Test;

import java.util.List;

import static org.easymock.EasyMock.contains;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.geq;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * User: beat
 * Date: 18.02.2011
 * Time: 20:20:18
 */
public class TestClientRunner {
    private static final String ERROR_TEXT = "qaywsxedcrfv";

    @Test
    public void runSimple() {
        StartupProgressListener mockListener = createStrictMock(StartupProgressListener.class);
        mockListener.onStart(StartupTestSeq.TEST_SIMPLE);

        mockListener.onNextTask(SimpleTestTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(SimpleTestTaskEnum.TEST_1));

        mockListener.onNextTask(SimpleTestTaskEnum.TEST_2);
        mockListener.onTaskFinished(eqAbstractStartupTask(SimpleTestTaskEnum.TEST_2));

        mockListener.onNextTask(SimpleTestTaskEnum.TEST_3);
        mockListener.onTaskFinished(eqAbstractStartupTask(SimpleTestTaskEnum.TEST_3));

        mockListener.onStartupFinished(eqStartupTaskInfo(StartupTestSeq.TEST_SIMPLE), geq(0L));
        replay(mockListener);
        ClientRunner clientRunner = new ClientRunner();
        clientRunner.addStartupProgressListener(mockListener);
        clientRunner.start(StartupTestSeq.TEST_SIMPLE);
        verify(mockListener);
    }

    @Test
    public void runDeferred() {
        StartupProgressListener mockListener = createStrictMock(StartupProgressListener.class);
        mockListener.onStart(StartupTestSeq.TEST_DEFERRED);

        mockListener.onNextTask(DeferredTestTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredTestTaskEnum.TEST_1));

        mockListener.onNextTask(DeferredTestTaskEnum.TEST_2);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredTestTaskEnum.TEST_2));

        mockListener.onNextTask(DeferredTestTaskEnum.TEST_3);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredTestTaskEnum.TEST_3));

        mockListener.onStartupFinished(eqStartupTaskInfo(StartupTestSeq.TEST_DEFERRED), geq(0L));
        replay(mockListener);
        ClientRunner clientRunner = new ClientRunner();
        clientRunner.addStartupProgressListener(mockListener);
        clientRunner.start(StartupTestSeq.TEST_DEFERRED);
        DeferredTestTaskEnum.TEST_1.getTestDeferredStartupTask().finished();
        DeferredTestTaskEnum.TEST_2.getTestDeferredStartupTask().finished();
        DeferredTestTaskEnum.TEST_3.getTestDeferredStartupTask().finished();

        verify(mockListener);
    }

    @Test
    public void runDeferredBackground() {
        StartupProgressListener mockListener = createStrictMock(StartupProgressListener.class);
        mockListener.onStart(StartupTestSeq.TEST_BACKGROUND);

        mockListener.onNextTask(DeferredBackgroundTestTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredBackgroundTestTaskEnum.TEST_1));

        mockListener.onNextTask(DeferredBackgroundTestTaskEnum.TEST_2_BACKGROUND);

        mockListener.onNextTask(DeferredBackgroundTestTaskEnum.TEST_3);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredBackgroundTestTaskEnum.TEST_3));

        mockListener.onNextTask(DeferredBackgroundTestTaskEnum.TEST_4);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredBackgroundTestTaskEnum.TEST_4));

        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredBackgroundTestTaskEnum.TEST_2_BACKGROUND));


        mockListener.onStartupFinished(eqStartupTaskInfo(new StartupTaskEnum[]{DeferredBackgroundTestTaskEnum.TEST_1,
                DeferredBackgroundTestTaskEnum.TEST_3,
                DeferredBackgroundTestTaskEnum.TEST_4,
                DeferredBackgroundTestTaskEnum.TEST_2_BACKGROUND}), geq(0L));

        replay(mockListener);
        ClientRunner clientRunner = new ClientRunner();
        clientRunner.addStartupProgressListener(mockListener);
        clientRunner.start(StartupTestSeq.TEST_BACKGROUND);
        DeferredBackgroundTestTaskEnum.TEST_1.getTestDeferredStartupTask().finished();
        DeferredBackgroundTestTaskEnum.TEST_3.getTestDeferredStartupTask().finished();
        DeferredBackgroundTestTaskEnum.TEST_4.getTestDeferredStartupTask().finished();
        DeferredBackgroundTestTaskEnum.TEST_2_BACKGROUND.getTestDeferredStartupTask().finished();

        verify(mockListener);
    }

    @Test
    public void runDeferredFinished() {
        StartupProgressListener mockListener = createStrictMock(StartupProgressListener.class);
        mockListener.onStart(StartupTestSeq.TEST_DEFERRED_FINISH);

        mockListener.onNextTask(DeferredFinishTestTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredFinishTestTaskEnum.TEST_1));

        mockListener.onNextTask(DeferredFinishTestTaskEnum.TEST_2_FINISH);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredFinishTestTaskEnum.TEST_2_FINISH));

        mockListener.onNextTask(DeferredFinishTestTaskEnum.TEST_3);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredFinishTestTaskEnum.TEST_3));


        mockListener.onStartupFinished(eqStartupTaskInfo(StartupTestSeq.TEST_DEFERRED_FINISH), geq(0L));

        replay(mockListener);
        ClientRunner clientRunner = new ClientRunner();
        clientRunner.addStartupProgressListener(mockListener);
        clientRunner.start(StartupTestSeq.TEST_DEFERRED_FINISH);
        DeferredFinishTestTaskEnum.TEST_1.getTestDeferredStartupTask().finished();
        DeferredFinishTestTaskEnum.TEST_3.getTestDeferredStartupTask().finished();
        verify(mockListener);
    }

    @Test
    public void runDeferredBackgroundFinished() {
        StartupProgressListener mockListener = createStrictMock(StartupProgressListener.class);
        mockListener.onStart(StartupTestSeq.TEST_DEFERRED_BACKGROUND_FINISH);

        mockListener.onNextTask(DeferredBackgroundFinishTestTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredBackgroundFinishTestTaskEnum.TEST_1));

        mockListener.onNextTask(DeferredBackgroundFinishTestTaskEnum.TEST_2_BACKGROUND);

        mockListener.onNextTask(DeferredBackgroundFinishTestTaskEnum.TEST_3_DEFERRED_BACKGROUND_FINISH);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredBackgroundFinishTestTaskEnum.TEST_3_DEFERRED_BACKGROUND_FINISH));

        mockListener.onNextTask(DeferredBackgroundFinishTestTaskEnum.TEST_4_DEFERRED_FINISH);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredBackgroundFinishTestTaskEnum.TEST_4_DEFERRED_FINISH));

        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredBackgroundFinishTestTaskEnum.TEST_2_BACKGROUND));

        mockListener.onStartupFinished(eqStartupTaskInfo(new StartupTaskEnum[]{DeferredBackgroundFinishTestTaskEnum.TEST_1,
                DeferredBackgroundFinishTestTaskEnum.TEST_3_DEFERRED_BACKGROUND_FINISH,
                DeferredBackgroundFinishTestTaskEnum.TEST_4_DEFERRED_FINISH,
                DeferredBackgroundFinishTestTaskEnum.TEST_2_BACKGROUND
        }), geq(0L));

        replay(mockListener);
        ClientRunner clientRunner = new ClientRunner();
        clientRunner.addStartupProgressListener(mockListener);
        clientRunner.start(StartupTestSeq.TEST_DEFERRED_BACKGROUND_FINISH);
        DeferredBackgroundFinishTestTaskEnum.TEST_1.getTestDeferredStartupTask().finished();
        DeferredBackgroundFinishTestTaskEnum.TEST_2_BACKGROUND.getTestDeferredStartupTask().finished();
        verify(mockListener);
    }

    @Test
    public void runMultiple() {
        StartupProgressListener mockListener = createStrictMock(StartupProgressListener.class);
        // 1
        mockListener.onStart(StartupTestSeq.TEST_SIMPLE);

        mockListener.onNextTask(SimpleTestTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(SimpleTestTaskEnum.TEST_1));

        mockListener.onNextTask(SimpleTestTaskEnum.TEST_2);
        mockListener.onTaskFinished(eqAbstractStartupTask(SimpleTestTaskEnum.TEST_2));

        mockListener.onNextTask(SimpleTestTaskEnum.TEST_3);
        mockListener.onTaskFinished(eqAbstractStartupTask(SimpleTestTaskEnum.TEST_3));

        mockListener.onStartupFinished(eqStartupTaskInfo(StartupTestSeq.TEST_SIMPLE), geq(0L));

        // 2
        mockListener.onStart(StartupTestSeq.TEST_DEFERRED);

        mockListener.onNextTask(DeferredTestTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredTestTaskEnum.TEST_1));

        mockListener.onNextTask(DeferredTestTaskEnum.TEST_2);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredTestTaskEnum.TEST_2));

        mockListener.onNextTask(DeferredTestTaskEnum.TEST_3);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredTestTaskEnum.TEST_3));

        mockListener.onStartupFinished(eqStartupTaskInfo(StartupTestSeq.TEST_DEFERRED), geq(0L));

        // 3
        mockListener.onStart(StartupTestSeq.TEST_BACKGROUND);

        mockListener.onNextTask(DeferredBackgroundTestTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredBackgroundTestTaskEnum.TEST_1));

        mockListener.onNextTask(DeferredBackgroundTestTaskEnum.TEST_2_BACKGROUND);

        mockListener.onNextTask(DeferredBackgroundTestTaskEnum.TEST_3);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredBackgroundTestTaskEnum.TEST_3));

        mockListener.onNextTask(DeferredBackgroundTestTaskEnum.TEST_4);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredBackgroundTestTaskEnum.TEST_4));

        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredBackgroundTestTaskEnum.TEST_2_BACKGROUND));


        mockListener.onStartupFinished(eqStartupTaskInfo(new StartupTaskEnum[]{DeferredBackgroundTestTaskEnum.TEST_1,
                DeferredBackgroundTestTaskEnum.TEST_3,
                DeferredBackgroundTestTaskEnum.TEST_4,
                DeferredBackgroundTestTaskEnum.TEST_2_BACKGROUND}), geq(0L));

        // 4
        mockListener.onStart(StartupTestSeq.TEST_DEFERRED_FINISH);

        mockListener.onNextTask(DeferredFinishTestTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredFinishTestTaskEnum.TEST_1));

        mockListener.onNextTask(DeferredFinishTestTaskEnum.TEST_2_FINISH);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredFinishTestTaskEnum.TEST_2_FINISH));

        mockListener.onNextTask(DeferredFinishTestTaskEnum.TEST_3);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredFinishTestTaskEnum.TEST_3));


        mockListener.onStartupFinished(eqStartupTaskInfo(StartupTestSeq.TEST_DEFERRED_FINISH), geq(0L));

        // 5
        mockListener.onStart(StartupTestSeq.TEST_DEFERRED_BACKGROUND_FINISH);

        mockListener.onNextTask(DeferredBackgroundFinishTestTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredBackgroundFinishTestTaskEnum.TEST_1));

        mockListener.onNextTask(DeferredBackgroundFinishTestTaskEnum.TEST_2_BACKGROUND);

        mockListener.onNextTask(DeferredBackgroundFinishTestTaskEnum.TEST_3_DEFERRED_BACKGROUND_FINISH);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredBackgroundFinishTestTaskEnum.TEST_3_DEFERRED_BACKGROUND_FINISH));

        mockListener.onNextTask(DeferredBackgroundFinishTestTaskEnum.TEST_4_DEFERRED_FINISH);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredBackgroundFinishTestTaskEnum.TEST_4_DEFERRED_FINISH));

        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredBackgroundFinishTestTaskEnum.TEST_2_BACKGROUND));

        mockListener.onStartupFinished(eqStartupTaskInfo(new StartupTaskEnum[]{DeferredBackgroundFinishTestTaskEnum.TEST_1,
                DeferredBackgroundFinishTestTaskEnum.TEST_3_DEFERRED_BACKGROUND_FINISH,
                DeferredBackgroundFinishTestTaskEnum.TEST_4_DEFERRED_FINISH,
                DeferredBackgroundFinishTestTaskEnum.TEST_2_BACKGROUND
        }), geq(0L));

        replay(mockListener);
        ClientRunner clientRunner = new ClientRunner();
        clientRunner.addStartupProgressListener(mockListener);

        // 1
        clientRunner.start(StartupTestSeq.TEST_SIMPLE);

        // 2
        clientRunner.start(StartupTestSeq.TEST_DEFERRED);
        DeferredTestTaskEnum.TEST_1.getTestDeferredStartupTask().finished();
        DeferredTestTaskEnum.TEST_2.getTestDeferredStartupTask().finished();
        DeferredTestTaskEnum.TEST_3.getTestDeferredStartupTask().finished();

        // 3
        clientRunner.start(StartupTestSeq.TEST_BACKGROUND);
        DeferredBackgroundTestTaskEnum.TEST_1.getTestDeferredStartupTask().finished();
        DeferredBackgroundTestTaskEnum.TEST_3.getTestDeferredStartupTask().finished();
        DeferredBackgroundTestTaskEnum.TEST_4.getTestDeferredStartupTask().finished();
        DeferredBackgroundTestTaskEnum.TEST_2_BACKGROUND.getTestDeferredStartupTask().finished();

        // 4
        clientRunner.start(StartupTestSeq.TEST_DEFERRED_FINISH);
        DeferredFinishTestTaskEnum.TEST_1.getTestDeferredStartupTask().finished();
        DeferredFinishTestTaskEnum.TEST_3.getTestDeferredStartupTask().finished();

        // 5
        clientRunner.start(StartupTestSeq.TEST_DEFERRED_BACKGROUND_FINISH);
        DeferredBackgroundFinishTestTaskEnum.TEST_1.getTestDeferredStartupTask().finished();
        DeferredBackgroundFinishTestTaskEnum.TEST_2_BACKGROUND.getTestDeferredStartupTask().finished();

        verify(mockListener);
    }

    @Test
    public void runSimpleException() {
        StartupProgressListener mockListener = createStrictMock(StartupProgressListener.class);
        mockListener.onStart(StartupTestSeq.TEST_SIMPLE_EXCEPTION);

        mockListener.onNextTask(SimpleExceptionTestTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(SimpleExceptionTestTaskEnum.TEST_1));

        mockListener.onNextTask(SimpleExceptionTestTaskEnum.TEST_2_EXCEPTION);
        //noinspection ThrowableResultOfMethodCallIgnored
        mockListener.onTaskFailed(eqAbstractStartupTask(SimpleExceptionTestTaskEnum.TEST_2_EXCEPTION), contains(SimpleExceptionStartupTestTask.ERROR_STRING), eq(SimpleExceptionStartupTestTask.EXCEPTION));

        mockListener.onStartupFailed(eqStartupTaskInfo(new StartupTaskEnum[]{SimpleExceptionTestTaskEnum.TEST_1, SimpleExceptionTestTaskEnum.TEST_2_EXCEPTION}), geq(0L));
        replay(mockListener);
        ClientRunner clientRunner = new ClientRunner();
        clientRunner.addStartupProgressListener(mockListener);
        clientRunner.start(StartupTestSeq.TEST_SIMPLE_EXCEPTION);
        verify(mockListener);
    }

    @Test
    public void runDeferredFail() {
        StartupProgressListener mockListener = createStrictMock(StartupProgressListener.class);
        mockListener.onStart(StartupTestSeq.TEST_BACKGROUND);

        mockListener.onNextTask(DeferredBackgroundTestTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredBackgroundTestTaskEnum.TEST_1));

        mockListener.onNextTask(DeferredBackgroundTestTaskEnum.TEST_2_BACKGROUND);

        mockListener.onNextTask(DeferredBackgroundTestTaskEnum.TEST_3);
        mockListener.onTaskFailed(eqAbstractStartupTask(DeferredBackgroundTestTaskEnum.TEST_3), contains(ERROR_TEXT), (Throwable) eq(null));


        mockListener.onStartupFailed(eqStartupTaskInfo(new StartupTaskEnum[]{DeferredBackgroundTestTaskEnum.TEST_1,
                DeferredBackgroundTestTaskEnum.TEST_3,
                DeferredBackgroundTestTaskEnum.TEST_4}),
                geq(0L));

        replay(mockListener);
        ClientRunner clientRunner = new ClientRunner();
        clientRunner.addStartupProgressListener(mockListener);
        clientRunner.start(StartupTestSeq.TEST_BACKGROUND);
        DeferredBackgroundTestTaskEnum.TEST_1.getTestDeferredStartupTask().finished();
        DeferredBackgroundTestTaskEnum.TEST_3.getTestDeferredStartupTask().failed(ERROR_TEXT);
        DeferredBackgroundTestTaskEnum.TEST_2_BACKGROUND.getTestDeferredStartupTask().finished();

        verify(mockListener);
    }

    @Test
    public void runDeferredBackgroundException() {
        StartupProgressListener mockListener = createStrictMock(StartupProgressListener.class);
        mockListener.onStart(StartupTestSeq.TEST_BACKGROUND);

        mockListener.onNextTask(DeferredBackgroundTestTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredBackgroundTestTaskEnum.TEST_1));

        mockListener.onNextTask(DeferredBackgroundTestTaskEnum.TEST_2_BACKGROUND);

        mockListener.onNextTask(DeferredBackgroundTestTaskEnum.TEST_3);
        mockListener.onTaskFinished(eqAbstractStartupTask(DeferredBackgroundTestTaskEnum.TEST_3));

        mockListener.onNextTask(DeferredBackgroundTestTaskEnum.TEST_4);

        //noinspection ThrowableInstanceNeverThrown
        Exception exception = new Exception("Test");
        //noinspection ThrowableResultOfMethodCallIgnored
        mockListener.onTaskFailed(eqAbstractStartupTask(DeferredBackgroundTestTaskEnum.TEST_2_BACKGROUND), eq(GwtCommon.setupStackTrace(null, exception)), eq(exception));


        mockListener.onStartupFailed(eqStartupTaskInfo(new StartupTaskEnum[]{DeferredBackgroundTestTaskEnum.TEST_1,
                DeferredBackgroundTestTaskEnum.TEST_3,
                DeferredBackgroundTestTaskEnum.TEST_2_BACKGROUND}),
                geq(0L));

        replay(mockListener);
        ClientRunner clientRunner = new ClientRunner();
        clientRunner.addStartupProgressListener(mockListener);
        clientRunner.start(StartupTestSeq.TEST_BACKGROUND);
        DeferredBackgroundTestTaskEnum.TEST_1.getTestDeferredStartupTask().finished();
        DeferredBackgroundTestTaskEnum.TEST_3.getTestDeferredStartupTask().finished();
        //noinspection ThrowableInstanceNeverThrown
        DeferredBackgroundTestTaskEnum.TEST_2_BACKGROUND.getTestDeferredStartupTask().failed(exception);
        DeferredBackgroundTestTaskEnum.TEST_4.getTestDeferredStartupTask().finished();

        verify(mockListener);
    }

    @Test
    public void runMultiStartup() {
        StartupProgressListener mockListener = createStrictMock(StartupProgressListener.class);
        mockListener.onStart(StartupTestSeq.TEST_MULTI);

        mockListener.onNextTask(MultiTestTaskEnum.SIMPLE);
        mockListener.onTaskFinished(eqAbstractStartupTask(MultiTestTaskEnum.SIMPLE));

        mockListener.onNextTask(MultiTestTaskEnum.DEFERRED);
        mockListener.onTaskFinished(eqAbstractStartupTask(MultiTestTaskEnum.DEFERRED));

        mockListener.onNextTask(MultiTestTaskEnum.SIMPLE_2);
        mockListener.onTaskFinished(eqAbstractStartupTask(MultiTestTaskEnum.SIMPLE_2));

        mockListener.onNextTask(MultiTestTaskEnum.DEFERRED_BACKGROUND_FINISH);
        mockListener.onTaskFinished(eqAbstractStartupTask(MultiTestTaskEnum.DEFERRED_BACKGROUND_FINISH));

        mockListener.onNextTask(MultiTestTaskEnum.SIMPLE_3);
        mockListener.onTaskFinished(eqAbstractStartupTask(MultiTestTaskEnum.SIMPLE_3));

        mockListener.onStartupFinished(eqStartupTaskInfo(StartupTestSeq.TEST_MULTI), geq(0L));

        replay(mockListener);
        ClientRunner clientRunner = new ClientRunner();
        clientRunner.addStartupProgressListener(mockListener);
        clientRunner.start(StartupTestSeq.TEST_MULTI);
        MultiTestTaskEnum.DEFERRED.getTestDeferredStartupTask().finished();
        verify(mockListener);
    }

    public static List<StartupTaskInfo> eqStartupTaskInfo(StartupSeq startupSeq) {
        EasyMock.reportMatcher(new StartupTaskInfoEquals(startupSeq));
        return null;
    }

    public static List<StartupTaskInfo> eqStartupTaskInfo(StartupTaskEnum[] taskEnum) {
        EasyMock.reportMatcher(new StartupTaskInfoEquals(taskEnum));
        return null;
    }

    public static class StartupTaskInfoEquals implements IArgumentMatcher {
        private String errorString;
        private StartupTaskEnum[] taskEnum;

        public StartupTaskInfoEquals(StartupSeq startupSeq) {
            taskEnum = startupSeq.getAbstractStartupTaskEnum();
        }

        public StartupTaskInfoEquals(StartupTaskEnum[] taskEnum) {
            this.taskEnum = taskEnum;
        }

        @Override
        public boolean matches(Object o) {
            List<StartupTaskInfo> startupTaskInfo = (List<StartupTaskInfo>) o;
            for (int i = 0, startupTaskInfoSize = startupTaskInfo.size(); i < startupTaskInfoSize; i++) {
                StartupTaskInfo taskInfo = startupTaskInfo.get(i);
                if (taskInfo.getDuration() < 0) {
                    errorString = "Invalid duration: " + taskInfo.getDuration();
                    return false;
                }
                if (taskInfo.getTaskEnum() != taskEnum[i]) {
                    errorString = "Expected task enum: " + taskEnum[i] + " actual task enum: " + taskInfo.getTaskEnum();
                    return false;
                }
            }
            return true;
        }

        @Override
        public void appendTo(StringBuffer stringBuffer) {
            stringBuffer.append(errorString);
        }
    }

    public static AbstractStartupTask eqAbstractStartupTask(StartupTaskEnum taskEnum) {
        EasyMock.reportMatcher(new AbstractStartupTaskEquals(taskEnum));
        return null;
    }

    public static class AbstractStartupTaskEquals implements IArgumentMatcher {
        private StartupTaskEnum taskEnum;
        private String errorString;

        public AbstractStartupTaskEquals(StartupTaskEnum taskEnum) {
            this.taskEnum = taskEnum;
        }

        @Override
        public boolean matches(Object o) {
            AbstractStartupTask abstractStartupTask = (AbstractStartupTask) o;
            if (abstractStartupTask.getDuration() < 0) {
                errorString = "Invalid duration: " + abstractStartupTask.getDuration();
                return false;
            }
            if (abstractStartupTask.getTaskEnum() != taskEnum) {
                errorString = "Expected task enum: " + taskEnum + " actual task enum: " + abstractStartupTask.getTaskEnum();
                return false;
            }
            return true;
        }

        @Override
        public void appendTo(StringBuffer stringBuffer) {
            stringBuffer.append(errorString);
        }
    }


}
