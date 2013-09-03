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

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.items.BaseDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseObject;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncConsumer;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncGenerator;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.planet.ServerEnergyService;

import java.util.Collection;
import java.util.HashMap;

/**
 * User: beat
 * Date: 23.12.2009
 * Time: 16:52:51
 */
public class ServerEnergyServiceImpl implements ServerEnergyService {
    private final HashMap<SimpleBase, BaseEnergy> baseEntries = new HashMap<>();
    private boolean pause = false;
    private ServerPlanetServices sererPlanetServices;

    public void init(ServerPlanetServices sererPlanetServices) {
        this.sererPlanetServices = sererPlanetServices;
    }

    @Override
    public void generatorActivated(SyncGenerator syncGenerator) {
        if (pause) {
            return;
        }
        getBaseEnergy(syncGenerator.getSyncBaseItem()).generatorActivated(syncGenerator);
    }

    @Override
    public void generatorDeactivated(SyncGenerator syncGenerator) {
        if (pause) {
            return;
        }
        getBaseEnergy(syncGenerator.getSyncBaseItem()).generatorDeactivated(syncGenerator);
    }

    @Override
    public void consumerActivated(SyncConsumer syncConsumer) {
        if (pause) {
            return;
        }
        getBaseEnergy(syncConsumer.getSyncBaseItem()).consumerActivated(syncConsumer);
    }

    @Override
    public void consumerDeactivated(SyncConsumer syncConsumer) {
        if (pause) {
            return;
        }
        getBaseEnergy(syncConsumer.getSyncBaseItem()).consumerDeactivated(syncConsumer);
    }

    private void onBaseItemRestored(SyncBaseItem syncBaseItem) {
        if (!syncBaseItem.isReady()) {
            return;
        }
        if (syncBaseItem.hasSyncConsumer()) {
            getBaseEnergy(syncBaseItem).consumerActivated(syncBaseItem.getSyncConsumer());
        }

        if (syncBaseItem.hasSyncGenerator()) {
            getBaseEnergy(syncBaseItem).generatorActivated(syncBaseItem.getSyncGenerator());
        }
    }

    @Override
    public void onBaseItemKilled(SyncBaseItem syncBaseItem) {
        if (pause) {
            return;
        }
        try {
            if (syncBaseItem.hasSyncConsumer()) {
                consumerDeactivated(syncBaseItem.getSyncConsumer());
            }

            if (syncBaseItem.hasSyncGenerator()) {
                generatorDeactivated(syncBaseItem.getSyncGenerator());
            }
        } catch (BaseDoesNotExistException ignore) {
            // Ignore
            // The last item of a base was killed
        }
    }

    @Override
    public void onBaseKilled(SimpleBase base) {
        if (pause) {
            return;
        }
        synchronized (baseEntries) {
            baseEntries.remove(base);
        }
    }

    @Override
    public void restore(Collection<SyncBaseObject> syncBaseObjects) {
        synchronized (baseEntries) {
            baseEntries.clear();
            for (SyncBaseObject syncBaseObject : syncBaseObjects) {
                if (syncBaseObject instanceof SyncBaseItem) {
                    onBaseItemRestored((SyncBaseItem) syncBaseObject);
                }
            }
        }
    }

    @Override
    public void pause(boolean pause) {
        this.pause = pause;
    }

    @Override
    public int getConsuming() {
        BaseEnergy baseEnergy = getBaseEnergy();
        if (baseEnergy == null) {
            return 0;
        }
        return baseEnergy.getConsuming();
    }

    @Override
    public int getGenerating() {
        BaseEnergy baseEnergy = getBaseEnergy();
        if (baseEnergy == null) {
            return 0;
        }
        return baseEnergy.getGenerating();
    }

    @Override
    public int getConsuming(SimpleBase simpleBase) {
        return getBaseEnergy(simpleBase).getConsuming();
    }

    @Override
    public int getGenerating(SimpleBase simpleBase) {
        return getBaseEnergy(simpleBase).getGenerating();
    }

    private BaseEnergy getBaseEnergy() {
        return baseEntries.get(sererPlanetServices.getBaseService().getBase().getSimpleBase());
    }

    private BaseEnergy getBaseEnergy(SyncBaseItem syncBaseItem) {
        SimpleBase simpleBase = syncBaseItem.getBase();
        return getBaseEnergy(simpleBase);
    }

    private BaseEnergy getBaseEnergy(SimpleBase simpleBase) {
        synchronized (baseEntries) {
            BaseEnergy baseEnergy = baseEntries.get(simpleBase);
            if (baseEnergy == null) {
                if (sererPlanetServices.getBaseService().getBase(simpleBase) == null) {
                    throw new BaseDoesNotExistException(simpleBase);
                }
                baseEnergy = new BaseEnergy(sererPlanetServices.getBaseService(), sererPlanetServices.getBaseService().getBase(simpleBase), sererPlanetServices.getActionService());
                baseEntries.put(simpleBase, baseEnergy);
            }
            return baseEnergy;
        }
    }

    @Override
    public void recalculateEnergy() {
        synchronized (baseEntries) {
            for (BaseEnergy baseEnergy : baseEntries.values()) {
                baseEnergy.recalculate();
            }
        }
    }

    @Override
    public void onItemChanged(SyncBaseItem syncBaseItem) {
        getBaseEnergy(syncBaseItem).onItemChanged(syncBaseItem);
    }
}
