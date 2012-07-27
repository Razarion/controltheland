package com.btxtech.game.services.mgmt;

import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * User: beat
 * Date: 26.07.12
 * Time: 16:52
 */
public class ClientPerfmonDto {
    private String sessionId;
    private Date lastActivated;
    private Collection<ClientPerfmonEntry> workTimes;
    private int totalTime;

    public ClientPerfmonDto(String sessionId, Map<PerfmonEnum, Integer> workTimes, int totalTime) {
        this.sessionId = sessionId;
        setWorkTimes(workTimes, totalTime);
    }

    public String getSessionId() {
        return sessionId;
    }

    public Collection<ClientPerfmonEntry> getWorkTimes() {
        return workTimes;
    }

    public void setWorkTimes(Map<PerfmonEnum, Integer> workTimes, int totalTime) {
        this.totalTime = totalTime;
        this.workTimes = new ArrayList<>();
        for (Map.Entry<PerfmonEnum, Integer> entry : workTimes.entrySet()) {
            this.workTimes.add(new ClientPerfmonEntry(entry.getKey(), entry.getValue()));
        }
        lastActivated = new Date();
    }

    public Date getLastActivated() {
        return lastActivated;
    }

    public int getTotalTime() {
        return totalTime;
    }
}
