package com.btxtech.game.jsre.client.control;

import com.btxtech.game.jsre.client.control.task.AbstractStartupTask;
import com.btxtech.game.jsre.common.StartupTaskInfo;

import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 18.02.2011
 * Time: 22:53:59
 */
public interface StartupProgressListener {
    void onStart(StartupSeq startupSeq);

    void onNextTask(StartupTaskEnum taskEnum);

    void onTaskFinished(AbstractStartupTask task);

    void onTaskFailed(AbstractStartupTask task, String error, Throwable t);

    void onStartupFinished(List<StartupTaskInfo> taskInfo, long totalTime);

    void onStartupFailed(List<StartupTaskInfo> taskInfo, long totalTime);
}
