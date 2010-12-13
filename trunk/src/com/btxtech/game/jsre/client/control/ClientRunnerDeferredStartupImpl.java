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

import com.btxtech.game.jsre.client.control.task.AbstractStartupTask;
import com.btxtech.game.jsre.client.control.task.DeferredStartup;

/**
 * User: beat
 * Date: 08.12.2010
 * Time: 12:55:22
 */
public class ClientRunnerDeferredStartupImpl implements DeferredStartup {
    private boolean isDeferred;
    private boolean isParallel;
    private AbstractStartupTask task;
    private ClientRunner clientRunner;

    public ClientRunnerDeferredStartupImpl(AbstractStartupTask task, ClientRunner clientRunner) {
        this.task = task;
        this.clientRunner = clientRunner;
    }

    @Override
    public void setDeferred() {
        isDeferred = true;
    }

    @Override
    public void finished() {
        task.correctDeferredDuration();
        clientRunner.onTaskFinished(task, this);
    }

    @Override
    public void failed(Throwable t) {
        task.correctDeferredDuration();
        clientRunner.onTaskFailed(task, t);
    }

    @Override
    public void failed(String error) {
        task.correctDeferredDuration();
        clientRunner.onTaskFailed(task, error);
    }

    @Override
    public void setParallel() {
        isParallel = true;
    }

    public boolean isDeferred() {
        return isDeferred;
    }

    public boolean isParallel() {
        return isParallel;
    }

}
