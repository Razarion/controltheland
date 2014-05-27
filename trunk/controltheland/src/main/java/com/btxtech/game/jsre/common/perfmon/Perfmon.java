package com.btxtech.game.jsre.common.perfmon;

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.common.ClientDateUtil;
import com.google.gwt.user.client.Timer;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 26.07.12
 * Time: 14:45
 */
public class Perfmon {
    private static final Perfmon INSTANCE = new Perfmon();
    private static final int SENDING_DELAY_MINUTES = 3;
    private Map<PerfmonEnum, Long> enterTimes = new HashMap<>();
    private Map<PerfmonEnum, Map<String, Long>> enterChildTimes = new HashMap<>();
    private Map<PerfmonEnum, Integer> workTimes = new HashMap<>();
    private Map<PerfmonEnum, Map<String, Integer>> workChildTimes = new HashMap<>();
    private Logger log = Logger.getLogger(Perfmon.class.getName());
    private long startTime;

    public static Perfmon getInstance() {
        return INSTANCE;
    }

    private Perfmon() {
        startTime = System.currentTimeMillis();
    }

    public void startTransmit(Integer delayInSeconds) {
        Timer timer = new TimerPerfmon(PerfmonEnum.PERFMON) {
            @Override
            public void runPerfmon() {
                try {
                    Connection.getInstance().sendPerfmonData(workTimes, workChildTimes, getTotalTime());
                } catch (Exception e) {
                    log.log(Level.SEVERE, "Perfmon: sendPerfmonData", e);
                }
            }
        };
        if (delayInSeconds != null) {
            timer.scheduleRepeating((int) ClientDateUtil.MILLIS_IN_SECOND * delayInSeconds);
        } else {
            timer.scheduleRepeating((int) ClientDateUtil.MILLIS_IN_MINUTE * SENDING_DELAY_MINUTES);
        }
    }

    public void onEntered(PerfmonEnum perfmonEnum, String childName) {
        if (childName != null) {
            Map<String, Long> childMap = enterChildTimes.get(perfmonEnum);
            if (childMap == null) {
                childMap = new HashMap<>();
                enterChildTimes.put(perfmonEnum, childMap);
            }
            if (childMap.containsKey(childName)) {
                log.warning("Perfmon.onEntered(): onEntered for child has already been called for " + perfmonEnum + "+" + childName);
            }
            childMap.put(childName, System.currentTimeMillis());
        } else {
            if (enterTimes.containsKey(perfmonEnum)) {
                log.warning("Perfmon.onEntered(): onEntered has already been called for " + perfmonEnum);
            }
            enterTimes.put(perfmonEnum, System.currentTimeMillis());
        }
    }

    public void onEntered(PerfmonEnum perfmonEnum) {
        onEntered(perfmonEnum, null);
    }

    public void onLeft(PerfmonEnum perfmonEnum, String childName) {
        if (childName != null) {
            Map<String, Long> childMap = enterChildTimes.get(perfmonEnum);
            if (childMap == null) {
                log.warning("Perfmon.onLeft(): onEntered for child 1 was not called before " + perfmonEnum + "+" + childName);
                return;
            }
            Long startTime = childMap.remove(childName);
            if (startTime == null) {
                log.warning("Perfmon.onLeft(): onEntered  for child 2 was not called before " + perfmonEnum + "+" + childName);
                return;
            }
            if (childMap.isEmpty()) {
                enterChildTimes.remove(perfmonEnum);
            }
            Map<String, Integer> childWorkMap = workChildTimes.get(perfmonEnum);
            if (childWorkMap == null) {
                childWorkMap = new HashMap<>();
                workChildTimes.put(perfmonEnum, childWorkMap);
            }
            Integer workTime = childWorkMap.get(childName);
            if (workTime == null) {
                workTime = 0;
            }
            childWorkMap.put(childName, workTime + (int) (System.currentTimeMillis() - startTime));
        } else {
            Long startTime = enterTimes.remove(perfmonEnum);
            if (startTime == null) {
                log.warning("Perfmon.onLeft(): onEntered was not called before " + perfmonEnum);
                return;
            }
            Integer workTime = workTimes.get(perfmonEnum);
            if (workTime == null) {
                workTime = 0;
            }
            workTimes.put(perfmonEnum, workTime + (int) (System.currentTimeMillis() - startTime));
        }
    }

    public void onLeft(PerfmonEnum perfmonEnum) {
        onLeft(perfmonEnum, null);
    }

    public Map<PerfmonEnum, Integer> getSummary() {
        return new HashMap<>(workTimes);
    }

    public int getTotalTime() {
        return (int) (System.currentTimeMillis() - startTime);
    }
}
