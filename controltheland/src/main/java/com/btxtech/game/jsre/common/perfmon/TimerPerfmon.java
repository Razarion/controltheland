package com.btxtech.game.jsre.common.perfmon;


import com.google.gwt.user.client.Timer;

/**
 * User: beat
 * Date: 26.07.12
 * Time: 22:17
 */
public abstract class TimerPerfmon extends Timer {
    private PerfmonEnum perfmonEnum;

    protected TimerPerfmon(PerfmonEnum perfmonEnum) {
        this.perfmonEnum = perfmonEnum;
    }

    @Override
    final public void run() {
        try {
            Perfmon.getInstance().onEntered(perfmonEnum);
            runPerfmon();
        } finally {
            Perfmon.getInstance().onLeft(perfmonEnum);
        }
    }

    public abstract void runPerfmon();
}
