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
import com.btxtech.game.jsre.client.simulation.SimulationConditionServiceImpl;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
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
@Deprecated
public class ClientSyncItem implements SyncItemListener {
    private SyncItem syncItem;
    private boolean isVisible = false;
    private boolean isHidden = false;
    private boolean isSelected;
    private static Logger log = Logger.getLogger(ClientSyncItem.class.getName());

    public ClientSyncItem(SyncItem syncItem) {
        this.syncItem = syncItem;
        //syncItem.addSyncItemListener(this);
    }

    @Override
    public void onItemChanged(Change change, SyncItem syncItem) {

    }

    /*
        public void checkVisibility(Rectangle viewRect) {
            // TODO tis is fast
            boolean isContainedIn = false;
            if (isSyncBaseItem()) {
                isContainedIn = getSyncBaseItem().isContainedIn();
            }
            Index middle = syncItem.getSyncItemArea().getPosition();
            int maxRadius = syncItem.getSyncItemArea().createBoundingBox().getMaxRadius();
            Rectangle fullRect = viewRect.copy();
            boolean isInVisibleRect = false;
            if (fullRect.contains(middle)) {
                isInVisibleRect = true;
            } else {
                fullRect.growEast(maxRadius);
                fullRect.growNorth(maxRadius);
                fullRect.growWest(maxRadius);
                fullRect.growSouth(maxRadius);
                if (fullRect.contains(middle)) {
                    isInVisibleRect = syncItem.getSyncItemArea().contains(viewRect);
                }
            }

            boolean tmpIsVisible = !isHidden && !isContainedIn && isInVisibleRect;
            if (tmpIsVisible != isVisible) {
                if (tmpIsVisible) {
                    ItemViewContainer.getInstance().onSyncItemVisible(this);
                } else {
                    ItemViewContainer.getInstance().onSyncItemInvisible(this);
                }
                isVisible = tmpIsVisible;
            }
        }
    */
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

    public SyncBoxItem getSyncBoxItem() {
        return (SyncBoxItem) syncItem;
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
        }
    }

    public boolean isMyOwnProperty() {
        return isSyncBaseItem() && ClientBase.getInstance().isMyOwnProperty(getSyncBaseItem());
    }

    public boolean isEnemy() {
        return isSyncBaseItem() && ClientBase.getInstance().isEnemy(getSyncBaseItem());
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

    public boolean isSyncBoxItem() {
        return syncItem instanceof SyncBoxItem;
    }

    public boolean isSyncTickItem() {
        return syncItem instanceof SyncTickItem;
    }

    public void setSelected(boolean selected) {
        if (isSelected != selected) {
            isSelected = selected;
        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public String toString() {
        return "ClientSyncItem: " + syncItem;
    }
}
