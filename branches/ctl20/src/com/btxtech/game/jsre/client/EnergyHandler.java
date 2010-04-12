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

package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.common.gameengine.services.energy.EnergyService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncConsumer;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncGenerator;

/**
 * User: beat
 * Date: 23.12.2009
 * Time: 14:10:00
 */
public class EnergyHandler implements EnergyService {
    private static final EnergyHandler INSTANCE = new EnergyHandler();
    private int consumerTotal = 0;
    private int generatorTotal = 0;

    public static EnergyHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void generatorActivated(SyncGenerator syncGenerator) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void generatorDeactivated(SyncGenerator syncGenerator) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void consumerActivated(SyncConsumer syncConsumer) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void consumerDeactivated(SyncConsumer syncConsumer) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public int getConsuming() {
        return consumerTotal;
    }

    public int getGenerating() {
        return generatorTotal;
    }
}
