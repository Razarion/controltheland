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

import com.btxtech.game.jsre.common.gameengine.itemType.GeneratorType;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;

/**
 * User: beat
 * Date: 22.11.2009
 * Time: 13:30:50
 */
public class SyncGenerator extends SyncBaseAbility{
    private GeneratorType generatorType;
    private boolean generating = false;

    public SyncGenerator(GeneratorType generatorType, SyncBaseItem syncBaseItem) {
        super(syncBaseItem);
        this.generatorType = generatorType;
    }

    @Override
    public void synchronize(SyncItemInfo syncItemInfo) {
        // Ignore
    }

    @Override
    public void fillSyncItemInfo(SyncItemInfo syncItemInfo) {
        // Ignore
    }

    public void setGenerating(boolean generating) {
        boolean oldState = this.generating;
        this.generating = generating;
        if(oldState != generating) {
            if(generating) {
                getPlanetServices().getEnergyService().generatorActivated(this);
            } else {
                getPlanetServices().getEnergyService().generatorDeactivated(this);
            }
        }
    }

    public int getWattage() {
        return generatorType.getWattage();
    }
}