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

package com.btxtech.game.jsre.common.timer;

import com.btxtech.game.jsre.client.GwtCommon;
import com.google.gwt.user.client.Timer;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * scheduleRepeating(int periodMillis)
 * schedule(int delayMillis)
 * abstract void run()
 * void cancel()
 */
public class TimerHelper {
    public static final long TOLERANCE = 20;
    private static final TimerHelper instance = new TimerHelper();
    private TreeSet<RunnableItem> items = new TreeSet<RunnableItem>();
    private Timer timer;

    public static TimerHelper getInstance() {
        return instance;
    }

    /**
     * Singleton
     */
    private TimerHelper() {
        timer = new Timer() {
            @Override
            public void run() {
                try {
                    handleExecution();
                } catch (Throwable t) {
                    GwtCommon.handleException(t);
                }
            }
        };
    }

    public void registerExecuter(int scheduleRepeatingTime, boolean exclusive, TimerRunnable timerRunnable) {
        RunnableItem runnableItem = new RunnableItem(timerRunnable, scheduleRepeatingTime, exclusive);
        runnableItem.updateNextExecutionTime();
        insert(runnableItem);
    }

    /**
     * // TODO does not work
     * @param delay
     * @param timerRunnable
     */
    public void registerSingleExecuter(int delay, TimerRunnable timerRunnable) {
        RunnableItem runnableItem = new RunnableItem(timerRunnable, delay);
        runnableItem.updateNextExecutionTime();
        insert(runnableItem);
    }

    public void unregisterExecuter(TimerRunnable timerRunnable) {
        for (Iterator<RunnableItem> it = items.iterator(); it.hasNext();) {
            RunnableItem item = it.next();
            if (item.getTimerRunnable().equals(timerRunnable)) {
                it.remove();
            }
        }
    }

    private void insert(RunnableItem runnableItem) {
        items.add(runnableItem);
        handleExecution();
    }

    private void handleExecution() {
        if (!items.isEmpty()) {
            RunnableItem runnableItem = items.last();
            if ((runnableItem.getNextExecutionTime() - System.currentTimeMillis()) <= TOLERANCE) {
                // TODO remove  registerSingleExecuter does not work               
                items.remove(runnableItem);
                if (!runnableItem.isExclusive()) {
                    runnableItem.updateNextExecutionTime();
                }
                try {
                    runnableItem.getTimerRunnable().execute();
                } catch (Throwable t) {
                    GwtCommon.handleException(t);
                }
                if (runnableItem.isExclusive()) {
                    runnableItem.updateNextExecutionTime();
                }
                if (runnableItem.isRunOnce()) {
                    handleExecution();
                } else {
                    insert(runnableItem);
                }
            } else {
                restartTimer(runnableItem);
            }
        }
    }

    private void restartTimer(RunnableItem runnableItem) {
        long delay = runnableItem.getNextExecutionTime() - System.currentTimeMillis();
        if (delay < 0) {
            delay = TOLERANCE;
        }
        timer.cancel();
        timer.schedule((int) delay);
    }

}
