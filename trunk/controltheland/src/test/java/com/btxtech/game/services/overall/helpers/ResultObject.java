package com.btxtech.game.services.overall.helpers;

import com.btxtech.game.jsre.client.common.Index;

/**
 * User: beat
 * Date: 28.10.2009
 * Time: 12:23:21
 */
public class ResultObject {
    private Index start;
    private Index destination;
    private long timeExpected;
    private long timeNeeded;
    private boolean ok;

    ResultObject(Index start, Index destination, long timeExpected, long timeNeeded, boolean isOk) {
        this.start = start;
        this.destination = destination;
        this.timeExpected = timeExpected;
        this.timeNeeded = timeNeeded;
        ok = isOk;
    }

    @Override
    public String toString() {
        return "Error Result: start: " + start + " destination: " + destination + " timeExpected:" + timeExpected + " timeNeeded:" + timeNeeded + " ok:" + ok;
    }

}

