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

import com.btxtech.game.jsre.client.common.Index;

import java.io.Serializable;
import java.util.Collection;

/**
 * User: beat
 * Date: 17.11.2009
 * Time: 23:13:22
 */
public class WeaponType implements Serializable {
    private int range;
    private int demage;
    private double reloadTime;
    private int muzzleFlashWidth;
    private int muzzleFlashLength;
    private boolean stretchMuzzleFlashToTarget;
    private Collection<Integer> allowedItemTypes;
    private Index[] muzzleFiresPositions;

    /**
     * Used by GWT
     */
    WeaponType() {
    }

    public WeaponType(int range, int demage, double reloadTime, int muzzlePointX_0, int muzzlePointY_0, int muzzlePointX_90, int muzzlePointY_90, int muzzleFlashWidth, int muzzleFlashLength, boolean stretchMuzzleFlashToTarget, Collection<Integer> allowedItemTypes) {
        this.range = range;
        this.demage = demage;
        this.reloadTime = reloadTime;
        this.muzzleFlashWidth = muzzleFlashWidth;
        this.muzzleFlashLength = muzzleFlashLength;
        this.stretchMuzzleFlashToTarget = stretchMuzzleFlashToTarget;
        this.allowedItemTypes = allowedItemTypes;
        // TODO replace
        muzzleFiresPositions = new Index[24];
        for (int i = 0; i < muzzleFiresPositions.length; i++) {
            muzzleFiresPositions[i] = new Index(0, 0);
        }
        // TODO replace ends
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
        muzzleFlashWidth = weaponType.muzzleFlashWidth;
        muzzleFlashLength = weaponType.muzzleFlashLength;
        stretchMuzzleFlashToTarget = weaponType.stretchMuzzleFlashToTarget;
        allowedItemTypes = weaponType.allowedItemTypes;
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

    public boolean isItemTypeAllowed(int itemTypeId) {
        return allowedItemTypes.contains(itemTypeId);
    }

    public Index getMuzzleFiresPosition(int imageNr) {
        return muzzleFiresPositions[imageNr];
    }

    public Index[] getMuzzleFiresPositions() {
        return muzzleFiresPositions;
    }
}
