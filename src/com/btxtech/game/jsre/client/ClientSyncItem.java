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

import com.btxtech.game.jsre.client.item.ItemViewContainer;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemListener;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;

/**
 * User: beat
 * Date: 14.08.2010
 * Time: 15:15:42
 */
public class ClientSyncItem implements SyncItemListener {
    private SyncItem syncItem;
    private boolean isVisible = false;
    private boolean isHidden = false;
    private boolean isSelected;
    private ClientSyncItemView clientSyncItemView;

    public ClientSyncItem(SyncItem syncItem) {
        this.syncItem = syncItem;
        syncItem.addSyncItemListener(this);
    }

    @Override
    public void onItemChanged(Change change, SyncItem syncItem) {
        switch (change) {
            case POSITION:
                checkVisibility();
                break;
        }
        if (clientSyncItemView != null) {
            clientSyncItemView.onModelChange(change);
        }
    }

    public void checkVisibility() {
        boolean isContainedIn = false;
        if (isSyncBaseItem()) {
            isContainedIn = getSyncBaseItem().isContainedIn();
        }
        boolean tmpIsVisible = !isHidden && !isContainedIn && TerrainView.getInstance().isItemVisible(syncItem);
        if (tmpIsVisible != isVisible) {
            if (tmpIsVisible) {
                ItemViewContainer.getInstance().onSyncItemVisible(this);
            } else {
                ItemViewContainer.getInstance().onSyncItemInvisible(this);
            }
            isVisible = tmpIsVisible;
        }
    }

    public SyncItem getSyncItem() {
        return syncItem;
    }

    public SyncBaseItem getSyncBaseItem() {
        return (SyncBaseItem) syncItem;
    }

    public SyncResourceItem getSyncResourceItem() {
        return (SyncResourceItem) syncItem;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        if (hidden != isHidden) {
            isHidden = hidden;
            checkVisibility();
        }
    }

    public void update() {
        if (isVisible()) {
            ItemViewContainer.getInstance().updateSyncItemView(this);
        }
    }

    public void dispose() {
        if (isVisible()) {
            ItemViewContainer.getInstance().removeSyncItemView(this);
        }
    }

    public boolean isMyOwnProperty() {
        return isSyncBaseItem() && ClientBase.getInstance().isMyOwnProperty(getSyncBaseItem());
    }

    public boolean isSyncBaseItem() {
        return syncItem instanceof SyncBaseItem;
    }

    public boolean isSyncResourceItem() {
        return syncItem instanceof SyncResourceItem;
    }

    public void setSelected(boolean selected) {
        if (isSelected != selected) {
            isSelected = selected;
            if (isVisible()) {
                ItemViewContainer.getInstance().onSelectionChanged(this, isSelected);
            }
        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public String toString() {
        return "ClientSyncItem: " + syncItem;
    }

    public void setClientSyncItemListener(ClientSyncItemView clientSyncItemView) {
        this.clientSyncItemView = clientSyncItemView;
    }
}
