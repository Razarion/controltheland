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

package com.btxtech.game.services.planet.impl;

import com.btxtech.game.jsre.common.gameengine.services.action.CommonActionService;
import com.btxtech.game.jsre.common.gameengine.services.energy.AbstractBaseEnergy;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncConsumer;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncGenerator;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.BaseService;

/**
 * User: beat
 * Date: 23.12.2009
 * Time: 16:57:08
 */
public class BaseEnergy extends AbstractBaseEnergy {
    private BaseService baseService;
    private Base base;

    public BaseEnergy(BaseService baseService, Base base, CommonActionService actionService) {
        super(actionService);
        this.baseService = baseService;
        this.base = base;
    }

    @Override
    protected void updateEnergyState() {
        baseService.sendEnergyUpdate(this, base);
    }

    public void recalculate() {
        recalculateConsumption();
        recalculateGeneration();
    }

    public void onItemChanged(SyncBaseItem syncBaseItem) {
        synchronized (getSyncObject()) {
            // Generators
            for (SyncGenerator syncGenerator : getSyncGenerators()) {
                if (syncGenerator.getSyncBaseItem().equals(syncBaseItem)) {
                    getSyncGenerators().remove(syncGenerator);
                    break;
                }
            }
            if (syncBaseItem.hasSyncGenerator() && !syncBaseItem.isContainedIn()) {
                getSyncGenerators().add(syncBaseItem.getSyncGenerator());
            }
            // Consumers
            for (SyncConsumer syncConsumer : getSyncConsumers()) {
                if (syncConsumer.getSyncBaseItem().equals(syncBaseItem)) {
                    getSyncConsumers().remove(syncConsumer);
                    break;
                }
            }
            if (syncBaseItem.hasSyncConsumer() && !syncBaseItem.isContainedIn()) {
                getSyncConsumers().add(syncBaseItem.getSyncConsumer());
            }
        }
        recalculate();
    }
}
