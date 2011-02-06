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

package com.btxtech.game.jsre.common.gameengine.syncObjects;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.TurnableType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;

/**
 * User: beat
 * Date: 18.11.2009
 * Time: 14:39:38
 */
public class SyncTurnable extends SyncBaseAbility {
    private TurnableType turnableType;
    private double angel = 0;

    public SyncTurnable(TurnableType turnableType,SyncBaseItem syncBaseItem) {
        super(syncBaseItem);
        this.turnableType = turnableType;
    }

    public void turnTo(Index dest) {
        if (dest.equals(getSyncBaseItem().getPosition())) {
            return;
        }

        double angel = getSyncBaseItem().getPosition().getAngleToNord(dest);
        if (angel != this.angel) {
            this.angel = angel;
            getSyncBaseItem().fireItemChanged(SyncItemListener.Change.ANGEL);
        }
    }

    public double getAngel() {
        return angel;
    }

    public void setAngel(double angel) {
        this.angel = angel;
    }

    @Override
    public void synchronize(SyncItemInfo syncItemInfo) {
        angel = syncItemInfo.getAngel();
    }

    @Override
    public void fillSyncItemInfo(SyncItemInfo syncItemInfo) {
        syncItemInfo.setAngel(angel);
    }

    public TurnableType getTurnableType() {
        return turnableType;
    }
}