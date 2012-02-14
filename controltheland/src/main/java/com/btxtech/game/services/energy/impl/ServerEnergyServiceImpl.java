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

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.items.BaseDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncConsumer;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncGenerator;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.energy.ServerEnergyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;

/**
 * User: beat
 * Date: 23.12.2009
 * Time: 16:52:51
 */
@Component("energyService")
public class ServerEnergyServiceImpl implements ServerEnergyService {
    @Autowired
    private BaseService baseService;
    @Autowired
    private ActionService actionService;
    private final HashMap<SimpleBase, BaseEnergy> baseEntries = new HashMap<SimpleBase, BaseEnergy>();
    private boolean pause = false;

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
        if (pause || !syncBaseItem.isReady()) {
            return;
        }
        if (syncBaseItem.hasSyncConsumer()) {
            consumerActivated(syncBaseItem.getSyncConsumer());
        }

        if (syncBaseItem.hasSyncGenerator()) {
            generatorActivated(syncBaseItem.getSyncGenerator());
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
    public void onBaseKilled(Base base) {
        if (pause) {
            return;
        }
        synchronized (baseEntries) {
            baseEntries.remove(base.getSimpleBase());
        }
    }

    @Override
    public void restoreItems(Collection<SyncItem> syncItems) {
        synchronized (baseEntries) {
            baseEntries.clear();
            for (SyncItem syncItem : syncItems) {
                if (syncItem instanceof SyncBaseItem) {
                    onBaseItemRestored((SyncBaseItem) syncItem);
                }
            }
        }
    }

    @Override
    public void pauseService(boolean pause) {
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
        Base base = baseService.getBase();
        return baseEntries.get(base.getSimpleBase());
    }

    private BaseEnergy getBaseEnergy(SyncBaseItem syncBaseItem) {
        SimpleBase simpleBase = syncBaseItem.getBase();
        return getBaseEnergy(simpleBase);
    }

    private BaseEnergy getBaseEnergy(SimpleBase simpleBase) {
        synchronized (baseEntries) {
            BaseEnergy baseEnergy = baseEntries.get(simpleBase);
            if (baseEnergy == null) {
                if (baseService.getBase(simpleBase) == null) {
                    throw new BaseDoesNotExistException(simpleBase);
                }
                baseEnergy = new BaseEnergy(baseService, baseService.getBase(simpleBase), actionService);
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
