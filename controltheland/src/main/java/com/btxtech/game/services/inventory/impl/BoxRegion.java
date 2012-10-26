package com.btxtech.game.services.inventory.impl;

import com.btxtech.game.services.planet.db.DbBoxRegion;

import java.io.Serializable;

/**
 * User: beat
 * Date: 15.05.12
 * Time: 22:02
 */
public class BoxRegion {
    private Serializable dbBoxRegionId;
    private long nextDropTime;
    private long minInterval;
    private long maxInterval;

    public BoxRegion(DbBoxRegion dbBoxRegion) {
        if (dbBoxRegion.getMaxInterval() < dbBoxRegion.getMinInterval()) {
            throw new IllegalArgumentException("Unable starting BoxRegion: " + dbBoxRegion.getName() + ". Maximum interval is smaller than minimal interval. minInterval:" + dbBoxRegion.getMinInterval() + " maxInterval:" + dbBoxRegion.getMaxInterval());
        }
        dbBoxRegionId = dbBoxRegion.getId();
        minInterval = dbBoxRegion.getMinInterval();
        maxInterval = dbBoxRegion.getMaxInterval();
        setupNextDropTime();
    }

    public void setupNextDropTime() {
        nextDropTime = Math.round(Math.random() * (maxInterval - minInterval)) + System.currentTimeMillis() + minInterval;
    }

    public boolean isDropTimeReached() {
        return System.currentTimeMillis() >= nextDropTime;
    }

    public Serializable getDbBoxRegionId() {
        return dbBoxRegionId;
    }

    public long getNextDropTime() {
        return nextDropTime;
    }
}
