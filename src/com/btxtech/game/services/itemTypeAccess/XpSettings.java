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

package com.btxtech.game.services.itemTypeAccess;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User: beat
 * Date: 17.02.2010
 * Time: 14:49:30
 */
@Entity(name = "XP_SETTINGS")
public class XpSettings implements Serializable, Cloneable {
    @Id
    @GeneratedValue
    private Integer id;
    private double killPriceFactor;
    private double periodItemFactor;
    private int periodMinutes;

    public double getKillPriceFactor() {
        return killPriceFactor;
    }

    public void setKillPriceFactor(double killPriceFactor) {
        this.killPriceFactor = killPriceFactor;
    }

    public double getPeriodItemFactor() {
        return periodItemFactor;
    }

    public void setPeriodItemFactor(double periodItemFactor) {
        this.periodItemFactor = periodItemFactor;
    }

    public int getPeriodMinutes() {
        return periodMinutes;
    }

    public void setPeriodMinutes(int periodMinutes) {
        this.periodMinutes = periodMinutes;
    }

    public long getPeriodMilliSeconds() {
        return periodMinutes * 1000 * 60;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XpSettings that = (XpSettings) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}