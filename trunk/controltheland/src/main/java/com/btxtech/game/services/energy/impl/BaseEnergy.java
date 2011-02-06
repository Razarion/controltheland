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

package com.btxtech.game.services.energy.impl;

import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncConsumer;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncGenerator;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import java.util.HashSet;

/**
 * User: beat
 * Date: 23.12.2009
 * Time: 16:57:08
 */
public class BaseEnergy {
    private int generating;
    private int consuming;
    private BaseService baseService;
    private Base base;
    private ActionService actionService;
    private HashSet<SyncGenerator> syncGenerators = new HashSet<SyncGenerator>();
    private HashSet<SyncConsumer> syncConsumers = new HashSet<SyncConsumer>();
    private final Object syncObject = new Object();

    public BaseEnergy(BaseService baseService, Base base, ActionService actionService) {
        this.baseService = baseService;
        this.base = base;
        this.actionService = actionService;
    }

    public void generatorActivated(SyncGenerator syncGenerator) {
        syncGenerators.add(syncGenerator);
        recalculateGeneration();
    }

    public void generatorDeactivated(SyncGenerator syncGenerator) {
        syncGenerators.remove(syncGenerator);
        recalculateGeneration();
    }

    public void consumerActivated(SyncConsumer syncConsumer) {
        syncConsumers.add(syncConsumer);
        recalculateConsumption();
        syncConsumer.setOperationState(hasEnoughPower(generating, consuming));
    }

    public void consumerDeactivated(SyncConsumer syncConsumer) {
        syncConsumers.remove(syncConsumer);
        recalculateConsumption();
    }

    private void recalculateGeneration() {
        synchronized (syncObject) {
            int tmpGenerating = 0;
            for (SyncGenerator syncGenerator : syncGenerators) {
                tmpGenerating += syncGenerator.getWattage();
            }
            if (tmpGenerating == generating) {
                return;
            }
            int oldGenerating = generating;
            generating = tmpGenerating;

            if (hasEnoughPower(oldGenerating, consuming) != hasEnoughPower(generating, consuming)) {
                setConsumerState(hasEnoughPower(generating, consuming));
            }
            baseService.sendEnergyUpdate(this, base);
        }
    }

    private void recalculateConsumption() {
        synchronized (syncObject) {
            int tmpConsuming = 0;
            for (SyncConsumer syncConsumer : syncConsumers) {
                tmpConsuming += syncConsumer.getWattage();
            }
            if (tmpConsuming == consuming) {
                return;
            }
            int oldConsuming = consuming;
            consuming = tmpConsuming;

            if (hasEnoughPower(generating, oldConsuming) != hasEnoughPower(generating, consuming)) {
                setConsumerState(hasEnoughPower(generating, consuming));
            }
            baseService.sendEnergyUpdate(this, base);
        }
    }

    private void setConsumerState(boolean operationState) {
        synchronized (syncObject) {
            for (SyncConsumer syncConsumer : syncConsumers) {
                syncConsumer.setOperationState(operationState);
                if (operationState) {
                    actionService.syncItemActivated(syncConsumer.getSyncBaseItem());
                }
            }
        }
    }

    private boolean hasEnoughPower(int generating, int consuming) {
        return generating >= consuming;
    }

    public int getGenerating() {
        return generating;
    }

    public int getConsuming() {
        return consuming;
    }

    public void recalculate() {
        recalculateConsumption();
        recalculateGeneration();
    }

    public void onItemChanged(SyncBaseItem syncBaseItem) {
        synchronized (syncObject) {
            // Generators
            for (SyncGenerator syncGenerator : syncGenerators) {
                if (syncGenerator.getSyncBaseItem().equals(syncBaseItem)) {
                    syncGenerators.remove(syncGenerator);
                    break;
                }
            }
            if (syncBaseItem.hasSyncGenerator() && !syncBaseItem.isContainedIn()) {
                syncGenerators.add(syncBaseItem.getSyncGenerator());
            }
            // Consumers
            for (SyncConsumer syncConsumer : syncConsumers) {
                if (syncConsumer.getSyncBaseItem().equals(syncBaseItem)) {
                    syncConsumers.remove(syncConsumer);
                    break;
                }
            }
            if (syncBaseItem.hasSyncConsumer() && !syncBaseItem.isContainedIn()) {
                syncConsumers.add(syncBaseItem.getSyncConsumer());
            }
        }
        recalculate();
    }
}
