package com.btxtech.game.services.mgmt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: beat
 * Date: 05.03.2012
 * Time: 22:33:47
 */
public class MemoryUsageHistory {
    private List<Date> dates;
    private int maxValue;
    private List<Integer> inits;
    private List<Integer> useds;
    private List<Integer> committeds;
    private List<Integer> maxs;


    public MemoryUsageHistory(int maxValue, List<Date> dates, List<Integer> inits, List<Integer> useds, List<Integer> committeds, List<Integer> maxs) {
        this.maxValue = maxValue;
        this.dates = dates;
        this.inits = inits;
        this.useds = useds;
        this.committeds = committeds;
        this.maxs = maxs;
    }

    public List<Date> getDates() {
        return dates;
    }

    public List<Integer> getInits() {
        return inits;
    }

    public List<Integer> getUseds() {
        return useds;
    }

    public List<Integer> getCommitteds() {
        return committeds;
    }

    public List<Integer> getMaxs() {
        return maxs;
    }

    public List<String> getSignificantDates(int count, SimpleDateFormat simpleDateFormat) {
        if (count < 2) {
            throw new IllegalArgumentException("Count mus be bigger the 1");
        }
        List<String> significantDates = new ArrayList<String>();
        if (dates.size() == 0) {
            for (int i = 0; i < count; i++) {
                significantDates.add("?");
            }
        } else if (dates.size() == 1) {
            significantDates.add(simpleDateFormat.format(dates.get(0)));
            for (int i = 1; i < count; i++) {
                significantDates.add("?");
            }
        } else {
            Date start = dates.get(0);
            Date end = dates.get(dates.size() - 1);
            long delta = (end.getTime() - start.getTime()) / (count - 1);

            significantDates.add(simpleDateFormat.format(start));
            for (int i = 1; i < count - 1; i++) {
                significantDates.add(simpleDateFormat.format(new Date(start.getTime() + i * delta)));
            }
            significantDates.add(simpleDateFormat.format(end));
        }
        return significantDates;
    }

    public List<String> getSignificantValues(int count) {
        if (count < 2) {
            throw new IllegalArgumentException("Count mus be bigger the 1");
        }
        List<String> significantValues = new ArrayList<String>();
        significantValues.add("0");
        int delta = maxValue / (count - 1);
        for (int i = 1; i < count - 1; i++) {
            significantValues.add(Integer.toString(delta * i));
        }
        significantValues.add(Integer.toString(maxValue));
        return significantValues;
    }
}
