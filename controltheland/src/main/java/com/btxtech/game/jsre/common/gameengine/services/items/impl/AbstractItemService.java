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

package com.btxtech.game.jsre.common.gameengine.services.items.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ProjectileItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ResourceType;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.base.AbstractBaseService;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncProjectileItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * User: beat
 * Date: 28.11.2009
 * Time: 13:04:56
 */
abstract public class AbstractItemService implements ItemService {
    private final HashMap<Integer, ItemType> itemTypes = new HashMap<Integer, ItemType>();

    public SyncItem newSyncItem(Id id, Index position, int itemTypeId, SimpleBase base, Services services) throws NoSuchItemTypeException {
        ItemType itemType = getItemType(itemTypeId);
        SyncItem syncItem;
        if (itemType instanceof BaseItemType) {
            if (base == null) {
                throw new NullPointerException(this + " base must be set for a SyncBaseItem");
            }
            syncItem = new SyncBaseItem(id, position, (BaseItemType) itemType, services, base);
        } else if (itemType instanceof ResourceType) {
            if (base != null) {
                throw new IllegalArgumentException(this + " ResourceType does not have a base");
            }
            syncItem = new SyncResourceItem(id, position, (ResourceType) itemType, services);
        } else if (itemType instanceof ProjectileItemType) {
            if (base == null) {
                throw new NullPointerException(this + " base must be set for a ProjectileItemType");
            }
            syncItem = new SyncProjectileItem(id, position, (ProjectileItemType) itemType, services, base);
        } else {
            throw new IllegalArgumentException(this + " ItemType not supported: " + itemType);
        }
        return syncItem;
    }

    @Override
    public ItemType getItemType(int itemTypeId) throws NoSuchItemTypeException {
        ItemType itemType = itemTypes.get(itemTypeId);
        if (itemType == null) {
            throw new NoSuchItemTypeException(itemTypeId);
        }
        return itemType;
    }

    @Override
    public ItemType getItemType(String name) throws NoSuchItemTypeException {
        for (ItemType itemType : itemTypes.values()) {
            if (itemType.getName().equals(name)) {
                return itemType;
            }
        }
        throw new NoSuchItemTypeException(name);
    }

    @Override
    public List<ItemType> getItemTypes() {
        return new ArrayList<ItemType>(itemTypes.values());
    }

    public void setItemTypes(Collection<ItemType> itemTypes) {
        this.itemTypes.clear();
        for (ItemType itemType : itemTypes) {
            if (this.itemTypes.containsKey(itemType.getId())) {
                throw new IllegalArgumentException(this + " ID already exists in items: " + itemType.getId());
            }
            this.itemTypes.put(itemType.getId(), itemType);
        }
    }

    public void addDeltaItemTypes(Collection<ItemType> itemTypes) {
        for (ItemType itemType : itemTypes) {
            if (!this.itemTypes.containsKey(itemType.getId())) {
                this.itemTypes.put(itemType.getId(), itemType);
            }
        }
    }

    protected void removeAll(ArrayList<ItemType> newItems) {
        for (ItemType newItem : newItems) {
            this.itemTypes.put(newItem.getId(), newItem);
        }
    }

    protected void changeAll(ArrayList<ItemType> changingItems) {
        for (ItemType changingItem : changingItems) {
            ItemType itemType = itemTypes.get(changingItem.getId());
            itemType.changeTo(changingItem);
        }
    }

    protected void putAll(ArrayList<ItemType> newItems) {
        for (ItemType newItem : newItems) {
            if (this.itemTypes.containsKey(newItem.getId())) {
                throw new IllegalArgumentException(this + " ID already exists in items: " + newItem.getId());
            }
            this.itemTypes.put(newItem.getId(), newItem);
        }
    }


    @Override
    public boolean areItemTypesLoaded() {
        return !itemTypes.isEmpty();
    }

    @Override
    public List<BaseItemType> ableToBuild(BaseItemType toBeBuilt) {
        ArrayList<BaseItemType> result = new ArrayList<BaseItemType>();
        for (ItemType itemType : itemTypes.values()) {
            if (!(itemType instanceof BaseItemType)) {
                continue;
            }
            BaseItemType baseItemType = (BaseItemType) itemType;
            if (baseItemType.getBuilderType() != null && baseItemType.getBuilderType().getAbleToBuild().contains(toBeBuilt.getId())) {
                result.add(baseItemType);
            }
            if (baseItemType.getFactoryType() != null && baseItemType.getFactoryType().getAbleToBuild().contains(toBeBuilt.getId())) {
                result.add(baseItemType);
            }
        }
        return result;
    }

    abstract protected AbstractBaseService getBaseService();

    public List<? extends SyncItem> getItems(String itemTypeName, SimpleBase simpleBase) throws NoSuchItemTypeException {
        return getItems(getItemType(itemTypeName), simpleBase);
    }


    @Override
    public List<SyncBaseItem> getBaseItems(List<Id> baseItemsIds) throws ItemDoesNotExistException {
        ArrayList<SyncBaseItem> syncBaseItems = new ArrayList<SyncBaseItem>();
        for (Id baseItemsId : baseItemsIds) {
            syncBaseItems.add((SyncBaseItem) getItem(baseItemsId));
        }
        return syncBaseItems;
    }

    @Override
    public List<Id> getBaseItemIds(List<SyncBaseItem> baseItems) {
        ArrayList<Id> syncBaseItemIds = new ArrayList<Id>();
        for (SyncBaseItem baseItem : baseItems) {
            syncBaseItemIds.add(baseItem.getId());
        }
        return syncBaseItemIds;
    }

    @Override
    public Collection<SyncBaseItem> getBaseItemsInRadius(Index position, int radius, SimpleBase simpleBase, Collection<BaseItemType> baseItemTypeFilter) {
        Collection<SyncBaseItem> syncBaseItems = getBaseItemsInRectangle(position.getRegion(2 * radius, 2 * radius), simpleBase, baseItemTypeFilter);
        for (Iterator<SyncBaseItem> iterator = syncBaseItems.iterator(); iterator.hasNext();) {
            SyncBaseItem syncBaseItem = iterator.next();
            if (!syncBaseItem.getSyncItemArea().isInRange(radius, position)) {
                iterator.remove();
            }
        }
        return syncBaseItems;
    }
}
