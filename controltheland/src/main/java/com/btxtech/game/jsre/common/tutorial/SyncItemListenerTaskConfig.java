package com.btxtech.game.jsre.common.tutorial;

import com.btxtech.game.jsre.client.simulation.AbstractTask;
import com.btxtech.game.jsre.client.simulation.SyncItemListenerTask;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemListener;

/**
 * User: beat
 * Date: 21.12.13
 * Time: 15:38
 */
public class SyncItemListenerTaskConfig extends AbstractTaskConfig {
    private int syncItemTypeToWatch;
    private SyncItemListener.Change syncItemChange;

    public int getSyncItemTypeToWatch() {
        return syncItemTypeToWatch;
    }

    public void setSyncItemTypeToWatch(int syncItemTypeToWatch) {
        this.syncItemTypeToWatch = syncItemTypeToWatch;
    }

    public SyncItemListener.Change getSyncItemChange() {
        return syncItemChange;
    }

    public void setSyncItemChange(SyncItemListener.Change syncItemChange) {
        this.syncItemChange = syncItemChange;
    }

    @Override
    public AbstractTask createTask() {
        return new SyncItemListenerTask(this);
    }
}
