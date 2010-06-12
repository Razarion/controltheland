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

package com.btxtech.game.jsre.common.gameengine.syncObjects;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemContainerType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.UnloadContainerCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * User: beat
 * Date: 01.05.2010
 * Time: 11:38:49
 */
public class SyncItemContainer extends SyncBaseAbility {
    private ItemContainerType itemContainerType;
    private List<SyncBaseItem> containedItems = new ArrayList<SyncBaseItem>();
    private Index unloadPos;

    public SyncItemContainer(ItemContainerType itemContainerType, SyncBaseItem syncBaseItem) {
        super(syncBaseItem);
        this.itemContainerType = itemContainerType;
    }

    @Override
    public void synchronize(SyncItemInfo syncItemInfo) throws NoSuchItemTypeException, ItemDoesNotExistException {
        unloadPos = syncItemInfo.getUnloadPos();
        if (syncItemInfo.getContainedItems() != null) {
            containedItems = getServices().getItemService().getBaseItems(syncItemInfo.getContainedItems());
        } else {
            containedItems.clear();
        }
    }

    @Override
    public void fillSyncItemInfo(SyncItemInfo syncItemInfo) {
        syncItemInfo.setUnloadPos(unloadPos);
        if (containedItems.isEmpty()) {
            syncItemInfo.setContainedItems(null);
        } else {
            syncItemInfo.setContainedItems(getServices().getItemService().getBaseItemIds(containedItems));
        }
    }

    public void load(SyncBaseItem syncBaseItem) {
        isAbleToContainThrow(syncBaseItem);

        containedItems.add(syncBaseItem);
        syncBaseItem.setContained(getSyncBaseItem().getId());
        getSyncBaseItem().fireItemChanged(SyncItemListener.Change.CONTAINER_COUNT_CHANGED);
    }

    private void isAbleToContainThrow(SyncBaseItem syncBaseItem) {
        if (getSyncBaseItem().equals(syncBaseItem)) {
            throw new IllegalArgumentException("Can not contain oneself: " + this);
        }

        if (!itemContainerType.isAbleToContain(syncBaseItem.getBaseItemType().getId())) {
            throw new IllegalArgumentException("Container " + getSyncBaseItem() + " is not able to contain: " + syncBaseItem);
        }

        if (!getServices().getTerritoryService().isAllowed(getSyncBaseItem().getPosition(), getSyncBaseItem())) {
            throw new IllegalArgumentException(this + " Container not allowed to load on territory: " + getSyncBaseItem().getPosition() + "  " + getSyncBaseItem());
        }

        if (containedItems.size() >= itemContainerType.getMaxCount()) {
            throw new IllegalArgumentException(this + " Container is full: " + containedItems.size());
        }
    }

    public boolean isAbleToContain(SyncBaseItem syncBaseItem) {
        try {
            isAbleToContainThrow(syncBaseItem);
            return true;
        } catch (IllegalArgumentException ignore) {
            return false;
        }
    }

    public void executeCommand(UnloadContainerCommand unloadContainerCommand) {
        if (containedItems.isEmpty()) {
            throw new IllegalStateException("No items in item container: " + getSyncBaseItem());
        }

        if (!getServices().getTerritoryService().isAllowed(unloadContainerCommand.getUnloadPos(), getSyncBaseItem())) {
            throw new IllegalArgumentException(this + " Container not allowed to unload on territory: " + unloadContainerCommand.getUnloadPos() + "  " + getSyncBaseItem());
        }

        unloadPos = unloadContainerCommand.getUnloadPos();
    }

    public boolean tick(double factor) {
        if (!getSyncBaseItem().isAlive()) {
            return false;
        }
        if (isTargetInRange(unloadPos, itemContainerType.getRange())) {
            if (getSyncBaseItem().hasSyncTurnable()) {
                getSyncBaseItem().getSyncTurnable().turnTo(unloadPos);
            }
            unload();
            stop();
            return false;
        } else {
            if (getSyncBaseItem().hasSyncMovable()) {
                getSyncBaseItem().getSyncMovable().tickMoveToTarget(factor, itemContainerType.getRange(), unloadPos);
                return true;
            } else {
                stop();
                return false;
            }
        }

    }

    private void unload() {
        SurfaceType surfaceType = getServices().getTerrainService().getSurfaceTypeAbsolute(unloadPos);
        for (Iterator<SyncBaseItem> iterator = containedItems.iterator(); iterator.hasNext();) {
            SyncBaseItem containedItem = iterator.next();
            if (allowedUnload(surfaceType, containedItem)) {
                containedItem.clearContained(unloadPos);
                iterator.remove();
            }
        }
        getSyncBaseItem().fireItemChanged(SyncItemListener.Change.CONTAINER_COUNT_CHANGED);
    }

    public void stop() {
        unloadPos = null;
        if (getSyncBaseItem().hasSyncMovable()) {
            getSyncBaseItem().getSyncMovable().stop();
        }
    }

    public boolean isActive() {
        return unloadPos != null;
    }

    public int getRange() {
        return itemContainerType.getRange();
    }

    public ItemContainerType getItemContainerType() {
        return itemContainerType;
    }

    public List<SyncBaseItem> getContainedItems() {
        return containedItems;
    }

    public boolean isAbleToContainAtLeastOne(Collection<SyncBaseItem> syncBaseItems) {
        for (SyncBaseItem syncBaseItem : syncBaseItems) {
            if (isAbleToContain(syncBaseItem)) {
                return true;
            }
        }
        return false;
    }

    public boolean atLeastOneAllowedToUnload(Index position) {
        SurfaceType surfaceType = getServices().getTerrainService().getSurfaceTypeAbsolute(position);
        for (SyncBaseItem containedItem : containedItems) {
            if (allowedUnload(surfaceType, containedItem)) {
                return true;
            }
        }
        return false;
    }

    private boolean allowedUnload(SurfaceType surfaceType, SyncBaseItem containedItem) {
        return containedItem.getTerrainType().allowSurfaceType(surfaceType);
    }

    public boolean isUnloadPosReachable(Index position) {
        try {
            getServices().getTerrainService().getNearestPoint(getSyncBaseItem().getTerrainType(), position, getItemContainerType().getRange());
            return true;
        } catch (IllegalArgumentException ignore) {
            return false;
        }
    }
}
