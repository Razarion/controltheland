package com.btxtech.game.jsre.client.control;

import com.btxtech.game.jsre.client.control.task.AbstractStartupTask;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.junit.Test;

import java.util.List;

import static org.easymock.EasyMock.*;

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
        mockListener.onStart();

        mockListener.onNextTask(TestSimpleTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestSimpleTaskEnum.TEST_1));

        mockListener.onNextTask(TestSimpleTaskEnum.TEST_2);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestSimpleTaskEnum.TEST_2));

        mockListener.onNextTask(TestSimpleTaskEnum.TEST_3);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestSimpleTaskEnum.TEST_3));

        mockListener.onStartupFinished(eqStartupTaskInfo(TestStartupSeq.TEST_SIMPLE), geq(0L));
        replay(mockListener);
        ClientRunner clientRunner = new ClientRunner();
        clientRunner.addStartupProgressListener(mockListener);
        clientRunner.start(TestStartupSeq.TEST_SIMPLE);
        verify(mockListener);
    }

    @Test
    public void runDeferred() {
        StartupProgressListener mockListener = createStrictMock(StartupProgressListener.class);
        mockListener.onStart();

        mockListener.onNextTask(TestDeferredTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredTaskEnum.TEST_1));

        mockListener.onNextTask(TestDeferredTaskEnum.TEST_2);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredTaskEnum.TEST_2));

        mockListener.onNextTask(TestDeferredTaskEnum.TEST_3);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredTaskEnum.TEST_3));

        mockListener.onStartupFinished(eqStartupTaskInfo(TestStartupSeq.TEST_DEFERRED), geq(0L));
        replay(mockListener);
        ClientRunner clientRunner = new ClientRunner();
        clientRunner.addStartupProgressListener(mockListener);
        clientRunner.start(TestStartupSeq.TEST_DEFERRED);
        TestDeferredTaskEnum.TEST_1.getTestDeferredStartupTask().finished();
        TestDeferredTaskEnum.TEST_2.getTestDeferredStartupTask().finished();
        TestDeferredTaskEnum.TEST_3.getTestDeferredStartupTask().finished();

        verify(mockListener);
    }

    @Test
    public void runDeferredBackground() {
        StartupProgressListener mockListener = createStrictMock(StartupProgressListener.class);
        mockListener.onStart();

        mockListener.onNextTask(TestDeferredBackgroundTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredBackgroundTaskEnum.TEST_1));

        mockListener.onNextTask(TestDeferredBackgroundTaskEnum.TEST_2_BACKGROUND);

        mockListener.onNextTask(TestDeferredBackgroundTaskEnum.TEST_3);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredBackgroundTaskEnum.TEST_3));

        mockListener.onNextTask(TestDeferredBackgroundTaskEnum.TEST_4);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredBackgroundTaskEnum.TEST_4));

        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredBackgroundTaskEnum.TEST_2_BACKGROUND));


        mockListener.onStartupFinished(eqStartupTaskInfo(new StartupTaskEnum[]{TestDeferredBackgroundTaskEnum.TEST_1,
                TestDeferredBackgroundTaskEnum.TEST_3,
                TestDeferredBackgroundTaskEnum.TEST_4,
                TestDeferredBackgroundTaskEnum.TEST_2_BACKGROUND}), geq(0L));

        replay(mockListener);
        ClientRunner clientRunner = new ClientRunner();
        clientRunner.addStartupProgressListener(mockListener);
        clientRunner.start(TestStartupSeq.TEST_BACKGROUND);
        TestDeferredBackgroundTaskEnum.TEST_1.getTestDeferredStartupTask().finished();
        TestDeferredBackgroundTaskEnum.TEST_3.getTestDeferredStartupTask().finished();
        TestDeferredBackgroundTaskEnum.TEST_4.getTestDeferredStartupTask().finished();
        TestDeferredBackgroundTaskEnum.TEST_2_BACKGROUND.getTestDeferredStartupTask().finished();

        verify(mockListener);
    }

    @Test
    public void runDeferredFinished() {
        StartupProgressListener mockListener = createStrictMock(StartupProgressListener.class);
        mockListener.onStart();

        mockListener.onNextTask(TestDeferredFinishTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredFinishTaskEnum.TEST_1));

        mockListener.onNextTask(TestDeferredFinishTaskEnum.TEST_2_FINISH);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredFinishTaskEnum.TEST_2_FINISH));

        mockListener.onNextTask(TestDeferredFinishTaskEnum.TEST_3);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredFinishTaskEnum.TEST_3));


        mockListener.onStartupFinished(eqStartupTaskInfo(TestStartupSeq.TEST_DEFERRED_FINISH), geq(0L));

        replay(mockListener);
        ClientRunner clientRunner = new ClientRunner();
        clientRunner.addStartupProgressListener(mockListener);
        clientRunner.start(TestStartupSeq.TEST_DEFERRED_FINISH);
        TestDeferredFinishTaskEnum.TEST_1.getTestDeferredStartupTask().finished();
        TestDeferredFinishTaskEnum.TEST_3.getTestDeferredStartupTask().finished();
        verify(mockListener);
    }

    @Test
    public void runDeferredBackgroundFinished() {
        StartupProgressListener mockListener = createStrictMock(StartupProgressListener.class);
        mockListener.onStart();

        mockListener.onNextTask(TestDeferredBackgroundFinishTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredBackgroundFinishTaskEnum.TEST_1));

        mockListener.onNextTask(TestDeferredBackgroundFinishTaskEnum.TEST_2_BACKGROUND);

        mockListener.onNextTask(TestDeferredBackgroundFinishTaskEnum.TEST_3_DEFERRED_BACKGROUND_FINISH);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredBackgroundFinishTaskEnum.TEST_3_DEFERRED_BACKGROUND_FINISH));

        mockListener.onNextTask(TestDeferredBackgroundFinishTaskEnum.TEST_4_DEFERRED_FINISH);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredBackgroundFinishTaskEnum.TEST_4_DEFERRED_FINISH));

        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredBackgroundFinishTaskEnum.TEST_2_BACKGROUND));

        mockListener.onStartupFinished(eqStartupTaskInfo(new StartupTaskEnum[]{TestDeferredBackgroundFinishTaskEnum.TEST_1,
                TestDeferredBackgroundFinishTaskEnum.TEST_3_DEFERRED_BACKGROUND_FINISH,
                TestDeferredBackgroundFinishTaskEnum.TEST_4_DEFERRED_FINISH,
                TestDeferredBackgroundFinishTaskEnum.TEST_2_BACKGROUND
        }), geq(0L));

        replay(mockListener);
        ClientRunner clientRunner = new ClientRunner();
        clientRunner.addStartupProgressListener(mockListener);
        clientRunner.start(TestStartupSeq.TEST_DEFERRED_BACKGROUND_FINISH);
        TestDeferredBackgroundFinishTaskEnum.TEST_1.getTestDeferredStartupTask().finished();
        TestDeferredBackgroundFinishTaskEnum.TEST_2_BACKGROUND.getTestDeferredStartupTask().finished();
        verify(mockListener);
    }

    @Test
    public void runMultiple() {
        StartupProgressListener mockListener = createStrictMock(StartupProgressListener.class);
        // 1
        mockListener.onStart();

        mockListener.onNextTask(TestSimpleTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestSimpleTaskEnum.TEST_1));

        mockListener.onNextTask(TestSimpleTaskEnum.TEST_2);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestSimpleTaskEnum.TEST_2));

        mockListener.onNextTask(TestSimpleTaskEnum.TEST_3);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestSimpleTaskEnum.TEST_3));

        mockListener.onStartupFinished(eqStartupTaskInfo(TestStartupSeq.TEST_SIMPLE), geq(0L));

        // 2
        mockListener.onStart();

        mockListener.onNextTask(TestDeferredTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredTaskEnum.TEST_1));

        mockListener.onNextTask(TestDeferredTaskEnum.TEST_2);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredTaskEnum.TEST_2));

        mockListener.onNextTask(TestDeferredTaskEnum.TEST_3);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredTaskEnum.TEST_3));

        mockListener.onStartupFinished(eqStartupTaskInfo(TestStartupSeq.TEST_DEFERRED), geq(0L));

        // 3
        mockListener.onStart();

        mockListener.onNextTask(TestDeferredBackgroundTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredBackgroundTaskEnum.TEST_1));

        mockListener.onNextTask(TestDeferredBackgroundTaskEnum.TEST_2_BACKGROUND);

        mockListener.onNextTask(TestDeferredBackgroundTaskEnum.TEST_3);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredBackgroundTaskEnum.TEST_3));

        mockListener.onNextTask(TestDeferredBackgroundTaskEnum.TEST_4);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredBackgroundTaskEnum.TEST_4));

        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredBackgroundTaskEnum.TEST_2_BACKGROUND));


        mockListener.onStartupFinished(eqStartupTaskInfo(new StartupTaskEnum[]{TestDeferredBackgroundTaskEnum.TEST_1,
                TestDeferredBackgroundTaskEnum.TEST_3,
                TestDeferredBackgroundTaskEnum.TEST_4,
                TestDeferredBackgroundTaskEnum.TEST_2_BACKGROUND}), geq(0L));

        // 4
        mockListener.onStart();

        mockListener.onNextTask(TestDeferredFinishTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredFinishTaskEnum.TEST_1));

        mockListener.onNextTask(TestDeferredFinishTaskEnum.TEST_2_FINISH);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredFinishTaskEnum.TEST_2_FINISH));

        mockListener.onNextTask(TestDeferredFinishTaskEnum.TEST_3);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredFinishTaskEnum.TEST_3));


        mockListener.onStartupFinished(eqStartupTaskInfo(TestStartupSeq.TEST_DEFERRED_FINISH), geq(0L));

        // 5
        mockListener.onStart();

        mockListener.onNextTask(TestDeferredBackgroundFinishTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredBackgroundFinishTaskEnum.TEST_1));

        mockListener.onNextTask(TestDeferredBackgroundFinishTaskEnum.TEST_2_BACKGROUND);

        mockListener.onNextTask(TestDeferredBackgroundFinishTaskEnum.TEST_3_DEFERRED_BACKGROUND_FINISH);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredBackgroundFinishTaskEnum.TEST_3_DEFERRED_BACKGROUND_FINISH));

        mockListener.onNextTask(TestDeferredBackgroundFinishTaskEnum.TEST_4_DEFERRED_FINISH);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredBackgroundFinishTaskEnum.TEST_4_DEFERRED_FINISH));

        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredBackgroundFinishTaskEnum.TEST_2_BACKGROUND));

        mockListener.onStartupFinished(eqStartupTaskInfo(new StartupTaskEnum[]{TestDeferredBackgroundFinishTaskEnum.TEST_1,
                TestDeferredBackgroundFinishTaskEnum.TEST_3_DEFERRED_BACKGROUND_FINISH,
                TestDeferredBackgroundFinishTaskEnum.TEST_4_DEFERRED_FINISH,
                TestDeferredBackgroundFinishTaskEnum.TEST_2_BACKGROUND
        }), geq(0L));

        replay(mockListener);
        ClientRunner clientRunner = new ClientRunner();
        clientRunner.addStartupProgressListener(mockListener);

        // 1
        clientRunner.start(TestStartupSeq.TEST_SIMPLE);

        // 2
        clientRunner.start(TestStartupSeq.TEST_DEFERRED);
        TestDeferredTaskEnum.TEST_1.getTestDeferredStartupTask().finished();
        TestDeferredTaskEnum.TEST_2.getTestDeferredStartupTask().finished();
        TestDeferredTaskEnum.TEST_3.getTestDeferredStartupTask().finished();

        // 3
        clientRunner.start(TestStartupSeq.TEST_BACKGROUND);
        TestDeferredBackgroundTaskEnum.TEST_1.getTestDeferredStartupTask().finished();
        TestDeferredBackgroundTaskEnum.TEST_3.getTestDeferredStartupTask().finished();
        TestDeferredBackgroundTaskEnum.TEST_4.getTestDeferredStartupTask().finished();
        TestDeferredBackgroundTaskEnum.TEST_2_BACKGROUND.getTestDeferredStartupTask().finished();

        // 4
        clientRunner.start(TestStartupSeq.TEST_DEFERRED_FINISH);
        TestDeferredFinishTaskEnum.TEST_1.getTestDeferredStartupTask().finished();
        TestDeferredFinishTaskEnum.TEST_3.getTestDeferredStartupTask().finished();

        // 5
        clientRunner.start(TestStartupSeq.TEST_DEFERRED_BACKGROUND_FINISH);
        TestDeferredBackgroundFinishTaskEnum.TEST_1.getTestDeferredStartupTask().finished();
        TestDeferredBackgroundFinishTaskEnum.TEST_2_BACKGROUND.getTestDeferredStartupTask().finished();

        verify(mockListener);
    }

    @Test
    public void runSimpleException() {
        StartupProgressListener mockListener = createStrictMock(StartupProgressListener.class);
        mockListener.onStart();

        mockListener.onNextTask(TestSimpleExceptionTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestSimpleExceptionTaskEnum.TEST_1));

        mockListener.onNextTask(TestSimpleExceptionTaskEnum.TEST_2_EXCEPTION);
        mockListener.onTaskFailed(eqAbstractStartupTask(TestSimpleExceptionTaskEnum.TEST_2_EXCEPTION), contains(TestSimpleExceptionStartupTask.ERROR_STRING));

        mockListener.onStartupFailed(eqStartupTaskInfo(new StartupTaskEnum[]{TestSimpleExceptionTaskEnum.TEST_1, TestSimpleExceptionTaskEnum.TEST_2_EXCEPTION}), geq(0L));
        replay(mockListener);
        ClientRunner clientRunner = new ClientRunner();
        clientRunner.addStartupProgressListener(mockListener);
        clientRunner.start(TestStartupSeq.TEST_SIMPLE_EXCEPTION);
        verify(mockListener);
    }

    @Test
    public void runDeferredException() {
        StartupProgressListener mockListener = createStrictMock(StartupProgressListener.class);
        mockListener.onStart();

        mockListener.onNextTask(TestDeferredBackgroundTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredBackgroundTaskEnum.TEST_1));

        mockListener.onNextTask(TestDeferredBackgroundTaskEnum.TEST_2_BACKGROUND);

        mockListener.onNextTask(TestDeferredBackgroundTaskEnum.TEST_3);
        mockListener.onTaskFailed(eqAbstractStartupTask(TestDeferredBackgroundTaskEnum.TEST_3), contains(ERROR_TEXT));


        mockListener.onStartupFailed(eqStartupTaskInfo(new StartupTaskEnum[]{TestDeferredBackgroundTaskEnum.TEST_1,
                TestDeferredBackgroundTaskEnum.TEST_3,
                TestDeferredBackgroundTaskEnum.TEST_4}),
                geq(0L));

        replay(mockListener);
        ClientRunner clientRunner = new ClientRunner();
        clientRunner.addStartupProgressListener(mockListener);
        clientRunner.start(TestStartupSeq.TEST_BACKGROUND);
        TestDeferredBackgroundTaskEnum.TEST_1.getTestDeferredStartupTask().finished();
        TestDeferredBackgroundTaskEnum.TEST_3.getTestDeferredStartupTask().failed(ERROR_TEXT);
        TestDeferredBackgroundTaskEnum.TEST_2_BACKGROUND.getTestDeferredStartupTask().finished();

        verify(mockListener);
    }

    @Test
    public void runDeferredBackgroundException() {
        StartupProgressListener mockListener = createStrictMock(StartupProgressListener.class);
        mockListener.onStart();

        mockListener.onNextTask(TestDeferredBackgroundTaskEnum.TEST_1);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredBackgroundTaskEnum.TEST_1));

        mockListener.onNextTask(TestDeferredBackgroundTaskEnum.TEST_2_BACKGROUND);

        mockListener.onNextTask(TestDeferredBackgroundTaskEnum.TEST_3);
        mockListener.onTaskFinished(eqAbstractStartupTask(TestDeferredBackgroundTaskEnum.TEST_3));

        mockListener.onNextTask(TestDeferredBackgroundTaskEnum.TEST_4);

        mockListener.onTaskFailed(eqAbstractStartupTask(TestDeferredBackgroundTaskEnum.TEST_2_BACKGROUND), contains(ERROR_TEXT));


        mockListener.onStartupFailed(eqStartupTaskInfo(new StartupTaskEnum[]{TestDeferredBackgroundTaskEnum.TEST_1,
                TestDeferredBackgroundTaskEnum.TEST_3,
                TestDeferredBackgroundTaskEnum.TEST_2_BACKGROUND}),
                geq(0L));

        replay(mockListener);
        ClientRunner clientRunner = new ClientRunner();
        clientRunner.addStartupProgressListener(mockListener);
        clientRunner.start(TestStartupSeq.TEST_BACKGROUND);
        TestDeferredBackgroundTaskEnum.TEST_1.getTestDeferredStartupTask().finished();
        TestDeferredBackgroundTaskEnum.TEST_3.getTestDeferredStartupTask().finished();
        TestDeferredBackgroundTaskEnum.TEST_2_BACKGROUND.getTestDeferredStartupTask().failed(ERROR_TEXT);
        TestDeferredBackgroundTaskEnum.TEST_4.getTestDeferredStartupTask().finished();

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
