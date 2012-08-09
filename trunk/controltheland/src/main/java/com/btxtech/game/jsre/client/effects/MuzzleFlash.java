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

package com.btxtech.game.jsre.client.effects;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

/**
 * User: beat
 * Date: 30.12.2009
 * Time: 12:48:08
 */
public class MuzzleFlash {
    public static final long MILIS_SHOW_TIME = 100;
    private SyncBaseItem syncBaseItem;
    private long endTime;
    private double muzzleRotationAngel;
    private Index absoluteMuzzleStart;
    private int width;
    private int height;
    private Rectangle viewRect;

    public MuzzleFlash(SyncBaseItem syncBaseItem, int muzzleFlashNr) throws ItemDoesNotExistException {
        endTime = System.currentTimeMillis() + MILIS_SHOW_TIME;
        this.syncBaseItem = syncBaseItem;
        BaseItemType baseItemType = syncBaseItem.getBaseItemType();
        WeaponType weaponType = baseItemType.getWeaponType();

        double angel = syncBaseItem.getSyncItemArea().getAngel();
        angel = baseItemType.getBoundingBox().getAllowedAngel(angel);
        int angelIndex = baseItemType.getBoundingBox().angelToAngelIndex(angel);
        Index muzzleStart = baseItemType.getWeaponType().getMuzzleFlashPosition(muzzleFlashNr, angelIndex);
        absoluteMuzzleStart = syncBaseItem.getSyncItemArea().getPosition().add(muzzleStart);
        width = weaponType.getMuzzleFlashWidth();
        if (weaponType.stretchMuzzleFlashToTarget()) {
            SyncItem target = ItemContainer.getInstance().getItem(syncBaseItem.getSyncWeapon().getTarget());
            height = target.getSyncItemArea().getPosition().getDistance(absoluteMuzzleStart);
            muzzleRotationAngel = absoluteMuzzleStart.getAngleToNord(target.getSyncItemArea().getPosition());
        } else {
            height = weaponType.getMuzzleFlashLength();
            muzzleRotationAngel = angel;
        }
        viewRect = Rectangle.generateRectangleFromMiddlePoint(absoluteMuzzleStart, width * 2, height * 2);
    }

    public boolean isInTime(long time) {
        return time < endTime;
    }

    public boolean isInViewRect(Rectangle viewRect) {
        return viewRect.adjoins(this.viewRect);
    }

    public SyncBaseItem getSyncBaseItem() {
        return syncBaseItem;
    }

    public Index getAbsoluteMuzzleStart() {
        return absoluteMuzzleStart;
    }

    public double getMuzzleRotationAngel() {
        return muzzleRotationAngel;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
