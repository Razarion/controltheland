/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.services.utg;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User: beat
 * Date: 17.02.2010
 * Time: 14:49:30
 */
@Entity(name = "XP_SETTINGS")
public class DbXpSettings implements Cloneable {
    @Id
    @GeneratedValue
    private Integer id;
    private double killPriceFactor;
    private long killQueuePeriod;
    private int killQueueSize;
    private double builtPriceFactor;

    public void fill(DbXpSettings dbXpSettings) {
        killPriceFactor = dbXpSettings.killPriceFactor;
        killQueuePeriod = dbXpSettings.killQueuePeriod;
        killQueueSize = dbXpSettings.killQueueSize;
        builtPriceFactor = dbXpSettings.builtPriceFactor;
    }

    public double getKillPriceFactor() {
        return killPriceFactor;
    }

    public void setKillPriceFactor(double killPriceFactor) {
        this.killPriceFactor = killPriceFactor;
    }

    public double getBuiltPriceFactor() {
        return builtPriceFactor;
    }

    public void setBuiltPriceFactor(double builtPriceFactor) {
        this.builtPriceFactor = builtPriceFactor;
    }

    public long getKillQueuePeriod() {
        return killQueuePeriod;
    }

    public void setKillQueuePeriod(long killQueuePeriod) {
        this.killQueuePeriod = killQueuePeriod;
    }

    public int getKillQueueSize() {
        return killQueueSize;
    }

    public void setKillQueueSize(int killQueueSize) {
        this.killQueueSize = killQueueSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbXpSettings that = (DbXpSettings) o;

        return id != null && id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}