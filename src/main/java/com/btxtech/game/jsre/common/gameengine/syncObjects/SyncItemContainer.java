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

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemContainerType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.UnloadContainerCommand;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;

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
    private List<Id> containedItems = new ArrayList<Id>();
    private Index unloadPos;

    public SyncItemContainer(ItemContainerType itemContainerType, SyncBaseItem syncBaseItem) {
        super(syncBaseItem);
        this.itemContainerType = itemContainerType;
    }

    @Override
    public void synchronize(SyncItemInfo syncItemInfo) throws NoSuchItemTypeException, ItemDoesNotExistException {
        unloadPos = syncItemInfo.getUnloadPos();
        containedItems = syncItemInfo.getContainedItems();
    }

    @Override
    public void fillSyncItemInfo(SyncItemInfo syncItemInfo) {
        syncItemInfo.setUnloadPos(Index.saveCopy(unloadPos));
        syncItemInfo.setContainedItems(CommonJava.saveArrayListCopy(containedItems));
    }

    public void load(SyncBaseItem syncBaseItem) throws ItemContainerFullException, WrongOperationSurfaceException {
        if (getPlanetServices().getConnectionService().getGameEngineMode() != GameEngineMode.MASTER) {
            return;
        }

        isAbleToContainThrow(syncBaseItem);
        isOnOperationSurfaceThrow();
        containedItems.add(syncBaseItem.getId());
        syncBaseItem.setContained(getSyncBaseItem().getId());
        getSyncBaseItem().fireItemChanged(SyncItemListener.Change.CONTAINER_COUNT_CHANGED, null);
        getPlanetServices().getConnectionService().sendSyncInfo(getSyncBaseItem());
    }

    public void executeCommand(UnloadContainerCommand unloadContainerCommand) throws WrongOperationSurfaceException {
        if (containedItems.isEmpty()) {
            throw new IllegalStateException("No items in item container: " + getSyncBaseItem());
        }
        isOnOperationSurfaceThrow();
        unloadPos = unloadContainerCommand.getUnloadPos();
    }

    public boolean tick(double factor) throws ItemDoesNotExistException {
        if (!getSyncBaseItem().isAlive()) {
            return false;
        }
        if (!isActive()) {
            return false;
        }

        getSyncItemArea().turnTo(unloadPos);
        unload();
        stop();
        return false;
    }

    private void unload() throws ItemDoesNotExistException {
        if (getPlanetServices().getConnectionService().getGameEngineMode() != GameEngineMode.MASTER) {
            return;
        }
        for (Iterator<Id> iterator = containedItems.iterator(); iterator.hasNext(); ) {
            Id containedItem = iterator.next();
            if (allowedUnload(unloadPos, containedItem)) {
                SyncBaseItem syncItem = (SyncBaseItem) getPlanetServices().getItemService().getItem(containedItem);
                syncItem.clearContained(unloadPos);
                getGlobalServices().getConditionService().onSyncItemUnloaded(syncItem);
                getPlanetServices().getConnectionService().sendSyncInfo(syncItem);
                iterator.remove();
            }
        }
        getSyncBaseItem().fireItemChanged(SyncItemListener.Change.CONTAINER_COUNT_CHANGED, null);
    }

    public void stop() {
        unloadPos = null;
        if (getSyncBaseItem().hasSyncMovable()) {
            getSyncBaseItem().getSyncMovable().stop();
        }
    }

    public Index getUnloadPos() {
        return unloadPos;
    }

    public void setUnloadPos(Index unloadPos) {
        this.unloadPos = unloadPos;
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

    public List<Id> getContainedItems() {
        return containedItems;
    }

    public void setContainedItems(List<Id> containedItems) {
        this.containedItems = containedItems;
    }

    private void isAbleToContainThrow(SyncBaseItem syncBaseItem) throws ItemContainerFullException {
        if (getSyncBaseItem().equals(syncBaseItem)) {
            throw new IllegalArgumentException("Can not contain oneself: " + this);
        }

        if (!itemContainerType.isAbleToContain(syncBaseItem.getBaseItemType().getId())) {
            throw new IllegalArgumentException("Container " + getSyncBaseItem() + " is not able to contain: " + syncBaseItem);
        }

        if (containedItems.size() >= itemContainerType.getMaxCount()) {
            throw new ItemContainerFullException(this, containedItems.size());
        }
    }

    private void isOnOperationSurfaceThrow() throws WrongOperationSurfaceException {
        SurfaceType operationSurfaceType = itemContainerType.getOperationSurfaceType();
        if (operationSurfaceType == null || operationSurfaceType == SurfaceType.NONE) {
            return;
        }
        if (!getPlanetServices().getTerrainService().hasSurfaceTypeInRegion(operationSurfaceType, getSyncItemArea().generateCoveringRectangle())) {
            throw new WrongOperationSurfaceException(getSyncBaseItem());
        }
    }

    public boolean isAbleToLoad(SyncBaseItem syncBaseItem) {
        try {
            isAbleToContainThrow(syncBaseItem);
            isOnOperationSurfaceThrow();
            return true;
        } catch (IllegalArgumentException ignore) {
            return false;
        } catch (ItemContainerFullException ignore) {
            return false;
        } catch (WrongOperationSurfaceException e) {
            return false;
        }
    }

    public boolean atLeastOneAllowedToLoad(Collection<SyncBaseItem> syncBaseItems) {
        for (SyncBaseItem syncBaseItem : syncBaseItems) {
            if (isAbleToLoad(syncBaseItem)) {
                return true;
            }
        }
        return false;
    }

    public boolean atLeastOneAllowedToUnload(Index position) {
        try {
            isOnOperationSurfaceThrow();
            for (Id containedItem : containedItems) {
                if (allowedUnload(position, containedItem)) {
                    return true;
                }
            }
        } catch (ItemDoesNotExistException e) {
            ClientExceptionHandler.handleException(e);
        } catch (WrongOperationSurfaceException e) {
            return false;
        }
        return false;
    }

    private boolean allowedUnload(Index position, Id containedItem) throws ItemDoesNotExistException {
        return isInUnloadRange(position) && getPlanetServices().getTerrainService().isFree(position, getPlanetServices().getItemService().getItem(containedItem).getItemType(), null, null);
    }

    private boolean isInUnloadRange(Index unloadPos) {
        return getSyncItemArea().isInRange(getRange(), unloadPos);
    }
}
