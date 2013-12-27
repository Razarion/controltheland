package com.btxtech.game.services.history;

import java.io.Serializable;

/**
 * User: beat
 * Date: 13.04.13
 * Time: 01:38
 */
public class GameHistoryFrame implements Serializable {
    private String sessionId;
    private long startTime;
    private long endTimeExclusive;
    private Integer baseId;

    public GameHistoryFrame(String sessionId, Integer baseId, long startTime, long endTimeExclusive) {
        this.sessionId = sessionId;
        this.startTime = startTime;
        this.endTimeExclusive = endTimeExclusive;
        this.baseId = baseId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public boolean hasStartTime() {
        return startTime > 0;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTimeExclusive() {
        return endTimeExclusive;
    }

    public boolean hasEndTimeExclusive() {
        return endTimeExclusive > 0;
    }

    public Integer getBaseId() {
        return baseId;
    }
}
