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
    void onStart();

    void onNextTask(StartupTaskEnum taskEnum);

    void onTaskFinished(AbstractStartupTask task);

    void onTaskFailed(AbstractStartupTask task, String error);

    void onStartupFinished(List<StartupTaskInfo> taskInfo, long totalTime);

    void onStartupFailed(Collection<StartupTaskInfo> taskInfo, long totalTime);
}
