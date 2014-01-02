package com.btxtech.game.jsre.client.simulation;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemListener;
import com.btxtech.game.jsre.common.tutorial.ContainedInListenerTaskConfig;

import java.util.Collection;

/**
 * User: beat
 * Date: 20.12.13
 * Time: 23:41
 */
public class ContainedInListenerTask extends AbstractTask implements SyncItemListener {
    private ContainedInListenerTaskConfig containedInListenerTaskConfig;
    private SyncBaseItem syncItemToWatch;

    public ContainedInListenerTask(ContainedInListenerTaskConfig containedInListenerTaskConfig) {
        super(containedInListenerTaskConfig);
        this.containedInListenerTaskConfig = containedInListenerTaskConfig;
    }

    @Override
    protected boolean isFulfilled() {
        if (containedInListenerTaskConfig.isCheckOnStart()) {
            if (containedInListenerTaskConfig.isContainedIn()) {
                return getSyncItemToWatch().isContainedIn();
            } else {
                return !getSyncItemToWatch().isContainedIn();
            }
        } else {
            return false;
        }
    }

    @Override
    protected void internStart() {
        syncItemToWatch = getSyncItemToWatch();
        syncItemToWatch.addSyncItemListener(this);
    }

    @Override
    protected void internCleanup() {
        if (syncItemToWatch != null) {
            syncItemToWatch.removeSyncItemListener(this);
        }
    }

    @Override
    public void onItemChanged(Change change, SyncItem syncItem, Object additionalCustomInfo) {
        if (change == Change.CONTAINED_IN_CHANGED) {
            if (syncItemToWatch.isContainedIn() == containedInListenerTaskConfig.isContainedIn()) {
                onTaskSucceeded();
            }
        }
    }

    private SyncBaseItem getSyncItemToWatch() {
        Collection<SyncBaseItem> syncItemsToWatch = ItemContainer.getInstance().getItems4BaseAndType(true, ClientBase.getInstance().getSimpleBase(), containedInListenerTaskConfig.getSyncItemTypeToWatch());
        if (syncItemsToWatch.isEmpty()) {
            throw new IllegalStateException("At least on sync item to watch must be available: " + containedInListenerTaskConfig.getSyncItemTypeToWatch());
        }
        return CommonJava.getFirst(syncItemsToWatch);
    }
}
