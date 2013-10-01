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

/**
 * TODO.
 */
public class RunnableItem implements Comparable<RunnableItem> {
    private TimerRunnable timerRunnable;
    private long scheduleRepeatingDelay;
    private boolean exclusive = false;
    private long nextExecutionTime;
    private boolean runOnce = false;

    public RunnableItem(TimerRunnable timerRunnable, long scheduleRepeatingDelay, boolean exclusive) {
        this.timerRunnable = timerRunnable;
        this.scheduleRepeatingDelay = scheduleRepeatingDelay;
        this.exclusive = exclusive;
    }

    public RunnableItem(TimerRunnable timerRunnable, int delay) {
        this.timerRunnable = timerRunnable;
        runOnce = true;
        scheduleRepeatingDelay = delay;
    }

    public long getNextExecutionTime() {
        return nextExecutionTime;
    }

    public void updateNextExecutionTime() {
        nextExecutionTime = System.currentTimeMillis() + scheduleRepeatingDelay;
    }

    public TimerRunnable getTimerRunnable() {
        return timerRunnable;
    }

    public long getScheduleRepeatingDelay() {
        return scheduleRepeatingDelay;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public boolean isRunOnce() {
        return runOnce;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RunnableItem)) {
            return false;
        }

        RunnableItem that = (RunnableItem) o;

        if (!timerRunnable.equals(that.timerRunnable)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return timerRunnable.hashCode();
    }

    public int compareTo(RunnableItem o) {
        if (this == o) {
            //Only return 0 if it is the same object otherwise the object will not be added
            return 0;
        } else if (nextExecutionTime > o.nextExecutionTime) {
            return -1;
        } else {
            return 1;
        }
    }
}
