/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.jsre.client.control;

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.control.task.AbstractStartupTask;
import com.btxtech.game.jsre.client.control.task.DeferredStartup;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 04.12.2010
 * Time: 10:56:33
 */
public class ClientRunner {
    private static ClientRunner INSTANCE = new ClientRunner();
    private long startupTimeStamp = System.currentTimeMillis();

    public static ClientRunner getInstance() {
        return INSTANCE;
    }

    private List<AbstractStartupTask> startupList = new ArrayList<AbstractStartupTask>();
    private List<DeferredStartup> deferredStartups = new ArrayList<DeferredStartup>();
    private List<AbstractStartupTask> finishedTasks = new ArrayList<AbstractStartupTask>();
    private boolean failed;

    /**
     * Singleton
     */
    private ClientRunner() {
    }

    public void start(StartupSeq startupSeq) {
        failed = false;
        StartupScreen.getInstance().setupScreen(startupSeq);
        setupStartupSeq(startupSeq);
        runNextTask();
    }

    private void runNextTask() {
        if (failed) {
            return;
        }
        if (startupList.isEmpty()) {
            onStartupFinish();
        } else {
            AbstractStartupTask task = startupList.remove(0);
            ClientRunnerDeferredStartupImpl deferredStartup = new ClientRunnerDeferredStartupImpl(task, this);
            StartupScreen.getInstance().displayTaskRunning(task.getTaskEnum());
            try {
                task.start(deferredStartup);
            } catch (Throwable t) {
                onTaskFailed(task, t);
                return;
            }
            if (deferredStartup.isDeferred()) {
                deferredStartups.add(deferredStartup);
                if (deferredStartup.isParallel()) {
                    runNextTask();
                }
            } else {
                onTaskFinished(task);
            }
        }
    }

    private void cleanup() {
        finishedTasks.clear();
        startupList.clear();
        deferredStartups.clear();
    }

    private Collection<StartupTaskInfo> createTaskInfo(AbstractStartupTask failedTask, String error) {
        Collection<StartupTaskInfo> infos = new ArrayList<StartupTaskInfo>();
        for (AbstractStartupTask finishedTask : finishedTasks) {
            infos.add(finishedTask.createStartupTaskInfo());
        }
        if (failedTask != null && error != null) {
            StartupTaskInfo failedTaskInfo = failedTask.createStartupTaskInfo();
            failedTaskInfo.setErrorText(error);
            infos.add(failedTaskInfo);
        }
        return infos;
    }


    private void onStartupFinish() {
        if (failed) {
            return;
        }
        if (deferredStartups.isEmpty()) {
            long totalTime = finishedTasks.isEmpty() ? 0 : System.currentTimeMillis() - finishedTasks.get(0).getStartTime();
            Connection.getInstance().sendStartupFinished(createTaskInfo(null, null), totalTime);
            cleanup();
            StartupScreen.getInstance().showCloseButton();
            StartupScreen.getInstance().hideStartScreen();
        }
    }


    void onTaskFinished(AbstractStartupTask task, DeferredStartup deferredStartup) {
        deferredStartups.remove(deferredStartup);
        onTaskFinished(task);
    }

    void onTaskFinished(AbstractStartupTask abstractStartupTask) {
        try {
            StartupScreen.getInstance().displayTaskFinished(abstractStartupTask);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        finishedTasks.add(abstractStartupTask);
        runNextTask();
    }

    void onTaskFailed(AbstractStartupTask abstractStartupTask, String error) {
        if (failed) {
            return;
        }
        failed = true;
        long totalTime = System.currentTimeMillis() - (finishedTasks.isEmpty() ? abstractStartupTask.getStartTime() : finishedTasks.get(0).getStartTime());
        Connection.getInstance().sendStartupFinished(createTaskInfo(abstractStartupTask, error), totalTime);
        cleanup();
        StartupScreen.getInstance().showCloseButton();
        StartupScreen.getInstance().hideStartScreen();
    }

    void onTaskFailed(AbstractStartupTask abstractStartupTask, Throwable t) {
        onTaskFailed(abstractStartupTask, GwtCommon.setupStackTrace(null, t));
    }

    private void setupStartupSeq(StartupSeq startupSeq) {
        startupList.clear();
        for (StartupTaskEnum startupTaskEnum : startupSeq.getAbstractStartupTaskEnum()) {
            startupList.add(startupTaskEnum.createTask());
        }
    }

    public long getStartupTimeStamp() {
        return startupTimeStamp;
    }
}
