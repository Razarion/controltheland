package com.btxtech.game.jsre.client.utg.tip.dialog;

/**
 * User: beat
 * Date: 19.12.2011
 * Time: 17:29:27
 */
public abstract class TipEntry {
    private int delay;
    private boolean repeat;
    private int showTime;

    protected TipEntry(int delay, int showTime, boolean repeat) {
        this.delay = delay;
        this.showTime = showTime;
        this.repeat = repeat;
    }

    public TipEntry(int delay, int showTime) {
        this(delay, showTime, false);
    }

    public int getDelay() {
        return delay;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public int getShowTime() {
        return showTime;
    }

    public abstract void show();

    public abstract void close();
}
