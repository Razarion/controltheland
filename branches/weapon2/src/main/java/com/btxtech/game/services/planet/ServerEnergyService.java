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

package com.btxtech.game.services.planet;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.energy.EnergyService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseObject;

import java.util.Collection;

/**
 * User: beat
 * Date: 23.12.2009
 * Time: 16:52:23
 */
public interface ServerEnergyService extends EnergyService {
    void onBaseItemKilled(SyncBaseItem syncBaseItem);

    void restore(Collection<SyncBaseObject> syncBaseObjects);

    void pause(boolean pause);

    int getConsuming(SimpleBase simpleBase);

    int getGenerating(SimpleBase simpleBase);

    void recalculateEnergy();

    void onItemChanged(SyncBaseItem syncBaseItem);
}
