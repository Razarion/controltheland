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

package com.btxtech.game.jsre.common.gameengine.itemType;

import java.io.Serializable;

/**
 * User: beat
 * Date: 17.11.2009
 * Time: 23:13:22
 */
public class WeaponType implements Serializable {
    private int range;
    private int demage;
    private double reloadTime;
    private int muzzlePointX_0;
    private int muzzlePointY_0;
    private int muzzlePointY_90;
    private int muzzlePointX_90;
    private int muzzleFlashWidth;
    private int muzzleFlashLength;
    private boolean stretchMuzzleFlashToTarget;

    /**
     * Used by GWT
     */
    WeaponType() {
    }

    public WeaponType(int range, int demage, double reloadTime, int muzzlePointX_0, int muzzlePointY_0, int muzzlePointX_90, int muzzlePointY_90, int muzzleFlashWidth, int muzzleFlashLength, boolean stretchMuzzleFlashToTarget) {
        this.range = range;
        this.demage = demage;
        this.reloadTime = reloadTime;
        this.muzzlePointX_0 = muzzlePointX_0;
        this.muzzlePointY_0 = muzzlePointY_0;
        this.muzzlePointY_90 = muzzlePointY_90;
        this.muzzlePointX_90 = muzzlePointX_90;
        this.muzzleFlashWidth = muzzleFlashWidth;
        this.muzzleFlashLength = muzzleFlashLength;
        this.stretchMuzzleFlashToTarget = stretchMuzzleFlashToTarget;
    }

    public int getRange() {
        return range;
    }

    public double getDemage() {
        return demage;
    }

    public double getReloadTime() {
        return reloadTime;
    }

    public void changeTo(WeaponType weaponType) {
        range = weaponType.range;
        demage = weaponType.demage;
        reloadTime = weaponType.reloadTime;
        muzzlePointX_0 = weaponType.muzzlePointX_0;
        muzzlePointY_0 = weaponType.muzzlePointY_0;
        muzzlePointY_90 = weaponType.muzzlePointY_90;
        muzzlePointX_90 = weaponType.muzzlePointX_90;
        muzzleFlashWidth = weaponType.muzzleFlashWidth;
        muzzleFlashLength = weaponType.muzzleFlashLength;
        stretchMuzzleFlashToTarget = weaponType.stretchMuzzleFlashToTarget;
    }

    public int getMuzzlePointX_0() {
        return muzzlePointX_0;
    }

    public int getMuzzlePointY_0() {
        return muzzlePointY_0;
    }

    public int getMuzzlePointY_90() {
        return muzzlePointY_90;
    }

    public int getMuzzlePointX_90() {
        return muzzlePointX_90;
    }

    public int getMuzzleFlashWidth() {
        return muzzleFlashWidth;
    }

    public int getMuzzleFlashLength() {
        return muzzleFlashLength;
    }

    public boolean stretchMuzzleFlashToTarget() {
        return stretchMuzzleFlashToTarget;
    }
}
