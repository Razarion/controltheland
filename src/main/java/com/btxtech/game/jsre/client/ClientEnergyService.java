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

import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.energy.AbstractBaseEnergy;
import com.btxtech.game.jsre.common.gameengine.services.energy.EnergyService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncConsumer;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncGenerator;
import com.btxtech.game.jsre.common.packets.EnergyPacket;

/**
 * User: beat
 * Date: 23.12.2009
 * Time: 14:10:00
 */
public class ClientEnergyService extends AbstractBaseEnergy implements EnergyService {
    private static final ClientEnergyService INSTANCE = new ClientEnergyService();

    /**
     * Singleton
     */
    private ClientEnergyService() {
        super(ActionHandler.getInstance());
    }

    public static ClientEnergyService getInstance() {
        return INSTANCE;
    }

    public void init() {
        getSyncConsumers().clear();
        getSyncGenerators().clear();
        setConsuming(0);
        setGenerating(0);
    }

    @Override
    public void generatorActivated(SyncGenerator syncGenerator) {
        if (checkState(syncGenerator.getSyncBaseItem())) {
            super.generatorActivated(syncGenerator);
        }
    }

    @Override
    public void generatorDeactivated(SyncGenerator syncGenerator) {
        if (checkState(syncGenerator.getSyncBaseItem())) {
            super.generatorDeactivated(syncGenerator);
        }
    }

    @Override
    public void consumerActivated(SyncConsumer syncConsumer) {
        if (checkState(syncConsumer.getSyncBaseItem())) {
            super.consumerActivated(syncConsumer);
        }
    }

    @Override
    public void consumerDeactivated(SyncConsumer syncConsumer) {
        if (checkState(syncConsumer.getSyncBaseItem())) {
            super.consumerDeactivated(syncConsumer);
        }
    }

    private boolean checkState(SyncBaseItem syncBaseItem) {
        return Connection.getInstance().getGameEngineMode() == GameEngineMode.MASTER && ClientBase.getInstance().isMyOwnProperty(syncBaseItem);
    }

    @Override
    protected void updateEnergyState() {
        SideCockpit.getInstance().updateEnergy(getGenerating(), getConsuming());
        RadarPanel.getInstance().updateEnergy(getGenerating(), getConsuming());
    }

    public void onEnergyPacket(EnergyPacket energyPacket) {
        onEnergyPacket(energyPacket.getGenerating(), energyPacket.getConsuming());
    }

    public void onEnergyPacket(int energyGenerating, int energyConsuming) {
        if (Connection.getInstance().getGameEngineMode() == GameEngineMode.SLAVE) {
            setConsuming(energyConsuming);
            setGenerating(energyGenerating);
            updateEnergyState();
        }
    }

    public void onSyncItemKilled(SyncBaseItem syncBaseItem) {
        if (syncBaseItem.hasSyncConsumer()) {
            consumerDeactivated(syncBaseItem.getSyncConsumer());
        }

        if (syncBaseItem.hasSyncGenerator()) {
            generatorDeactivated(syncBaseItem.getSyncGenerator());
        }
    }

    @Override
    public void onBaseKilled(SimpleBase simpleBase) {
        // Not Used on client
    }
}
