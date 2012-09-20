package com.btxtech.game.jsre.common.gameengine.services.energy;

import com.btxtech.game.jsre.common.gameengine.services.action.CommonActionService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncConsumer;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncGenerator;

import java.util.Collection;
import java.util.HashSet;

/**
 * User: beat
 * Date: 22.03.2011
 * Time: 21:02:19
 */
public abstract class AbstractBaseEnergy {
    private int generating;
    private int consuming;
    private CommonActionService actionService;
    private HashSet<SyncGenerator> syncGenerators = new HashSet<SyncGenerator>();
    private HashSet<SyncConsumer> syncConsumers = new HashSet<SyncConsumer>();
    private final Object syncObject = new Object();

    public AbstractBaseEnergy(CommonActionService actionService) {
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

    protected void recalculateGeneration() {
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
            updateEnergyState();
        }
    }

    protected abstract void updateEnergyState();

    protected void recalculateConsumption() {
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
            updateEnergyState();
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

    protected Object getSyncObject() {
        return syncObject;
    }

    protected Collection<SyncGenerator> getSyncGenerators() {
        return syncGenerators;
    }

    protected Collection<SyncConsumer> getSyncConsumers() {
        return syncConsumers;
    }

    public int getGenerating() {
        return generating;
    }

    public int getConsuming() {
        return consuming;
    }

    protected void setGenerating(int generating) {
        this.generating = generating;
    }

    protected void setConsuming(int consuming) {
        this.consuming = consuming;
    }


}
