package com.btxtech.game.services.mgmt.impl;

import java.util.Date;

/**
 * User: beat
 * Date: 06.03.2012
 * Time: 14:45:23
 */
public class MemoryUsageEntry {
    private Date date;
    private long init;
    private long used;
    private long committed;
    private long max;

    public MemoryUsageEntry(Date date, long init, long used, long committed, long max) {
        this.date = date;
        this.init = init;
        this.used = used;
        this.committed = committed;
        this.max = max;
    }

    public Date getDate() {
        return date;
    }

    public long getInit() {
        return init;
    }

    public long getUsed() {
        return used;
    }

    public long getCommitted() {
        return committed;
    }

    public long getMax() {
        return max;
    }
}
