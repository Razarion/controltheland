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
import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.PositionTakenException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.BoxItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ProjectileItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ResourceType;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.base.AbstractBaseService;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncProjectileItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 28.11.2009
 * Time: 13:04:56
 */
abstract public class AbstractItemService implements ItemService {
    private final HashMap<Integer, ItemType> itemTypes = new HashMap<Integer, ItemType>();
    private Logger log = Logger.getLogger(AbstractItemService.class.getName());

    /**
     * Iterates over all sync items
     *
     * @param itemHandler   see ItemHandler
     * @param defaultReturn if iteration is finished without an aport, this param is returned
     * @param <T>           return type
     * @return the parameter from the itemHandler or the defaultReturn
     */
    protected abstract <T> T iterateOverItems(ItemHandler<T> itemHandler, T defaultReturn);

    protected abstract Services getServices();

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
        } else if (itemType instanceof BoxItemType) {
            if (base != null) {
                throw new IllegalArgumentException(this + " BoxItemType does not have a base");
            }
            syncItem = new SyncBoxItem(id, position, (BoxItemType) itemType, services);
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
        for (Iterator<SyncBaseItem> iterator = syncBaseItems.iterator(); iterator.hasNext(); ) {
            SyncBaseItem syncBaseItem = iterator.next();
            if (!syncBaseItem.getSyncItemArea().isInRange(radius, position)) {
                iterator.remove();
            }
        }
        return syncBaseItems;
    }

    @Override
    public boolean isSyncItemOverlapping(SyncItem syncItem) {
        return isSyncItemOverlapping(syncItem, null, null, null);
    }

    @Override
    public boolean isSyncItemOverlapping(final SyncItem syncItem, final Index positionToCheck, final Double angelToCheck, final Collection<SyncItem> exceptionThem) {

        return iterateOverItems(new ItemHandler<Boolean>() {
            @Override
            public Boolean handleItem(SyncItem otherItem) {
                if (otherItem.equals(syncItem)) {
                    return null;
                }
                if (!otherItem.getSyncItemArea().hasPosition()) {
                    return null;
                }

                if (exceptionThem != null && exceptionThem.contains(otherItem)) {
                    return null;
                }

                if (otherItem instanceof SyncBaseItem) {
                    SyncBaseItem syncBaseItem = (SyncBaseItem) otherItem;
                    if (!syncBaseItem.hasSyncMovable() || !syncBaseItem.getSyncMovable().isActive()) {
                        if (positionToCheck == null) {
                            if (syncItem.getSyncItemArea().contains(syncBaseItem)) {
                                return true;
                            }
                        } else {
                            if (syncItem.getSyncItemArea().contains(syncBaseItem, positionToCheck, angelToCheck)) {
                                return true;
                            }
                        }
                    }
                } else if (otherItem instanceof SyncResourceItem) {
                    if (positionToCheck == null) {
                        if (syncItem.getSyncItemArea().contains(otherItem)) {
                            return true;
                        }
                    } else {
                        if (syncItem.getSyncItemArea().contains(otherItem, positionToCheck, angelToCheck)) {
                            return true;
                        }
                    }
                }
                return null;
            }
        }, false);
    }

    @Override
    public boolean hasStandingItemsInRect(final Rectangle rectangle, final SyncItem exceptThat) {
        return iterateOverItems(new ItemHandler<Boolean>() {
            @Override
            public Boolean handleItem(SyncItem syncItem) {
                if (syncItem.equals(exceptThat)) {
                    return null;
                }
                if (!syncItem.getSyncItemArea().hasPosition()) {
                    return null;
                }
                if (syncItem instanceof SyncBaseItem) {
                    SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                    if (!syncBaseItem.hasSyncMovable() || !syncBaseItem.getSyncMovable().isActive()) {
                        if (syncItem.getSyncItemArea().contains(rectangle)) {
                            return true;
                        }
                    }
                } else if (syncItem instanceof SyncResourceItem) {
                    if (syncItem.getSyncItemArea().contains(rectangle)) {
                        return true;
                    }
                }
                return null;
            }
        }, false);
    }

    @Override
    public boolean hasItemsInRectangle(final Rectangle rectangle) {
        return iterateOverItems(new ItemHandler<Boolean>() {
            @Override
            public Boolean handleItem(SyncItem syncItem) {
                if (!syncItem.getSyncItemArea().hasPosition()) {
                    return null;
                }

                return syncItem.getSyncItemArea().contains(rectangle) ? true : null;
            }
        }, false);
    }

    @Override
    public void checkBuildingsInRect(BaseItemType toBeBuiltType, Index toBeBuildPosition) {
        if (isUnmovableSyncItemOverlapping(toBeBuiltType.getBoundingBox(), toBeBuildPosition)) {
            throw new PositionTakenException(toBeBuildPosition, toBeBuiltType);
        }
    }

    @Override
    public boolean isUnmovableSyncItemOverlapping(final BoundingBox boundingBox, final Index positionToCheck) {
        return iterateOverItems(new ItemHandler<Boolean>() {
            @Override
            public Boolean handleItem(SyncItem syncItem) {
                if (!syncItem.getSyncItemArea().hasPosition()) {
                    return null;
                }

                if (syncItem instanceof SyncBaseItem) {
                    SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                    if (!syncBaseItem.hasSyncMovable()) {
                        if (syncBaseItem.getSyncItemArea().contains(boundingBox, positionToCheck)) {
                            return true;
                        }
                    }
                } else if (syncItem instanceof SyncResourceItem) {
                    if (syncItem.getSyncItemArea().contains(boundingBox, positionToCheck)) {
                        return true;
                    }
                }
                return null;
            }
        }, false);
    }

    @Override
    public Collection<SyncBaseItem> getItems4Base(final SimpleBase simpleBase) {
        final Collection<SyncBaseItem> itemsInBase = new ArrayList<SyncBaseItem>();
        iterateOverItems(new ItemHandler<Void>() {
            @Override
            public Void handleItem(SyncItem syncItem) {
                if (!syncItem.getSyncItemArea().hasPosition()) {
                    return null;
                }

                if (syncItem instanceof SyncBaseItem && ((SyncBaseItem) syncItem).getBase().equals(simpleBase)) {
                    itemsInBase.add((SyncBaseItem) syncItem);
                }
                return null;
            }
        }, null);
        return itemsInBase;
    }

    @Override
    public void killSyncItems(Collection<SyncItem> itemsToKill) {
        for (SyncItem syncItem : itemsToKill) {
            try {
                killSyncItem(syncItem, null, true, false);
            } catch (Exception e) {
                log.log(Level.SEVERE, "", e);
            }
        }
    }

    @Override
    public Collection<SyncBaseItem> getEnemyItems(final SimpleBase simpleBase, final Rectangle region) {
        final Collection<SyncBaseItem> enemyItems = new ArrayList<SyncBaseItem>();
        iterateOverItems(new ItemHandler<Void>() {
            @Override
            public Void handleItem(SyncItem syncItem) {
                if (!syncItem.getSyncItemArea().hasPosition()) {
                    return null;
                }

                if (syncItem instanceof SyncBaseItem
                        && ((SyncBaseItem) syncItem).isEnemy(simpleBase)
                        && region.contains(syncItem.getSyncItemArea().getPosition())) {
                    enemyItems.add((SyncBaseItem) syncItem);
                }

                return null;
            }
        }, null);
        return enemyItems;
    }

    @Override
    public SyncBaseItem getFirstEnemyItemInRange(final SyncBaseItem baseSyncItem) {
        return iterateOverItems(new ItemHandler<SyncBaseItem>() {
            @Override
            public SyncBaseItem handleItem(SyncItem syncItem) {
                if (!syncItem.getSyncItemArea().hasPosition()) {
                    return null;
                }

                if (syncItem instanceof SyncBaseItem
                        && baseSyncItem.isEnemy((SyncBaseItem) syncItem)
                        && baseSyncItem.getSyncWeapon().isAttackAllowedWithoutMoving(syncItem)) {
                    return (SyncBaseItem) syncItem;
                }
                return null;
            }
        }, null);
    }

    @Override
    public Collection<SyncBaseItem> getBaseItemsInRectangle(final Rectangle rectangle, final SimpleBase simpleBase, final Collection<BaseItemType> baseItemTypeFilter) {
        final Collection<SyncBaseItem> itemsInBase = new ArrayList<SyncBaseItem>();
        iterateOverItems(new ItemHandler<Void>() {
            @Override
            public Void handleItem(SyncItem syncItem) {
                if (!syncItem.getSyncItemArea().hasPosition()) {
                    return null;
                }

                if (!(syncItem instanceof SyncBaseItem)) {
                    return null;
                }
                SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                if (simpleBase != null && !(syncBaseItem.getBase().equals(simpleBase))) {
                    return null;
                }
                if (!syncBaseItem.getSyncItemArea().contains(rectangle)) {
                    return null;
                }
                if (baseItemTypeFilter != null && !baseItemTypeFilter.contains(syncBaseItem.getBaseItemType())) {
                    return null;
                }

                itemsInBase.add((SyncBaseItem) syncItem);
                return null;
            }
        }, null);
        return itemsInBase;
    }

    @Override
    public Collection<? extends SyncItem> getItems(final ItemType itemType, final SimpleBase simpleBase) {
        final Collection<SyncItem> items = new ArrayList<SyncItem>();
        iterateOverItems(new ItemHandler<Void>() {
            @Override
            public Void handleItem(SyncItem syncItem) {
                if (!syncItem.getItemType().equals(itemType)) {
                    return null;
                }
                if (simpleBase != null) {
                    if (syncItem instanceof SyncBaseItem && ((SyncBaseItem) syncItem).getBase().equals(simpleBase)) {
                        items.add(syncItem);
                    }
                } else {
                    items.add(syncItem);
                }
                return null;
            }
        }, null);
        return items;
    }

    @Override
    public void sellItem(Id id) throws ItemDoesNotExistException, NotYourBaseException {
        try {
            SyncBaseItem syncBaseItem = (SyncBaseItem) getItem(id);
            getServices().getBaseService().checkBaseAccess(syncBaseItem);
            double health = syncBaseItem.getHealth();
            double fullHealth = syncBaseItem.getBaseItemType().getHealth();
            double price = syncBaseItem.getBaseItemType().getPrice();
            double buildup = syncBaseItem.getBuildup();
            killSyncItem(syncBaseItem, null, true, false);
            SimpleBase simpleBase = syncBaseItem.getBase();
            // May last item sold
            if (getServices().getBaseService().isAlive(simpleBase)) {
                double money = health / fullHealth * buildup * price * getServices().getCommonUserGuidanceService().getLevelScope().getItemSellFactor();
                getServices().getBaseService().depositResource(money, simpleBase);
                getServices().getBaseService().sendAccountBaseUpdate(simpleBase);
            }
        } catch (ItemDoesNotExistException ignore) {
            // Ignore
            // Item may have been killed
        }
    }


}
