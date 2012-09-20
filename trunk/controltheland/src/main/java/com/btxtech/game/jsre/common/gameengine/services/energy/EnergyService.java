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

package com.btxtech.game.jsre.common.gameengine.services.energy;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncConsumer;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncGenerator;

/**
 * User: beat
 * Date: 23.12.2009
 * Time: 13:41:21
 */
public interface EnergyService {
    void generatorActivated(SyncGenerator syncGenerator);

    void generatorDeactivated(SyncGenerator syncGenerator);

    void consumerActivated(SyncConsumer syncConsumer);

    void consumerDeactivated(SyncConsumer syncConsumer);

    int getConsuming();

    int getGenerating();

    void onBaseKilled(SimpleBase simpleBase);
}
