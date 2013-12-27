package com.btxtech.game.services.mgmt;

import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;

/**
 * User: beat
 * Date: 27.07.12
 * Time: 00:28
 */
public class ClientPerfmonEntry {
    private final PerfmonEnum perfmonEnum;
    private final Integer time;

    public ClientPerfmonEntry(PerfmonEnum perfmonEnum, Integer time) {
        this.perfmonEnum = perfmonEnum;
        this.time = time;
    }

    public PerfmonEnum getPerfmonEnum() {
        return perfmonEnum;
    }

    public Integer getTime() {
        return time;
    }
}
