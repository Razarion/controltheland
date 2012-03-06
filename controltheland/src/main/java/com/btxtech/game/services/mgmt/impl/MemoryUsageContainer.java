package com.btxtech.game.services.mgmt.impl;

import com.btxtech.game.services.mgmt.MemoryUsageHistory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: beat
 * Date: 06.03.2012
 * Time: 14:42:32
 */
public class MemoryUsageContainer {
    private final List<MemoryUsageEntry> memoryUsageEntries = new ArrayList<MemoryUsageEntry>();
    private int size;

    public MemoryUsageContainer(int size) {
        this.size = size;
    }

    public void addSample(Date date, long init, long used, long committed, long max) {
        synchronized (memoryUsageEntries) {
            memoryUsageEntries.add(new MemoryUsageEntry(date, init, used, committed, max));
            if (memoryUsageEntries.size() > size) {
                memoryUsageEntries.remove(0);
            }
        }
    }

    public MemoryUsageHistory generateMemoryUsageHistory() {
        List<Date> dates = new ArrayList<Date>();
        long maxValue = 0;
        List<Integer> inits = new ArrayList<Integer>();
        List<Integer> useds = new ArrayList<Integer>();
        List<Integer> committeds = new ArrayList<Integer>();
        List<Integer> maxs = new ArrayList<Integer>();

        synchronized (memoryUsageEntries) {
            for (MemoryUsageEntry memoryUsageEntry : memoryUsageEntries) {
                dates.add(memoryUsageEntry.getDate());
                maxValue = Math.max(maxValue, memoryUsageEntry.getCommitted());
                maxValue = Math.max(maxValue, memoryUsageEntry.getInit());
                maxValue = Math.max(maxValue, memoryUsageEntry.getMax());
                maxValue = Math.max(maxValue, memoryUsageEntry.getUsed());
            }

            for (MemoryUsageEntry memoryUsageEntry : memoryUsageEntries) {
                inits.add(calculate100Value(maxValue, memoryUsageEntry.getInit()));
                useds.add(calculate100Value(maxValue, memoryUsageEntry.getUsed()));
                committeds.add(calculate100Value(maxValue, memoryUsageEntry.getCommitted()));
                maxs.add(calculate100Value(maxValue, memoryUsageEntry.getMax()));
            }
        }

        return new MemoryUsageHistory((int) maxValue, dates, inits, useds, committeds, maxs);
    }

    private int calculate100Value(long maxValue, long committed) {
        return (int) ((double)committed / (double)maxValue * 100.0);
    }
}
