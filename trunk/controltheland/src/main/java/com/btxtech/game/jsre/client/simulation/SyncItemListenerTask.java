package com.btxtech.game.jsre.client.simulation;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemListener;
import com.btxtech.game.jsre.common.tutorial.SyncItemListenerTaskConfig;

import java.util.Collection;

/**
 * User: beat
 * Date: 20.12.13
 * Time: 23:41
 */
public class SyncItemListenerTask extends AbstractTask implements SyncItemListener {
    private SyncItemListenerTaskConfig syncItemListenerTaskConfig;
    private SyncBaseItem syncItemToWatch;

    public SyncItemListenerTask(SyncItemListenerTaskConfig syncItemListenerTaskConfig) {
        super(syncItemListenerTaskConfig);
        this.syncItemListenerTaskConfig = syncItemListenerTaskConfig;
    }

    @Override
    protected void internStart() {
        Collection<SyncBaseItem> syncItemsToWatch = ItemContainer.getInstance().getItems4BaseAndType(true, ClientBase.getInstance().getSimpleBase(), syncItemListenerTaskConfig.getSyncItemTypeToWatch());
        if (syncItemsToWatch.isEmpty()) {
            throw new IllegalStateException("At least on sync item to watch must be available: " + syncItemListenerTaskConfig.getSyncItemTypeToWatch());
        }
        syncItemToWatch = CommonJava.getFirst(syncItemsToWatch);
        syncItemToWatch.addSyncItemListener(this);
    }

    @Override
    protected void internCleanup() {
        syncItemToWatch.removeSyncItemListener(this);
    }

    @Override
    public void onItemChanged(Change change, SyncItem syncItem, Object additionalCustomInfo) {
        if (change == syncItemListenerTaskConfig.getSyncItemChange()) {
            onTaskSucceeded();
        }
    }
}
