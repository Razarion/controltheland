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
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.item.ItemViewContainer;
import com.btxtech.game.jsre.client.simulation.SimulationConditionServiceImpl;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemListener;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncProjectileItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncTickItem;

import java.util.logging.Level;
import java.util.logging.Logger;

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
    private static Logger log = Logger.getLogger(ClientSyncItem.class.getName());

    public ClientSyncItem(SyncItem syncItem) {
        this.syncItem = syncItem;
        syncItem.addSyncItemListener(this);
    }

    @Override
    public void onItemChanged(Change change, SyncItem syncItem) {
        // TODO Remove if big found
        switch (change) {
            case POSITION:
                try {
                    checkVisibility();
                    if (syncItem instanceof SyncBaseItem && Connection.getInstance().getGameEngineMode() == GameEngineMode.MASTER) {
                        ActionHandler.getInstance().interactionGuardingItems((SyncBaseItem) syncItem);
                    }
                } catch (Throwable t) {
                    log.log(Level.SEVERE, "ClientSyncItem.onItemChanged() failed POSITION: " + syncItem, t);
                }
                break;
            case BUILD:
                try {
                    if (syncItem instanceof SyncBaseItem && ((SyncBaseItem) syncItem).isReady()) {
                        SimulationConditionServiceImpl.getInstance().onSyncItemBuilt(((SyncBaseItem) syncItem));
                        ClientBase.getInstance().recalculate4FakedHouseSpace((SyncBaseItem) syncItem);
                        if (Connection.getInstance().getGameEngineMode() == GameEngineMode.MASTER) {
                            ActionHandler.getInstance().addGuardingBaseItem((SyncBaseItem) syncItem);
                            ItemContainer.getInstance().checkSpecialChanged(syncItem);
                        }
                    }
                } catch (Throwable t) {
                    log.log(Level.SEVERE, "ClientSyncItem.onItemChanged() failed BUILD: " + syncItem, t);
                }
                break;
            case ITEM_TYPE_CHANGED:
                try {
                    RadarPanel.getInstance().onItemTypeChanged(this);
                } catch (Throwable t) {
                    log.log(Level.SEVERE, "ClientSyncItem.onItemChanged() failed ITEM_TYPE_CHANGED: " + syncItem, t);
                }
                break;
        }
        try {
            if (clientSyncItemView != null) {
                clientSyncItemView.onModelChange(change);
            }
        } catch (Throwable t) {
            log.log(Level.SEVERE, "ClientSyncItem.onItemChanged() failed: " + syncItem, t);
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

    public SyncProjectileItem getSyncProjectileItem() {
        return (SyncProjectileItem) syncItem;
    }

    public SyncTickItem getSyncTickItem() {
        return (SyncTickItem) syncItem;
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

    public boolean isSyncProjectileItem() {
        return syncItem instanceof SyncProjectileItem;
    }

    public boolean isSyncTickItem() {
        return syncItem instanceof SyncTickItem;
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

    public ClientSyncItemView getClientSyncItemView() {
        return clientSyncItemView;
    }
}
