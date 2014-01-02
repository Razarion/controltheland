package com.btxtech.game.jsre.common.tutorial;

import com.btxtech.game.jsre.client.simulation.AbstractTask;
import com.btxtech.game.jsre.client.simulation.ContainedInListenerTask;

/**
 * User: beat
 * Date: 21.12.13
 * Time: 15:38
 */
public class ContainedInListenerTaskConfig extends AbstractTaskConfig {
    private int syncItemTypeToWatch;
    private boolean containedIn;
    private boolean checkOnStart;

    public int getSyncItemTypeToWatch() {
        return syncItemTypeToWatch;
    }

    public void setSyncItemTypeToWatch(int syncItemTypeToWatch) {
        this.syncItemTypeToWatch = syncItemTypeToWatch;
    }

    public boolean isCheckOnStart() {
        return checkOnStart;
    }

    public void setCheckOnStart(boolean checkOnStart) {
        this.checkOnStart = checkOnStart;
    }

    public boolean isContainedIn() {
        return containedIn;
    }

    public void setContainedIn(boolean containedIn) {
        this.containedIn = containedIn;
    }

    @Override
    public AbstractTask createTask() {
        return new ContainedInListenerTask(this);
    }
}
