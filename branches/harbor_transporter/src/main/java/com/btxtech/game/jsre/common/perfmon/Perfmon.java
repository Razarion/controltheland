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
    private Map<PerfmonEnum, Long> enterTimes = new HashMap<PerfmonEnum, Long>();
    private Map<PerfmonEnum, Integer> workTimes = new HashMap<PerfmonEnum, Integer>();
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
                    Connection.getInstance().sendPerfmonData(workTimes, getTotalTime());
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

    public void onEntered(PerfmonEnum perfmonEnum) {
        if (enterTimes.containsKey(perfmonEnum)) {
            log.warning("Perfmon.onEntered(): onEntered has already been called for " + perfmonEnum);
        }
        enterTimes.put(perfmonEnum, System.currentTimeMillis());
    }

    public void onLeft(PerfmonEnum perfmonEnum) {
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

    public Map<PerfmonEnum, Integer> getSummary() {
        return new HashMap<PerfmonEnum, Integer>(workTimes);
    }

    public int getTotalTime() {
        return (int) (System.currentTimeMillis() - startTime);
    }

}
