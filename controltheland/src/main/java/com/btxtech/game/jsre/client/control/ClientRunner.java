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

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.control.task.AbstractStartupTask;
import com.btxtech.game.jsre.client.control.task.DeferredStartup;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.StartupTaskInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 04.12.2010
 * Time: 10:56:33
 */
public class ClientRunner {
    private Collection<StartupProgressListener> listeners = new ArrayList<StartupProgressListener>();
    private List<AbstractStartupTask> startupList = new ArrayList<AbstractStartupTask>();
    private List<DeferredStartup> deferredStartups = new ArrayList<DeferredStartup>();
    private List<AbstractStartupTask> finishedTasks = new ArrayList<AbstractStartupTask>();
    private boolean failed;
    private Logger log = Logger.getLogger(ClientRunner.class.getName());
    private String startUuid;

    public void addStartupProgressListener(StartupProgressListener startupProgressListener) {
        listeners.add(startupProgressListener);
    }

    public void removeStartupProgressListener(StartupProgressListener startupProgressListener) {
        listeners.remove(startupProgressListener);
    }

    public void cleanupBeforeTest() {
        listeners.clear();
    }

    public void start(StartupSeq startupSeq) {
        startUuid = MathHelper.generateUuid();
        failed = false;
        for (StartupProgressListener listener : listeners) {
            listener.onStart(startupSeq);
        }
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
            for (StartupProgressListener listener : listeners) {
                try {
                    listener.onNextTask(task.getTaskEnum());
                } catch (Throwable t) {
                    log.log(Level.SEVERE, "", t);
                }
            }
            try {
                task.start(deferredStartup);
            } catch (Throwable t) {
                onTaskFailed(task, t);
                return;
            }
            if (deferredStartup.isDeferred()) {
                if (deferredStartup.isFinished()) {
                    onTaskFinished(task);
                } else {
                    deferredStartups.add(deferredStartup);
                }

                if (deferredStartup.isBackground()) {
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

    private List<StartupTaskInfo> createTaskInfo(AbstractStartupTask failedTask, String error) {
        List<StartupTaskInfo> infos = new ArrayList<StartupTaskInfo>();
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
            if (!listeners.isEmpty()) {
                long totalTime = finishedTasks.isEmpty() ? 0 : System.currentTimeMillis() - finishedTasks.get(0).getStartTime();
                List<StartupTaskInfo> startupTaskInfos = createTaskInfo(null, null);
                for (StartupProgressListener listener : listeners) {
                    listener.onStartupFinished(startupTaskInfos, totalTime);
                }
            }
            cleanup();
        }
    }


    void onTaskFinished(AbstractStartupTask task, DeferredStartup deferredStartup) {
        if (deferredStartups.remove(deferredStartup)) {
            onTaskFinished(task);
        }
    }

    void onTaskFinished(AbstractStartupTask abstractStartupTask) {
        if (failed) {
            return;
        }
        for (StartupProgressListener listener : listeners) {
            listener.onTaskFinished(abstractStartupTask);
        }

        finishedTasks.add(abstractStartupTask);
        if (!abstractStartupTask.isBackground()) {
            runNextTask();
        } else if (startupList.isEmpty()) {
            onStartupFinish();
        }
    }

    void onTaskFailed(AbstractStartupTask abstractStartupTask, String error, Throwable t) {
        if (failed) {
            return;
        }
        failed = true;
        if (listeners.isEmpty()) {
            log.severe("ClientRunner.onTaskFailed(): " + error);
        } else {
            for (StartupProgressListener listener : listeners) {
                listener.onTaskFailed(abstractStartupTask, error, t);
            }

            long totalTime = System.currentTimeMillis() - (finishedTasks.isEmpty() ? abstractStartupTask.getStartTime() : finishedTasks.get(0).getStartTime());
            List<StartupTaskInfo> startupTaskInfos = createTaskInfo(abstractStartupTask, error);
            for (StartupProgressListener listener : listeners) {
                listener.onStartupFailed(startupTaskInfos, totalTime);
            }
        }
        cleanup();
    }

    void onTaskFailed(AbstractStartupTask abstractStartupTask, Throwable t) {
        onTaskFailed(abstractStartupTask, GwtCommon.setupStackTrace(null, t), t);
    }

    private void setupStartupSeq(StartupSeq startupSeq) {
        startupList.clear();
        for (StartupTaskEnum startupTaskEnum : startupSeq.getAbstractStartupTaskEnum()) {
            startupList.add(startupTaskEnum.createTask());
        }
    }

    public String getStartUuid() {
        return startUuid;
    }
}
