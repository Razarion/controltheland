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

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.SoundHandler;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * User: beat
 * Date: Jun 20, 2009
 * Time: 1:29:15 PM
 */
public class MuzzleFlashHandler {
    public final static int ATTACK_EFFECT_TIMER_DELAY = 50;
    private static final MuzzleFlashHandler INSTANCE = new MuzzleFlashHandler();
    private final HashSet<MuzzleFlash> muzzleFlashes = new HashSet<MuzzleFlash>();

    public static MuzzleFlashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private MuzzleFlashHandler() {
    }

    public void onAttack(SyncItem syncItem) {
        try {
            if (syncItem.getSyncItemArea().contains(TerrainView.getInstance().getViewRect())) {
                SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                SoundHandler.getInstance().playMuzzleFlashSound(syncBaseItem.getBaseItemType());
                int count = syncBaseItem.getSyncWeapon().getWeaponType().getMuzzleFlashCount();
                for (int i = 0; i < count; i++) {
                    muzzleFlashes.add(new MuzzleFlash(syncBaseItem, i));
                }
            }
        } catch (Exception e) {
            GwtCommon.handleException(e);
        }
    }

    public Collection<MuzzleFlash> getMuzzleFlashInRegion(long timeStamp, Rectangle viewRect) {
        Collection<MuzzleFlash> muzzleFlashes = new ArrayList<MuzzleFlash>();
        for (Iterator<MuzzleFlash> iterator = this.muzzleFlashes.iterator(); iterator.hasNext(); ) {
            MuzzleFlash attack = iterator.next();
            if (attack.isInTime(timeStamp) && attack.isInViewRect(viewRect)) {
                muzzleFlashes.add(attack);
            } else {
                iterator.remove();
            }
        }
        return muzzleFlashes;
    }
}
